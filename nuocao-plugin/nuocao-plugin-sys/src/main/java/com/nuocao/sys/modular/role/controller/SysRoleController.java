package com.nuocao.sys.modular.role.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.pojo.CommonValidList;
import com.nuocao.sys.api.dto.DataScopeEditDTO;
import com.nuocao.sys.modular.role.entity.SysRole;
import com.nuocao.sys.modular.role.pojo.param.*;
import com.nuocao.sys.modular.role.pojo.result.SysRolePermissionResult;
import com.nuocao.sys.modular.role.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import org.noear.solon.validation.annotation.NotEmpty;

/**
 * 角色控制器
 */
@Api(tags = "角色控制器")
@Controller
@Valid
public class SysRoleController {

    @Inject
    private SysRoleService sysRoleService;

    /**
     * 获取角色分页
     */
    @ApiOperation("获取角色分页")
    @Get
    @SaCheckPermission("sys:role:query")
    @Mapping("/sys/role/page")
    public CommonResult<PageResult<SysRole>> page(@Validated SysRolePageParam sysRolePageParam) {
        return CommonResult.data(sysRoleService.page(sysRolePageParam));
    }

    /**
     * 添加角色
     */
    @ApiOperation("添加角色")
    @CommonLog("添加角色")
    @Post
    @SaCheckPermission("sys:role:add")
    @Mapping("/sys/role/add")
    public CommonResult<String> add(@Validated SysRoleAddParam sysRoleAddParam) {
        sysRoleService.add(sysRoleAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑角色
     */
    @ApiOperation("编辑角色")
    @CommonLog("编辑角色")
    @Post
    @SaCheckPermission("sys:role:edit")
    @Mapping("/sys/role/edit")
    public CommonResult<String> edit(@Validated SysRoleEditParam sysRoleEditParam) {
        sysRoleService.edit(sysRoleEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除角色
     */
    @ApiOperation("删除角色")
    @CommonLog("删除角色")
    @Post
    @SaCheckPermission("sys:role:delete")
    @Mapping("/sys/role/delete")
    public CommonResult<String> delete(@Validated @NotEmpty(message = "集合不能为空")
                                       CommonValidList<Long> ids) {
        sysRoleService.deleteByIds(ids);
        return CommonResult.ok();
    }

    /**
     * 获取角色详情
     */
    @ApiOperation("获取角色详情")
    @Get
    @SaCheckPermission("sys:role:query")
    @Mapping("/sys/role/detail")
    public CommonResult<SysRole> detail(@Validated SysRoleIdParam sysRoleIdParam) {
        return CommonResult.data(sysRoleService.detail(sysRoleIdParam));
    }

    /**
     * 获取角色拥有资源
     */
    @ApiOperation("获取角色拥有资源")
    @Get
    @SaCheckPermission("sys:role:permission")
    @Mapping("/sys/role/getPermission")
    public CommonResult<SysRolePermissionResult> getPermission(@Validated SysRoleIdParam sysRoleIdParam) {
        return CommonResult.data(sysRoleService.getPermission(sysRoleIdParam));
    }

    /**
     * 给角色授权资源
     */
    @ApiOperation("设置色授权资源")
    @CommonLog("设置色授权资源")
    @Post
    @SaCheckPermission("sys:role:permission")
    @Mapping("/sys/role/setPermission")
    public CommonResult<Void> setPermission(@Validated SysRoleSetPermissionParam permissionEdit) {
        sysRoleService.setPermission(permissionEdit);
        return CommonResult.ok();
    }

    /**
     * 获取角色拥有数据权限
     *
     */
    @ApiOperation("获取角色拥有数据权限")
    @Get
    @SaCheckPermission("sys:role:data-space")
    @Mapping("/sys/role/getDataScope")
    public CommonResult<DataScopeEditDTO> getDataScope(@Validated SysRoleIdParam sysRoleIdParam) {
        return CommonResult.data(sysRoleService.getDataSpace(sysRoleIdParam.getId()));
    }

    /**
     * 设置角色数据权限授权
     *
     */
    @ApiOperation("设置角色数据权限授权")
    @Post
    @SaCheckPermission("sys:role:data-space")
    @Mapping("/sys/role/setDataScope")
    public CommonResult<Void> setDataScope(@Validated SysRoleSetDataScopeParam dataScopeParam) {
        sysRoleService.setDataScope(dataScopeParam);
        return CommonResult.ok();
    }





}
