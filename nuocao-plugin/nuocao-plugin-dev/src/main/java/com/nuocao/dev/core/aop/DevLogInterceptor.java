package com.nuocao.dev.core.aop;

import com.nuocao.common.annotation.CommonLog;
import com.nuocao.dev.modular.log.util.DevLogUtils;
import com.nuocao.plugin.dev.api.dto.log.DevLogAddDTO;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

/**
 * 操作日志拦截器：拦截标注了 {@link CommonLog} 的方法，把「谁、什么时间、调了哪个接口、传了什么参数、结果如何、耗时多少」落到操作日志表。
 *
 * <h2>注册方式</h2>
 * 由 {@code com.nuocao.dev.core.integration.PluginImpl} 在 Solon 启动时注册：
 * <pre>
 * context.beanInterceptorAdd(CommonLog.class, new DevLogInterceptor());
 * </pre>
 * 所以它只对<b>容器管理的 Bean</b>（Controller / Service / Component）生效，普通 {@code new} 出来的对象不会走这里；
 * 同一个 Bean 内部的<b>自调用</b>（{@code this.xxx()}）也不会走代理，同样不会被拦截。
 *
 * <h2>执行时序</h2>
 * <pre>
 * doIntercept(inv)
 *   ├─ 取 {@link CommonLog} 注解 ── 为空则直接放行（不做任何记录）
 *   ├─ buildLogDto()   ← 必须在当前线程采集（Sa-Token / Context 均为 ThreadLocal）
 *   │
 *   ├─ try ─ inv.invoke()          ← 只包住「业务调用」这一步
 *   │        └─ 抛异常 ─→ executeExceptionLog(...) ─→ throw e（原样抛出）
 *   │
 *   └─ executeOperationLog(...)    ← 在 try 之外：它自身的异常不会被误判成业务失败
 *            └─ return result
 * </pre>
 *
 * <h2>实现要点（三条铁律）</h2>
 * <ol>
 *     <li><b>上下文只在这个线程采集</b>：登录态（Sa-Token）和当前请求（{@code Context.current()}）都绑定在
 *         ThreadLocal 上，异步线程里取不到。所以采集动作必须在 {@link Invocation#invoke()} 之前、当前线程内
 *         一次性完成，异步线程只负责最后一步「落库」。</li>
 *     <li><b>不改变业务异常</b>：业务异常一律<b>原样抛出</b>，绝不包装。一旦包装成新异常，
 *         {@code CommonExceptionI18n} 自带的错误码 / i18n 文案、Sa-Token 的 401 / 403 等语义会全部丢失，
 *         前端只能拿到一个笼统的 500。<br>
 *         注意：{@code MethodWrap#invoke} 已经用 {@code Utils.throwableUnwrap} 拆掉了
 *         {@code InvocationTargetException}，所以这里拿到的就是业务异常本身。</li>
 *     <li><b>日志失败不影响业务</b>：<b>本类与被调用的 {@link DevLogUtils} 都不允许把「写日志」产生的异常抛出去</b>。
 *         因此 {@code try} 只包住业务调用：成功路径的日志写在 {@code try} 之外，
 *         失败路径的日志也由 {@link DevLogUtils} 内部兜底，
 *         任何环节失败都只打 warn，绝不会让一次已经成功的业务操作变成接口报错。</li>
 * </ol>
 *
 * <h2>已知边界（不是缺陷，按需取舍）</h2>
 * <ul>
 *     <li><b>401 / 403 不产生操作日志</b>：登录与 {@code @SaCheckPermission} 等鉴权由
 *         {@code SaTokenInterceptor}（{@code RouterInterceptor}，路由层）在执行 Controller 之前完成。
 *         请求一旦被它拦下，根本不会进入本拦截器，自然也没有日志。
 *         若确实需要记录「尝试操作但无权限」，得把鉴权从路由层下沉到 AOP 层。</li>
 *     <li><b>非 Web 线程</b>：如定时任务、消息消费等无 HTTP 上下文的场景，采集不到 URL / IP / 浏览器，
 *         日志里这些字段会为空或占位，其余字段仍会记录。</li>
 * </ul>
 *
 * @see CommonLog 触发本拦截器的注解
 * @see DevLogUtils 日志组装与落库
 */
public class DevLogInterceptor implements Interceptor {

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        // ① 取注解。只有标注了 @CommonLog 的方法才会被派发到这里；为空则直接放行，不做任何记录。
        CommonLog commonLog = inv.getMethodAnnotation(CommonLog.class);
        if (commonLog == null) {
            return inv.invoke();
        }

        // ② 当前线程一次性采集上下文（请求URL、登录用户、租户、开始时间）。
        //    该方法内部已兜底，任何失败都会退化成「最小可用日志」，绝不会影响业务。
        DevLogAddDTO logDto = DevLogUtils.buildLogDto();

        // ③ 只把「业务调用」放进 try：这样 catch 里记的一定是真正的业务异常，
        //    「记日志出错」不会被误判成业务失败，更不会污染返回给前端的响应。
        Object result;
        try {
            result = inv.invoke();
        } catch (Throwable e) {
            // 失败日志：内部吞掉一切异常
            DevLogUtils.executeExceptionLog(commonLog, logDto, inv, e);
            // 原样抛出，保留原始异常类型、错误码与堆栈
            throw e;
        }

        // ④ 成功日志：位于 try 之外，其自身异常由 DevLogUtils 内部吞掉，不影响返回值
        DevLogUtils.executeOperationLog(commonLog, logDto, inv, result);
        return result;
    }
}
