package com.nuocao.sys.modular.account.service.impl;

import cn.hutool.core.util.*;
import com.alibaba.excel.util.StringUtils;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.nuocao.auth.api.dto.AccountLoginInfoDTO;
import com.nuocao.common.ObjectUtils;
import com.nuocao.common.cache.CacheUtils;
import com.nuocao.common.captcha.ImageCaptchaUtils;
import com.nuocao.common.enums.CommonEnableStatusEnum;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.mapper.LambdaQueryWrapperX;
import com.nuocao.common.page.CommonPicValidCodeResult;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.KeyValue;
import com.nuocao.common.utils.*;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.common.utils.http.IpUtils;
import com.nuocao.data.access.tenant.config.TenantConfig;
import com.nuocao.sys.api.SysUserApi;
import com.nuocao.sys.api.dto.SysAccountDTO;
import com.nuocao.sys.core.pass.AccountPasswordUtils;
import com.nuocao.sys.modular.account.convert.SysAccountConvert;
import com.nuocao.sys.modular.account.entity.SysAccount;
import com.nuocao.sys.modular.account.enums.SysAccountFromSourceEnum;
import com.nuocao.sys.modular.account.mapper.SysAccountMapper;
import com.nuocao.sys.modular.account.pojo.param.*;
import com.nuocao.sys.modular.account.pojo.result.SysAccountDetailResult;
import com.nuocao.sys.modular.account.pojo.result.SysAccountPageResult;
import com.nuocao.sys.modular.account.pojo.result.SysAccountSimpleResult;
import com.nuocao.sys.modular.account.service.SysAccountService;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Component;
import org.noear.solon.data.annotation.Transaction;

import java.util.List;

import static com.nuocao.common.constants.Constants.SYS_ADMIN_USERNAME;

/**
 * 系统账户 service 接口实现类
 *
 **/
@Component
@Slf4j
public class SysAccountServiceImpl  implements SysAccountService {

    //dto缓存key
    private static final String ACCOUNT_DTO_CACHE_KEY= "sys:account:pojo:{}";
    private static final String ACCOUNT_ID_LINK_CACHE_KEY= "sys:account:idlink:{}";
    private static final String ACCOUNT_CREATE_BY_USERNAME_LOCK_KEY= "sys:account:create-lock:{}";
    private static final Integer ACCOUNT_DTO_CACHE_EXPIRE= 60*60; //存储一个小时

    private static final String IMAGE_CAPTCHA_GROUP="acount";

    @Inject
    private TenantConfig tenantConfig;

    @Inject
    private SysAccountMapper mapper;

    @Inject
    private CacheUtils cacheUtils;
    @Inject
    private LockUtils lockUtils;

    @Inject
    private SysUserApi sysUserApi;

    @Inject
    private ImageCaptchaUtils imageCaptchaUtils;

    @Override
    public AccountLoginInfoDTO getLoginInfoByAccountId(Long id) {
        return genLogInfoDto(mapper.selectOneById(id));
    }

    @Override
    public AccountLoginInfoDTO getLoginInfoByUsername(String username) {
        return genLogInfoDto(mapper.selectOne(SysAccount::getUsername, username));
    }


    @Override
    public AccountLoginInfoDTO getLoginInfoByPhone(String phone) {
        return genLogInfoDto(mapper.selectOne(SysAccount::getPhone,phone));
    }

    @Override
    public AccountLoginInfoDTO getLoginInfoByEmail(String email) {
        return genLogInfoDto(mapper.selectOne(SysAccount::getEmail,email));
    }

    /**
     * 创建登录信息dto
     * @param sysAccount
     * @return
     */
    private AccountLoginInfoDTO genLogInfoDto(SysAccount sysAccount){
        if (ObjectUtils.isNotEmpty(sysAccount)) {
            AccountLoginInfoDTO loginInfo = SysAccountConvert.INSTANCE.convertLoginInfoDto(sysAccount);
            if (StringUtils.isNotBlank(loginInfo.getUsername())) {
                loginInfo.setPhone(CryptUtils.sm4CbcDecrypt(loginInfo.getPhone()));
            }
            return loginInfo;
        }
        return null;
    }

