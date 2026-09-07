package com.nuocao.auth.api;

import com.nuocao.auth.api.dto.AuthLoginUserDTO;
import com.nuocao.auth.api.dto.SaUserLoginInfoDTO;

import java.util.List;

/**
 * 登录用户API，由其他模块实现
 */
public interface AuthUserLoginApi {

    /**
     * 获取用户登录信息，不存在返回null
     **/
    SaUserLoginInfoDTO getUserById(Long userId);


    /**
     * 获取用户登录信息，不存在返回null
     **/
    SaUserLoginInfoDTO getUserByUsername(String username);


    /**
     * 根据手机号获取B端用户信息，查不到则返回null
     **/
    SaUserLoginInfoDTO getUserByPhone(String phone);

    /**
     * 根据邮箱获取B端用户信息，查不到则返回null
     **/
    SaUserLoginInfoDTO getUserByEmail(String email);

    /**
     * 根据用户id获取用户集合
     */
    List<AuthLoginUserDTO> listUserByUserIdList(List<Long> userIdList);

    /**
     * 根据用户id获取角色集合
    */
    List<Long> getRoleIdListByUserId(Long userId);

    /**
     * 根据用户id获取角色集合
     */
    List<String> getRoleCodeListByUserId(Long userId);

    /**
     * 根据用户id获取权限code集合
     */
    List<String> getPermissionListByUserId(Long userId);

    /**
     * 根据用户id获取权限集合
     */
    List<Long> getMenuIds(Long userId);

    /**
     * 更新用户的登录时间和登录ip等信息
     */
    void updateUserLoginInfo(Long userId, String device);


    /**
     * 获取账户所在租户列表
     * @param account
     * @return
     */
    List<AuthLoginUserDTO> getAllTenantUserByAccount(String account);

}
