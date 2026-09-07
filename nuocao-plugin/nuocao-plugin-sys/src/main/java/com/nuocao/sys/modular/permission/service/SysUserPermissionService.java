package com.nuocao.sys.modular.permission.service;


import com.nuocao.sys.api.dto.UserPermissionEditDTO;
import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;
import com.nuocao.sys.modular.permission.entity.SysUserPermission;
import com.nuocao.sys.modular.permission.result.SysUserPermissionViewResult;

import java.util.List;

/**
 * 功能权限 Service接口
 *
 **/
public interface SysUserPermissionService {


    /**
     * 更新功能权限信息，没有添加，有责更新
     */
    void updatePermission(UserPermissionEditDTO data);


    /**
     * 获取功能权限详情
     **/
    SysUserPermissionViewResult getViewByObjectId(String objectType, Long objectId);

    /**
     * 获取功能权限信息，不存在返回null
     * @param objectType
     * @param objectId
     * @return
     */
    SysUserPermission get(String objectType, Long objectId);

    /**
     * 获取功能权限更新对象，用于各模块嗲用更新方法
     * @param objectType
     * @param objectId
     * @return
     */
    UserPermissionEditDTO getEditDto(String objectType,Long objectId);
    /**
     * 得到对象可用功能权限得菜单ID集合
     * @return
     */
    List<Long> getUserHaveMenuIdList(String objectType, Long objectId);

    /**
     * 得到对象可用功能权限得菜单code集合
     * @param userId
     * @return
     */
    List<String> getUserHaveMenuCodeList(Long userId);
    /**
     * 得到用户所有权限的code，从缓存中读取，这个缓存在修改用户信息、登录时需要清空
     * @param userId
     * @return
     */
    List<String> getUserHaveMenuCodeListByCache(Long userId);

    /**
     * 清理用户权限缓存
     * @param userId
     */
    void clearUserHaveMenuCodeCache(Long userId);

    /**
     * 得到对象可用功能权限得菜单ID集合
     * @param userId
     * @return
     */
    List<Long> getUserHaveMenuIdList(Long userId);


    /**
     * 获取用户有权访问的菜单tree
     * @param userId
     * @return
     */
    List<SysMenuTreeResultDTO> getUserHaveMenuTree(Long userId);


}
