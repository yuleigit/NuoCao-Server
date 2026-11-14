package com.nuocao.dev.modular.mfa.service.impl;

import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.page.PageResult;
import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.data.access.tenant.util.TenantUtils;
import com.nuocao.dev.modular.mfa.entity.DevMfa;
import com.nuocao.dev.modular.mfa.mapper.DevMfaMapper;
import com.nuocao.dev.modular.mfa.param.DevMfaOwnerParam;
import com.nuocao.dev.modular.mfa.param.DevMfaPageParam;
import com.nuocao.dev.modular.mfa.result.DevMfaPageResult;
import com.nuocao.dev.modular.mfa.service.DevMfaEventRecorder;
import com.nuocao.dev.modular.mfa.service.DevMfaService;
import com.nuocao.plugin.dev.api.enums.mfa.DevMfaEventEnum;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

/**
 * 2FA 绑定管理服务实现
 **/
@Component
public class DevMfaServiceImpl implements DevMfaService {

    private static final String OWNER_TYPE_USER = "user";

    @Inject
    private DevMfaMapper devMfaMapper;

    @Inject
    private DevMfaEventRecorder eventRecorder;

    @Override
    public PageResult<DevMfaPageResult> page(DevMfaPageParam param) {
        QueryWrapper query = QueryWrapper.create();
        Long tenantId = param.getTenantId() != null ? param.getTenantId() : TenantUtils.currentTenantId();
        if (tenantId != null) {
            query.eq(DevMfa::getTenantId, tenantId);
        }
        query.eq(DevMfa::getOwnerType, StrUtil.isBlank(param.getOwnerType()) ? OWNER_TYPE_USER : param.getOwnerType());
        if (param.getOwnerId() != null) {
            query.eq(DevMfa::getOwnerId, param.getOwnerId());
        }
        if (StrUtil.isNotBlank(param.getOwnerName())) {
            query.like(DevMfa::getOwnerName, param.getOwnerName());
        }
        if (param.getStatus() != null) {
            query.eq(DevMfa::getStatus, param.getStatus());
        }
        query.orderBy(DevMfa::getCreateTime, false);
        return devMfaMapper.selectJoinPage(param, DevMfaPageResult.class, query);
    }

    @Override
    public DevMfaPageResult detail(DevMfaOwnerParam param) {
        DevMfa mfa = devMfaMapper.selectByOwner(param.getTenantId(), ownerType(param.getOwnerType()), param.getOwnerId());
        CommonExceptionI18n.notNull(mfa, "dev.mfa.notExists");
        return toResult(mfa);
    }

    @Override
    public void reset(DevMfaOwnerParam param) {
        String ownerType = ownerType(param.getOwnerType());
        int updated = devMfaMapper.resetFailure(param.getTenantId(), ownerType, param.getOwnerId(), operator());
        CommonExceptionI18n.isTrue(updated > 0, "dev.mfa.notExists");
        eventRecorder.record(param.getTenantId(), ownerType, param.getOwnerId(),
                DevMfaEventEnum.ADMIN_RESET, null, "管理员重置失败次数并解锁", null, null);
    }

    @Override
    public void disable(DevMfaOwnerParam param) {
        String ownerType = ownerType(param.getOwnerType());
        int updated = devMfaMapper.disable(param.getTenantId(), ownerType, param.getOwnerId(), operator());
        CommonExceptionI18n.isTrue(updated > 0, "dev.mfa.notExists");
        eventRecorder.record(param.getTenantId(), ownerType, param.getOwnerId(),
                DevMfaEventEnum.ADMIN_DISABLE, null, "管理员失效绑定", null, null);
    }

    @Override
    public void delete(DevMfaOwnerParam param) {
        String ownerType = ownerType(param.getOwnerType());
        int updated = devMfaMapper.logicDelete(param.getTenantId(), ownerType, param.getOwnerId(), operator());
        CommonExceptionI18n.isTrue(updated > 0, "dev.mfa.notExists");
        eventRecorder.record(param.getTenantId(), ownerType, param.getOwnerId(),
                DevMfaEventEnum.ADMIN_DELETE, null, "管理员删除绑定", null, null);
    }

    private DevMfaPageResult toResult(DevMfa mfa) {
        DevMfaPageResult result = new DevMfaPageResult();
        result.setTenantId(mfa.getTenantId());
        result.setOwnerType(mfa.getOwnerType());
        result.setOwnerId(mfa.getOwnerId());
        result.setOwnerName(mfa.getOwnerName());
        result.setStatus(mfa.getStatus());
        result.setAlgorithm(mfa.getAlgorithm());
        result.setDigits(mfa.getDigits());
        result.setPeriodSeconds(mfa.getPeriodSeconds());
        result.setFailedAttempts(mfa.getFailedAttempts());
        result.setLockedUntil(mfa.getLockedUntil());
        result.setBindExpiresAt(mfa.getBindExpiresAt());
        result.setActivatedAt(mfa.getActivatedAt());
        result.setLastUsedAt(mfa.getLastUsedAt());
        result.setCreateTime(mfa.getCreateTime());
        return result;
    }

    private String ownerType(String ownerType) {
        return StrUtil.isBlank(ownerType) ? OWNER_TYPE_USER : ownerType;
    }

    private String operator() {
        String username = StpLoginUserUtils.getLoginUsername();
        return StrUtil.isBlank(username) ? "system" : username;
    }
}