    @Override
    public SysAccountSimpleResult getUserSimpleByUsername(String username) {
        return SysAccountConvert.INSTANCE.convertSimple(mapper.selectOne(SysAccount::getUsername, username));
    }

    @Override
    public PageResult<SysAccountPageResult> page(SysAccountPageParam pageParam) {
        QueryWrapper query = getQueryWrapper(pageParam);
        return SysAccountConvert.INSTANCE.convertPage(mapper.selectPage(pageParam,query));
    }

    @Override
    public Long add(SysAccountAddParam addParam,String fromSource,boolean passwordIsSm2) {

        SysAccount sysAccount = SysAccountConvert.INSTANCE.convert(addParam);

        String addLockKey = getAddLockKey(addParam);
        String password_de = passwordIsSm2? CryptUtils.sm2Decrypt(addParam.getPassword()):addParam.getPassword();
        if(!StringUtils.isEmpty(password_de)){ //输入了密码，验证密码
            AccountPasswordUtils.validNewPassword(password_de);
        }
        return lockUtils.lock(addLockKey,50*1000L,()->{
            validInfo(sysAccount);
            if(StringUtils.isEmpty(password_de)) {
                // 设置默认密码
                sysAccount.setPassword(CryptUtils.hashValue(AccountPasswordUtils.getDefaultPassword()));
            } else {
                // 设置传入的密码
                sysAccount.setPassword(CryptUtils.hashValue(password_de));
            }
            // 设置状态
            sysAccount.setStatus(CommonEnableStatusEnum.ENABLE.getValue());
            sysAccount.setFromSource(fromSource);
            // 保存用户
            mapper.insert(sysAccount);

            return sysAccount.getId();
        });

    }

    private String getAddLockKey(SysAccountAddParam addParam){
        String key = "sys:account:add:lock";
        return key;
    }

    @Override
    public void edit(SysAccountEditParam editParam) {
        SysAccount sysAccount = SysAccountConvert.INSTANCE.convert(editParam);
        validInfo(sysAccount);
        mapper.updateById(sysAccount);
    }

    @Override
    public void disableAccount(Long accountId) {
        UpdateChain.of(SysAccount.class).eq(SysAccount::getId,accountId)
                .set(SysAccount::getStatus,CommonEnableStatusEnum.DISABLE.getValue())
                .update();
    }

    @Override
    public void enableAccount(Long accountId) {
        UpdateChain.of(SysAccount.class).eq(SysAccount::getId,accountId)
                .set(SysAccount::getStatus,CommonEnableStatusEnum.ENABLE.getValue())
                .update();
    }

    @Override
    public String updatePassword(Long accountId, String newPassword) {
        SysAccount account = queryEntity(accountId);
        //生成随机新密码
        AccountPasswordUtils.validNewPassword(newPassword);
        //String newPassword = AccountPasswordUtils.genPassword(8);
        String password= CryptUtils.hashValue(newPassword);
        syncPasswordByUsername(account.getUsername(),password,true);
        //这里修改后同步用户端密码是为了方便兼容两种登录模式，可根据自己业务需求更新为只在account中存储或去掉account这个表
        sysUserApi.syncPasswordByUsername(account.getUsername(),password,true);
        return newPassword;
    }

    @Override
    public void delete(List<Long> accountIds) {
        if(CollectionUtils.isEmpty(accountIds)){
            return;
        }
        //得到所有账户列表
        List<String> accountList = mapper.selectObjectListByQueryAs(new QueryWrapper().select(SysAccount::getUsername).in(SysAccount::getId,accountIds),String.class);

        for (String account : accountList){
            //存在账户的不允许删除
            if(CollectionUtils.isNotEmpty(sysUserApi.getAllUserListByUsernameIgnoreTenant(account))){
                throw new CommonExceptionI18n("sys.account.delete.existsUser",account);
            }
        }
        mapper.deleteBatchByIds(accountIds);
    }

    @Override
    public SysAccountDetailResult detail(Long accountId) {
        return SysAccountConvert.INSTANCE.convertDetail(this.queryEntity(accountId));
    }

