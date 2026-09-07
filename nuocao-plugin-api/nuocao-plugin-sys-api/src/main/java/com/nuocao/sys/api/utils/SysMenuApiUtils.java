package com.nuocao.sys.api.utils;

import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.sys.api.SysMenuApi;
import com.nuocao.sys.api.SysUserApi;


/**
 * sysMenuApi 静态方法调用类
 */
public class SysMenuApiUtils {

    private static SysMenuApi sysMenuApi;

    private static SysMenuApi getApi(){
        if(sysMenuApi==null){
            sysMenuApi= BeanUtils.getBean(SysMenuApi.class);
        }
        return sysMenuApi;
    }

    /**
     * 获取上级路径
     * @param id
     * @return
     */
    public static String getTitlePath(Long id){
        if(id==null) return "";
        return getApi().getTitlePathByCache(id);
    }


}
