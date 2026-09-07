package com.nuocao.data.access.datascope.util;


import com.nuocao.data.access.datascope.context.DataScopeContext;
import com.nuocao.data.access.tenant.constants.TenantConstants;
import com.nuocao.data.access.tenant.context.TenantContext;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 数据权限 Util
 *
 * @author LEI ，参考@yudao
 */
public class DataScopeUtils {

    /**
     * 忽略数据权限执行方法，一般用于多数据权限后台数据定时任务执行。
     * 如数据分拣、聚合等
     *
     * @param runnable 逻辑
     */
    public static void executeIgnore(Runnable runnable) {
        Boolean oldIgnore = DataScopeContext.isIgnore();
        try {
            DataScopeContext.setIgnore(true);
            // 执行逻辑
            runnable.run();
        } finally {
            DataScopeContext.setIgnore(oldIgnore);
        }
    }

    public static <V> V executeIgnore(Callable<V> callable) {
        Boolean oldIgnore = DataScopeContext.isIgnore();
        try {
            DataScopeContext.setIgnore(true);
            // 执行逻辑
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DataScopeContext.setIgnore(oldIgnore);
        }

    }


}
