package com.nuocao.auth.modular.login.service.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.nuocao.auth.api.AuthApi;
import com.nuocao.auth.api.AuthAccountLoginApi;
import com.nuocao.auth.api.AuthUserLoginApi;
import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.auth.api.dto.AccountLoginInfoDTO;
import com.nuocao.auth.api.dto.AuthLoginUserDTO;
import com.nuocao.auth.api.dto.SaUserLoginInfoDTO;
import com.nuocao.auth.modular.login.enums.AuthDeviceTypeEnum;
import com.nuocao.auth.modular.login.enums.AuthExceptionEnum;
import com.nuocao.auth.modular.login.param.common.AuthGetEmailValidCodeParam;
import com.nuocao.auth.modular.login.param.common.AuthGetPhoneValidCodeParam;
import com.nuocao.auth.modular.login.param.login.AuthAccountPasswordLoginParam;
import com.nuocao.auth.modular.login.param.login.AuthEmailValidCodeLoginParam;
import com.nuocao.auth.modular.login.param.login.AuthMfaLoginParam;
import com.nuocao.auth.modular.login.param.login.AuthPhoneValidCodeLoginParam;
import com.nuocao.auth.modular.login.param.password.AuthEmailFindPasswordParam;
import com.nuocao.auth.modular.login.param.password.AuthPhoneFindPasswordParam;
import com.nuocao.auth.modular.login.param.reg.AuthRegisterParam;
import com.nuocao.auth.modular.login.result.AuthPicValidCodeResult;
import com.nuocao.auth.modular.login.result.login.AuthLoginResult;
import com.nuocao.auth.modular.login.service.AuthAccountUserService;
import com.nuocao.common.ObjectUtils;
import com.nuocao.common.cache.CacheUtils;
import com.nuocao.common.captcha.CaptchaGroupEnum;
import com.nuocao.common.captcha.ImageCaptchaUtils;
import com.nuocao.common.enums.CommonEnableStatusEnum;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.page.CommonPicValidCodeResult;
import com.nuocao.common.pojo.KeyValue;
import com.nuocao.common.utils.CryptUtils;
import com.nuocao.common.utils.NumberUtils;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.common.utils.ValidationUtils;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.common.utils.http.RequestUtils;
import com.nuocao.data.access.tenant.util.TenantUtils;
import com.nuocao.plugin.dev.api.enums.DevConfigSceneEnum;
import com.nuocao.sys.api.SysAccountApi;
import com.nuocao.sys.api.SysTenantApi;
import com.nuocao.plugin.dev.api.DevConfigApi;
import com.nuocao.plugin.dev.api.DevEmailApi;
import com.nuocao.plugin.dev.api.DevMfaApi;
import com.nuocao.plugin.dev.api.DevSmsApi;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaLoginCheckDTO;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaLoginCheckParamDTO;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaVerifyResultDTO;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录Service接口实现类
 */
@Component
public class AuthAccountUserServiceImpl implements AuthAccountUserService {


    //验证码分组
    private final static String VALID_CODE_GROUP=CaptchaGroupEnum.AUTH.getValue();

    private final static String AUTH_LOGIN_CAPTCHA_OPEN_KEY="auth_login_captcha_open";

    //用户密码错误次数计数器缓存
    private final static String AUTH_LOGIN_PASSWORD_ERROR_COUNT_CACHE_KEY_PREFIX="auth-login-pwd-error:count:";

    //密码错误限制次数配置key，取值为分钟
    private final static String AUTH_LOGIN_PASSWORD_ERROR_COUNT_LIMIT_KEY="auth_login_pwd_error_count_limit";
    //密码错误累计时间间隔配置key，取值为分钟
    private final static String AUTH_LOGIN_PASSWORD_ERROR_COUNT_DURATION_KEY="auth_login_pwd_error_count_duration";
    //密码错误后等待时间
    private final static String AUTH_LOGIN_PASSWORD_ERROR_DISABLE_TIME_KEY="auth_login_pwd_error_disable_time";

    @Inject
    private AuthAccountLoginApi accountLoginApi; //因负责账号验证，不登录

