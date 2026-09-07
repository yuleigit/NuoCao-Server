package com.nuocao.sys.modular.user.service.impl;

import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;
import com.nuocao.sys.modular.permission.service.SysUserPermissionService;
import com.nuocao.sys.modular.user.service.SysUserCenterService;
import com.nuocao.sys.modular.user.service.SysUserService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.List;

/**
 * 用户中心Servic 实现
 */
@Component
public class SysUserCenterServiceImpl implements SysUserCenterService {

    @Inject
    private SysUserService sysUserService;

    @Inject
    private SysUserPermissionService sysUserPermissionService;

    @Override
    public List<SysMenuTreeResultDTO> getLoginMenuTree() {
        return  sysUserPermissionService.getUserHaveMenuTree(StpLoginUserUtils.getLoginUserId());
    }
}