    @Override
    public void updateLastLoginInfo(Long accountId, String device) {
        //查询账户信息
        SysAccount sysAccount = this.queryEntity(accountId);
        //创建更新实例
        SysAccount up =new SysAccount();
        up.setId(sysAccount.getId());
        //记录上一次登录情况
        up.setLastLoginTime(sysAccount.getLatestLoginTime());
        up.setLastLoginIp(sysAccount.getLatestLoginIp());
        up.setLastLoginAddress(sysAccount.getLatestLoginAddress());
        up.setLastLoginDevice(sysAccount.getLatestLoginDevice());
        //记录本次登录情况
        String ip = IpUtils.getRequestIp();
        up.setLatestLoginTime(LocalDateTimeUtils.now());
        up.setLatestLoginIp(ip);
        up.setLatestLoginAddress(IpUtils.getCityInfo(ip));
        up.setLatestLoginDevice(device);
        mapper.updateById(up);
    }

    @Override
    public SysAccount queryEntity(Long accountId) {
        SysAccount account = mapper.selectOneById(accountId);
        CommonExceptionI18n.notNull(account,"sys.account.notExists",accountId);
        return account;
    }

    @Override
    public SysAccount queryEntityByUsername(String username) {
        SysAccount sysAccount = mapper.selectOne(SysAccount::getUsername, username);
        CommonExceptionI18n.notNull(sysAccount,"sys.account.notExists", username);
        return sysAccount;
    }


    @Transaction
    @Override
    public SysAccount createWithUsername(String username, String nickname, String phone, String email, String password) {
        SysAccountAddParam addParam = new SysAccountAddParam();
        addParam.setUsername(username);
        addParam.setPhone(phone);
        addParam.setEmail(email);
        addParam.setPassword(password);
        addParam.setNickname(nickname);
        Long accountId=  this.add(addParam, SysAccountFromSourceEnum.USER_REG.getValue(),false);
        if(NumberUtils.isLeZero(accountId)){
            throw new CommonExceptionI18n("sys.account.addFailed");
        }
        //获取用户
        SysAccount sysAccount = mapper.selectOneById(accountId);

        //可以增加短信提醒等业务
        return sysAccount;

    }

    @Override
    public Boolean isPasswordExpired(Long accountId) {
        return null;
    }

