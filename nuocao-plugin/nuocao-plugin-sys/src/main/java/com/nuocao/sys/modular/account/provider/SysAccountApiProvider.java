package com.nuocao.sys.modular.account.provider;

import cn.hutool.core.bean.BeanUtil;
import com.nuocao.sys.api.SysAccountApi;
import com.nuocao.sys.api.dto.SysAccountDTO;
import com.nuocao.sys.modular.account.service.SysAccountService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.List;


/**
 * 系统账户 接口实现
 *
 **/
@Component
public class SysAccountApiProvider implements SysAccountApi {

    @Inject
    private SysAccountService sysAccountService;


    @Override
    public SysAccountDTO getAccountById(Long id) {
        return sysAccountService.getAccountDtoByCache(id);
    }

    @Override
    public SysAccountDTO getAccountByUsername(String username) {
        return sysAccountService.getAccountDtoByCache(username);
    }

    @Override
    public SysAccountDTO getOrCrateByUsername(String username, String password, String name,String nickname, String phone, String email,String formSource,  boolean passwordIsSm2) {
        return sysAccountService.getOrCrateByUsername(username, password,name, nickname, phone, email, formSource,passwordIsSm2) ;
    }

    @Override
    public void updateByUsername(String username, String name,String nickname, String phone, String email) {
        sysAccountService.updateByUsername(username,name,nickname,phone,email);
    }

    @Override
    public void deleteByUsernames(List<String> usernames) {
        sysAccountService.deleteByUsernames(usernames);
    }

    @Override
    public void syncPassword(String username, String password, boolean passwordHashValue) {
        sysAccountService.syncPasswordByUsername(username, password,  passwordHashValue);
    }
}