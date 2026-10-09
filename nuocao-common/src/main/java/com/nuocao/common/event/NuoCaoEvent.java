package com.nuocao.common.event;

/**
 * 内部事件总线的事件契约：所有内部事件都实现本接口。
 *
 * <h2>设计约定：事件 = 事件数据 + 事件处理逻辑</h2>
 * 与常见的「事件对象 + 独立监听器」写法不同，本项目把两者合并在同一个类里：
 * <ul>
 *     <li>类字段即事件负载（构造事件时传入的入参）；</li>
 *     <li>{@link #execute()} 即该事件被触发时要执行的业务逻辑。</li>
 * </ul>
 * 好处是新增一个业务事件只需新增一个类，不必再单独编写并注册对应的监听器。
 *
 * <h2>事件流转链路</h2>
 * <pre>
 *  业务代码  NuoCaoEventHelper.send(event)              // ① 发送事件
 *                └─ EventBus.publish(event)              // ② 发布到 Solon 事件总线
 *                     └─ NuoCaoEventListener.onEvent(e)  // ③ 总线回调监听器
 *                          └─ event.execute()            // ④ 回到事件自身执行逻辑
 * </pre>
 *
 * <h2>派发规则</h2>
 * {@link NuoCaoEventListener} 是以父类型 {@code NuoCaoEvent} 注册到 Solon {@code EventBus} 的；
 * 总线在派发时按 {@code 注册类型.isInstance(事件对象)} 匹配，因此<b>任意实现类（及其子类）实例
 * 都会被同一个监听器接收到</b>，再由监听器回调各自的 {@link #execute()}。
 *
 * <h2>使用方式</h2>
 * <pre>
 * // 1) 定义事件：字段承载参数，execute() 写业务逻辑
 * public class UserPermissionUpdateEvent implements NuoCaoEvent {
 *     private final Long userId;
 *     public UserPermissionUpdateEvent(Long userId) { this.userId = userId; }
 *
 *     &#64;Override
 *     public void execute() { ... }
 * }
 *
 * // 2) 发送事件（发送方不需要知道由谁处理）
 * NuoCaoEventHelper.send(new UserPermissionUpdateEvent(userId));
 * </pre>
 *
 * <h2>注意事项</h2>
 * <ul>
 *     <li>实现类通常由 {@code new} 直接创建，不是容器管理的 Bean，因此 {@code @Async} 之类的
 *         注解对它<b>不会生效</b>；需要异步执行请改用 {@link NuoCaoEventHelper#sendAsync(NuoCaoEvent)}。</li>
 *     <li>{@link #execute()} 未声明 {@code throws}，实现类只能抛出非受检异常，受检异常需在实现内部自行处理。</li>
 *     <li>实现类内获取依赖一般通过 {@code BeanUtils.getBean(...)} 服务定位，请务必对返回值判空，
 *         否则容器中缺少该 Bean 时会抛 {@code NullPointerException}。</li>
 *     <li>事件类同时承载「数据 + 行为」，因此不适合作为纯 DTO 序列化后跨进程传输（如投递 MQ）；
 *         这类场景请另建消息体。</li>
 * </ul>
 *
 * @see NuoCaoEventHelper 事件发送入口
 * @see NuoCaoEventListener 事件监听器（总线出口）
 */
public interface NuoCaoEvent {
    /**
     * 执行该事件对应的业务逻辑。
     * <p>
     * 由 {@link NuoCaoEventListener} 在事件发布后回调，业务代码一般不直接调用。
     */
    void execute();
}
