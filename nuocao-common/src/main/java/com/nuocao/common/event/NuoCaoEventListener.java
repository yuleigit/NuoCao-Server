package com.nuocao.common.event;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.event.EventListener;

/**
 * 内部事件总线的统一监听器：把 Solon 事件总线收到的 {@link NuoCaoEvent} 转交给事件自身执行。
 *
 * <h2>职责</h2>
 * 它是整个总线唯一的「出口」，本身不含任何业务逻辑，只做一次转发：
 * <pre>
 * EventBus.publish(event) → 本类 onEvent(event) → event.execute()
 * </pre>
 * 具体要做什么，由各个 {@link NuoCaoEvent} 实现类的 {@code execute()} 决定（详见 {@link NuoCaoEvent}）。
 *
 * <h2>为什么只需要一个监听器</h2>
 * 本类注册在一次性的父类型 {@link NuoCaoEvent} 上，而 Solon 的 {@code EventBus} 派发时按
 * {@code 注册类型.isInstance(事件对象)} 匹配，因此所有 {@code NuoCaoEvent} 的子类型事件
 * 都会落到这里，无需为每个事件单独写监听器。
 *
 * <h2>注册方式（重要，勿删注解）</h2>
 * Solon 只在<b>把 Bean 送入容器时</b>（{@code AppContext#beanDeliver}）自动识别并订阅容器中
 * {@code EventListener} 类型的 Bean；没有任何注解的普通类不会被建成 Bean，也就不会被订阅，
 * 此时 {@link NuoCaoEventHelper#send} 发布的事件会被<b>静默丢弃且不报任何错</b>。
 * <p>
 * 所以本类的 {@code @Component} 是总线生效的前提，请不要移除。若改为手工注册，等价写法是
 * {@code Solon.context().onEvent(NuoCaoEvent.class, new NuoCaoEventListener())} 或
 * {@code EventBus.subscribe(NuoCaoEvent.class, new NuoCaoEventListener())}。
 *
 * @see NuoCaoEvent 事件契约（事件数据 + 处理逻辑）
 * @see NuoCaoEventHelper 事件的发送入口
 */
@Component
public class NuoCaoEventListener implements EventListener<NuoCaoEvent> {

    /**
     * 事件总线回调入口。
     * <p>
     * 由 Solon 的 {@code EventBus} 在事件发布后调用，业务代码不应直接调用本方法。
     *
     * @param event 已发布的 {@link NuoCaoEvent} 实例（含其任意子类实例）
     * @throws Throwable 由 {@code event.execute()} 抛出。Solon 的 {@code EventBus.publish} 会把异常
     *                   抛给发送方（受检异常会被包装为 {@code RuntimeException}）；
     *                   {@code publishTry} 只记录日志；{@code publishAsync} 则在异步线程中抛出
     */
    @Override
    public void onEvent(NuoCaoEvent event) throws Throwable {
        event.execute();
    }
}
