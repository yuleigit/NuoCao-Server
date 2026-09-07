package com.nuocao.sys.api.utils;

import com.alibaba.excel.util.StringUtils;
import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.sys.api.SysUserApi;


/**
 * userApi 静态方法调用类
 */
public class SysUserApiUtils {

    private static SysUserApi sysUserApi;

    private static SysUserApi getApi(){
        if(sysUserApi==null){
            sysUserApi= BeanUtils.getBean(SysUserApi.class);
        }
        return sysUserApi;
    }

    public static String getNameByCache(Long userId){
        if(userId==null) return "";
        return getApi().getUserNameById(userId);
    }


    public static String getRoleNames(Long userId){
        return getApi().getRoleNames(userId);
    }

}
