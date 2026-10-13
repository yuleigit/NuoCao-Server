package com.nuocao.data.access.datascope.context;

/**
 * 数据权限上下文
 */
public class DataScopeContext {
    private static final ThreadLocal<Boolean> IGNORE_DATA_SCOPE = ThreadLocal.withInitial(()->false);


    /**
     * 设置是否忽略租户验证
     */
    public static void setIgnore(boolean ignore) {
        IGNORE_DATA_SCOPE.set(ignore);
    }

    /**
     * 是否忽略租户
     * @return
     */
    public static boolean isIgnore(){
        return IGNORE_DATA_SCOPE.get();
    }

    /**
     * 清理线程上下文。
     * <p>与 {@code TenantContext.clear()} 对齐：请求级入口归零、出口清理，避免线程池复用时的状态残留。
     */
    public static void clear() {
        IGNORE_DATA_SCOPE.remove();
    }
}