    @Inject
    private AuthUserLoginApi userLoginApi; //负责实际权限登录，如需要去掉账户验证层，当做单租户使用，可以简化doLoginUser和其前置验证类


    @Inject
    private DevConfigApi devConfigApi;

    @Inject
    private DevSmsApi devSmsApi;

    @Inject
    private DevEmailApi devEmailApi;

    @Inject
    private DevMfaApi devMfaApi;

    @Inject
    private CacheUtils cacheUtils;

    @Inject
    private SysTenantApi sysTenantApi;

    @Inject
    private ImageCaptchaUtils imageCaptchaUtils;

    @Inject
    private AuthApi authApi;

    @Inject
    private SysAccountApi sysAccountApi;

    //启用账号注册
    private static final String ACCOUNT_REGISTER_ENABLE = "account_register_enable";
    //账号注册需要手机验证码
    private static final String ACCOUNT_REGISTER_SMS_CODE_ENABLE = "account_register_sms_code_enable";

    @Override
    public CommonPicValidCodeResult getPicCaptcha() {
        //创建验证码
        KeyValue<String,String> captcha=imageCaptchaUtils.get(VALID_CODE_GROUP);
        return AuthPicValidCodeResult.craete(captcha.getKey(),captcha.getValue());
    }

    @Override
    public String getPhoneLoginValidCode(AuthGetPhoneValidCodeParam param) {
        //验证图形验证码
        imageCaptchaUtils.validate(VALID_CODE_GROUP,param.getValidCodeReqId(),param.getValidCode());
        //发送手机验证码
        return devSmsApi.sendSmsCodeByScene(DevConfigSceneEnum.ACCOUNT_LOGIN,VALID_CODE_GROUP,param.getPhone());
    }

    @Override
    public String getEmailLoginValidCode(AuthGetEmailValidCodeParam authGetEmailValidCodeParam) {
        throw new CommonException("EMmail不可使用");
    }

    @Override
    public AuthLoginResult doLogin(AuthAccountPasswordLoginParam authAccountPasswordLoginParam) {
        String uesrname = authAccountPasswordLoginParam.getUsername();
        //验证下是不是手机号，如果手机号则从手机号中找到对应账号
        if(ValidationUtils.isMobile(uesrname)) {
            AccountLoginInfoDTO accountLoginInfo = accountLoginApi.getAccountByPhone(uesrname);
            if(accountLoginInfo!=null){
                uesrname= accountLoginInfo.getUsername();
            }
        }else if(ValidationUtils.isEmail(uesrname)){
            AccountLoginInfoDTO accountLoginInfo = accountLoginApi.getAccountByEmail(uesrname);
            if(accountLoginInfo!=null){
                uesrname= accountLoginInfo.getUsername();
            }
        }

        //验证密码错误次数是不是超限
        validAccountDisableTime(uesrname);

        // 获取密码
        String password = authAccountPasswordLoginParam.getPassword();
        // 获取设备
        String device = authAccountPasswordLoginParam.getDevice();
        String deviceId= authAccountPasswordLoginParam.getDeviceId();
        // 默认指定为PC，如在小程序跟移动端的情况下，自行指定即可
        if(ObjectUtil.isEmpty(device)) {
            device = AuthDeviceTypeEnum.PC.getValue();
        } else {
            AuthDeviceTypeEnum.validate(device);
        }
        // 图片校验验证码
        Boolean openCaptcha =  devConfigApi.getBooleanValue(AUTH_LOGIN_CAPTCHA_OPEN_KEY,false);

        //是否启用图片验证码
        if(openCaptcha){
            String validCode = authAccountPasswordLoginParam.getValidCode();
            String validCodeReqId = authAccountPasswordLoginParam.getValidCodeReqId();
            imageCaptchaUtils.validate(VALID_CODE_GROUP,validCodeReqId,validCode);
        }

        // SM2解密并获得前端传来的密码哈希值
        String passwordHash;
        try
        {
            //解密密码
            passwordHash = CryptUtils.sm2Decrypt(password);
        }catch (Exception ex){
            throw new CommonExceptionI18n(AuthExceptionEnum.PWD_DECRYPT_ERROR.getI18nKey(),ex);
        }
        try {
            // 解密哈希值
            passwordHash = CryptUtils.hashValue(passwordHash);
        } catch (Exception e) {
        }
        // 根据账号获取用户信息
        AuthLoginResult result = doLoginUser(uesrname,device,deviceId,passwordHash);
        if(result!=null){
            result.setUsername(uesrname); //返回真正的账号 、为了兼容phone和account兼容的登录
        }

        return result;
    }

