package com.nuocao.auth.api;

import com.nuocao.auth.api.dto.AuthLoginResultDTO;

/**
 * 账号管理APi
 */
public interface AuthApi {

    /**
     * 禁用账号,一般用于登录时针对账号的急用
     * @param account
     * @param time
     */
    void accountDisable(String account,long time);

    /**
     * 获取账号禁用时间
     * @param account
     * @return 禁用时间,0=不禁用，单位：秒（-1=永久封禁，-2=未被封禁）
     */
    long getAccountDisableTime(String account);

    /**
     * 解禁账号
     * @param account
     */
    void accountUntieDisable(String account);

    /**
     * 禁用用户ID，由于系统中用户ID是唯一的所以可以通用
     * @param userId
     * @param time
     */
    void userIdDisable(Long userId,long time);

    /**
     * 获取用户ID禁用时间
     * @param userId
     * @return 禁用时间,0=不禁用，单位：秒（-1=永久封禁，-2=未被封禁）
     */
    long getUserIdDisableTime(Long userId);

    /**
     * 解禁用户ID
     * @param userId
     */
    void userIdUntieDisable(Long userId);


    /**
     * 禁用会员,一般用于登录时针对账号的急用
     * @param username
     * @param time
     */
    void memberDisable(String username,long time);

    /**
     * 获取会员禁用时间
     * @param username
     * @return 禁用时间,0=不禁用，单位：秒（-1=永久封禁，-2=未被封禁）
     */
    long getMemberDisableTime(String username);

    /**
     * 解禁会员
     * @param username
     */
    void memberUntieDisable(String username);


    /**
     * 根据租户登录账号，sys_user的对应切换接口
     * @param tenantId
     * @param account
     * @return
     */
    AuthLoginResultDTO loginDoByTenantId(Long tenantId, String account, String device,String deviceId, String checkPasswordHash);
}
