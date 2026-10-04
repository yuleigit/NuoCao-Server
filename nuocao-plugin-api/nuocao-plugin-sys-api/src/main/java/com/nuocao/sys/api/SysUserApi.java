package com.nuocao.sys.api;

import com.nuocao.common.page.PageResult;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.sys.api.dto.SysUserDTO;
import com.nuocao.sys.api.dto.SysUserSampleResultDTO;

import java.util.List;
import java.util.Map;

/**
 * 用户Api
 **/
public interface SysUserApi {

    /**
     * 根据用户id获取用户对象，没有则返回null
     **/
    SysUserDTO getUser(Long userId);

    /**
     * 根据用户id获取用户对象列表，没有的则为空，都没有则返回空集合
     **/
    List<SysUserDTO> getUserList(List<Long> userIdList);


    /**
     * 根据用户账号获取用户对象，没有则抛出异常
     **/
    SysUserDTO getUser(String account);


    /**
     * 获取用户拥有角色Id集合
     */
    List<Long> getUserRoleIds(Long userId);

    /**
     * 根据组织id集合获取组织下用户id集合
     **/
    List<Long> getUserIdListByOrgIdList(List<Long> orgIdList);

    /**
     * 获取用户选择器
     */
    PageResult<SysUserDTO> userSelector(Long orgId, String searchKey);

    /**
     * 通过用户id获取用户名称
     * @param userId 用户id
     * @return 用户名称
     * @author chengchuanyao
     * @date 2025/1/6 16:35
     */
    String getUserNameById(Long userId);


    /**
     * 得到角色集合
     * @param userId
     * @return
     */
    String getRoleNames(Long userId);


    /**
     * 获取用户角色权限标识code
     * 根据用户授权和角色授权组合获取
     * 用于权限验证使用
     * @param userId
     * @return
     */
    List<String> getUserAllPermissionCode(Long userId);


    /**
     * 根据账号获取所有用户信息，忽略租户
     * @param account
     * @return
     */
    List<SysUserDTO> getAllUserListByUsernameIgnoreTenant(String account);


    /**
     * 修改密码
     * @param username
     * @param password
     * @param passwordHashValue 是否经过hashValue加密
     */
    void syncPasswordByUsername(String username,String password,boolean  passwordHashValue);


    Map<Long,String> getUserNameMapByIds(List<Long> userIds);

    List<SysUserSampleResultDTO> getUserSampleListByIds(List<Long> userIds);

    default SysUserSampleResultDTO getUserSampleListById(Long userId){
        List<SysUserSampleResultDTO> list = getUserSampleListByIds(CollectionUtils.toList(userId));
        return CollectionUtils.isEmpty(list)?null:list.get(0);
    }

}
