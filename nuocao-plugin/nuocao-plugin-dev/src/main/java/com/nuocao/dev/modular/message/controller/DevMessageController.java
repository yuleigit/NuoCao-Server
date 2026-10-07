package com.nuocao.dev.modular.message.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.pojo.CommonValidList;
import com.nuocao.dev.modular.message.entity.DevMessage;
import com.nuocao.dev.modular.message.pojo.param.DevMessageIdParam;
import com.nuocao.dev.modular.message.pojo.param.DevMessagePageParam;
import com.nuocao.dev.modular.message.pojo.param.DevMessageSendParam;
import com.nuocao.dev.modular.message.pojo.result.DevMessageDetailResult;
import com.nuocao.dev.modular.message.service.DevMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.NotEmpty;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;


/**
 * 站内信控制器[管理员用]
 **/
@Api(tags = "站内信控制器[管理员用]")
@Controller
@Valid
public class DevMessageController {

    @Inject
    private DevMessageService devMessageService;

    /**
     * 发送站内信
     */
    @ApiOperation("发送站内信")
    @CommonLog("发送站内信")
    @SaCheckPermission("dev:message:send")
    @Post
    @Mapping("/dev/message/send")
    public CommonResult<String> send(@Validated DevMessageSendParam devMessageSendParam) {
        devMessageService.send(devMessageSendParam);
        return CommonResult.ok();
    }

    /**
     * 获取站内信分页
     */
    @ApiOperation("获取站内信分页")
    @SaCheckPermission("dev:message:query")
    @Get
    @Mapping("/dev/message/page")
    public CommonResult<PageResult<DevMessage>> page(DevMessagePageParam devMessagePageParam) {
        return CommonResult.data(devMessageService.page(devMessagePageParam));
    }

    /**
     * 删除站内信
     */
    @ApiOperation("删除站内信")
    @CommonLog("删除站内信")
    @SaCheckPermission("dev:message:delete")
    @Post
    @Mapping("/dev/message/delete")
    public CommonResult<String> delete(@Validated @NotEmpty(message = "集合不能为空")
                                       CommonValidList<DevMessageIdParam> devMessageIdParamList) {
        devMessageService.delete(devMessageIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取站内信详情
     */
    @ApiOperation("获取站内信详情")
    @SaCheckPermission("dev:message:query")
    @Get@Mapping("/dev/message/detail")
    public CommonResult<DevMessageDetailResult> detail(@Validated DevMessageIdParam devMessageIdParam) {
        return CommonResult.data(devMessageService.detail(devMessageIdParam.getId()));
    }
}
