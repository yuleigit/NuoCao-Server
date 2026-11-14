package com.nuocao.sys.modular.account.service;

import com.nuocao.common.page.CommonPicValidCodeResult;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaBindInitDTO;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaStatusDTO;
import com.nuocao.sys.modular.account.param.*;
import com.nuocao.sys.modular.account.result.SysAccountDetailResult;

/**
 * 系统账户 service

 **/
public interface SysAccountCenterService {
    /**
     * 获取图片验证码
     **/
    CommonPicValidCodeResult getPicCaptcha();
    /**
     * 修改密码获取手机验证码
     * @return 请求号码，之后验证使用需携带
     **/
    String updatePasswordGetPhoneValidCode(SysAccountUpdatePwdGetPhoneValidCodeParam getPhoneValidCodeParam);

    /**
     * 修改密码获取邮箱验证码
     * @return 请求号码，之后验证使用需携带
     **/
    String updatePasswordGetEmailValidCode(SysAccountGetEmailValidCodeParam getEmailValidCodeParam);

    /**
     * 通过验证旧密码修改用户密码
     **/
    void updatePasswordByOld(SysAccountUpdatePwdByOldParam updatePwdByOldParam);

    /**
     * 通过验证手机号修改用户密码
     **/
    void updatePasswordByPhone(SysAccountUpdatePwdByPhoneParam accountUpdatePwdByPhoneParam);

    /**
     * 通过验证邮箱修改用户密码
     *
     **/
    void updatePasswordByEmail(SysAccountUpdatePwdByEmailParam accountUpdatePwdByEmailParam);

    /**
     * 绑定手机号获取手机验证码
     * @return 请求号码，之后验证使用需携带
     **/
    String bindPhoneGetPhoneValidCode(SysAccountBindPhoneGetValidCodeParam getPhoneValidCodeParam);

    /**
     * 绑定手机号
     **/
    void bindPhone(SysAccountBindPhoneParam bindPhoneParam);

    /**
     * 绑定邮箱获取邮箱验证码
     **/
    String bindEmailGetEmailValidCode(SysAccountGetEmailValidCodeParam getEmailValidCodeParam);
    /**
     * 绑定邮箱
     **/
    void bindEmail(SysAccountBindEmailParam bindEmailParam);


    /**
     * 获取详细信息
     * @return
     */
    SysAccountDetailResult detail();

    /**
     * 当前登录用户 2FA 状态
     **/
    DevMfaStatusDTO mfaStatus();

    /**
     * 当前登录用户发起 2FA 绑定
     **/
    DevMfaBindInitDTO mfaBindInit();

    /**
     * 当前登录用户确认 2FA 绑定
     **/
    void mfaBindConfirm(SysAccountMfaBindConfirmParam param);

    /**
     * 登录流程中未绑定用户发起绑定（凭 mfaReqId 免登录）
     **/
    DevMfaBindInitDTO mfaBindInitByLogin(SysAccountMfaBindLoginParam param);

    /**
     * 登录流程中未绑定用户确认绑定（凭 mfaReqId 免登录）
     **/
    void mfaBindConfirmByLogin(SysAccountMfaBindLoginConfirmParam param);
}
