package com.nuocao.sys.core.event;

import com.nuocao.sys.modular.permission.service.SysUserPermissionService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.event.EventListener;

///**
// * 用户权限更新操作监听
// */
//@Component
//public class UserPermissionUpdateEventListener implements EventListener<UserPermissionUpdateEvent> {
//    @Inject
//    private SysUserPermissionService sysUserPermissionService;
//    @Override
//    public void onEvent(UserPermissionUpdateEvent event) throws Throwable {
//        sysUserPermissionService.clearUserHaveMenuCodeCache(event.getUserId());
//    }
//}
