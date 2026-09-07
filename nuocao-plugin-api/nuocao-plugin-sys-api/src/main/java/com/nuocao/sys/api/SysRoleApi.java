package com.nuocao.sys.api;


import com.nuocao.sys.api.dto.SysOrgTreeResult;
import com.nuocao.sys.api.dto.SysRoleDTO;

import java.util.List;

/**
 * 角色 API
 **/
public interface SysRoleApi {

    /**
     * 根据id获取名称
     **/
    String getNameById(Long roleId);

    /**
     * 获取用户角色列表，join sys_user_role
     * @param userId
     * @return
     */
    List<SysRoleDTO> getRoleListByUserId(Long userId);


}
