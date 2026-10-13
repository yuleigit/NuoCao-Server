package com.nuocao.data.access.tenant.aop;

import com.mybatisflex.core.tenant.TenantFactory;
import com.nuocao.data.access.tenant.context.TenantContext;
import lombok.extern.slf4j.Slf4j;

/**
 * mybatis-flex 租户方法实现
 */
@Slf4j
public class NuoCaoTenantFactory implements TenantFactory {

    /**
     * 兜底租户ID：一个数据表中不会存在的占位值。
     * <p>用于"没有租户上下文、也没有显式忽略租户"的场景 —— 宁可不返回数据，也不要跨租户查全量。
     */
    private static final Long NO_TENANT_ID = -1L;

    @Override
    public Object[] getTenantIds() {
        // 显式忽略租户（@TenantIgnore / 路径白名单 / TenantUtils.executeIgnore）→ 不加 tenant_id 条件
        if (TenantContext.isIgnore()) {
            return new Object[0];
        }

        Long tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            // 「没有租户上下文」≠「忽略租户」。定时任务 / 异步线程 / MQ 消费等非请求链路，
            // 请显式使用 TenantUtils.executeIgnore(...)，否则会静默跨租户查全量数据。
            log.error("[NuoCaoTenantFactory] 无租户上下文且未显式忽略租户，已按 tenant_id={} 过滤，请检查调用链", NO_TENANT_ID);
            return new Object[]{NO_TENANT_ID};
        }

        return new Object[]{tenantId};
    }
}
