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
}
