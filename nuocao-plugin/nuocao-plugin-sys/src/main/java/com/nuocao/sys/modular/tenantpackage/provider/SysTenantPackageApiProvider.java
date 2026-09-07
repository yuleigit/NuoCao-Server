package com.nuocao.sys.modular.tenantpackage.provider;

import com.nuocao.sys.api.SysTenantPackageApi;
import com.nuocao.sys.modular.tenantpackage.entity.SysTenantPackage;
import com.nuocao.sys.modular.tenantpackage.service.SysTenantPackageService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * 租户套餐接口 实现
 */
@Component
public class SysTenantPackageApiProvider implements SysTenantPackageApi {

    @Inject
    private SysTenantPackageService sysTenantPackageService;

    @Override
    public String getNameByCache(Long packageId) {
        return sysTenantPackageService.getNameByCache(packageId);
    }

    @Override
    public List<Long> getMenuIds(Long packageId) {
        SysTenantPackage tenantPackage = sysTenantPackageService.getById(packageId);
        return tenantPackage==null || tenantPackage.getMenuIds()==null?new ArrayList<>(): tenantPackage.getMenuIds();
    }
}
