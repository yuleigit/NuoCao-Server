package com.nuocao.auth.api.core.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.nuocao.auth.api.dto.SaUserLoginInfoDTO;

/**
 * 系统账户 登录用户工具类
 * 系统账户为登录的基础认证单位，不管时admin的sys_user，还是以后扩展member，都可以使用account登录后再跳转到对应登录状态来处理
 * 因为多租户的时候如果米有一个account层，那有可能会存在多个租户中有重复用户，用户登录必须选择租户，现在可以作为
 * account的下级，登陆后可以自助切换租户来维护
 *

 **/
public class StpLoginAccountUtil {

    /**
     * 获取当前B端登录用户
     **/
    public static SaUserLoginInfoDTO getLoginUser() {
        return (SaUserLoginInfoDTO) StpUtil.getTokenSession().get("loginUser");
    }

    /**
     * 获取当前B端登录用户
     **/
    public static Long getLoginUserId() {
        SaUserLoginInfoDTO user= (SaUserLoginInfoDTO) StpUtil.getTokenSession().get("loginUser");
        return user==null?0L: user.getId();
    }


}