    @Override
    public AuthLoginResult doLoginByPhone(AuthPhoneValidCodeLoginParam authPhoneValidCodeLoginParam) {
        String phone = authPhoneValidCodeLoginParam.getPhone();

        //校验手机验证码
        devSmsApi.validateSmsCode(VALID_CODE_GROUP,phone,authPhoneValidCodeLoginParam.getValidCodeReqId(),authPhoneValidCodeLoginParam.getValidCode());
        // 设备
        String device = authPhoneValidCodeLoginParam.getDevice();
        // 默认指定为PC，如在小程序跟移动端的情况下，自行指定即可
        if(ObjectUtil.isEmpty(device)) {
            device = AuthDeviceTypeEnum.PC.getValue();
        } else {
            AuthDeviceTypeEnum.validate(device);
        }

        //根据手机号得到用户
        AccountLoginInfoDTO loginInfo = accountLoginApi.getAccountByPhone(phone);

        CommonExceptionI18n.notNull(loginInfo,"auth.login.account.phone.notExists",phone);
        return doLoginUser(loginInfo.getUsername(),device, authPhoneValidCodeLoginParam.getDeviceId(), null);
    }

    @Override
    public AuthLoginResult doLoginByEmail(AuthEmailValidCodeLoginParam authEmailValidCodeLoginParam) {

        throw new CommonException("暂不支持email登录");
    }

