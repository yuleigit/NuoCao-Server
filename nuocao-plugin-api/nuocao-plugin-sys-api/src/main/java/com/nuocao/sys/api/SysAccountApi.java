package com.nuocao.sys.api;

import com.nuocao.sys.api.dto.SysAccountDTO;

import java.util.List;

/**
 * 系统账户 API
 *
 **/
public interface SysAccountApi {

    SysAccountDTO getAccountById(Long accountId);

    SysAccountDTO getAccountByUsername(String account);

    /**
     * 根据用户名获取用户信息，不存在则修改，存在则更新，再多租户模式下，仅获取信息
     * @param username
     * @param password
     * @param nickname
     * @param phone
     * @param email
     * @param passwordIsSm2
     * @return
     */
    SysAccountDTO getOrCrateByUsername(String username,String password,String name,String nickname,String phone,String email,String formSource, boolean passwordIsSm2);

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


    void syncPassword(String username,String password,boolean passwordHashValue);



}
