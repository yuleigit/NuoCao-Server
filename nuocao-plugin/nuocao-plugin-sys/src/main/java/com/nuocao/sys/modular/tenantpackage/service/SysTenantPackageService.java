package com.nuocao.sys.modular.tenantpackage.service;

import com.nuocao.common.page.PageResult;
import com.nuocao.sys.api.dto.menu.SysMenuDTO;
import com.nuocao.sys.modular.tenantpackage.entity.SysTenantPackage;
import com.nuocao.sys.modular.tenantpackage.param.*;


import java.util.List;

/**
 * 租户套餐Service接口
 *
 * @author Lei
 * @date  2025/06/19 16:30
 **/
public interface SysTenantPackageService  {

    /**
     * 获取租户套餐分页
     */
    PageResult<SysTenantPackage> page(SysTenantPackagePageParam SysTenantPackagePageParam);

    /**
     * 添加租户套餐
     */
    void add(SysTenantPackageAddParam SysTenantPackageAddParam);

    /**
     * 编辑租户套餐
     */
    void edit(SysTenantPackageEditParam SysTenantPackageEditParam);

    /**
     * 删除租户套餐
     */
    void delete(List<SysTenantPackageIdParam> SysTenantPackageIdParamList);

    void deleteByIds(List<Long> ids);

    /**
     * 获取租户套餐详情
     */
    SysTenantPackage detail(SysTenantPackageIdParam SysTenantPackageIdParam);

    /**
     * 获取租户套餐详情
     **/
    SysTenantPackage queryEntity(Long id);



    /**
     * 获取套餐详情，如果没有则返回null
     * @param packageId
     * @return 没有则返回null
     */
    SysTenantPackage getById(Long packageId);

    List<SysTenantPackage> getSelector();

    /**
     * 从缓存中读取套餐名字
     * @param packageId
     * @return
     */
    String getNameByCache(Long packageId);

    /**
     * 清理缓存
     * @param packageId
     */
    void clearCache(Long packageId);

    /**
     * 获取套餐菜单id集合
     * @param packageId
     * @return 无权限则返回empty
     */
    List<Long> getPackageMenuIdsByCache(Long packageId);
    /**
     * 获取套餐菜单id集合
     * @param packageId
     * @return 无权限则返回empty
     */
    List<Long> getPackageMenuIds(Long packageId);

    /**
     * 获取套餐可用权限列表
     * @param packageId
     * @return 无权限返回empty
     */
    List<SysMenuDTO> getPackageMenuList(Long packageId);
}
