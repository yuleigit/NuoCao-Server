package com.nuocao.sys.api.utils;


import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.sys.api.SysOrgApi;

/**
 * SysOrgApi 静态方法调用类
 */
public class SysOrgApiUtils {

    private static SysOrgApi sysOrgApi;

    private static SysOrgApi getApi(){
        if(sysOrgApi==null){
            sysOrgApi= BeanUtils.getBean(SysOrgApi.class);
        }
        return sysOrgApi;
    }

    public static String getName(Long orgId){
        if(orgId==null) return "";
        return getApi().getNameCacheById(orgId);
    }

}
