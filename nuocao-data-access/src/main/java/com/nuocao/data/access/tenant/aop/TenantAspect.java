package com.nuocao.data.access.tenant.aop;

import com.nuocao.data.access.tenant.annotation.TenantIgnore;
import com.nuocao.data.access.tenant.config.TenantConfig;
import com.nuocao.data.access.tenant.context.TenantContext;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

@Component
public class TenantAspect implements Interceptor {
    @Inject
    private TenantConfig tenantConfig;

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        // 如果未开启租户功能，直接执行方法
        if (!tenantConfig.getTenantEnable()) {
            return inv.invoke();
        }

        // 如果已经忽略租户，直接执行方法（保持当前状态）
        if (TenantContext.isIgnore()) {
            return inv.invoke();
        }

        // 检查是否有忽略注解
        TenantIgnore methodAnnotation = inv.getMethodAnnotation(TenantIgnore.class);
        TenantIgnore classAnnotation = inv.getTargetAnnotation(TenantIgnore.class);
        boolean shouldIgnore = (methodAnnotation != null || classAnnotation != null);

        // 如果没有忽略注解，直接执行方法
        if (!shouldIgnore) {
            return inv.invoke();
        }

        // 有忽略注解：保存当前状态，设置忽略，执行后恢复
        boolean previousState = TenantContext.isIgnore();

        try {
            TenantContext.setIgnore(true);
            return inv.invoke();
        } finally {
            // 恢复之前的状态
            TenantContext.setIgnore(previousState);
        }
    }
}