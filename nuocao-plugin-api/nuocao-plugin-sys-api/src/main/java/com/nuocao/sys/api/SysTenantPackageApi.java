package com.nuocao.sys.api;


import com.nuocao.sys.api.dto.SysTenantDTO;

import java.util.List;

/**
 * 租户API
 **/
public interface SysTenantPackageApi {

    /**
     * 获取租户套餐缓存信息
     * @param packageId
     * @return
     */
    String getNameByCache(Long packageId);


    /**
     * 获取套餐菜单列表
     * @param packageId
     * @return 不存在返回空列表
     */
    List<Long> getMenuIds(Long packageId);

}
