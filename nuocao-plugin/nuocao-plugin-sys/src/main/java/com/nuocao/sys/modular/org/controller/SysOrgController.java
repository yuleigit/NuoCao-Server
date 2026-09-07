package com.nuocao.sys.modular.org.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.pojo.CommonValidList;
import com.nuocao.sys.api.dto.SysOrgTreeResult;
import com.nuocao.sys.modular.org.entity.SysOrg;
import com.nuocao.sys.modular.org.pojo.param.SysOrgAddParam;
import com.nuocao.sys.modular.org.pojo.param.SysOrgEditParam;
import com.nuocao.sys.modular.org.pojo.param.SysOrgIdParam;
import com.nuocao.sys.modular.org.pojo.param.SysOrgPageParam;
import com.nuocao.sys.modular.org.pojo.result.SysOrgResult;
import com.nuocao.sys.modular.org.service.SysOrgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;


import org.noear.solon.validation.annotation.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 组织控制器
 */
@Api(tags = "组织控制器")
@Controller
@Valid
public class SysOrgController {

    @Inject
    private SysOrgService sysOrgService;

    /**
     * 获取组织分页
     */
    @ApiOperation("获取组织分页")
    @Get
    @SaCheckPermission("sys:org:query")
    @Mapping("/sys/org/page")
    public CommonResult<PageResult<SysOrg>> page(SysOrgPageParam sysOrgPageParam) {
        return CommonResult.data(sysOrgService.page(sysOrgPageParam));
    }

    /**
     * 获取组织树
     */
    @ApiOperation("获取组全部织树")
    @SaCheckPermission("sys:org:query")
    @Get@Mapping("/sys/org/treeAll")
    public CommonResult<List<SysOrgTreeResult>> treeAll() {
        return CommonResult.data(sysOrgService.treeAll());
    }

    /**
     * 获取组织树
     */
    @ApiOperation("获取组织树,我有权使用的")
    @Get@Mapping("/sys/org/tree")
    public CommonResult<List<SysOrgTreeResult>> tree() {
        return CommonResult.data(sysOrgService.tree());
    }

    /**
     * 添加组织
     */
    @ApiOperation("添加组织")
    @CommonLog("添加组织")
    @SaCheckPermission("sys:org:add")
    @Post
    @Mapping("/sys/org/add")
    public CommonResult<Long> add(@Validated SysOrgAddParam sysOrgAddParam) {

        return CommonResult.data(sysOrgService.add(sysOrgAddParam));
    }

    /**
     * 编辑组织
     */
    @ApiOperation("编辑组织")
    @CommonLog("编辑组织")
    @SaCheckPermission("sys:org:edit")
    @Post
    @Mapping("/sys/org/edit")
    public CommonResult<Void> edit(@Validated SysOrgEditParam sysOrgEditParam) {
        sysOrgService.edit(sysOrgEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除组织
     */
    @ApiOperation("删除组织")
    @CommonLog("删除组织")
    @SaCheckPermission("sys:org:delete")
    @Post
    @Mapping("/sys/org/delete")
    public CommonResult<Void> delete(@Validated @NotEmpty(message = "集合不能为空")
                                       CommonValidList<Long> ids) {
        sysOrgService.deleteByIds(ids);
        return CommonResult.ok();
    }

    /**
     * 获取组织详情
     */
    @ApiOperation("获取组织详情")
    @SaCheckPermission("sys:org:query")
    @Get
    @Mapping("/sys/org/detail")
    public CommonResult<SysOrgResult> detail(@Validated SysOrgIdParam sysOrgIdParam) {
        return CommonResult.data(sysOrgService.detail(sysOrgIdParam));
    }

    /**
     * 获取组织树选择器（通用接口）
     */
    @ApiOperation("获取组织树选择器")
    @Get
    @Mapping("/sys/org/selector")
    public CommonResult<List<SysOrgTreeResult>> orgTreeSelector() {
        return CommonResult.data(sysOrgService.orgTreeSelector());
    }
}
