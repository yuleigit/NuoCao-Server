package com.nuocao.sys.modular.tenantpackage.convert;

import com.nuocao.auth.api.dto.AccountLoginInfoDTO;
import com.nuocao.sys.api.dto.SysAccountDTO;
import com.nuocao.sys.modular.account.entity.SysAccount;
import com.nuocao.sys.modular.account.result.SysAccountSimpleResult;
import com.nuocao.sys.modular.tenantpackage.entity.SysTenantPackage;
import com.nuocao.sys.modular.tenantpackage.param.SysTenantPackageAddParam;
import com.nuocao.sys.modular.tenantpackage.param.SysTenantPackageEditParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 系统账号 转换类
 **/
@Mapper
public interface SysTenantPackageConvert {

    SysTenantPackageConvert INSTANCE = Mappers.getMapper(SysTenantPackageConvert.class);

    SysTenantPackage convert(SysTenantPackageAddParam bean);
    SysTenantPackage convert(SysTenantPackageEditParam bean);

}
