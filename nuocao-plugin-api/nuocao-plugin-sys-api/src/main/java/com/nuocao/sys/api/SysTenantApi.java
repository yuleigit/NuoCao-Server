package com.nuocao.sys.api;


import com.nuocao.sys.api.dto.SysTenantDTO;

import java.util.List;

/**
 * 租户API
 **/
public interface SysTenantApi {

    /**
     * 从缓存获取租户信息
     * @param tenantId
     * @return
     */
    String getNameByCache(Long tenantId);


    /**
     * 获取租户信息
     * @param tenantId
     * @return 不存在返回null
     */
    SysTenantDTO getTenant(Long tenantId);


    /**
     * 获取租户信息
     * @param tenantId
     * @return 不存在报错
     */
    SysTenantDTO getAndValidTenant(Long tenantId);

    /**
     * 获取租户信息
     * @param tenantCode
     * @return 不存在返回null
     */
    SysTenantDTO getDTObyCode(String tenantCode);

    /**
    判断是否为租户管理员
     */
    Boolean isTenantAdmin(Long tenantId,Long userId);


    /**
     * 获取租户的所有权限编码
     * @param tenantId
     * @return
     */
    List<String> getTenantPremissionCodeList(Long tenantId);

    /**
     * 获取租户的所有权限菜单ID
     * @param tenantId
     * @return
     */
    List<Long> getTenantPremissionMenuIdList(Long tenantId);

    /**
     * 根据账号得到对应租户信息
     * @param account
     * @return
     */
    List<SysTenantDTO> getAccountTenantList(String account);

    /**
     * 根据套餐统计租户数
     * @param packageId
     * @return
     */
    int countByPackageId(Long packageId);

}
