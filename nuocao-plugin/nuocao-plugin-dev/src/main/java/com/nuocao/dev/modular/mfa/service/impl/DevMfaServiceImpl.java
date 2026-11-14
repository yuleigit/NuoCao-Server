package com.nuocao.dev.modular.mfa.service.impl;

import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.mapper.LambdaQueryWrapperX;
import com.nuocao.common.page.PageResult;
import com.nuocao.data.access.tenant.util.TenantUtils;
import com.nuocao.dev.modular.mfa.entity.DevMfa;
import com.nuocao.dev.modular.mfa.mapper.DevMfaMapper;
import com.nuocao.dev.modular.mfa.param.DevMfaOwnerParam;
import com.nuocao.dev.modular.mfa.param.DevMfaPageParam;
import com.nuocao.dev.modular.mfa.result.DevMfaPageResult;
import com.nuocao.dev.modular.mfa.service.DevMfaEventRecorder;
import com.nuocao.dev.modular.mfa.service.DevMfaService;
import com.nuocao.plugin.dev.api.enums.mfa.DevMfaEventEnum;
import com.nuocao.plugin.dev.api.enums.mfa.DevMfaStatusEnum;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.time.LocalDateTime;

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
        Long tenantId = param.getTenantId() != null ? param.getTenantId() : TenantUtils.currentTenantId();
        LambdaQueryWrapperX<DevMfa> query = new LambdaQueryWrapperX<>();
        query.eqIfPresent(DevMfa::getTenantId, tenantId)
                .eqX(DevMfa::getOwnerType, StrUtil.isBlank(param.getOwnerType()) ? OWNER_TYPE_USER : param.getOwnerType())
                .eqIfPresent(DevMfa::getOwnerId, param.getOwnerId())
                .likeIfPresent(DevMfa::getOwnerName, param.getOwnerName())
                .eqIfPresent(DevMfa::getStatus, param.getStatus())
                .orderBy(DevMfa::getCreateTime, false);
        return devMfaMapper.selectJoinPage(param, DevMfaPageResult.class, query);
    }

    @Override
    public DevMfaPageResult detail(DevMfaOwnerParam param) {
        DevMfa mfa = devMfaMapper.selectOneByQuery(ownerQuery(param));
        CommonExceptionI18n.notNull(mfa, "dev.mfa.notExists");
        return toResult(mfa);
    }

    @Override
    public void reset(DevMfaOwnerParam param) {
        String ownerType = ownerType(param.getOwnerType());
        boolean updated = UpdateChain.of(DevMfa.class)
                .set(DevMfa::getFailedAttempts, 0)
                .set(DevMfa::getLockedUntil, null)
                .set(DevMfa::getUpdateUser, operator())
                .set(DevMfa::getUpdateTime, LocalDateTime.now())
                .where(DevMfa::getTenantId).eq(tenantId(param.getTenantId()))
                .and(DevMfa::getOwnerType).eq(ownerType)
                .and(DevMfa::getOwnerId).eq(param.getOwnerId())
                .update();
        CommonExceptionI18n.isTrue(updated, "dev.mfa.notExists");
        eventRecorder.record(param.getTenantId(), ownerType, param.getOwnerId(),
                DevMfaEventEnum.ADMIN_RESET, null, "管理员重置失败次数并解锁", null, null);
    }

    @Override
    public void disable(DevMfaOwnerParam param) {
        String ownerType = ownerType(param.getOwnerType());
        boolean updated = UpdateChain.of(DevMfa.class)
                .set(DevMfa::getStatus, DevMfaStatusEnum.DISABLED.getValue())
                .set(DevMfa::getBindId, null)
                .set(DevMfa::getBindExpiresAt, null)
                .set(DevMfa::getFailedAttempts, 0)
                .set(DevMfa::getLockedUntil, null)
                .set(DevMfa::getUpdateUser, operator())
                .set(DevMfa::getUpdateTime, LocalDateTime.now())
                .where(DevMfa::getTenantId).eq(tenantId(param.getTenantId()))
                .and(DevMfa::getOwnerType).eq(ownerType)
                .and(DevMfa::getOwnerId).eq(param.getOwnerId())
                .update();
        CommonExceptionI18n.isTrue(updated, "dev.mfa.notExists");
        eventRecorder.record(param.getTenantId(), ownerType, param.getOwnerId(),
                DevMfaEventEnum.ADMIN_DISABLE, null, "管理员失效绑定", null, null);
    }

    @Override
    public void delete(DevMfaOwnerParam param) {
        String ownerType = ownerType(param.getOwnerType());
        // 物理删除，避免逻辑删除行占用唯一键导致同一用户无法重新绑定
        int deleted = LogicDeleteManager.execWithoutLogicDelete(
                () -> devMfaMapper.deleteByQuery(ownerQuery(param)));
        CommonExceptionI18n.isTrue(deleted > 0, "dev.mfa.notExists");
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

    private Long tenantId(Long tenantId) {
        return tenantId != null ? tenantId : TenantUtils.currentTenantId();
    }

    private QueryWrapper ownerQuery(DevMfaOwnerParam param) {
        return new QueryWrapper()
                .eq(DevMfa::getTenantId, tenantId(param.getTenantId()))
                .eq(DevMfa::getOwnerType, ownerType(param.getOwnerType()))
                .eq(DevMfa::getOwnerId, param.getOwnerId());
    }

    private String operator() {
        String username = StpLoginUserUtils.getLoginUsername();
        return StrUtil.isBlank(username) ? "system" : username;
    }
}
