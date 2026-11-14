package com.nuocao.dev.modular.mfa.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.dev.modular.mfa.param.DevMfaEventPageParam;
import com.nuocao.dev.modular.mfa.result.DevMfaEventPageResult;
import com.nuocao.dev.modular.mfa.service.DevMfaEventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.validation.annotation.Valid;

/**
 * 2FA 操作事件控制器（管理端，只读）
 **/
@Api(tags = "2FA操作事件控制器")
@Controller
@Valid
public class DevMfaEventController {

    @Inject
    private DevMfaEventService devMfaEventService;

    /**
     * 获取操作事件分页
     */
    @ApiOperation("获取2FA操作事件分页")
    @Get
    @SaCheckPermission("dev:mfaEvent:query")
    @Mapping("/dev/mfaEvent/page")
    public CommonResult<PageResult<DevMfaEventPageResult>> page(DevMfaEventPageParam param) {
        return CommonResult.data(devMfaEventService.page(param));
    }
}
