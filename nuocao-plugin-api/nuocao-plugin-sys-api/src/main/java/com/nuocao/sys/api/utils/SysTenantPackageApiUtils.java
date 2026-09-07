package com.nuocao.sys.api.utils;


import com.nuocao.common.utils.ConvertUtils;
import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.sys.api.SysTenantApi;
import com.nuocao.sys.api.SysTenantPackageApi;
import com.nuocao.sys.api.dto.SysTenantDTO;

/**
 * tenantApi 静态方法调用类
 */
public class SysTenantPackageApiUtils {

    private static SysTenantPackageApi tenantPackageApi;

    private static SysTenantPackageApi getApi(){
        if(tenantPackageApi==null){
            tenantPackageApi= BeanUtils.getBean(SysTenantPackageApi.class);
        }
        return tenantPackageApi;
    }


    public static String getNameByCache(Long tenantId){
        return getApi().getNameByCache(tenantId);
    }



}
