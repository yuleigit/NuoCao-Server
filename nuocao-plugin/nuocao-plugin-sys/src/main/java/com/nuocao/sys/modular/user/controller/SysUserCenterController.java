package com.nuocao.sys.modular.user.controller;

import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.utils.NumberUtils;
import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.sys.modular.user.pojo.param.SysUserMessagePageParam;
import com.nuocao.sys.modular.user.pojo.param.SysUserMessageSendParam;
import com.nuocao.sys.modular.user.pojo.param.SysUserUpdateInfoParam;
import com.nuocao.sys.modular.user.pojo.param.SysUserUpdatePwdByOldParam;
import com.nuocao.sys.modular.user.pojo.result.SysUserResult;
import com.nuocao.sys.modular.user.service.SysUserCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nuocao.plugin.dev.api.DevMessageApi;
import nuocao.plugin.dev.api.dto.message.DevMessagePageParamDTO;
import nuocao.plugin.dev.api.dto.message.DevMessageResultDTO;
import nuocao.plugin.dev.api.dto.message.DevMessageSendParamDTO;
import nuocao.plugin.dev.api.enums.message.DevMessageCategoryEnum;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;
import org.noear.solon.web.sse.SseEmitter;

import java.util.List;

/**
 * 用户中心控制器
 **/
@Api(tags = "用户中心控制器")
@Controller
@Valid
public class SysUserCenterController {

    @Inject
    private SysUserCenterService sysUserCenterService;
    @Inject
    private DevMessageApi devMessageApi;

    @ApiOperation("获取用户信息")
    @Get@Mapping("/sys/userCenter/getProfile")
    public CommonResult<SysUserResult> getProfile() {
        return CommonResult.data(sysUserCenterService.getProfile());
    }


    @ApiOperation("通过验证旧密码修改用户密码")
    @CommonLog("通过验证旧密码修改用户密码")
    @Post@Mapping("/sys/userCenter/updatePassword")
    public CommonResult<String> updatePasswordByOld(@Validated SysUserUpdatePwdByOldParam sysUserUpdatePwdByOldParam) {
        sysUserCenterService.updatePasswordByOld(sysUserUpdatePwdByOldParam);
        return CommonResult.ok();
    }



    @ApiOperation("修改用户头像")
    @CommonLog("修改用户头像")
    @Post
    @Mapping("/sys/userCenter/updateAvatar")
    public CommonResult<String> updateAvatar(@ApiParam(value="文件", required = true) UploadedFile file) {
        return CommonResult.data(sysUserCenterService.updateAvatar(file));
    }


    @ApiOperation("修改用户头像")
    @CommonLog("修改用户头像")
    @Post
    @Mapping("/sys/userCenter/updateAvatarBase64")
    public CommonResult<String> updateAvatar(@ApiParam(value="头像base64", required = true) String avatar) {
        return CommonResult.data(sysUserCenterService.updateAvatarByBase64(avatar));
    }


    @ApiOperation("编辑个人信息")
    @CommonLog("编辑个人信息")
    @Post
    @Mapping("/sys/userCenter/updateUserInfo")
    public CommonResult<String> updateUserInfo(@Validated SysUserUpdateInfoParam sysUserUpdateInfoParam) {
        sysUserCenterService.updateUserProfileInfo(sysUserUpdateInfoParam);
        return CommonResult.ok();
    }

    @ApiOperation("站内消息列表")
    @Get
    @Mapping("/sys/userCenter/message/page")
    public CommonResult<PageResult<DevMessageResultDTO>> messagePage(@Validated SysUserMessagePageParam pageParam) {
        DevMessagePageParamDTO paramDto = BeanUtils.toBean(pageParam, DevMessagePageParamDTO.class);
        paramDto.setReceiverUserId(StpLoginUserUtils.getLoginUserId());
        return CommonResult.data(devMessageApi.page(paramDto));
    }


    @ApiOperation("获取站内信详情")
    @Get
    @Mapping("/sys/userCenter/message/detail/{id}")
    public CommonResult<DevMessageResultDTO> messageDetail(Long id) {
        return CommonResult.data(devMessageApi.detail(id));
    }

    @ApiOperation("将自己信息全部设为已读")
    @Post
    @Mapping("/sys/userCenter/message/allRead")
    public CommonResult<Void> allRead() {
        devMessageApi.allMessageMarkRead(StpLoginUserUtils.getLoginUserId());
        return CommonResult.ok();
    }


    @ApiOperation("获取未读消息列表（最新多少个）")
    @Get
    @Mapping("/sys/userCenter/message/list")
    public CommonResult<List<DevMessageResultDTO>> messageList( Integer limit) {
        if(NumberUtils.isLeZero(limit)) limit=10;
        return CommonResult.data(devMessageApi.list(StpLoginUserUtils.getLoginUserId(),limit));
    }

    @ApiOperation("创建sse连接")
    @Get
    @Mapping("/sys/userCenter/message/createSseConnect")
    public SseEmitter createSseConnect(String clientId){
        return sysUserCenterService.createSseConnect(clientId);
    }


    @ApiOperation("将自己信息全部设为已读")
    @Post
    @Mapping("/sys/userCenter/message/send")
    public CommonResult<Void> send(@Validated SysUserMessageSendParam sendParam) {
        DevMessageSendParamDTO param = DevMessageSendParamDTO.builder().title(sendParam.getTitle())
                        .content(sendParam.getContent())
                                .senderUserId(StpLoginUserUtils.getLoginUserId())
                                        .receiverIdList(sendParam.getReceiverIdList())
                                                .category(DevMessageCategoryEnum.MSG).build();
        devMessageApi.sendMessage(param);
        return CommonResult.ok();
    }

}
