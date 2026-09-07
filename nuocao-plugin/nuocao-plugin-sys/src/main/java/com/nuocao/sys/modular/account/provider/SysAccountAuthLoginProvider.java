package com.nuocao.sys.modular.account.provider;

import com.nuocao.auth.api.AuthAccountLoginApi;
import com.nuocao.auth.api.dto.AccountLoginInfoDTO;
import com.nuocao.sys.modular.account.service.SysAccountService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;


/**
 * 系统账户 账户登录等接口实现
 *
 **/
@Component
public class SysAccountAuthLoginProvider implements AuthAccountLoginApi {

    @Inject
    private SysAccountService sysAccountService;


    @Override
    public AccountLoginInfoDTO getAccountById(Long accountId) {
        return sysAccountService.getLoginInfoByAccountId(accountId);
    }

    @Override
    public AccountLoginInfoDTO getAccountByUsername(String account) {
        return sysAccountService.getLoginInfoByUsername(account);
    }

    @Override
    public AccountLoginInfoDTO getAccountByPhone(String phone) {
        return sysAccountService.getLoginInfoByPhone(phone);
    }

    @Override
    public AccountLoginInfoDTO getAccountByEmail(String email) {
        return sysAccountService.getLoginInfoByEmail(email);
    }

    @Override
    public void updateUserLoginInfo(Long accountId, String device) {
        sysAccountService.updateLastLoginInfo(accountId,device);
    }

    @Override
    public void doRegister(String username, String name, String phone, String password) {
        sysAccountService.doRegister(username,name,phone,password);
    }

    @Override
    public void doRegister(String username, String password) {
        sysAccountService.doRegister(username,"","",password);
    }
}