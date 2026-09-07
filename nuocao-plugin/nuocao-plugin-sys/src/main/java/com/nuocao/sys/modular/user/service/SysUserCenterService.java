package com.nuocao.sys.modular.user.service;

import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;

import java.util.List;

/**
 * 用户中心Service接口
 *
 **/
public interface SysUserCenterService {

    /**
     * 获取当前用户可用菜单tree
     * @return
     */
    List<SysMenuTreeResultDTO> getLoginMenuTree();

}
