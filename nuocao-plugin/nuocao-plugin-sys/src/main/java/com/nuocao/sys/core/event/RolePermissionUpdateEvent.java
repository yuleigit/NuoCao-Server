package com.nuocao.sys.core.event;

import com.nuocao.common.event.NuoCaoEvent;
import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.data.access.tenant.context.TenantContext;
import com.nuocao.data.access.tenant.util.TenantUtils;
import com.nuocao.sys.modular.permission.service.SysUserPermissionService;
import com.nuocao.sys.modular.user.service.SysUserIdentityService;
import lombok.Getter;

import java.util.List;

/**
 * 角色权限变更事件：清理该角色下所有用户的权限缓存。
 *
 * <p>目的是解决授权的时效性问题 —— 角色权限改完后，已缓存的用户权限要尽快失效，
 * 而不是等缓存自然过期（缓存 5 分钟）。当然也可以不做处理，容忍这段时间的延迟。</p>
 *
 * <p>由于要分两步做「查角色下的用户 + 逐个清缓存」，属于较重且可容忍失败的旁路逻辑，
 * 发送时建议使用 {@code NuoCaoEventHelper.sendAsync(...)} 异步执行，不要阻塞授权变更接口。</p>
 *
 * <p><b>为什么要把 tenantId 带在事件里：</b>{@code execute()} 跑在异步线程上，ThreadLocal 形式的
 * 租户上下文不会跟着过去。若不做处理，{@code sys_user_identity}（带 {@code @Column(tenantId = true)}）
 * 的查询会落在「无租户上下文」上 —— 默认收紧后直接查不到数据，缓存清理静默失效。
 * 因此在<b>构造时</b>（仍在请求线程、上下文有效）捕获租户，异步执行时再用 {@code TenantUtils} 重新建立。</p>
 *
 * @see UserPermissionUpdateEvent 单用户权限变更事件（轻量，可同步发送）
 */
@Getter
public class RolePermissionUpdateEvent implements NuoCaoEvent {

    /**
     * 角色ID
     */
    private final Long roleId;

    /**
     * 发送事件时所在线程的租户ID（可能为 null：未启用多租户时）
     */
    private final Long tenantId;

    public RolePermissionUpdateEvent(Long roleId) {
        this.roleId = roleId;
        this.tenantId = TenantContext.getCurrentTenant();
    }

    @Override
    public void execute() {
        Runnable task = this::doExecute;

        if (tenantId == null) {
            // 发送方本来就没有租户上下文（未启用多租户等），保持"不加租户条件"的原有语义
            TenantUtils.executeIgnore(task);
        } else {
            TenantUtils.execute(tenantId, task);
        }
    }

    private void doExecute() {
        SysUserPermissionService service = BeanUtils.getBean(SysUserPermissionService.class);
        SysUserIdentityService identityService = BeanUtils.getBean(SysUserIdentityService.class);
        if (service == null || identityService == null) {
            return;
        }
        //得到所有用户ID集合
        List<Long> userIds = identityService.selectUserIdListByRoleId(roleId);
        if (userIds == null) {
            return;
        }
        userIds.forEach(service::clearUserHaveMenuCodeCache);
    }
}
