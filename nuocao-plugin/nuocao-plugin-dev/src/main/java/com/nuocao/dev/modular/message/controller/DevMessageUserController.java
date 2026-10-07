package com.nuocao.dev.modular.message.controller;

import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.utils.NumberUtils;
import com.nuocao.dev.modular.message.pojo.param.DevMessageListParam;
import com.nuocao.dev.modular.message.pojo.param.DevMessagePageParam;
import com.nuocao.dev.modular.message.pojo.param.DevMessageSendParam;
import com.nuocao.dev.modular.message.pojo.result.DevMessageDetailResult;
import com.nuocao.dev.modular.message.pojo.result.DevMessageReceiverResult;
import com.nuocao.dev.modular.message.pojo.result.DevMessageSenderResult;
import com.nuocao.dev.modular.message.service.DevMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nuocao.plugin.dev.api.DevMessageApi;
import nuocao.plugin.dev.api.dto.message.DevMessageResultDTO;
import nuocao.plugin.dev.api.dto.message.DevMessageSendParamDTO;
import nuocao.plugin.dev.api.enums.message.DevMessageCategoryEnum;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;
import org.noear.solon.web.sse.SseEmitter;

import java.util.List;


/**
 * 用户站内信控制器[用户用]
 **/
@Api(tags = "用户站内信控制器")
@Controller
@Valid
public class DevMessageUserController {

    @Inject
    private DevMessageService devMessageService;

    @Inject
    private DevMessageApi messageApi;

    @ApiOperation("收到的站内信列表")
    @Get
    @Mapping("/dev/message-user/page")
    public CommonResult<PageResult<DevMessageReceiverResult>> receiverPage(@Validated DevMessagePageParam pageParam) {
        pageParam.setReceiverUserId(StpLoginUserUtils.getLoginUserId());
        return CommonResult.data(devMessageService.pageResult(pageParam));
    }


    @ApiOperation("获取站内信详情")
    @Get
    @Mapping("/dev/message-user/detail/{id}")
    public CommonResult<DevMessageReceiverResult> receiverDetail(Long id) {
        return CommonResult.data(devMessageService.detailReader(id));
    }

    @ApiOperation("将自己信息全部设为已读")
    @Post
    @Mapping("/dev/message-user/allRead")
    public CommonResult<Void> allRead() {
        messageApi.allMessageMarkRead(StpLoginUserUtils.getLoginUserId());
        return CommonResult.ok();
    }


    @ApiOperation("获取未读消息列表（最新多少个）")
    @Get
    @Mapping("/dev/message-user/list")
    public CommonResult<List<DevMessageReceiverResult>> messageList( Integer limit) {
        if(NumberUtils.isLeZero(limit)) limit=10;
        DevMessageListParam devMessageListParam = new DevMessageListParam();
        devMessageListParam.setReceiverId(StpLoginUserUtils.getLoginUserId());
        devMessageListParam.setLimit(limit);
        return CommonResult.data(devMessageService.list(devMessageListParam));
    }

    @ApiOperation("创建sse连接")
    @Get
    @Mapping("/dev/message-user/createSseConnect")
    public SseEmitter createSseConnect(String clientId){
        return devMessageService.createSseConnect(clientId);
    }


    @ApiOperation("将自己信息全部设为已读")
    @Post
    @Mapping("/dev/message-user/message/send")
    public CommonResult<Void> send(@Validated DevMessageSendParam sendParam) {
        sendParam.setCategory(DevMessageCategoryEnum.MSG.getValue());
        sendParam.setSenderUserId(StpLoginUserUtils.getLoginUserId());
        devMessageService.send(sendParam);
        return CommonResult.ok();
    }

    @ApiOperation("我发送的信息")
    @Get
    @Mapping("/dev/message-user/sendPage")
    public CommonResult<PageResult<DevMessageSenderResult>> sendPage(@Validated DevMessagePageParam pageParam) {
        pageParam.setSenderUserId(StpLoginUserUtils.getLoginUserId());
        return CommonResult.data(devMessageService.userSendPage(pageParam));
    }

    @ApiOperation("我发送消息详情")
    @Get
    @Mapping("/dev/message-user/sendDetail/{id}")
    public CommonResult<DevMessageSenderResult> sendDetail(Long id) {
        return CommonResult.data(devMessageService.detailSender(id));
    }

}
