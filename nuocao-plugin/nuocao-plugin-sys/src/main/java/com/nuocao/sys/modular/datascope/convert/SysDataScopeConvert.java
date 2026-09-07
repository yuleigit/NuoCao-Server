package com.nuocao.sys.modular.datascope.convert;

import com.nuocao.sys.api.dto.DataScopeEditDTO;
import com.nuocao.sys.modular.datascope.entity.SysDataScope;
import com.nuocao.sys.modular.datascope.result.SysDataScopeViewResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


/**
 * 数据权限 转换类
 *
 * @author LEI
 * @date  2025/09/25 18:23
 **/
@Mapper
public interface SysDataScopeConvert {

    SysDataScopeConvert INSTANCE = Mappers.getMapper(SysDataScopeConvert.class);


     SysDataScope convert(DataScopeEditDTO bean);

     DataScopeEditDTO convert(SysDataScope bean);

     SysDataScopeViewResult convertView(SysDataScope bean);
}
