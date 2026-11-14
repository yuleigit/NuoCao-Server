package com.nuocao.auth.modular.login.service;


import com.nuocao.auth.api.dto.AccountLoginInfoDTO;
import com.nuocao.auth.api.dto.SaUserLoginInfoDTO;
import com.nuocao.auth.modular.login.param.common.AuthGetEmailValidCodeParam;
import com.nuocao.auth.modular.login.param.common.AuthGetPhoneValidCodeParam;
import com.nuocao.auth.modular.login.param.password.AuthEmailFindPasswordParam;
import com.nuocao.auth.modular.login.param.password.AuthPhoneFindPasswordParam;
import com.nuocao.auth.modular.login.param.reg.AuthRegisterParam;
import com.nuocao.auth.modular.login.result.login.AuthLoginResult;
import com.nuocao.auth.modular.login.param.login.AuthAccountPasswordLoginParam;
import com.nuocao.auth.modular.login.param.login.AuthEmailValidCodeLoginParam;
import com.nuocao.auth.modular.login.param.login.AuthMfaLoginParam;
import com.nuocao.auth.modular.login.param.login.AuthPhoneValidCodeLoginParam;
import com.nuocao.common.page.CommonPicValidCodeResult;

/**
 * 系统账户Service接口
 */
public interface AuthAccountUserService {

    /**
     * 获取图片验证码
     **/
    CommonPicValidCodeResult getPicCaptcha();

    /**
     * 获取手机登录验证码
     **/
    String getPhoneLoginValidCode(AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam);

    /**
     * 获取邮箱登录验证码
     **/
    String getEmailLoginValidCode(AuthGetEmailValidCodeParam authGetEmailValidCodeParam);

    /**
     * 账号密码登录
     **/
    AuthLoginResult doLogin(AuthAccountPasswordLoginParam authAccountPasswordLoginParam);

    /**
     * 手机验证码登录
     **/
    AuthLoginResult doLoginByPhone(AuthPhoneValidCodeLoginParam authPhoneValidCodeLoginParam);

    /**
     * 2FA 动态码登录验证，成功后签发 token
     **/
    AuthLoginResult doMfaLogin(AuthMfaLoginParam authMfaLoginParam);

    /**
     * 邮箱验证码登录
     **/
    AuthLoginResult doLoginByEmail(AuthEmailValidCodeLoginParam authEmailValidCodeLoginParam);


    /**
     * 直接登录账户，如输入密码则验证密码
     * ***** 注意该方法可配合三方登录等实现无密码登录，但调用时一定要做足前置认证，避免越权执行
     * @param userAccount
     * @param device
     * @param checkPasswordHash 密码的hash值，为null时则跳过密码验证，直接登录账号，请配合TenantUtils执行使用
     * @param lockTenantId 锁定登录的租户ID
     * @return
     */
    AuthLoginResult doLoginUser(String userAccount,  String device,String deviceId,String checkPasswordHash,Long lockTenantId);

    default AuthLoginResult doLoginUser(String userAccount,  String device,String deviceId,String checkPasswordHash){
        return doLoginUser(userAccount,device,deviceId,checkPasswordHash,null);
    }


    /**
     * 登录到置顶租户的用户上
     * @param tenantId
     * @param userAccount
     * @param device
     * @param deviceId 登录设备ID
     * @param checkPasswordHash 为null时则跳过密码验证，直接登录账号，请配合TenantUtils执行使用
     * @return
     */
    AuthLoginResult doLoginUser(Long tenantId,String userAccount,  String device,String deviceId,String checkPasswordHash);
    /**
     * 获取用户登录信息
     **/
    SaUserLoginInfoDTO getLoginUser();

    /**
     * 获取登录的账号信息
     **/
    AccountLoginInfoDTO getLoginAccount();

    /**
     * 获取手机注册验证码
     **/
    String getPhoneRegValidCode(AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam);

    /**
     * 获取手机注册验证码
     **/
    String getPhoneRegEmailCode(AuthEmailValidCodeLoginParam authEmailValidCodeLoginParam);

    /**
     * 注册账号
     */
    void register(AuthRegisterParam authRegisterParam);


    /**
     * 使用账号查询
     * @param account
     * @return
     */
    String getFindPasswordUserInfo(String account);

    /**
     * 获取找回密码手机验证码
     * @param authGetPhoneValidCodeParam
     * @return
     */
    String getPhoneFindPasswordValidCode(AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam);

    /**
     * 获取找回密码邮箱验证码
     * @param authGetEmailValidCodeParam
     * @return
     */
    String getEmailFindPasswordValid(AuthGetEmailValidCodeParam authGetEmailValidCodeParam);


    /**
     * 使用手机号找回密码
     * @param authPhoneFindPasswordParam
     */
    void doPhoneFindPassword(AuthPhoneFindPasswordParam authPhoneFindPasswordParam);

    /**
     * 使用邮箱号找回密码
     * @param authEmailFindPasswordParam
     */
    void doEmailFindPassword(AuthEmailFindPasswordParam authEmailFindPasswordParam);



}
