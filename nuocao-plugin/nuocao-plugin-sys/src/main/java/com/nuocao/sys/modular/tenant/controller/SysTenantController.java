package com.nuocao.sys.modular.tenant.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.config.SaTokenConfig;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.pojo.CommonValidList;
import com.nuocao.data.access.tenant.annotation.TenantIgnore;
import com.nuocao.sys.modular.tenant.entity.SysTenant;
import com.nuocao.sys.modular.tenant.param.SysTenantAddParam;
import com.nuocao.sys.modular.tenant.param.SysTenantEditParam;
import com.nuocao.sys.modular.tenant.param.SysTenantIdParam;
import com.nuocao.sys.modular.tenant.param.SysTenantPageParam;
import com.nuocao.sys.modular.tenant.reslut.SysTenantPageResult;
import com.nuocao.sys.modular.tenant.reslut.SysTenantViewResult;
import com.nuocao.sys.modular.tenant.service.SysTenantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.NotEmpty;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;
import org.noear.solon.web.cors.annotation.CrossOrigin;


import java.util.List;

/**
 * 客户机构控制器
 *
 * @author Lei
 * @date  2025/06/20 14:26
 */
@Api(tags = "客户机构控制器")
@Controller
@Valid
@TenantIgnore
public class SysTenantController {

    @Inject
    private SysTenantService sysTenantService;
    @Inject
    private SaTokenConfig saTokenConfig; // 注入配置类


//    @Init // Solon 初始化完成后执行
//    public void printSaTokenConfig() {
//        System.out.println("===== Sa-Token 配置 =====");
//        System.out.println("token-name: " + saTokenConfig.getTokenName());
//        System.out.println("timeout: " + saTokenConfig.getTimeout());
//        System.out.println("is-share: " + saTokenConfig.getIsShare());
//        System.out.println("token-prefix: " + saTokenConfig.getTokenPrefix());
//        System.out.println("is-read-cookie: " + saTokenConfig.getIsReadCookie());
//
//        // 正常输出应包含: SaTokenDaoRedis
//    }
    /**
     * 获取客户机构分页
     *
     * @author Lei
     * @date  2025/06/20 14:26
     */
    @ApiOperation("获取客户机构分页")
    @SaCheckPermission("sys:tenant:query")
    @Get
    @Mapping("/sys/tenant/page")
    public CommonResult<PageResult<SysTenantPageResult>> page(SysTenantPageParam sysTenantPageParam) {
        return CommonResult.data(sysTenantService.page(sysTenantPageParam));
    }

    /**
     * 添加客户机构
     *
     * @author Lei
     * @date  2025/06/20 14:26
     */
    @ApiOperation("添加客户机构")
    @SaCheckPermission("sys:tenant:add")
    @Post@Mapping("/sys/tenant/add")
    @CrossOrigin(origins = "*")
    public CommonResult<String> add( SysTenantAddParam sysTenantAddParam) {
        sysTenantService.add(sysTenantAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑客户机构
     *
     * @author Lei
     * @date  2025/06/20 14:26
     */
    @ApiOperation("编辑客户机构")
    @CommonLog("编辑客户机构")
    @SaCheckPermission("sys:tenant:edit")
    @Post
    @Mapping("/sys/tenant/edit")
    public CommonResult<String> edit(@Validated SysTenantEditParam sysTenantEditParam) {
        sysTenantService.edit(sysTenantEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除客户机构
     *
     * @author Lei
     * @date  2025/06/20 14:26
     */
    @ApiOperation("删除客户机构")
    @CommonLog("删除客户机构")
    @SaCheckPermission("sys:tenant:delete")
    @Post
    @Mapping("/sys/tenant/delete")
    public CommonResult<String> delete(@Validated @NotEmpty
                                       List<SysTenantIdParam> sysTenantIdParamList) {
        sysTenantService.delete(sysTenantIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取客户机构详情
     *
     * @author Lei
     * @date  2025/06/20 14:26
     */
    @ApiOperation("获取客户机构详情")
    @SaCheckPermission("sys:tenant:query")
    @Get
    @Mapping("/sys/tenant/detail")
    public CommonResult<SysTenant> detail(SysTenantIdParam sysTenantIdParam) {
        return CommonResult.data(sysTenantService.detail(sysTenantIdParam));
    }

    /**
     * 获取客户机构详情
     *
     * @author Lei
     * @date  2025/06/20 14:26
     */
    @ApiOperation("获得客户展示信息")
    @SaCheckPermission("sys:tenant:detail")
    @Get
    @Mapping("/sys/tenant/view")
    public CommonResult<SysTenantViewResult> view(SysTenantIdParam sysTenantIdParam) {
        return CommonResult.data(sysTenantService.viewStat(sysTenantIdParam));
    }



    /**
     * 获取套餐选择器
     */
    @ApiOperation("获取租户选择器")
    @Get@Mapping("/sys/tenant/get-selector")
    @TenantIgnore
    public CommonResult<List<SysTenantPageResult>> getSelector(SysTenantPageParam pageParam) {
        return CommonResult.data(sysTenantService.getSelectorAll(pageParam));
    }




}
