package com.nuocao.sys.modular.tenant.convert;


import com.nuocao.common.page.PageResult;
import com.nuocao.sys.api.dto.SysTenantDTO;
import com.nuocao.sys.modular.tenant.entity.SysTenant;
import com.nuocao.sys.modular.tenant.result.SysTenantPageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SysTenantConvert {

    SysTenantConvert INSTANCE = Mappers.getMapper(SysTenantConvert.class);

    PageResult<SysTenantPageResult> convertPage(PageResult<SysTenant> page);
    List<SysTenantPageResult> convertList(List<SysTenant> page);


    SysTenantDTO convertDTO(SysTenant bean);


    List<SysTenantDTO> convertListDTO(List<SysTenant> list);
}
