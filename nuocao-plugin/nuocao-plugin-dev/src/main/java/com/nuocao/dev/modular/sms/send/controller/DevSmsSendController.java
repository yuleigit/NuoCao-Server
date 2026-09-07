package com.nuocao.dev.modular.sms.send.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.dev.modular.sms.DevSmsHelper;
import com.nuocao.dev.modular.sms.log.service.DevSmsLogService;
import com.nuocao.dev.modular.sms.send.params.DevSmsSendParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

/**
 * 短信发送 控制器
 **/
@Api(tags = "短信日志控制器")
@Controller
@Valid
public class DevSmsSendController {

    @Inject
    private DevSmsLogService devSmsLogService;
    @Inject
    private DevSmsHelper devSmsHelper;

    /**
     * 使用指定通道发送短信
     */
    @ApiOperation("使用指定通道发送短信")
    @Post
    @SaCheckPermission("dev:sms:send")
    @Mapping("/dev/sms/send/{channelCode}")
    public CommonResult<String> send(
            @Path String channelCode,
            @Validated DevSmsSendParam sendParam) {
        devSmsHelper.sendSms(sendParam.getPhoneNumber(),channelCode,sendParam.getTemplateCode(),sendParam.getParams());
        return CommonResult.ok();
    }


}
