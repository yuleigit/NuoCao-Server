package com.nuocao.sys.modular.user.provider;

import com.nuocao.common.constants.Constants;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.sys.api.SysUserApi;
import com.nuocao.sys.api.dto.SysRoleDTO;
import com.nuocao.sys.api.dto.SysUserDTO;
import com.nuocao.sys.modular.permission.service.SysUserPermissionService;
import com.nuocao.sys.modular.role.service.SysRoleService;
import com.nuocao.sys.modular.user.convert.SysUserConvert;
import com.nuocao.sys.modular.user.service.SysUserService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户API接口提供者
 **/
@Component
public class SysUserApiProvider implements SysUserApi {

    @Inject
    private SysUserService sysUserService;
    @Inject
    private SysRoleService roleService;
    @Inject
    private SysUserPermissionService sysUserPermissionService;

    @Override
    public SysUserDTO getUser(Long userId) {
        return SysUserConvert.INSTANCE.convertDto(sysUserService.getById(userId));
    }

    @Override
    public List<SysUserDTO> getUserList(List<Long> userIdList) {
        return sysUserService.getUserDtoList(userIdList);
    }

    @Override
    public SysUserDTO getUser(String username) {
        return SysUserConvert.INSTANCE.convertDto(sysUserService.getByUsername(username));
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return sysUserService.getUserRoleIdListByCache(userId);
    }

    @Override
    public List<Long> getUserIdListByOrgIdList(List<Long> orgIdList) {
        return sysUserService.getUserIdListByOrgIdList(orgIdList);
    }

    @Override
    public PageResult<SysUserDTO> userSelector(Long orgId, String searchKey) {
        return null;
    }

    @Override
    public String getUserNameById(Long userId) {
        SysUserDTO dto =sysUserService.getUserDtoCacheByUserId(userId);
        return dto == null?"":dto.getName();
    }

    @Override
    public String getRoleNames(Long userId) {
        List<SysRoleDTO> roleList = roleService.getRoleListByUserId(userId);
        return StringUtils.join(roleList.stream().map(SysRoleDTO::getName).collect(Collectors.toList()), Constants.SPLIT_STR);
    }

    @Override
    public List<String> getUserAllPermissionCode(Long userId) {
        return sysUserPermissionService.getUserHaveMenuCodeList(userId);
    }


    @Override
    public List<SysUserDTO> getAllUserListByUsernameIgnoreTenant(String username) {
        return sysUserService.selectListByUsernameIgnoreTenant(username);
    }

    @Override
    public void syncPasswordByUsername(String username, String password, boolean passwordHashValue) {
        sysUserService.syncPasswordByUsername(username,password,passwordHashValue);
    }
}
