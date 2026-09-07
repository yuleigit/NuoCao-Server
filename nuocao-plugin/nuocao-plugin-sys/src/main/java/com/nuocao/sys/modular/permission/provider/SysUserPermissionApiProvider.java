package com.nuocao.sys.modular.permission.provider;

import com.nuocao.sys.api.SysUserPermissionApi;
import com.nuocao.sys.api.dto.UserPermissionEditDTO;
import com.nuocao.sys.api.enums.DataScopeObjectTypeEnum;
import com.nuocao.sys.modular.permission.service.SysUserPermissionService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.List;


/**
 * 功能权限 接口实现
 */
@Component
public class SysUserPermissionApiProvider implements SysUserPermissionApi {


    @Inject
    private SysUserPermissionService sysUserPermissionService;


    @Override
    public List<String> getPermissionCodeList(Long userId) {
        return sysUserPermissionService.getUserHaveMenuCodeList(userId);
    }

    @Override
    public void updatePermission(UserPermissionEditDTO data) {
         sysUserPermissionService.updatePermission(data);
    }

    @Override
    public UserPermissionEditDTO getPermission(DataScopeObjectTypeEnum objectType, Long objectId) {
        return sysUserPermissionService.getEditDto(objectType.getType(),objectId);
    }

    @Override
    public List<Long> getPermissionMenuIdList(DataScopeObjectTypeEnum objectType, Long objectId) {
        return sysUserPermissionService.getUserHaveMenuIdList(objectType.getType(),objectId);
    }
}
