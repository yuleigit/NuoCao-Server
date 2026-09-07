package com.nuocao.sys.modular.role.convert;


import com.nuocao.common.page.PageResult;
import com.nuocao.sys.modular.role.entity.SysRole;
import com.nuocao.sys.modular.role.pojo.param.SysRoleAddParam;
import com.nuocao.sys.modular.role.pojo.param.SysRoleEditParam;
import com.nuocao.sys.modular.role.pojo.result.SysRolePermissionResult;
import com.nuocao.sys.modular.role.pojo.result.SysRoleResult;
import com.nuocao.sys.modular.user.entity.SysUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


/**
 * 系统角色 转换器
 */
@Mapper
public interface SysRoleConvert {

    SysRoleConvert INSTANCE = Mappers.getMapper(SysRoleConvert.class);

    PageResult<SysRoleResult> convertPage(PageResult<SysRole> page);
    SysRoleResult convertResult(SysUser bean);

    SysRole convert(SysRoleAddParam bean);

    SysRole convert(SysRoleEditParam bean);

    default SysRolePermissionResult convertPermissionResult(SysRole role, List<Long> menuIds){
        SysRolePermissionResult result =new SysRolePermissionResult();
        result.setId(role.getId());
        result.setCode(role.getCode());
        result.setName(role.getName());
        result.setMenuIds(menuIds);
        return result;
    }
}
