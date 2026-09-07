package com.nuocao.sys.core.event;

import com.nuocao.common.event.NuoCaoEvent;
import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.sys.modular.permission.service.SysUserPermissionService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPermissionUpdateEvent implements NuoCaoEvent {
    /**
     * 用户ID
     */
    private Long userId;

    @Override
    public void execute() {
        SysUserPermissionService service = BeanUtils.getBean(SysUserPermissionService.class);
        if(service==null){return;}
        service.clearUserHaveMenuCodeCache(this.userId);
    }
}
