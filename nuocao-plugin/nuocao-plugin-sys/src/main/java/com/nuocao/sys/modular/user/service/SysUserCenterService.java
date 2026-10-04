package com.nuocao.sys.modular.user.service;

import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;
import com.nuocao.sys.modular.user.pojo.param.SysUserUpdateInfoParam;
import com.nuocao.sys.modular.user.pojo.param.SysUserUpdatePwdByOldParam;
import com.nuocao.sys.modular.user.pojo.result.SysUserResult;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.web.sse.SseEmitter;

import java.util.List;

/**
 * 用户中心Service接口
 *
 **/
public interface SysUserCenterService {

    SysUserResult getProfile();

    /**
     * 获取当前用户可用菜单tree
     * @return
     */
    List<SysMenuTreeResultDTO> getLoginMenuTree();

    /**
     * 根据旧密码修改新密码
     * @param sysUserUpdatePwdByOldParam
     */
    void updatePasswordByOld(SysUserUpdatePwdByOldParam sysUserUpdatePwdByOldParam);
    /**
     * 修改用户头像返回base64
     **/
    String updateAvatar(UploadedFile file);

    /**
     * 修改用户头像返回base64
     **/
    String updateAvatarByBase64(String base64);

    /**
     * 编辑个人信息，由会员你自己发起,仅修改在当前租户的信息
     */
    void updateUserProfileInfo(SysUserUpdateInfoParam sysUserUpdateInfoParam);



    SseEmitter createSseConnect(String clientId);

}