    @Override
    public AuthLoginResult doLoginUser(String userAccount, String device, String deviceId, String checkPasswordHash,Long lockTenantId) {

        //得到用户登录的账号信息，用于验证密码
        AccountLoginInfoDTO accountInfoDTO = accountLoginApi.getAccountByUsername(userAccount);

        if (ObjectUtils.isEmpty(accountInfoDTO)) {
            // 提示账号错误
            throw new CommonExceptionI18n(AuthExceptionEnum.ACCOUNT_ERROR.getI18nKey());
        }


        if(checkPasswordHash!=null){
            if (!accountInfoDTO.getPassword().equals(checkPasswordHash)) {
                // 记录并验证密码错误次数
                handlePasswordErrorTimer(userAccount);
                throw new CommonExceptionI18n(AuthExceptionEnum.PWD_ERROR.getI18nKey());
            }
        }
        //这里先更新下account登录状态，user最后登录状态由sa监听器AuthListener更新，因为account只在登录验证时使用，不会真正存储登录状态
        //就算之后用户验证那失败了，其实也登录成功了
        accountLoginApi.updateUserLoginInfo(accountInfoDTO.getId(),device);
        //存储真实登录用户对象
        AuthLoginUserDTO realLoginUser ;
        //根据账户，得到所属租户的列表

        List<AuthLoginUserDTO> userList = userLoginApi.getAllTenantUserByAccount(userAccount);
        if(lockTenantId!=null){
            userList=userList.stream().filter(o->o.getTenantId().equals(lockTenantId)).collect(Collectors.toList());
            //TODO:这里后续需要改成按tenantId读取，或者直接在doLoginUser外使用TenantUtils.Execute执行，不然每次都读取全量用户
        }
        if (CollectionUtils.isEmpty(userList)) {
            // 提示账号错误，注意，前段这个编码，弹出用户创建或加入租户的对话框
            throw new CommonExceptionI18n(500404, AuthExceptionEnum.ACCOUNT_NOT_EXISTS_USER.getI18nKey());
        }

        //按时间排序，为null一般为新创建的，所以靠前拍
        userList.sort(Comparator.comparing(
                AuthLoginUserDTO::getLatestLoginTime,
                Comparator.nullsFirst(Comparator.reverseOrder())
        ));
        //取第一个
        realLoginUser = userList.stream().filter(o -> o.getStatus().equals(CommonEnableStatusEnum.ENABLE.getValue())).findFirst().orElse(null);
        if(realLoginUser==null){
            CommonExceptionI18n.notNull(userAccount,"auth.login.account.tenant.disable",userAccount);
        }


        //账号密码登录时先做 2FA 校验：未通过前不签发 token
        if (checkPasswordHash != null) {
            DevMfaLoginCheckDTO mfaCheck = devMfaApi.checkLogin(
                    buildMfaCheckParam(realLoginUser, userAccount, device, deviceId));
            if (mfaCheck.isNeedMfa() || mfaCheck.isNeedBindMfa()) {
                AuthLoginResult pending = new AuthLoginResult(realLoginUser.getTenantId());
                pending.setUsername(userAccount);
                pending.setNeedMfa(mfaCheck.isNeedMfa());
                pending.setNeedBindMfa(mfaCheck.isNeedBindMfa());
                pending.setMfaReqId(mfaCheck.getMfaReqId());
                pending.setUserId(realLoginUser.getId());
                pending.setMfaExpiresIn(mfaCheck.getExpiresIn());
                pending.setBindId(mfaCheck.getBindId());
                pending.setOtpauthUri(mfaCheck.getOtpauthUri());
                pending.setQrCodeDataUrl(mfaCheck.getQrCodeDataUrl());
                pending.setBindExpiresIn(mfaCheck.getBindExpiresIn());
                return pending;
            }
        }

        //这里直接获取user登录信息，如果不想使用account层，请该用user表验证上边验证信息
        //最终，以user身份登录
        StpUtil.login(realLoginUser.getId(), new SaLoginModel().setDevice(device).setDeviceId(deviceId).setExtra("name", StringUtils.firstNotBlank(realLoginUser.getName(),realLoginUser.getNickname())));
        //填充登录用户信息
        fillLoginUserBaseInfo(realLoginUser);
        AuthLoginResult loginResult = new AuthLoginResult(StpUtil.getTokenInfo().tokenValue,realLoginUser.getTenantId());
        loginResult.setUsername(userAccount);
        loginResult.setName(realLoginUser.getName());
        return loginResult;
    }

    @Override
    public AuthLoginResult doLoginUser(Long tenantId, String userAccount, String device,String deviceId, String checkPasswordHash) {
        return TenantUtils.execute(tenantId,()->{
            return doLoginUser(userAccount,device,deviceId,checkPasswordHash);
        });
    }

    @Override
    public AuthLoginResult doMfaLogin(AuthMfaLoginParam authMfaLoginParam) {
        //1. 校验动态码，成功后返回挑战中记录的用户与设备信息
        DevMfaVerifyResultDTO verified = devMfaApi.verifyLogin(authMfaLoginParam.getMfaReqId(),
                authMfaLoginParam.getUserId(), authMfaLoginParam.getCode(),
                currentIp(), currentUserAgent());

        //2. 在对应租户下重新加载用户，避免挑战数据被伪造
        SaUserLoginInfoDTO loginUser = TenantUtils.execute(verified.getTenantId(),
                () -> userLoginApi.getUserById(verified.getUserId()));
        CommonExceptionI18n.notNull(loginUser, "auth.login.account.tenant.disable", verified.getUsername());
        CommonExceptionI18n.isTrue(Boolean.TRUE.equals(loginUser.getEnabled()),
                "auth.login.account.tenant.disable", verified.getUsername());

        //3. 写入登录态
        String device = StringUtils.firstNotBlank(verified.getDevice(), AuthDeviceTypeEnum.PC.getValue());
        StpUtil.login(loginUser.getId(), new SaLoginModel()
                .setDevice(device)
                .setDeviceId(verified.getDeviceId())
                .setExtra("name", StringUtils.firstNotBlank(loginUser.getName(), loginUser.getNickname())));
        StpLoginUserUtils.setLoginUser(loginUser);

        AuthLoginResult result = new AuthLoginResult(StpUtil.getTokenInfo().tokenValue, verified.getTenantId());
        result.setUsername(verified.getUsername());
        result.setName(loginUser.getName());
        return result;
    }

