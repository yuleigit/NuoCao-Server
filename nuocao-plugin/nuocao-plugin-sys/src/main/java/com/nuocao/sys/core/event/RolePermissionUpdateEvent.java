package com.nuocao.sys.core.event;

import com.nuocao.common.event.NuoCaoEvent;
import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.sys.modular.permission.service.SysUserPermissionService;
import com.nuocao.sys.modular.user.service.SysUserIdentityService;
import com.nuocao.sys.modular.user.service.SysUserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;
import org.noear.solon.scheduling.annotation.Async;

import java.util.List;

/**
 * 角色修改时，对角色权限缓存惊醒清理，
 * 这个是为了解决授权时效性问题
 * 当然也可以不处理，等待缓存自然丢失
 */
@Getter
@AllArgsConstructor
public class RolePermissionUpdateEvent implements NuoCaoEvent {
    /**
     * 用户ID
     */
    private Long roleId;

    @Override
    @Async
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
