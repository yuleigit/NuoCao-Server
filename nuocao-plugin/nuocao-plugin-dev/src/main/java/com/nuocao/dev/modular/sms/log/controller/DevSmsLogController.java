package com.nuocao.dev.modular.sms.log.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.pojo.CommonValidList;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.dev.modular.sms.log.entity.DevSmsLog;
import com.nuocao.dev.modular.sms.log.param.DevSmsLogIdParam;
import com.nuocao.dev.modular.sms.log.param.DevSmsLogPageParam;
import com.nuocao.dev.modular.sms.log.service.DevSmsLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import org.noear.solon.validation.annotation.NotEmpty;

/**
 * 短信日志控制器
 **/
@Api(tags = "短信日志控制器")
@Controller
@Valid
public class DevSmsLogController {

    @Inject
    private DevSmsLogService devSmsLogService;

    /**
     * 获取短信日志分页
     */
    @ApiOperation("获取短信日志分页")
    @Get
    @SaCheckPermission("dev:sms:log:query")
    @Mapping("/dev/sms/log/page")
    public CommonResult<PageResult<DevSmsLog>> page(DevSmsLogPageParam pageParam) {
        return CommonResult.data(devSmsLogService.page(pageParam));
    }

    /**
     * 删除短信日志
     */
    @ApiOperation("删除短信日志")
    @CommonLog("删除短信日志")
    @SaCheckPermission("dev:sms:log:delete")
    @Post
    @Mapping("/dev/sms/log/delete")
    public CommonResult<String> delete(@Validated @NotEmpty(message = "集合不能为空")
                                       CommonValidList<DevSmsLogIdParam> idParams) {
        devSmsLogService.deleteByIds(CollectionUtils.toList(idParams,DevSmsLogIdParam::getId));
        return CommonResult.ok();
    }

    /**
     * 获取短信日志详情
     */
    @ApiOperation("获取短信日志详情")
    @Get
    @SaCheckPermission("dev:sms:log:query")
    @Mapping("/dev/sms/log/detail")
    public CommonResult<DevSmsLog> detail(@Validated DevSmsLogIdParam idParam) {
        return CommonResult.data(devSmsLogService.detail(idParam));
    }
}
