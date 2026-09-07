package com.nuocao.sys.modular.menu.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.pojo.CommonValidList;
import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;
import com.nuocao.sys.modular.menu.convert.SysMenuConvert;
import com.nuocao.sys.modular.menu.pojo.params.SysMenuAddParam;
import com.nuocao.sys.modular.menu.pojo.params.SysMenuEditParam;
import com.nuocao.sys.modular.menu.pojo.params.SysMenuIdParam;
import com.nuocao.sys.modular.menu.pojo.params.SysMenuTreeParam;
import com.nuocao.sys.modular.menu.pojo.result.SysMenuDetailResult;
import com.nuocao.sys.modular.menu.pojo.result.SysMenuSelectorResult;
import com.nuocao.sys.modular.menu.service.SysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;


import org.noear.solon.validation.annotation.NotEmpty;

import java.util.Arrays;
import java.util.List;

/**
 * 菜单控制器
 *
 **/
@Api(tags = "菜单控制器")
@Controller
@Valid
public class SysMenuController {

    @Inject
    private SysMenuService sysMenuService;


    /**
     * 获取菜单树
     */
    @ApiOperation("获取菜单树")
    @Get
    @SaCheckPermission(value = "sys:menu:query")
    @Mapping("/sys/menu/tree")
    public CommonResult<List<SysMenuTreeResultDTO>> tree(SysMenuTreeParam sysMenuTreeParam) {
        return CommonResult.data(sysMenuService.treeMenu(sysMenuTreeParam));
    }

    /**
     * 添加菜单
     */
    @ApiOperation("添加菜单")
    @CommonLog("添加菜单")
    @SaCheckPermission(value = "sys:menu:add")
    @Post
    @Mapping("/sys/menu/add")
    public CommonResult<Long> add(@Validated SysMenuAddParam sysMenuAddParam) {
        return CommonResult.data(sysMenuService.add(sysMenuAddParam));
    }

    /**
     * 编辑菜单
     */
    @ApiOperation("编辑菜单")
    @CommonLog("编辑菜单")
    @SaCheckPermission(value = "sys:menu:edit")
    @Post
    @Mapping("/sys/menu/edit")
    public CommonResult<Long> edit(@Validated SysMenuEditParam sysMenuEditParam) {
        return CommonResult.data(sysMenuService.edit(sysMenuEditParam));
    }

    /**
     * 删除菜单
     */
    @ApiOperation("删除菜单")
    @CommonLog("删除菜单")
    @SaCheckPermission(value = "sys:menu:batchDelete")
    @Post
    @Mapping("/sys/menu/batchDelete")
    public CommonResult<String> batchDelete(@Validated @NotEmpty
                                       CommonValidList<SysMenuIdParam> ids) {
        sysMenuService.delete(ids);
        return CommonResult.ok();
    }

    /**
     * 删除菜单
     */
    @ApiOperation("删除菜单")
    @CommonLog("删除菜单")
    @SaCheckPermission(value = "sys:menu:delete")
    @Post
    @Mapping("/sys/menu/delete")
    public CommonResult<String> delete(@Validated SysMenuIdParam sysMenuIdParam) {
        sysMenuService.delete(Arrays.asList(sysMenuIdParam));
        return CommonResult.ok();
    }

    /**
     * 获取菜单详情
     */
    @ApiOperation("获取菜单详情")
    @SaCheckPermission(value = "sys:menu:query")
    @Get
    @Mapping("/sys/menu/detail")
    public CommonResult<SysMenuDetailResult> detail(@Validated SysMenuIdParam sysMenuIdParam) {
        return CommonResult.data(sysMenuService.detail(sysMenuIdParam));
    }

    /* ====菜单部分所需要用到的选择器==== */

    /**
     * 获取模块选择器
     */
    @ApiOperation("获取菜单选择器")
    @Get
    @Mapping("/sys/menu/selector")
    public CommonResult<List<SysMenuSelectorResult>> menuSelector(SysMenuTreeParam sysMenuTreeParam) {
        return CommonResult.data(SysMenuConvert.INSTANCE.convertSelector(sysMenuService.treeSelector(sysMenuTreeParam)));
    }
    /**
     * 获取模块选择器
     */
    @ApiOperation("获取带button列表的菜单tree，授权使用")
    @Get
    @Mapping("/sys/menu/treeButton")
    public CommonResult<List<SysMenuTreeResultDTO>> treeButton(SysMenuTreeParam sysMenuTreeParam) {
        return CommonResult.data(sysMenuService.tree(sysMenuTreeParam));
    }

}
