package com.nuocao.data.access.tenant.context;

/**
 * 租户上下文
 */
public class TenantContext {
    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> IGNORE_TENANT = ThreadLocal.withInitial(()->false);


    /**
     * 设置租户ID
     * @param tenantId
     */
    public static void setCurrentTenant(Long tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    /**
     * 获取租户ID
     * @return
     */
    public static Long getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
        IGNORE_TENANT.remove();
    }

    /**
     * 设置是否忽略租户验证
     */
    public static void setIgnore(boolean ignore) {
        IGNORE_TENANT.set(ignore);
    }

    /**
     * 是否忽略租户
     * @return
     */
    public static boolean isIgnore(){
       return IGNORE_TENANT.get();
    }
}
