package com.nuocao.common.event;

import org.noear.solon.core.event.EventBus;

/**
 * 内部事件总线的发送入口（静态工具类）。
 *
 * <p>业务代码只需调用这里的发送方法把事件发出去即可，不需要感知由谁处理、也不需要注入任何 Bean，
 * 因此在静态方法、工具类等没有容器上下文的场景中同样可用。</p>
 *
 * <h2>发布流程</h2>
 * <pre>
 * NuoCaoEventHelper.send / sendAsync(event)
 *      └─ EventBus.publish / publishAsync(event)   // Solon 事件总线
 *           └─ NuoCaoEventListener.onEvent()       // 统一监听器
 *                └─ event.execute()                // 事件自身的业务逻辑
 * </pre>
 * 详见 {@link NuoCaoEvent}。
 *
 * <h2>两种发送方式（按需选择）</h2>
 * <table border="1">
 *     <caption>发送方式对比</caption>
 *     <tr><th>方法</th><th>执行线程</th><th>异常处理</th><th>适用场景</th></tr>
 *     <tr>
 *         <td>{@link #send(NuoCaoEvent)}</td>
 *         <td>发送方线程（同步阻塞）</td>
 *         <td>抛回发送方</td>
 *         <td>逻辑很轻、且失败必须让主流程感知</td>
 *     </tr>
 *     <tr>
 *         <td>{@link #sendAsync(NuoCaoEvent)}</td>
 *         <td>异步线程池（不阻塞发送方）</td>
 *         <td>不会影响发送方</td>
 *         <td>逻辑较重（查库、批量清缓存等）或失败可容忍</td>
 *     </tr>
 * </table>
 *
 * <h2>使用示例</h2>
 * <pre>
 * // 轻量、需同步感知：清单个用户的缓存
 * NuoCaoEventHelper.send(new UserPermissionUpdateEvent(userId));
 *
 * // 较重、可容忍失败：清某角色下所有用户的缓存
 * NuoCaoEventHelper.sendAsync(new RolePermissionUpdateEvent(roleId));
 * </pre>
 *
 * <h2>注意事项</h2>
 * <ul>
 *     <li>{@link #send(NuoCaoEvent)} 是<b>同步</b>发布，{@code execute()} 在发送方线程内执行，
 *         耗时逻辑会直接拖慢调用方；并且它抛出的异常会被原样抛回发送方
 *         （受检异常包装为 {@code RuntimeException}），可能把一次成功的业务操作变成接口报错。</li>
 *     <li>{@link #sendAsync(NuoCaoEvent)} 使用 Solon 的全局异步线程池（{@code RunUtil}）：开启虚拟线程时
 *         为「一任务一线程」，未开启时是<b>CPU 核数规模的共享固定线程池</b>。因此异步任务里不要做长时间阻塞操作，
 *         否则会占用整个应用共享的异步线程。</li>
 *     <li>异步发送意味着失去了「主流程与处理逻辑」的时序关系，若处理逻辑依赖主流程已提交的数据
 *         （如清缓存时又去读库），请确认此时事务已提交，否则可能读到旧数据。</li>
 * </ul>
 *
 * @see NuoCaoEvent 事件契约（事件数据 + 处理逻辑）
 * @see NuoCaoEventListener 事件监听器（总线出口）
 */
public class NuoCaoEventHelper {

    /**
     * 同步发布一个内部事件：在当前线程内执行事件的 {@code execute()}，执行完才返回。
     *
     * <p>事件处理逻辑抛出的异常会抛回调用方（受检异常包装为 {@code RuntimeException}）。
     * 如果该逻辑属于「失败了也不该影响主流程」的旁路操作，请改用 {@link #sendAsync(NuoCaoEvent)}。</p>
     *
     * @param event 待发布的事件，其 {@code execute()} 将被监听器回调执行
     */
    public static void send(NuoCaoEvent event){
        EventBus.publish(event);
    }

    /**
     * 异步发布一个内部事件：把 {@code execute()} 提交到 Solon 的全局异步线程池后立即返回，不阻塞调用方。
     *
     * <p>事件处理逻辑抛出的异常不会影响调用方，只会在异步线程中被抛出/记录。
     * 适用于处理逻辑较重（查库、批量清缓存等）或失败可容忍的场景。</p>
     *
     * <p>注意：<b>不</b>保证与调用方主流程的先后顺序，也不保证事务可见性。</p>
     *
     * @param event 待发布的事件，其 {@code execute()} 将在异步线程中执行
     */
    public static void sendAsync(NuoCaoEvent event){
        EventBus.publishAsync(event);
    }
}
