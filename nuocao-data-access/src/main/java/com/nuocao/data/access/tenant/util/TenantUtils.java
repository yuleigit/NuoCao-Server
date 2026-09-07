package com.nuocao.data.access.tenant.util;


import com.nuocao.data.access.tenant.constants.TenantConstants;
import com.nuocao.data.access.tenant.context.TenantContext;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 多租户 Util
 *
 * @author LEI ，参考@yudao
 */
public class TenantUtils {
    /**
     * 使用指定租户执行方法
     *
     * 制定后将不再忽略租户，执行完成后恢复原状态
     *
     * @param tenantId 租户编号
     * @param runnable 要执行的方法
     */
    public static void execute(Long tenantId, Runnable runnable) {
        Long oldTenantId = TenantContext.getCurrentTenant();
        Boolean oldIgnore = TenantContext.isIgnore();
        try {
            TenantContext.setCurrentTenant(tenantId);
            TenantContext.setIgnore(false);
            // 执行逻辑
            runnable.run();
        } finally {
            TenantContext.setCurrentTenant(oldTenantId);
            TenantContext.setIgnore(oldIgnore);
        }
    }

    /**
     * 使用指定租户，执行对应的逻辑
     *
     * 制定后将不再忽略租户，执行完成后恢复原状态
     *
     * @param tenantId 租户编号
     * @param callable 要执行的方法
     */
    public static <V> V execute(Long tenantId, Callable<V> callable) {
        Long oldTenantId = TenantContext.getCurrentTenant();
        Boolean oldIgnore = TenantContext.isIgnore();
        try {
            TenantContext.setCurrentTenant(tenantId);
            TenantContext.setIgnore(false);
            // 执行逻辑
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            TenantContext.setCurrentTenant(oldTenantId);
            TenantContext.setIgnore(oldIgnore);
        }
    }

    /**
     * 忽略租户执行方法，一般用于多租户后台数据定时任务执行。
     * 如数据分拣、聚合等
     *
     * @param runnable 逻辑
     */
    public static void executeIgnore(Runnable runnable) {
        Boolean oldIgnore = TenantContext.isIgnore();
        try {
            TenantContext.setIgnore(true);
            // 执行逻辑
            runnable.run();
        } finally {
            TenantContext.setIgnore(oldIgnore);
        }
    }

    public static <V> V executeIgnore(Callable<V> callable) {
        Boolean oldIgnore = TenantContext.isIgnore();
        try {
            TenantContext.setIgnore(true);
            // 执行逻辑
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            TenantContext.setIgnore(oldIgnore);
        }

    }


    /**
     * 将多租户编号，添加到 header 中
     *
     * @param headers HTTP 请求 headers
     * @param tenantId 租户编号
     */
    public static void addTenantHeader(Map<String, String> headers, Long tenantId) {
        if (tenantId != null) {
            headers.put(TenantConstants.TENANT_ID_HEADER, tenantId.toString());
        }
    }

    /**
     * 获取租户ID，如果不存在则返回默认租户ID
     * @param defaultTenantId
     * @return
     */
    public static Long currentTenantId(Long defaultTenantId){
        return TenantContext.getCurrentTenant()!=null?TenantContext.getCurrentTenant():defaultTenantId;
    }

    public static Long currentTenantId(){
        return TenantContext.getCurrentTenant();
    }
}
