package com.nuocao.data.access.datascope.aop;

import com.nuocao.data.access.datascope.annotation.DataScopeIgnore;
import com.nuocao.data.access.datascope.context.DataScopeContext;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

/**
 * {@link DataScopeIgnore} 的切面实现（注解上的 {@code @Around} 指向本类）。
 *
 * <p>语义：在<b>被注解的方法调用栈</b>内把 {@code DataScopeContext.ignore} 置为 true，调用结束（含异常）后还原。
 *
 * <p>同类内部 {@code this.xxx()} 调用不会走代理，切面不触发；这些场景请用
 * {@code DataScopeUtils.executeIgnore(...)}。
 *
 * <p>待办：数据权限目前没有全局开关（租户侧有 {@code tenant.enable}），
 * 需要整体关闭时只能逐个方法加 {@code @DataScopeIgnore}。
 */
@Component
public class DataScopeAspect implements Interceptor {

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        // 本切面是由 @DataScopeIgnore 自身的 @Around 元注解触发的，这里再确认一次，防止被误注册后"无脑忽略"
        boolean annotated = inv.getMethodAnnotation(DataScopeIgnore.class) != null
                || inv.getTargetAnnotation(DataScopeIgnore.class) != null;
        if (!annotated) {
            return inv.invoke();
        }

        // save / restore：嵌套调用与异常都能正确还原
        boolean previous = DataScopeContext.isIgnore();
        try {
            DataScopeContext.setIgnore(true);
            return inv.invoke();
        } finally {
            DataScopeContext.setIgnore(previous);
        }
    }
}
