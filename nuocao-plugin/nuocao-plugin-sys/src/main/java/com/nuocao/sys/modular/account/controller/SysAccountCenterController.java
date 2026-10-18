package com.nuocao.sys.modular.account.controller;

import com.nuocao.common.page.CommonPicValidCodeResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.utils.DesensitizationUtils;
import com.nuocao.sys.modular.account.param.*;
import com.nuocao.sys.modular.account.result.SysAccountDetailResult;
import com.nuocao.sys.modular.account.service.SysAccountCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

/**
 * 账户中心 控制器
 **/
@Api(tags = "账户中心控制器")
@Controller
@Valid
public class SysAccountCenterController {

    @Inject
    SysAccountCenterService accountCenterService;

    @ApiOperation("获取图片验证码")
    @Get
    @Mapping("/sys/accountCenter/getPicCaptcha")
    public CommonResult<CommonPicValidCodeResult> getPicCaptcha() {
        return CommonResult.data(accountCenterService.getPicCaptcha());
    }


    @ApiOperation("获取修改密码手机验证码")
    @Get
    @Mapping("/sys/accountCenter/updatePwdGetPhoneValidCode")
    public CommonResult<String> updatePwdGetPhoneValidCode(@Validated SysAccountUpdatePwdGetPhoneValidCodeParam param) {
        return CommonResult.data(accountCenterService.updatePasswordGetPhoneValidCode(param));
    }
    @ApiOperation("通过手机验证码修改手密码")
    @Post
    @Mapping("/sys/accountCenter/updatePwdByPhone")
    public CommonResult<Void> updatePasswordByPhone(@Validated SysAccountUpdatePwdByPhoneParam param) {
        accountCenterService.updatePasswordByPhone(param);
        return CommonResult.ok();
    }

    @ApiOperation("获取绑定手机号验证码")
    @Get
    @Mapping("/sys/accountCenter/bindPhoneGetValidCode")
    public CommonResult<String> BuildPhoneGetValidCode(@Validated SysAccountBindPhoneGetValidCodeParam param) {
        return CommonResult.data(accountCenterService.bindPhoneGetPhoneValidCode(param));
    }
    @ApiOperation("绑定新手机号")
    @Post
    @Mapping("/sys/accountCenter/bindPhone")
    public CommonResult<Void> getPicCaptcha(@Validated SysAccountBindPhoneParam param) {
        accountCenterService.bindPhone(param);
        return CommonResult.ok();
    }


    @ApiOperation("使用旧密码修改登录账号")
    @Post
    @Mapping("/sys/accountCenter/updatePwdByOld")
    public CommonResult<Void> updatePwdByOld(@Validated SysAccountUpdatePwdByOldParam param) {
        accountCenterService.updatePasswordByOld(param);
        return CommonResult.ok();
    }

    @ApiOperation("使用旧密码修改登录账号")
    @Get
    @Mapping("/sys/accountCenter/detail")
    public CommonResult<SysAccountDetailResult> detail() {
        SysAccountDetailResult account = accountCenterService.detail();
        account.setPhone(DesensitizationUtils.desensitizePhone(account.getPhone()));
        return CommonResult.data(account);
    }

}
