package com.nuocao.sys.api.utils;


import com.nuocao.common.utils.ConvertUtils;
import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.sys.api.SysTenantApi;
import com.nuocao.sys.api.SysTenantPackageApi;
import com.nuocao.sys.api.dto.SysTenantDTO;

/**
 * tenantApi 静态方法调用类
 */
public class SysTenantApiUtils {

    private static SysTenantApi sysTenantApi;

    private static SysTenantApi getApi(){
        if(sysTenantApi==null){
            sysTenantApi= BeanUtils.getBean(SysTenantApi.class);
        }
        return sysTenantApi;
    }

    public static String getNameByCache(Long tenantId){
        return getApi().getNameByCache(tenantId);
    }
    public static SysTenantDTO getTenant(Long tenantId){
        return getApi().getTenant(tenantId);
    }

    /**
     * 是否是租户管理员
     * @param tenantId
     * @param userId
     * @return
     */
    public static Boolean isTenantAdmin(Long tenantId,Long userId){
        SysTenantDTO dto = getTenant(tenantId);
        return dto != null && dto.getTenantAdminId() != null && dto.getTenantAdminId().equals(userId);
    }

    public static Boolean isTenantAdmin(Long tenantId,String accountId){
        return isTenantAdmin(tenantId, ConvertUtils.toLong(accountId,0L));
    }


}
