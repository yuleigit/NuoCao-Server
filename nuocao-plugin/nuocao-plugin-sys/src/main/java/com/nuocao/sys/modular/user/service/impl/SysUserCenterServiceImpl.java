package com.nuocao.sys.modular.user.service.impl;

import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;
import com.nuocao.sys.modular.permission.service.SysUserPermissionService;
import com.nuocao.sys.modular.user.convert.SysUserConvert;
import com.nuocao.sys.modular.user.pojo.param.SysUserUpdateInfoParam;
import com.nuocao.sys.modular.user.pojo.param.SysUserUpdatePwdByOldParam;
import com.nuocao.sys.modular.user.pojo.result.SysUserResult;
import com.nuocao.sys.modular.user.service.SysUserCenterService;
import com.nuocao.sys.modular.user.service.SysUserService;
import com.nuocao.plugin.dev.api.DevMessageApi;
import com.nuocao.plugin.dev.api.DevSseApi;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.UploadedFile;

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

    @Inject
    private DevMessageApi devMessageApi;
    @Inject
    private DevSseApi devSseApi;
    @Override
    public SysUserResult getProfile() {
        return SysUserConvert.INSTANCE.convert(sysUserService.getById(StpLoginUserUtils.getLoginUserId()));
    }

    @Override
    public List<SysMenuTreeResultDTO> getLoginMenuTree() {
        return  sysUserPermissionService.getUserHaveMenuTree(StpLoginUserUtils.getLoginUserId());
    }

    @Override
    public void updatePasswordByOld(SysUserUpdatePwdByOldParam sysUserUpdatePwdByOldParam) {
        sysUserService.updatePasswordByOld(sysUserUpdatePwdByOldParam);
    }

    @Override
    public String updateAvatar(UploadedFile file) {
       return sysUserService.updateAvatar(file);
    }

    @Override
    public String updateAvatarByBase64(String base64) {
        return sysUserService.updateAvatarByBase64(base64);
    }

    @Override
    public void updateUserProfileInfo(SysUserUpdateInfoParam sysUserUpdateInfoParam) {
        sysUserService.updateUserProfileInfo(sysUserUpdateInfoParam);
    }


}
