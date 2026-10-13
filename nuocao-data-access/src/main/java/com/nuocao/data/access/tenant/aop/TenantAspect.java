package com.nuocao.data.access.tenant.aop;

import com.nuocao.data.access.tenant.annotation.TenantIgnore;
import com.nuocao.data.access.tenant.config.TenantConfig;
import com.nuocao.data.access.tenant.context.TenantContext;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

/**
 * {@link TenantIgnore} 的切面实现（注解上的 {@code @Around} 指向本类）。
 *
 * <p>语义：在<b>被注解的方法调用栈</b>内把 {@code TenantContext.ignore} 置为 true，
 * 从而让 {@code NuoCaoTenantFactory} 不再追加 {@code tenant_id} 条件；调用结束（含异常）后还原。
 *
 * <p>注意两个固有限制：
 * <ul>
 *     <li>同类内部 {@code this.xxx()} 调用不会走代理，切面不触发；这些场景请用
 *     {@code TenantUtils.executeIgnore(...)}。</li>
 *     <li>方法层的忽略只影响 SQL，不影响请求级校验（"必须携带 Tenant-Id"等由
 *     {@code TenantInterceptor} 负责）。</li>
 * </ul>
 */
@Component
public class TenantAspect implements Interceptor {

    @Inject
    private TenantConfig tenantConfig;

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        // 未启用多租户，或已被更外层忽略（请求级 @TenantIgnore / 路径白名单 / TenantUtils.executeIgnore）
        if (!tenantConfig.isEnabled() || TenantContext.isIgnore()) {
            return inv.invoke();
        }

        // 本切面是由 @TenantIgnore 自身的 @Around 元注解触发的，这里再确认一次，防止被误注册后"无脑忽略"
        boolean annotated = inv.getMethodAnnotation(TenantIgnore.class) != null
                || inv.getTargetAnnotation(TenantIgnore.class) != null;
        if (!annotated) {
            return inv.invoke();
        }

        // save / restore：嵌套调用与异常都能正确还原
        boolean previous = TenantContext.isIgnore();
        try {
            TenantContext.setIgnore(true);
            return inv.invoke();
        } finally {
            TenantContext.setIgnore(previous);
        }
    }
}
