package com.nuocao.sys.modular.permission.convert;

import com.nuocao.sys.api.dto.DataScopeEditDTO;
import com.nuocao.sys.api.dto.UserPermissionEditDTO;
import com.nuocao.sys.modular.datascope.entity.SysDataScope;
import com.nuocao.sys.modular.datascope.result.SysDataScopeViewResult;
import com.nuocao.sys.modular.permission.entity.SysUserPermission;
import com.nuocao.sys.modular.permission.result.SysUserPermissionViewResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


/**
 * 数据权限 转换类
 *
 * @author LEI
 * @date  2025/09/25 18:23
 **/
@Mapper
public interface SysUserPermissionConvert {

    SysUserPermissionConvert INSTANCE = Mappers.getMapper(SysUserPermissionConvert.class);


     SysUserPermission convert(UserPermissionEditDTO bean);

     UserPermissionEditDTO convert(SysUserPermission bean);

     SysUserPermissionViewResult convertView(SysUserPermission bean);
}