    @Override
    public Boolean isNeedBindPhone(Long accountId) {
        // 获取账户
        SysAccount sysAccount = this.queryEntity(accountId);
        // 如果非用户注册的，则不需要绑定
        if(!SysAccountFromSourceEnum.USER_REG.getValue().equals(sysAccount.getFromSource())){
            return false;
        }
        //账号为空
        if(StringUtils.isBlank(sysAccount.getPhone())) {
            //后续可扩展增加绑定设置逻辑
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean isNeedBindEmail(Long accountId) {
        // 获取账户
        SysAccount sysAccount = this.queryEntity(accountId);
        // 如果非用户注册的，则不需要绑定
        if(!SysAccountFromSourceEnum.USER_REG.getValue().equals(sysAccount.getFromSource())){
            return false;
        }
        //账号为空
        if(StringUtils.isBlank(sysAccount.getEmail())) {
            //后续可扩展增加绑定设置逻辑
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void doRegister(String username, String password) {
        this.createWithUsername(username,"","","",password);
    }

    @Override
    public void doRegister(String account, String name, String phone, String password) {
        this.createWithUsername(account,name,phone,"",password);

    }

    @Override
    public void doRegisterByEmail(String username, String name, String phone, String email, String password) {
        this.createWithUsername(username,name,phone,email,password);
    }

    @Override
    public CommonPicValidCodeResult getPicValidCode() {
        CommonPicValidCodeResult result =new CommonPicValidCodeResult();
        KeyValue<String,String> validCode= imageCaptchaUtils.get(IMAGE_CAPTCHA_GROUP);
        result.setImage(validCode.getValue());
        result.setValidCodeReqId(validCode.getKey());
        return result;
    }

    @Override
    public String findPasswordGetPhoneValidCode(SysAccountGetPhoneValidCodeParam getPhoneValidCodeParam) {
        // 手机号
        String phone = getPhoneValidCodeParam.getPhone();
        // 验证码正确则校验手机号格式
        if (!ValidationUtils.isMobile(phone)) {
            throw new CommonExceptionI18n("common.mobile.error", phone);
        }
        // 执行校验验证码
        imageCaptchaUtils.validate(IMAGE_CAPTCHA_GROUP,getPhoneValidCodeParam.getValidCodeReqId(),getPhoneValidCodeParam.getValidCode());
        // 根据手机号获取用户信息，判断用户是否存在
        if (ObjectUtils.isEmpty(this.getLoginInfoByPhone(phone))) {
            throw new CommonExceptionI18n("sys.account.phone.notExists", phone);
        }

        //之后的代码单独封装短信模块的验证码来处理，参考imageCaptchaUtils
        // 生成手机验证码的值，随机6为数字
        String phoneValidCode = RandomUtil.randomNumbers(6);
        // 生成手机验证码的请求号
        String phoneValidCodeReqId = IdUtil.getSnowflake().nextIdStr();

        // TODO 使用阿里云执行发送验证码，将验证码作为短信内容的参数变量放入，
        // TODO 签名不传则使用系统默认配置的签名，支持传入多个参数，示例：{"name":"张三","number":"15038****76"}
        //devSmsApi.sendSmsAliyun(phone, null, "验证码模板号", JSONUtil.toJsonStr(JSONUtil.createObj().set("validCode", phoneValidCode)));

        // TODO 使用腾讯云执行发送验证码，将验证码作为短信内容的参数变量放入，
        // TODO sdkAppId和签名不传则使用系统默认配置的sdkAppId和签名，支持传入多个参数，逗号拼接，示例："张三,15038****76,进行中"
        //devSmsApi.sendSmsTencent(null, phone, null, "验证码模板号", phoneValidCode);

        // 将请求号作为key，验证码的值作为value放到redis，用于校验，5分钟有效
        //commonCacheOperator.put(USER_VALID_CODE_CACHE_KEY + phone + StrUtil.UNDERLINE + phoneValidCodeReqNo, phoneValidCode, 5 * 60);
        // 返回请求号
        return phoneValidCodeReqId;
    }

    @Override
    public String findPasswordGetEmailValidCode(SysAccountGetEmailValidCodeParam getEmailValidCodeParam) {
        return null;
    }

    @Override
    public void findPasswordByPhone(SysAccountFindPwdByPhoneParam findPwdByPhoneParam) {

    }

    @Override
    public void findPasswordByEmail(SysAccountFindPwdByEmailParam findPwdByEmailParam) {

    }

    @Override
    public String updatePasswordGetPhoneValidCode(SysAccountGetPhoneValidCodeParam getPhoneValidCodeParam) {
        return null;
    }

    @Override
    public String updatePasswordGetEmailValidCode(SysAccountGetEmailValidCodeParam getEmailValidCodeParam) {
        return null;
    }

    @Override
    public void updatePasswordByOld(SysAccountUpdatePwdByOldParam updatePwdByOldParam) {

    }

    @Override
    public void updatePasswordByPhone(SysAccountUpdatePwdByPhoneParam clientUserUpdatePwdByPhoneParam) {

    }

    @Override
    public void updatePasswordByEmail(SysAccountUpdatePwdByEmailParam clientUserUpdatePwdByEmailParam) {

    }

    @Override
    public String bindPhoneGetPhoneValidCode(SysAccountGetPhoneValidCodeParam getPhoneValidCodeParam) {
        return null;
    }

    @Override
    public void bindPhone(SysAccountBindPhoneParam bindPhoneParam) {

    }

    @Override
    public String bindEmailGetEmailValidCode(SysAccountGetEmailValidCodeParam getEmailValidCodeParam) {
        return null;
    }

    @Override
    public void bindEmail(SysAccountBindEmailParam bindEmailParam) {

    }


    @Override
    public SysAccountDTO getAccountDtoByCache(Long accountId) {
        String cacheKey = StrFormatUtils.doFormat(ACCOUNT_DTO_CACHE_KEY,accountId);
        SysAccountDTO dto= cacheUtils.getOrSetObject(cacheKey,SysAccountDTO.class,()->{
            //得到数据
            SysAccount sysAccount = mapper.selectOne(SysAccount::getId,accountId);
            SysAccountDTO newDto = SysAccountConvert.INSTANCE.convertDto(sysAccount);
            if(newDto==null){
                newDto = new SysAccountDTO();
                newDto.setId(0L); //空的直接就设置为0，这样不存在的用户就不需要每次都差数据库了
            }
            return newDto;
        });
        return dto==null || dto.getId().equals(0L)?null:dto;
    }

    @Override
    public SysAccountDTO getAccountDtoByCache(String username) {
        String cacheKey = StrFormatUtils.doFormat(ACCOUNT_ID_LINK_CACHE_KEY, username);
        Long accountId = cacheUtils.getOrSetObject(cacheKey,Long.class,()->{
            QueryWrapper query = new QueryWrapper();
            query.eq(SysAccount::getUsername, username).limit(1);
            Long id = mapper.selectObjectByQueryAs(query,Long.class);
            return id==null?0:id;
        });
        return accountId.equals(0L)?null:getAccountDtoByCache(accountId);
    }

    @Override
    public SysAccountDTO getOrCrateByUsername(String username, String password,String name, String nickname, String phone, String email,String formSource, boolean passwordIsSm2) {
        return lockUtils.lock(ACCOUNT_CREATE_BY_USERNAME_LOCK_KEY+username,()->{
                    SysAccountDTO accountDto = getAccountDtoByCache(username);
                    if(accountDto==null){
                        //如果是多租户情况则没有注册让他去注册
                        if(tenantConfig.isEnabled())
                        {
                            throw new CommonExceptionI18n("sys.user.account.notExists",username);
                        }else{
                            //如果不是多租户模式，则
                            SysAccountAddParam addParam =new SysAccountAddParam();
                            addParam.setUsername(username);
                            addParam.setPhone(phone);
                            addParam.setPassword(password);
                            addParam.setNickname(nickname);
                            addParam.setEmail(email);
                            Long newId =  add(addParam,formSource,passwordIsSm2);
                            return getAccountDtoByCache(username);
                        }
                    }
                    return accountDto;
                }
        );

    }


    @Override
    public void updateByUsername(String username,String name, String nickname, String phone, String email) {
        lockUtils.lock(ACCOUNT_CREATE_BY_USERNAME_LOCK_KEY+username,()->{
            UpdateChain<SysAccount> update = UpdateChain.of(SysAccount.class);
            update.eq(SysAccount::getUsername,username);
            boolean isUpdate= false;
            if(nickname!=null){
                update.set(SysAccount::getNickname,nickname);
                isUpdate=true;
            }
            if(name!=null){
                update.set(SysAccount::getName,name);
                isUpdate=true;
            }
            if(phone!=null){
                update.set(SysAccount::getPhone,phone);
                isUpdate=true;
            }
            if(email!=null){
                update.set(SysAccount::getEmail,email);
                isUpdate=true;
            }
            if(isUpdate){
                update.update();
            }
        });
    }

    @Override
    public void deleteByUsernames(List<String> usernames) {
        LambdaQueryWrapperX<SysAccount> query = new LambdaQueryWrapperX<>();
        query.inX(SysAccount::getUsername,usernames);
        mapper.deleteByQuery(query);
    }

    @Override
    public void syncPasswordByUsername(String username, String password, boolean passwordHashValue) {
        String pwd = passwordHashValue?password: CryptUtils.hashValue(password);
        UpdateChain.of(SysAccount.class).eq(SysAccount::getUsername,username)
                .set(SysAccount::getPassword,pwd).update();
    }

    /**
     * 获取查询条件
     */
    private QueryWrapper getQueryWrapper(SysAccountPageParam pageParams){
        if(pageParams==null) return new QueryWrapper();
        LambdaQueryWrapperX<SysAccount> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.notIn(SysAccount::getUsername,new String[]{SYS_ADMIN_USERNAME});
        queryWrapper.likeIfPresent(SysAccount::getUsername, pageParams.getUsername());
        queryWrapper.likeIfPresent(SysAccount::getNickname, pageParams.getNickname());
        queryWrapper.eqIfPresent(SysAccount::getPhone, pageParams.getPhone());
        queryWrapper.eqIfPresent(SysAccount::getStatus, pageParams.getStatus());

        if(ObjectUtils.isEmpty(pageParams.getSortingFields())) {
            queryWrapper.orderBy(SysAccount::getId,false).orderBy(SysAccount::getCreateTime,false);
        }
        return queryWrapper;
    }


    /**
     * 验证信息，如有问题则返回错误
     * @param sysAccount
     */
    private void validInfo(SysAccount sysAccount){
        //如果密码

    }


}
