package com.nuocao.sys.modular.tenant.provider;

import com.nuocao.common.enums.CommonEnableStatusEnum;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.utils.LocalDateTimeUtils;
import com.nuocao.sys.api.SysTenantApi;
import com.nuocao.sys.api.dto.SysTenantDTO;
import com.nuocao.sys.modular.tenant.convert.SysTenantConvert;
import com.nuocao.sys.modular.tenant.entity.SysTenant;
import com.nuocao.sys.modular.tenant.service.SysTenantService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;


import java.util.List;

/**
 * 租户API实现
 */
@Component
public class SysTenantApiProvider implements SysTenantApi {

    @Inject
    private SysTenantService sysTenantService;


    @Override
    public String getNameByCache(Long tenantId) {
        return sysTenantService.getNameByCache(tenantId);
    }

    @Override
    public SysTenantDTO getTenant(Long tenantId) {
        return sysTenantService.getDTO(tenantId);
    }

    @Override
    public SysTenantDTO getAndValidTenant(Long tenantId) {
        SysTenant tenant =sysTenantService.queryEntity(tenantId);
        if(tenant.getId().equals(1L)){
            return SysTenantConvert.INSTANCE.convertDTO(tenant);
        }
        if(!CommonEnableStatusEnum.ENABLE.getCode().equals(tenant.getEnableStatus()))
        {
            throw new CommonExceptionI18n("sys.tenant.disable",tenant.getTenantName());
        }
        if(LocalDateTimeUtils.now().isAfter(tenant.getExpireTime())){
            throw new CommonExceptionI18n("sys.tenant.expired",tenant.getTenantName());
        }
        return SysTenantConvert.INSTANCE.convertDTO(tenant);
    }

    @Override
    public SysTenantDTO getDTObyCode(String tenantCode) {
        return sysTenantService.getDTObyCode(tenantCode);
    }

    @Override
    public Boolean isTenantAdmin(Long tenantId,Long userId) {
        return sysTenantService.isTenantAdmin(tenantId,userId);
    }


    @Override
    public List<String> getTenantPremissionCodeList(Long tenantId) {
        return sysTenantService.getTenantPermissionCodeList(tenantId);
    }

    @Override
    public List<Long> getTenantPremissionMenuIdList(Long tenantId) {
        return sysTenantService.getTenantPermissionMenuIdList(tenantId);
    }

    /**
     * 根据账号得到对应信息
     * @param username
     * @return
     */
    @Override
    public List<SysTenantDTO> getAccountTenantList(String username){
        return sysTenantService.getTenantListByUsername(username);
    }

    @Override
    public int countByPackageId(Long packageId) {
        return sysTenantService.countByPackageId(packageId);
    }
}
