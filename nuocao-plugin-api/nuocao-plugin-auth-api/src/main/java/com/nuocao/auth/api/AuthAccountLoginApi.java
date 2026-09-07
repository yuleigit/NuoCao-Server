package com.nuocao.auth.api;

import com.nuocao.auth.api.dto.AccountLoginInfoDTO;

/**
 * 账户用户API，由其他模块实现
 */
public interface AuthAccountLoginApi {


    /**
     * 获取登录信息，不存在返回null
     **/
    AccountLoginInfoDTO getAccountById(Long accountId);


    /**
     * 获取登录信息，不存在返回null
     **/
    AccountLoginInfoDTO getAccountByUsername(String username);

    /**
     * 获取登录信息，不存在返回null
     **/
    AccountLoginInfoDTO getAccountByPhone(String phone);

    /**
     * 获取登录信息，不存在返回null
     **/
    AccountLoginInfoDTO getAccountByEmail(String email);

    /**
     * 缺一个获取所有用户的方法，后续补齐
     */


    /**
     * 更新用户的登录时间和登录ip等信息
     */
    void updateUserLoginInfo(Long accountId, String device);


    /**
     * 执行注册
     **/
    void doRegister(String username,String name,String phone, String password);

    /**
     * 执行注册
     **/
    void doRegister(String username, String password);
}