    /**
     * 构造 2FA 策略检查参数
     */
    private DevMfaLoginCheckParamDTO buildMfaCheckParam(AuthLoginUserDTO realLoginUser, String userAccount,
                                                        String device, String deviceId) {
        DevMfaLoginCheckParamDTO param = new DevMfaLoginCheckParamDTO();
        param.setTenantId(realLoginUser.getTenantId());
        param.setUserId(realLoginUser.getId());
        param.setUsername(userAccount);
        param.setDevice(device);
        param.setDeviceId(deviceId);
        param.setIp(currentIp());
        param.setUserAgent(currentUserAgent());
        return param;
    }

    private String currentIp() {
        try {
            return RequestUtils.isWeb() ? RequestUtils.getCurrentRequest().realIp() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String currentUserAgent() {
        try {
            return RequestUtils.isWeb() ? RequestUtils.getCurrentRequest().userAgent() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private void fillLoginUserBaseInfo(AuthLoginUserDTO realLoginUser) {
        Long tenantId = realLoginUser.getTenantId();
        String username = realLoginUser.getUsername();

        // 1. 根据是否包含租户 ID，在对应的上下文中获取用户信息
        SaUserLoginInfoDTO loginUser = NumberUtils.isLeZero(tenantId)
                ? userLoginApi.getUserByUsername(username)
                : TenantUtils.execute(tenantId, () -> userLoginApi.getUserByUsername(username));

        // 2. 统一判断防空并设置 LoginUser
        if (loginUser != null) {
            StpLoginUserUtils.setLoginUser(loginUser);
        }
    }

    @Override
    public SaUserLoginInfoDTO getLoginUser() {
        // 获取当前缓存的用户信息
        SaUserLoginInfoDTO userLoginInfo =  StpLoginUserUtils.getLoginUser();

        //得到用户权限集合，从缓存中读取
        userLoginInfo.setPermissions(userLoginApi.getPermissionListByUserId(userLoginInfo.getId()));
        userLoginInfo.setRoles(userLoginApi.getRoleCodeListByUserId(userLoginInfo.getId()));

        // 返回
        return userLoginInfo;
    }

    @Override
    public AccountLoginInfoDTO getLoginAccount() {
        //这个根据getLoginUser再去反查account
        SaUserLoginInfoDTO userLoginInfo =  StpLoginUserUtils.getLoginUser();
        if(userLoginInfo==null && StringUtils.isBlank(userLoginInfo.getUsername())){
            return null;
        }
        //获取用户账户信息
        return accountLoginApi.getAccountByUsername(userLoginInfo.getUsername());
    }

    @Override
    public String getPhoneRegValidCode(AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam) {
        // 手机号
        String phone = authGetPhoneValidCodeParam.getPhone();
        // 校验获取手机号验证码的图片验证码
        imageCaptchaUtils.validate(VALID_CODE_GROUP,authGetPhoneValidCodeParam.getValidCodeReqId(),authGetPhoneValidCodeParam.getValidCode());
        //根据注册场景配置发送验证码
        return devSmsApi.sendSmsCodeByScene(DevConfigSceneEnum.ACCOUNT_REG,VALID_CODE_GROUP,phone);

    }

    @Override
    public String getPhoneRegEmailCode(AuthEmailValidCodeLoginParam authEmailValidCodeLoginParam) {
        return null;
    }

    @Override
    public void register(AuthRegisterParam authRegisterParam) {
        // 校验是否允许注册
        checkRegisterEnableByConfig();

        // 获取账号
        String username = authRegisterParam.getUsername();
        String name = authRegisterParam.getName();
        // 获取账号
        String phone = authRegisterParam.getPhone();
        // 获取密码
        String password = authRegisterParam.getPassword();
        // 获取验证码
        if(devConfigApi.getBooleanValue(ACCOUNT_REGISTER_SMS_CODE_ENABLE,false)){
            devSmsApi.validateSmsCode(VALID_CODE_GROUP,phone,authRegisterParam.getValidCodeReqId(),authRegisterParam.getValidCode());
        }
        else{ //没开启短信验证码则验证图形验证码
            imageCaptchaUtils.validate(VALID_CODE_GROUP,authRegisterParam.getValidCodeReqId(),authRegisterParam.getValidCode());
        }
        // SM2解密前端传来的密码
        String passwordDecrypt = CryptUtils.sm2Decrypt(password);
        accountLoginApi.doRegister(username,name,phone,passwordDecrypt);

    }

    /**
     * 验证注册配置
     */
    private void checkRegisterEnableByConfig(){
        CommonExceptionI18n.isTrue(devConfigApi.getBooleanValue(ACCOUNT_REGISTER_ENABLE,false),"auth.account.register.unavailable");
    }

    @Override
    public String getFindPasswordUserInfo(String account) {
        return null;
    }

    @Override
    public String getPhoneFindPasswordValidCode(AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam) {
        AccountLoginInfoDTO account = accountLoginApi.getAccountByPhone(authGetPhoneValidCodeParam.getPhone());
        CommonExceptionI18n.notNull(account,"auth.login.account.phone.notExists",authGetPhoneValidCodeParam.getPhone());
        // 校验获取手机号验证码的图片验证码
        imageCaptchaUtils.validate(VALID_CODE_GROUP,authGetPhoneValidCodeParam.getValidCodeReqId(),authGetPhoneValidCodeParam.getValidCode());
        //根据注册场景配置发送验证码
        return devSmsApi.sendSmsCodeByScene(DevConfigSceneEnum.PASSWORD_FIND,VALID_CODE_GROUP,authGetPhoneValidCodeParam.getPhone());
    }

    @Override
    public String getEmailFindPasswordValid(AuthGetEmailValidCodeParam authGetEmailValidCodeParam) {
        throw new CommonExceptionI18n("common.notSupported", RequestUtils.getReqUrl());
    }

    @Override
    public void doPhoneFindPassword(AuthPhoneFindPasswordParam authPhoneFindPasswordParam) {
        // 获取账号
        String phone = authPhoneFindPasswordParam.getPhone();
        // 获取密码
        String password = authPhoneFindPasswordParam.getNewPassword();
        // 验证短信验证码
        devSmsApi.validateSmsCode(VALID_CODE_GROUP,phone,authPhoneFindPasswordParam.getValidCodeReqId(),authPhoneFindPasswordParam.getValidCode());
        AccountLoginInfoDTO account = accountLoginApi.getAccountByPhone(phone);
        CommonExceptionI18n.notNull(account,"auth.login.account.phone.notExists",phone);
        // SM2解密前端传来的密码
        String passwordDecrypt = CryptUtils.sm2Decrypt(password);
        // 查询是否有这个用户
        sysAccountApi.updatePassword(account.getId(),passwordDecrypt);

    }

    @Override
    public void doEmailFindPassword(AuthEmailFindPasswordParam authEmailFindPasswordParam) {
        throw new CommonExceptionI18n("common.notSupported", RequestUtils.getReqUrl());
    }


    /**
     * 处理密码错误计数,如已超过限制则等待一段时间分钟
     * @param account
     */
    private void handlePasswordErrorTimer(String account){
        //先写入密码错误日志

        saveLoginPasswordTimer(account);
    }

    private void validAccountDisableTime(String account){
        //改为sa-token实现
        long disableTime = authApi.getAccountDisableTime(account);
        if(disableTime==-1){
            throw new CommonExceptionI18n("auth.account.disable",account);
        }
        if(disableTime>0){
            // 向上取整公式：(秒数 + 59) / 60
            disableTime = (disableTime + 59) / 60;
            throw new CommonExceptionI18n("auth.login.pwd.error.timer.limit",disableTime);
        }
        //得到错误次数
//        Integer pwdErrorCount = cacheUtils.get(AUTH_LOGIN_PASSWORD_ERROR_COUNT_CACHE_KEY_PREFIX+account,Integer.class);
//        if(NumberUtils.isLeZero(pwdErrorCount)){
//            return;
//        }
//        //密码最多错误多少次
//        int pwdErrorCountLimit = devConfigApi.getIntegerValue(AUTH_LOGIN_PASSWORD_ERROR_COUNT_LIMIT_KEY,10);
//
//        //未达到错误次数限制，则继续
//        if(pwdErrorCountLimit<pwdErrorCount){
//            return;
//        }
//        //获取错误后需要等待的时间，默认5分钟
//        int pwdErrorTimeLimit = devConfigApi.getIntegerValue(AUTH_LOGIN_PASSWORD_ERROR_DISABLE_TIME_KEY,15);
//        pwdErrorTimeLimit=pwdErrorTimeLimit*60; //设置为分钟，转为秒
//        //用户最后一次密码错误时间
//        Long userPwdErrorTime = cacheUtils.get(AUTH_LOGIN_PASSWORD_ERROR_TIME_CACHE_KEY_PREFIX+account,Long.class);
//        if(userPwdErrorTime==null){ //没有最后时间的统计，则退出；
//            return;
//        }
//        //计算错误时间差,由于缓存最多存储十几分钟，所以这个int足够用了
//        int disableTime = (int)(LocalDateTimeUtils.currentTimestampSeconds() - userPwdErrorTime);
//        if(disableTime>0){
//            // 向上取整公式：(秒数 + 59) / 60
//            disableTime = (disableTime + 59) / 60;
//            throw new CommonExceptionI18n("auth.login.pwd.error.timer.limit",disableTime);
//        }
    }


    /**
     * 记录密码错误次数，并返回需要等待时间，如果0则无需等待
     */
    private void saveLoginPasswordTimer(String userAccount){

        //得到密码错误连续时间值分钟砖秒，默认5分钟,这是连续错误的时间，不是禁用时间
        int pwdErrorCountDuration = devConfigApi.getIntegerValue(AUTH_LOGIN_PASSWORD_ERROR_COUNT_DURATION_KEY,5)*60;
        if(pwdErrorCountDuration<=0){
            pwdErrorCountDuration=5*60;
        }
        //密码错误次数限制，默认5次
        int pwdErrorCountLimit = devConfigApi.getIntegerValue(AUTH_LOGIN_PASSWORD_ERROR_COUNT_LIMIT_KEY,5);
        if(pwdErrorCountLimit<=0){
            pwdErrorCountLimit=5;
        }



        String loginPwdErrorCountCacheKey = AUTH_LOGIN_PASSWORD_ERROR_COUNT_CACHE_KEY_PREFIX+userAccount;


        Integer userPwdErrorCount = cacheUtils.get(loginPwdErrorCountCacheKey,Integer.class);
        if(userPwdErrorCount==null){
            userPwdErrorCount=0;
        }

        userPwdErrorCount=userPwdErrorCount+1; //增加一次


        if(userPwdErrorCount>=pwdErrorCountLimit){ //超过系统设置次数禁用账户登录权限
            //获取用户禁用时间
            int userDisableTime = devConfigApi.getIntegerValue(AUTH_LOGIN_PASSWORD_ERROR_DISABLE_TIME_KEY,15);
            if(userDisableTime<=0){
                userDisableTime=15*60;
            }
            //禁用账号
            authApi.accountDisable(userAccount,userDisableTime);
            clearLoginErrorTimes(userAccount);
        }else
        {
            //写入错误次数缓存
            cacheUtils.setObject(loginPwdErrorCountCacheKey,userPwdErrorCount,pwdErrorCountDuration); //存储时间就是连续时间
        }
    }



    /**
     * 登录成功、清空登录次数
     * @param userAccount 账号
     */
    private void clearLoginErrorTimes(String userAccount) {
        // 获取登录失败次数缓存键
        String loginPwdErrorCountCacheKey = AUTH_LOGIN_PASSWORD_ERROR_COUNT_CACHE_KEY_PREFIX  + ":" + userAccount;
        // 删除redis中的key
        cacheUtils.remove(loginPwdErrorCountCacheKey);
    }
}
