package com.nuocao.dev.modular.mfa.service.impl;

import com.nuocao.common.mapper.LambdaQueryWrapperX;
import com.nuocao.common.page.PageResult;
import com.nuocao.data.access.tenant.util.TenantUtils;
import com.nuocao.dev.modular.mfa.entity.DevMfaEvent;
import com.nuocao.dev.modular.mfa.mapper.DevMfaEventMapper;
import com.nuocao.dev.modular.mfa.param.DevMfaEventPageParam;
import com.nuocao.dev.modular.mfa.result.DevMfaEventPageResult;
import com.nuocao.dev.modular.mfa.service.DevMfaEventService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

/**
 * 2FA 操作事件查询服务实现
 **/
@Component
public class DevMfaEventServiceImpl implements DevMfaEventService {

    @Inject
    private DevMfaEventMapper devMfaEventMapper;

    @Override
    public PageResult<DevMfaEventPageResult> page(DevMfaEventPageParam param) {
        Long tenantId = param.getTenantId() != null ? param.getTenantId() : TenantUtils.currentTenantId();
        LambdaQueryWrapperX<DevMfaEvent> query = new LambdaQueryWrapperX<>();
        query.eqIfPresent(DevMfaEvent::getTenantId, tenantId)
                .eqIfPresent(DevMfaEvent::getOwnerType, param.getOwnerType())
                .eqIfPresent(DevMfaEvent::getOwnerId, param.getOwnerId())
                .eqIfPresent(DevMfaEvent::getEvent, param.getEvent())
                .betweenIfPresent(DevMfaEvent::getCreateTime, param.getStartTime(), param.getEndTime())
                .orderBy(DevMfaEvent::getCreateTime, false);
        return devMfaEventMapper.selectJoinPage(param, DevMfaEventPageResult.class, query);
    }
}
