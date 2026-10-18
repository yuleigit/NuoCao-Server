package com.nuocao.sys.modular.account.service.impl;


import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.common.captcha.CaptchaGroupEnum;
import com.nuocao.common.captcha.ImageCaptchaUtils;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.exception.NotSupportedException;
import com.nuocao.common.page.CommonPicValidCodeResult;
import com.nuocao.common.pojo.KeyValue;
import com.nuocao.common.utils.CryptUtils;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.plugin.dev.api.DevSmsApi;
import com.nuocao.plugin.dev.api.enums.DevConfigSceneEnum;
import com.nuocao.sys.modular.account.entity.SysAccount;
import com.nuocao.sys.modular.account.param.*;
import com.nuocao.sys.modular.account.result.SysAccountDetailResult;
import com.nuocao.sys.modular.account.service.SysAccountCenterService;
import com.nuocao.sys.modular.account.service.SysAccountService;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

@Component
@Slf4j
public class SysAccountCenterServiceImpl implements SysAccountCenterService {

    //验证码分组
    private final static String VALID_CODE_GROUP= CaptchaGroupEnum.ACCOUNT.getValue();


    @Inject
    private SysAccountService sysAccountService;
    @Inject
    private ImageCaptchaUtils imageCaptchaUtils;
    @Inject
    private DevSmsApi devSmsApi;


    @Override
    public CommonPicValidCodeResult getPicCaptcha() {
        //创建验证码
        KeyValue<String,String> captcha=imageCaptchaUtils.get(VALID_CODE_GROUP);
        return CommonPicValidCodeResult.craete(captcha.getKey(),captcha.getValue());
    }

    @Override
    public String updatePasswordGetPhoneValidCode(SysAccountUpdatePwdGetPhoneValidCodeParam param) {
        //验证图形验证码
        imageCaptchaUtils.validate(VALID_CODE_GROUP,param.getValidCodeReqId(),param.getValidCode());
        //得到并验证账户信息
        SysAccount account = validatedAccountByUserLogin();
        //发送手机验证码
        return devSmsApi.sendSmsCodeByScene(DevConfigSceneEnum.ACCOUNT_LOGIN,VALID_CODE_GROUP,account.getPhone());
    }

    @Override
    public String updatePasswordGetEmailValidCode(SysAccountGetEmailValidCodeParam getEmailValidCodeParam) {
        throw new NotSupportedException();
    }

    @Override
    public void updatePasswordByOld(SysAccountUpdatePwdByOldParam updatePwdByOldParam) {
        String oldPassword= CryptUtils.sm2Decrypt(updatePwdByOldParam.getPassword());
        String newPassword=  CryptUtils.sm2Decrypt(updatePwdByOldParam.getNewPassword());
        //得到并验证账户信息
        SysAccount account = validatedAccountByUserLogin();

        if(!account.getPassword().equals(CryptUtils.hashValue(oldPassword))){
            throw new CommonExceptionI18n("sys.user.password.oldError");
        }

        String newPasswordHash = CryptUtils.hashValue(newPassword);

        sysAccountService.updatePassword(account.getId(),newPassword);

    }

    @Override
    public void updatePasswordByPhone(SysAccountUpdatePwdByPhoneParam accountUpdatePwdByPhoneParam) {
        SysAccount account = validatedAccountByUserLogin();
        String newPassword=  CryptUtils.sm2Decrypt(accountUpdatePwdByPhoneParam.getNewPassword());
        //校验手机验证码
        devSmsApi.validateSmsCode(VALID_CODE_GROUP,account.getPhone(),accountUpdatePwdByPhoneParam.getValidCodeReqId(),accountUpdatePwdByPhoneParam.getValidCode());
        sysAccountService.updatePassword(account.getId(),newPassword);
    }

    @Override
    public void updatePasswordByEmail(SysAccountUpdatePwdByEmailParam clientUserUpdatePwdByEmailParam) {
        throw new NotSupportedException();

    }

    @Override
    public String bindPhoneGetPhoneValidCode(SysAccountBindPhoneGetValidCodeParam getPhoneValidCodeParam) {
        //验证图形验证码
        imageCaptchaUtils.validate(VALID_CODE_GROUP,getPhoneValidCodeParam.getValidCodeReqId(),getPhoneValidCodeParam.getValidCode());
        //得到并验证账户信息
        SysAccount account = validatedAccountByUserLogin();
        if(account.getPhone().equals(getPhoneValidCodeParam.getPhone())){
            throw new CommonExceptionI18n("sys.user.phone.notChange");
        }
        //发送手机验证码
        return devSmsApi.sendSmsCodeByScene(DevConfigSceneEnum.ACCOUNT_LOGIN,VALID_CODE_GROUP,account.getPhone());
    }

    @Override
    public void bindPhone(SysAccountBindPhoneParam bindPhoneParam) {
        SysAccount account = validatedAccountByUserLogin();
        //校验手机验证码
        devSmsApi.validateSmsCode(VALID_CODE_GROUP,bindPhoneParam.getPhone(),bindPhoneParam.getValidCodeReqId(),bindPhoneParam.getValidCode());
        sysAccountService.updatePhoneById(account.getId(),bindPhoneParam.getPhone());
    }

    @Override
    public String bindEmailGetEmailValidCode(SysAccountGetEmailValidCodeParam getEmailValidCodeParam) {
        throw new NotSupportedException();
    }

    @Override
    public void bindEmail(SysAccountBindEmailParam bindEmailParam) {
        throw new NotSupportedException();
    }

    @Override
    public SysAccountDetailResult detail() {

        return sysAccountService.detailByUsername(getCurrentUsername());
    }

    /**
     * 根据当前登录的用户ID获取账户信息（用户对应）
     * TODO: 我为了省事儿，没保存account登录状态，额可以自行调整
     * @return
     */
    private SysAccount validatedAccountByUserLogin(){
        String username =getCurrentUsername();
        return  sysAccountService.queryEntityByUsername(username);
    }

    private String getCurrentUsername(){
        String username = StpLoginUserUtils.getLoginUsername();
        if(StringUtils.isBlank(username)){
            throw new  CommonExceptionI18n("sys.user.notExists");
        }
        return username;
    }
}
