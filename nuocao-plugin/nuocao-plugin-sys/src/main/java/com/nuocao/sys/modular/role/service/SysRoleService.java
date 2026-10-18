package com.nuocao.sys.modular.role.service;

import com.nuocao.common.page.PageResult;
import com.nuocao.sys.api.dto.DataScopeEditDTO;
import com.nuocao.sys.api.dto.SysRoleDTO;
import com.nuocao.sys.modular.role.entity.SysRole;
import com.nuocao.sys.modular.role.pojo.param.*;
import com.nuocao.sys.modular.role.pojo.result.SysRolePermissionResult;

import java.util.List;

/**
 * 角色Service接口
 **/
public interface SysRoleService  {

    /**
     * 获取角色分页
     */
    PageResult<SysRole> page(SysRolePageParam sysRolePageParam);

    /**
     * 添加角色
     */
    String add(SysRoleAddParam sysRoleAddParam);

    /**
     * 编辑角色
     */
    void edit(SysRoleEditParam sysRoleEditParam);

    /**
     * 删除角色
     */
    void delete(List<SysRoleIdParam> sysRoleIdParamList);

    void deleteByIds(List<Long> ids);

    /**
     * 获取角色详情
     */
    SysRole detail(SysRoleIdParam sysRoleIdParam);

    /**
     * 获取角色详情
     **/
    SysRole queryEntity(Long roleId);

    /**
     * 获取角色拥有权限编辑对象
     */
    SysRolePermissionResult getPermission(SysRoleIdParam sysRoleIdParam);

    /**
     * 给角色授权功能权限
     **/
    void setPermission(SysRoleSetPermissionParam permissionEdit);

    /**
     * 获取角色数据权限
     * @param roleId
     * @return
     */
    DataScopeEditDTO getDataSpace(Long roleId);

    /**
     * 授权角色数据权限
     * @param param
     */
    void setDataScope(SysRoleSetDataScopeParam param);

    /**
     * 获取name集合
     * @param roleIds
     * @return
     */
    List<String> getNames(List<Long> roleIds);

    /**
     * 根据用户id获取所有角色名
     * @param userId
     * @return
     */
    String getNamesByUserId(Long userId);

    /**
     * 根据用户获取所有角色
     * @param userId
     * @return
     */
    List<SysRoleDTO> getRoleListByUserId(Long userId);


    /**
     * 虎丘用户的下级角色列表，以自身角色选最大值确认，有角色管理权限和租户管理员可以管理全部
     * @return
     */
    List<SysRoleDTO> getLoginUserChildrenRoleList();

}
