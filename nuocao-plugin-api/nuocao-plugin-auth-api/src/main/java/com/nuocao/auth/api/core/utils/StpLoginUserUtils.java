package com.nuocao.auth.api.core.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.nuocao.auth.api.dto.SaUserLoginInfoDTO;
import com.nuocao.common.ObjectUtils;
import com.nuocao.common.exception.CommonException;

/**
 * 系统用户 登录用户工具类
 **/
public class StpLoginUserUtils {

    /**
     * 获取当前端登录user
     **/
    public static SaUserLoginInfoDTO getLoginUser() {
        return (SaUserLoginInfoDTO) StpUtil.getTokenSession().get("loginUser");
    }

    /**
     * 获取当前端user用户ID
     **/
    public static Long getLoginUserId() {
        SaUserLoginInfoDTO user= (SaUserLoginInfoDTO) StpUtil.getTokenSession().get("loginUser");
        return user==null?0L: user.getId();
    }

    public static void setLoginUser(SaUserLoginInfoDTO loginUser){
        if(ObjectUtils.isEmpty(loginUser)){
            throw new CommonException("loginUser cannot be empty");
        }
        StpUtil.getTokenSession().set("loginUser",loginUser);
    }


    public static boolean hasPermission(String permission){
        return StpUtil.hasPermission(permission);
    }

    /**
     * 验证权限
     * @param permissionArray
     * @return
     */
    public static boolean hasPermissionAnd(String... permissionArray) {
        return StpUtil.hasPermissionAnd(permissionArray);
    }
    /**
     * 验证权限
     * @param permissionArray
     * @return
     */
    public static boolean hasPermissionOr(String... permissionArray) {
        return StpUtil.hasPermissionOr(permissionArray);
    }
}
