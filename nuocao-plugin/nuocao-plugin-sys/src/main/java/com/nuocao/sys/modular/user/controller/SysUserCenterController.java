package com.nuocao.sys.modular.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.tree.Tree;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.pojo.CommonValidList;
import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;
import com.nuocao.sys.modular.user.entity.SysUser;
import com.nuocao.sys.modular.user.enums.SysUserSourceFromTypeEnum;
import com.nuocao.sys.modular.user.pojo.param.SysUserUpdateInfoParam;
import com.nuocao.sys.modular.user.pojo.param.SysUserUpdatePwdByOldParam;
import com.nuocao.sys.modular.user.pojo.param.SysUserUpdateWorkbenchParam;
import com.nuocao.sys.modular.user.pojo.result.SysUserResult;
import com.nuocao.sys.modular.user.service.SysUserCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.validation.annotation.NotEmpty;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.io.IOException;
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


}
