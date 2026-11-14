package com.nuocao.dev.modular.mfa.provider;

import com.nuocao.dev.modular.mfa.service.DevMfaAuthService;
import com.nuocao.plugin.dev.api.DevMfaApi;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaBindInitDTO;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaLoginCheckDTO;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaLoginCheckParamDTO;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaStatusDTO;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaVerifyResultDTO;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

/**
 * 2FA API 提供者
 **/
@Component
public class DevMfaApiProvider implements DevMfaApi {

    @Inject
    private DevMfaAuthService devMfaAuthService;

    @Override
    public DevMfaLoginCheckDTO checkLogin(DevMfaLoginCheckParamDTO param) {
        return devMfaAuthService.checkLogin(param);
    }

    @Override
    public DevMfaVerifyResultDTO verifyLogin(String mfaReqId, Long userId, String code,
                                             String ip, String userAgent) {
        return devMfaAuthService.verifyLogin(mfaReqId, userId, code, ip, userAgent);
    }

    @Override
    public DevMfaStatusDTO getStatus(Long tenantId, Long userId) {
        return devMfaAuthService.getStatus(tenantId, userId);
    }

    @Override
    public DevMfaBindInitDTO beginBind(Long tenantId, Long userId, String username,
                                       String ip, String userAgent) {
        return devMfaAuthService.beginBind(tenantId, userId, username, ip, userAgent);
    }

    @Override
    public void confirmBind(Long tenantId, Long userId, String bindId, String code,
                            String ip, String userAgent) {
        devMfaAuthService.confirmBind(tenantId, userId, bindId, code, ip, userAgent);
    }

    @Override
    public DevMfaBindInitDTO beginBindByLogin(String mfaReqId, Long userId, String ip, String userAgent) {
        return devMfaAuthService.beginBindByLogin(mfaReqId, userId, ip, userAgent);
    }

    @Override
    public void confirmBindByLogin(String mfaReqId, Long userId, String bindId, String code,
                                   String ip, String userAgent) {
        devMfaAuthService.confirmBindByLogin(mfaReqId, userId, bindId, code, ip, userAgent);
    }
}
