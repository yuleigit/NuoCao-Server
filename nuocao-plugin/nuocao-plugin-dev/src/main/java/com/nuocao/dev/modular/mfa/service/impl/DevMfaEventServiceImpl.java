package com.nuocao.dev.modular.mfa.service.impl;

import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
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
        QueryWrapper query = QueryWrapper.create();
        Long tenantId = param.getTenantId() != null ? param.getTenantId() : TenantUtils.currentTenantId();
        if (tenantId != null) {
            query.eq(DevMfaEvent::getTenantId, tenantId);
        }
        if (StrUtil.isNotBlank(param.getOwnerType())) {
            query.eq(DevMfaEvent::getOwnerType, param.getOwnerType());
        }
        if (param.getOwnerId() != null) {
            query.eq(DevMfaEvent::getOwnerId, param.getOwnerId());
        }
        if (StrUtil.isNotBlank(param.getEvent())) {
            query.eq(DevMfaEvent::getEvent, param.getEvent());
        }
        if (param.getStartTime() != null) {
            query.ge(DevMfaEvent::getCreateTime, param.getStartTime());
        }
        if (param.getEndTime() != null) {
            query.le(DevMfaEvent::getCreateTime, param.getEndTime());
        }
        query.orderBy(DevMfaEvent::getCreateTime, false);
        return devMfaEventMapper.selectJoinPage(param, DevMfaEventPageResult.class, query);
    }
}
