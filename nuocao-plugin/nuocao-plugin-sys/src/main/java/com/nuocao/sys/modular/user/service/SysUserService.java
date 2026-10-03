package com.nuocao.sys.modular.user.service;

import com.nuocao.common.page.PageResult;
import com.nuocao.sys.api.dto.SysAccountDTO;
import com.nuocao.sys.api.dto.SysRoleDTO;
import com.nuocao.sys.api.dto.SysUserDTO;
import com.nuocao.sys.modular.user.entity.SysUser;
import com.nuocao.sys.modular.user.pojo.param.*;
import com.nuocao.sys.modular.user.pojo.result.SaSysLoginUser;
import com.nuocao.sys.modular.user.pojo.result.SysUserPageResult;
import com.nuocao.sys.modular.user.pojo.result.UserSelectorResult;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;


import java.io.IOException;
import java.util.List;

/**
 * 用户Service接口
 *
 **/
public interface SysUserService  {

    /**
     * 根据id获取用户信息，查不到则返回null
     */
    SaSysLoginUser getLoginUserById(Long id);

    /**
     * 根据账户获取用户信息，查不到则返回null
     */
    SaSysLoginUser getLoginUserByUsername(String username);


    /**
     * 根据手机号获取用户信息，查不到则返回null
     */
    SaSysLoginUser getLoginUserByPhone(String phone);

    /**
     * 根据邮箱获取用户信息，查不到则返回null
     */
    SaSysLoginUser getLoginUserByEmail(String email);

    /**
     * 获取用户分页
     */
    PageResult<SysUser> page(SysUserPageParam sysUserPageParam);

    /**
     * 获取用户管理员专用页面的，一般只在管理用户时调用
     */
    PageResult<SysUserPageResult> pageResult(SysUserPageParam sysUserPageParam);

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    SysUser getById(Long userId);

    /**
     * 获取用户信息
     * @param account
     * @return
     */
    SysUser getByUsername(String account);

    List<SysUserDTO> getUserDtoList(List<Long> userIds);

    /**
     * 根据ID获取用户dto缓存
     * @param userId
     * @return
     */
    SysUserDTO getUserDtoCacheByUserId(Long userId);

    /**
     * 忽略租户，获取同账号的所有用户
     * @param username
     * @return
     */
    List<SysUserDTO> selectListByUsernameIgnoreTenant(String username);

    /**
     * 添加用户
     * @param passwordIsHashValue 密码是否是hashValue
     */
    void add(SysUserAddParam sysUserAddParam, String sourceFromType, boolean passwordIsHashValue);

    default void add(SysUserAddParam sysUserAddParam, String sourceFromType){
        add(sysUserAddParam,sourceFromType,false);
    }

    /**
     * 编辑用户
     */
    void edit(SysUserEditParam sysUserEditParam);

    /**
     * 删除用户
     */
    void delete(List<SysUserIdParam> sysUserIdParamList);


    void deleteByIds(List<Long> userIds);



    /**
     * 获取用户详情
     */
    SysUser detail(SysUserIdParam sysUserIdParam);

    /**
     * 获取用户详情
     **/
    SysUser queryEntity(Long id);

    /**
     * 禁用用户
     **/
    void disableUser(SysUserIdParam sysUserIdParam);

    /**
     * 启用用户
     **/
    void enableUser(SysUserIdParam sysUserIdParam);

    /**
     * 重置用户密码,并返回新密码
     **/
    String updatePassword(Long userId, String newPassword);


    /**
     * 修改用户头像返回base64
     **/
    String updateAvatar(UploadedFile file);
    /**
     * 修改用户头像返回base64
     **/
    String updateAvatarByBase64(String base64);

    /**
     * 更新用户的登录时间和登录ip等信息
     */
    void updateUserLoginInfo(Long userId, String device);


    /**
     * 更新租户ID
     * @param userId
     * @param tenantId
     */
    void updateTenantId(Long userId,Long tenantId);

    /**
     * 通过验证旧密码修改用户密码
     **/
    void updatePasswordByOld(SysUserUpdatePwdByOldParam sysUserUpdatePwdByOldParam);

    Integer getCountByTenant(Long tenantId);

    Integer getCountByRoleId(Long roleId);


    /**
     * 根据账号信息创建用户
     * @param accountDto
     * @param defaultRoleId
     * @return
     */
    SysUser createUserWithAccount(SysAccountDTO accountDto,  Long defaultRoleId);


    /**
     * 根据账号密码创建用户
     **/
    SysUser createUserWithAccount(String username,String name,String phone, String password,Long defaultRoleId,boolean passwordIsHashValue);

    default SysUser createUserWithAccount(String username,String name,String phone, String password,Long defaultRoleId){
        return  createUserWithAccount(username,name,phone,password,defaultRoleId,false);
    }


    /**
     * 获取用户角色ID集合
     * @param userId
     * @return
     */
    List<Long> getUserRoleIdList(Long userId);
    /**
     * 获取用户角色编码集合
     * @param userId
     * @return
     */
    List<String> getUserRoleCodeList(Long userId);
    /**
     * 获取用户所有角色（缓存）
     * @param userId
     * @return
     */
    List<Long> getUserRoleIdListByCache(Long userId);


    /**
     * 清理用户缓存
     * @param userId
     */
    void clearUserCache(Long userId);

    /**
     * 根据组织ID获取所有的用户ID
     * @param orgIds
     * @return
     */
    List<Long> getUserIdListByOrgIdList(List<Long> orgIds);

    /**
     * 设置用户角色ID集合
     * @param userId 用户ID
     * @param roleIds 角色Id集合
     */
    void setUserRoleIds(Long userId,List<Long> roleIds);

    /**
     * 获取用户对应角色ID
     * @param userId
     * @return
     */
    List<SysRoleDTO>  getUserRoles(Long userId);

    /**
     * 用户导入
     */
    void importUser(UploadedFile file);

    /**
     * 用户导出
     **/
    void exportUser(SysUserExportParam sysUserExportParam, Context ctx) throws IOException;


    /**
     * 获取用户选择列表
     * @param pageParam
     * @return
     */
    PageResult<UserSelectorResult> selectorPage(SysUserPageParam pageParam);


    /**
     * 获取选中用户集合
     * @param userIds
     * @return
     */
    List<UserSelectorResult> getSelectedUserList(List<Long> userIds);

    /**
     * 通过id获取用户列表
     * @param ids
     * @return
     */
    List<SysUser> selectUserListByIds(List<Long> ids);

    /**
     * 填充select返回信息
     * @param list
     * @return
     */
    List<UserSelectorResult> buildSelectorResultList(List<UserSelectorResult> list);


    /**
     * 同步用户密码
     * @param username
     * @param password
     * @param passwordHashValue
     */
    public void syncPasswordByUsername(String username, String password, boolean passwordHashValue);

    /**
     * 编辑个人信息，由会员你自己发起,仅修改在当前租户的信息
     */
    void updateUserProfileInfo(SysUserUpdateInfoParam sysUserUpdateInfoParam);
}
