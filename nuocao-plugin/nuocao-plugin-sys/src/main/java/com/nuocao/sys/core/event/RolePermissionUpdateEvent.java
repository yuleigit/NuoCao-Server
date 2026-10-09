package com.nuocao.sys.core.event;

import com.nuocao.common.event.NuoCaoEvent;
import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.sys.modular.permission.service.SysUserPermissionService;
import com.nuocao.sys.modular.user.service.SysUserIdentityService;
import lombok.AllArgsConstructor;
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
 * @see UserPermissionUpdateEvent 单用户权限变更事件（轻量，可同步发送）
 */
@Getter
@AllArgsConstructor
public class RolePermissionUpdateEvent implements NuoCaoEvent {

    /**
     * 角色ID
     */
    private Long roleId;

    @Override
    public void execute() {
        SysUserPermissionService service = BeanUtils.getBean(SysUserPermissionService.class);
        SysUserIdentityService identityService = BeanUtils.getBean(SysUserIdentityService.class);
        //得到所有用户ID集合
        List<Long> userIds= identityService.selectUserIdListByRoleId(roleId);
        userIds.forEach(id->{
            service.clearUserHaveMenuCodeCache(id);
        });
    }
}
