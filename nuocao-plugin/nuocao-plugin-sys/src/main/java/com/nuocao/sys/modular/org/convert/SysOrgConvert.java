package com.nuocao.sys.modular.org.convert;

import com.nuocao.sys.api.dto.SysOrgTreeResult;
import com.nuocao.sys.modular.org.entity.SysOrg;
import com.nuocao.sys.modular.org.pojo.param.SysOrgAddParam;
import com.nuocao.sys.modular.org.pojo.param.SysOrgEditParam;
import com.nuocao.sys.modular.org.pojo.result.SysOrgResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 系统组织 转换类
 **/
@Mapper
public interface SysOrgConvert {

    SysOrgConvert INSTANCE = Mappers.getMapper(SysOrgConvert.class);

    SysOrgResult convertResult(SysOrg bean);

    SysOrg convert(SysOrgAddParam bean);

    SysOrg convert(SysOrgEditParam bean);

     SysOrgTreeResult convertTreeResult(SysOrg bean);

     List<SysOrgTreeResult> convertTreeResultList(List<SysOrg> bean);



}
