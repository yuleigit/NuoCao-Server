package com.nuocao.sys.modular.user.provider;

import com.nuocao.auth.api.AuthUserLoginApi;
import com.nuocao.auth.api.dto.AuthLoginUserDTO;
import com.nuocao.auth.api.dto.SaUserLoginInfoDTO;
import com.nuocao.sys.modular.permission.service.SysUserPermissionService;
import com.nuocao.sys.modular.user.convert.SysUserConvert;
import com.nuocao.sys.modular.user.service.SysUserService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.List;

/**
 * 用户登录API 实现
 */
@Component
public class SysUserAuthLoginProvider implements AuthUserLoginApi {

    @Inject
    private SysUserService sysUserService;
    @Inject
    private SysUserPermissionService userPermissionService;
    @Override
    public SaUserLoginInfoDTO getUserById(Long userId) {
        return sysUserService.getLoginUserById(userId);
    }

    @Override
    public SaUserLoginInfoDTO getUserByUsername(String username) {
        return sysUserService.getLoginUserByUsername(username);
    }

    @Override
    public SaUserLoginInfoDTO getUserByPhone(String phone) {
        return sysUserService.getLoginUserByPhone(phone);
    }

    @Override
    public SaUserLoginInfoDTO getUserByEmail(String email) {
        return sysUserService.getLoginUserByEmail(email);
    }

    @Override
    public List<AuthLoginUserDTO> listUserByUserIdList(List<Long> userIdList) {
        return SysUserConvert.INSTANCE.convertAuthLoginUser(sysUserService.getUserDtoList(userIdList));
    }

    @Override
    public List<Long> getRoleIdListByUserId(Long userId) {
        return sysUserService.getUserRoleIdList(userId);
    }

    public List<String> getRoleCodeListByUserId(Long userId){
        return sysUserService.getUserRoleCodeList(userId);
    }

    @Override
    public List<String> getPermissionListByUserId(Long userId) {
        return userPermissionService.getUserHaveMenuCodeList(userId);
    }


    @Override
    public List<Long> getMenuIds(Long userId) {
        return userPermissionService.getUserHaveMenuIdList(userId);
    }

    @Override
    public void updateUserLoginInfo(Long userId, String device) {
        sysUserService.updateUserLoginInfo(userId,device);
    }

    @Override
    public List<AuthLoginUserDTO> getAllTenantUserByAccount(String account) {
        return SysUserConvert.INSTANCE.convertAuthLoginUser(sysUserService.selectListByUsernameIgnoreTenant(account));
    }
}
