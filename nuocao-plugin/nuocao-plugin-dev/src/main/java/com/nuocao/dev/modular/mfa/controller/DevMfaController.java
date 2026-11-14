package com.nuocao.dev.modular.mfa.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.dev.modular.mfa.param.DevMfaOwnerParam;
import com.nuocao.dev.modular.mfa.param.DevMfaPageParam;
import com.nuocao.dev.modular.mfa.result.DevMfaPageResult;
import com.nuocao.dev.modular.mfa.service.DevMfaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

/**
 * 2FA 绑定管理控制器（管理端）
 **/
@Api(tags = "2FA绑定管理控制器")
@Controller
@Valid
public class DevMfaController {

    @Inject
    private DevMfaService devMfaService;

    /**
     * 获取绑定分页
     */
    @ApiOperation("获取2FA绑定分页")
    @Get
    @SaCheckPermission("dev:mfa:query")
    @Mapping("/dev/mfa/page")
    public CommonResult<PageResult<DevMfaPageResult>> page(DevMfaPageParam param) {
        return CommonResult.data(devMfaService.page(param));
    }

    /**
     * 获取绑定详情
     */
    @ApiOperation("获取2FA绑定详情")
    @Get
    @SaCheckPermission("dev:mfa:query")
    @Mapping("/dev/mfa/detail")
    public CommonResult<DevMfaPageResult> detail(@Validated DevMfaOwnerParam param) {
        return CommonResult.data(devMfaService.detail(param));
    }

    /**
     * 重置失败次数并解锁
     */
    @ApiOperation("重置2FA失败次数并解锁")
    @Post
    @CommonLog("重置2FA失败次数")
    @SaCheckPermission("dev:mfa:reset")
    @Mapping("/dev/mfa/reset")
    public CommonResult<?> reset(@Validated DevMfaOwnerParam param) {
        devMfaService.reset(param);
        return CommonResult.ok();
    }

    /**
     * 失效绑定
     */
    @ApiOperation("失效2FA绑定")
    @Post
    @CommonLog("失效2FA绑定")
    @SaCheckPermission("dev:mfa:disable")
    @Mapping("/dev/mfa/disable")
    public CommonResult<?> disable(@Validated DevMfaOwnerParam param) {
        devMfaService.disable(param);
        return CommonResult.ok();
    }

    /**
     * 删除绑定
     */
    @ApiOperation("删除2FA绑定")
    @Post
    @CommonLog("删除2FA绑定")
    @SaCheckPermission("dev:mfa:delete")
    @Mapping("/dev/mfa/delete")
    public CommonResult<?> delete(@Validated DevMfaOwnerParam param) {
        devMfaService.delete(param);
        return CommonResult.ok();
    }
}
