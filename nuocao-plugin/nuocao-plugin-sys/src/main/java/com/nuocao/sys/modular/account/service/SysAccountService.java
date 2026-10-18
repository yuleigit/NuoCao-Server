package com.nuocao.sys.modular.account.service;

import com.nuocao.auth.api.dto.AccountLoginInfoDTO;
import com.nuocao.common.page.CommonPicValidCodeResult;
import com.nuocao.common.page.PageResult;
import com.nuocao.sys.api.dto.SysAccountDTO;
import com.nuocao.sys.modular.account.entity.SysAccount;
import com.nuocao.sys.modular.account.param.*;
import com.nuocao.sys.modular.account.result.SysAccountDetailResult;
import com.nuocao.sys.modular.account.result.SysAccountPageResult;
import com.nuocao.sys.modular.account.result.SysAccountSimpleResult;

import java.util.List;

/**
 * 系统账户 service

 **/
public interface SysAccountService {

    /**
     * 根据id获取用户信息，查不到则返回null
     *      *
     * @author LEI
     */
    AccountLoginInfoDTO getLoginInfoByAccountId(Long accountId);

    /**
     * 根据账户获取信息，查不到则返回null
     *
     */
    AccountLoginInfoDTO getLoginInfoByUsername(String username);



    /**
     * 根据手机号获取用户信息，查不到则返回null
     *
     */
    AccountLoginInfoDTO getLoginInfoByPhone(String phone);

    /**
     * 根据邮箱获取用户信息，查不到则返回null

     */
    AccountLoginInfoDTO getLoginInfoByEmail(String email);

    /**
     * 获取用户简单信息
     */
    SysAccountSimpleResult getUserSimpleByUsername(String username);

    /**
     *  获取账户分页
     *
     */
    PageResult<SysAccountPageResult> page(SysAccountPageParam pageParam);

    /**
     * 添加账户（仅是账户，与sys_user无自动关联）
     * @param passwordIsSm2 密码是否进行sm2加密
     *
     */
    Long add(SysAccountAddParam clientUserAddParam, String formSource, boolean passwordIsSm2);

    /**
     * 编辑账户,用于后台修改，用户自己不适用
     */
    void edit(SysAccountEditParam clientUserEditParam);

    /**
     * 禁用账户
     * @param accountId 账户ID
     */
    void disableAccount(Long accountId);

    /**
     * 启用用户
     * @param accountId 账户ID
     */
    void enableAccount(Long accountId);


    /**
     * 修改密码
     *
     * @param accountId
     * @return
     */
    String updatePassword(Long accountId, String newPassword);


    /**
     * 删除账户
     *
     */
    void delete(List<Long> accountIds);

    /**
     * 获获取账户
     *
     */
    SysAccountDetailResult detail(Long accountId);
    /**
     * 获获取账户
     *
             */
    SysAccountDetailResult detailByUsername(String username);

    /**
     * 更新登录信息
     *
     */
    void updateLastLoginInfo(Long accountId, String device);

    /**
     * 查询账户详情，如不存在则报错
     * @param accountId 账户ID
     * @return 账户详情
     */
    SysAccount queryEntity(Long accountId);


    /**
     * 使用账户查询详情，如不存在报错
     * @param username
     * @return 账户详情
     */
    SysAccount queryEntityByUsername(String username);



    /**
     *
     * @param username 账号
     * @param nickname 昵称
     * @param phone 电话手机
     * @param password 密码
     * @return
     */
    SysAccount createWithUsername(String username, String nickname, String phone, String email, String password);


    /**
     * 判断当前用户密码是否过期
     * @param accountId  账户ID
     **/
    Boolean isPasswordExpired(Long accountId);

    /**
     * 判断当前用户是否需要绑定手机号
     * @param accountId  账户ID
     **/
    Boolean isNeedBindPhone(Long accountId);

    /**
     * 判断当前用户是否需要绑定邮箱
     * @param accountId  账户ID
     **/
    Boolean isNeedBindEmail(Long accountId);

    /**
     * 执行注册(账户、密码注册）
     *
     **/
    void doRegister(String username, String password);


    /**
     * 执行注册（账户、密码、手机号注册）
     **/
    void doRegister(String username,String name,String phone, String password);

    /**
     * 执行注册（账户、密码、邮箱号注册）
     *
     **/
    void doRegisterByEmail(String username,String name,String phone,String email, String password);

    /**
     * 获取用户操作图片验证码
     *
     **/
    CommonPicValidCodeResult getPicValidCode();

    /**
     * 找回密码获取手机验证码
     * @return 请求号码，之后验证使用需携带
     * @deprecated 已迁移至 auth 模块，请使用 AuthAccountUserService#getPhoneFindPasswordValidCode
     **/
    @Deprecated
    String findPasswordGetPhoneValidCode(SysAccountBindPhoneGetValidCodeParam getPhoneValidCodeParam);

    /**
     * 找回密码获取邮箱验证码【暂时无效，email系统暂时不介入】
     * @return 请求号码，之后验证使用需携带
     * @deprecated 已迁移至 auth 模块，请使用 AuthAccountUserService#getEmailFindPasswordValid
     **/
    @Deprecated
    String findPasswordGetEmailValidCode(SysAccountGetEmailValidCodeParam getEmailValidCodeParam);

    /**
     * 通过手机号找回用户密码
     * @deprecated 已迁移至 auth 模块，请使用 AuthAccountUserService#doPhoneFindPassword
     **/
    @Deprecated
    void findPasswordByPhone(SysAccountFindPwdByPhoneParam findPwdByPhoneParam);

    /**
     * 通过邮箱找回用户密码
     * @deprecated 已迁移至 auth 模块，请使用 AuthAccountUserService#doEmailFindPassword
     **/
    @Deprecated
    void findPasswordByEmail(SysAccountFindPwdByEmailParam findPwdByEmailParam);




    /**
     * 通过账号Id获取用户dto缓存
     * @param accountId
     * @return 如果账户不存在则返回null
     */
    SysAccountDTO getAccountDtoByCache(Long accountId);


    /**
     * 通过账号获取用户dto缓存
     * @param username
     * @return 如果账户不存在则返回null
     */
    SysAccountDTO getAccountDtoByCache(String username);

    /**
     * 根据用户名获取用户信息，不存在则修改，存在则更新，再多租户模式下，仅获取信息
     * @param username
     * @param password
     * @param nickname
     * @param phone
     * @param email
     * @param passwordIsSm2 密码是否是hashValue,如果是需要解码后加密存储，默认都是true
     * @return
     */
    SysAccountDTO getOrCrateByUsername(String username,String password,String name,String nickname,String phone,String email,String formSource,boolean passwordIsSm2);

    /**
     * 根据用户名修改账户信息
     * @param username
     * @param nickname
     * @param phone
     * @param email
     */
    void updateByUsername(String username,String name,String nickname,String phone,String email);

    /**
     * 根据用户名删除账户信息
     * @param usernames
     */
    void deleteByUsernames(List<String> usernames);

    /**
     * 修改密码
     * @param username
     * @param password
     * @param passwordHashValue 是否经过hashValue加密
     */
    void syncPasswordByUsername(String username,String password,boolean  passwordHashValue);


    void updatePhoneById(Long id,String phone);
}
