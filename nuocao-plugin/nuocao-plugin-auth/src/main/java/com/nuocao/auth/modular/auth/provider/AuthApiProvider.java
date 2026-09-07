package com.nuocao.auth.modular.auth.provider;

import cn.dev33.satoken.stp.StpUtil;
import com.nuocao.auth.api.AuthApi;
import com.nuocao.auth.api.dto.AuthLoginResultDTO;
import com.nuocao.auth.modular.login.result.login.AuthLoginResult;
import com.nuocao.auth.modular.login.service.AuthAccountUserService;
import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.data.access.tenant.util.TenantUtils;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

@Component
public class AuthApiProvider implements AuthApi {

    private static final String ACCOUNT_KEY_PREFIX="account:";
    private static final String MEMBER_KEY_PREFIX="member:";
    private static final String USERID_KEY_PREFIX="userid:";

    @Inject
    private AuthAccountUserService accountUserService;

    @Override
    public void accountDisable(String account, long time) {
        StpUtil.disable(ACCOUNT_KEY_PREFIX+account,time);
    }

    @Override
    public long getAccountDisableTime(String account) {
        return StpUtil.getDisableTime(ACCOUNT_KEY_PREFIX+account);
    }

    @Override
    public void accountUntieDisable(String account) {
        StpUtil.untieDisable(account);
    }

    @Override
    public void userIdDisable(Long userId, long time) {
        StpUtil.disable(USERID_KEY_PREFIX+userId,time);
    }

    @Override
    public long getUserIdDisableTime(Long userId) {
        return StpUtil.getDisableTime(USERID_KEY_PREFIX+userId);
    }

    @Override
    public void userIdUntieDisable(Long userId) {
        StpUtil.untieDisable(USERID_KEY_PREFIX+userId);
    }

    @Override
    public void memberDisable(String username, long time) {
        StpUtil.disable(MEMBER_KEY_PREFIX+username,time);
    }

    @Override
    public long getMemberDisableTime(String username) {
        return StpUtil.getDisableTime(MEMBER_KEY_PREFIX+username);
    }

    @Override
    public void memberUntieDisable(String username) {
        StpUtil.untieDisable(MEMBER_KEY_PREFIX+username);
    }

    @Override
    public AuthLoginResultDTO loginDoByTenantId(Long tenantId, String account, String device,String deviceId, String checkPasswordHash) {
        AuthLoginResult result = TenantUtils.execute(tenantId,()->{
            return accountUserService.doLoginUser(account,
                    device,
                    deviceId,
                    null);
        });
        return BeanUtils.toBean(result,AuthLoginResultDTO.class);
    }
}
