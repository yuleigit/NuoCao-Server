package com.nuocao.plugin.dev.api;

import com.nuocao.plugin.dev.api.dto.mfa.DevMfaBindInitDTO;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaLoginCheckDTO;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaLoginCheckParamDTO;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaStatusDTO;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaVerifyResultDTO;

/**
 * 2FA 动态码 API
 **/
public interface DevMfaApi {

    /**
     * 登录鉴权通过后检查 2FA 策略。
     * <p>返回 needMfa 表示需要输入动态码；返回 needBindMfa 表示强制开启但用户未绑定，需要先扫码绑定。
     */
    DevMfaLoginCheckDTO checkLogin(DevMfaLoginCheckParamDTO param);

    /**
     * 校验登录动态码，成功返回登录所需的用户与设备信息。
     */
    DevMfaVerifyResultDTO verifyLogin(String mfaReqId, Long userId, String code, String ip, String userAgent);

    /**
     * 查询用户 2FA 状态。
     */
    DevMfaStatusDTO getStatus(Long tenantId, Long userId);

    /**
     * 已登录用户发起绑定。
     */
    DevMfaBindInitDTO beginBind(Long tenantId, Long userId, String username, String ip, String userAgent);

    /**
     * 已登录用户确认绑定。
     */
    void confirmBind(Long tenantId, Long userId, String bindId, String code, String ip, String userAgent);

    /**
     * 登录流程中未绑定用户发起绑定（凭 mfaReqId 免登录）。
     */
    DevMfaBindInitDTO beginBindByLogin(String mfaReqId, Long userId, String ip, String userAgent);

    /**
     * 登录流程中未绑定用户确认绑定（凭 mfaReqId 免登录）。
     */
    void confirmBindByLogin(String mfaReqId, Long userId, String bindId, String code, String ip, String userAgent);
}
