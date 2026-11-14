package com.nuocao.dev.modular.mfa.service;

import com.nuocao.dev.modular.mfa.entity.DevMfaEvent;
import com.nuocao.dev.modular.mfa.mapper.DevMfaEventMapper;
import com.nuocao.plugin.dev.api.enums.mfa.DevMfaEventEnum;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.time.LocalDateTime;

/**
 * 2FA 事件记录器。
 * <p>审计失败只记录日志，不能影响登录或绑定主流程。
 **/
@Component
@Slf4j
public class DevMfaEventRecorder {

    @Inject
    private DevMfaEventMapper devMfaEventMapper;

    public void record(Long tenantId, String ownerType, Long ownerId, DevMfaEventEnum event,
                       String refId, String detail, String ip, String userAgent) {
        try {
            DevMfaEvent entity = new DevMfaEvent();
            entity.setTenantId(tenantId);
            entity.setOwnerType(ownerType);
            entity.setOwnerId(ownerId);
            entity.setEvent(event.getValue());
            entity.setRefId(refId);
            entity.setDetail(detail);
            entity.setIp(ip);
            entity.setUserAgent(userAgent);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            devMfaEventMapper.insert(entity);
        } catch (Exception e) {
            log.warn("记录 2FA 事件失败，tenantId={}, ownerId={}, event={}", tenantId, ownerId, event.getValue(), e);
        }
    }
}
