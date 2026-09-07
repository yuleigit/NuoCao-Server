package com.nuocao.sys.modular.tenantpackage.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.pojo.CommonValidList;
import com.nuocao.sys.modular.tenantpackage.entity.SysTenantPackage;
import com.nuocao.sys.modular.tenantpackage.param.SysTenantPackageAddParam;
import com.nuocao.sys.modular.tenantpackage.param.SysTenantPackageEditParam;
import com.nuocao.sys.modular.tenantpackage.param.SysTenantPackageIdParam;
import com.nuocao.sys.modular.tenantpackage.param.SysTenantPackagePageParam;
import com.nuocao.sys.modular.tenantpackage.service.SysTenantPackageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.NotEmpty;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.util.List;

/**
 * 租户套餐控制器
 */
@Api(tags = "套餐控制器")
@Controller
@Valid
public class SysTenantPackageController {

    @Inject
    private SysTenantPackageService sysTenantPackageService;

    /**
     * 获取租户套餐分页
     */
    @ApiOperation("获取租户套餐分页")
    @SaCheckPermission("/sys/tenantpackage/page")
    @Get
    @Mapping("/sys/tenantpackage/page")
    public CommonResult<PageResult<SysTenantPackage>> page(SysTenantPackagePageParam sysTenantPackagePageParam) {
        return CommonResult.data(sysTenantPackageService.page(sysTenantPackagePageParam));
    }

    /**
     * 添加租户套餐
     */
    @ApiOperation("添加租户套餐")
    @CommonLog("添加租户套餐")
    @SaCheckPermission("/sys/tenantpackage/add")
    @Post
    @Mapping("/sys/tenantpackage/add")
    public CommonResult<String> add(SysTenantPackageAddParam sysTenantPackageAddParam) {
        sysTenantPackageService.add(sysTenantPackageAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑租户套餐
     */
    @ApiOperation("编辑租户套餐")
    @CommonLog("编辑租户套餐")
    @SaCheckPermission("/sys/tenantpackage/edit")
    @Post
    @Mapping("/sys/tenantpackage/edit")
    public CommonResult<String> edit(SysTenantPackageEditParam sysTenantPackageEditParam) {
        sysTenantPackageService.edit(sysTenantPackageEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除租户套餐
     */
    @ApiOperation("删除租户套餐")
    @CommonLog("删除租户套餐")
    @SaCheckPermission("sys:tenant:package:delete")
    @Post
    @Mapping("/sys/tenantpackage/delete")
    public CommonResult<String> delete(@Validated @NotEmpty(message = "集合不能为空")
                                       List<SysTenantPackageIdParam> sysTenantPackageIdParamList) {
        sysTenantPackageService.delete(sysTenantPackageIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取租户套餐详情
     */
    @ApiOperation("获取租户套餐详情")
    @SaCheckPermission("/sys/tenantpackage/detail")
    @Get
    @Mapping("/sys/tenantpackage/detail")
    public CommonResult<SysTenantPackage> detail(@Validated SysTenantPackageIdParam sysTenantPackageIdParam) {
        return CommonResult.data(sysTenantPackageService.detail(sysTenantPackageIdParam));
    }



    /**
     * 获取套餐选择器
     */
    @ApiOperation("获取用户选择器")
    @Get@Mapping("/sys/tenantpackage/selector")
    public CommonResult<List<SysTenantPackage>> selector() {
        return CommonResult.data(sysTenantPackageService.getSelector());
    }


}
