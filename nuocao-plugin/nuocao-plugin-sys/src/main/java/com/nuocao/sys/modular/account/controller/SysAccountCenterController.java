package com.nuocao.sys.modular.account.controller;

import com.nuocao.common.annotation.PermitAll;
import com.nuocao.common.page.CommonPicValidCodeResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.utils.DesensitizationUtils;
import com.nuocao.data.access.tenant.annotation.TenantIgnore;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaBindInitDTO;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaStatusDTO;
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

    @ApiOperation("获取当前用户2FA状态")
    @Get
    @Mapping("/sys/accountCenter/mfa/status")
    public CommonResult<DevMfaStatusDTO> mfaStatus() {
        return CommonResult.data(accountCenterService.mfaStatus());
    }

    @ApiOperation("发起2FA绑定")
    @Post
    @Mapping("/sys/accountCenter/mfa/bindInit")
    public CommonResult<DevMfaBindInitDTO> mfaBindInit() {
        return CommonResult.data(accountCenterService.mfaBindInit());
    }

    @ApiOperation("确认2FA绑定")
    @Post
    @Mapping("/sys/accountCenter/mfa/bindConfirm")
    public CommonResult<Void> mfaBindConfirm(@Validated SysAccountMfaBindConfirmParam param) {
        accountCenterService.mfaBindConfirm(param);
        return CommonResult.ok();
    }

    /**
     * 登录流程中用户尚未绑定 2FA，凭一次性 mfaReqId 发起绑定，无需登录态
     */
    @ApiOperation("登录时发起2FA绑定")
    @Post
    @PermitAll
    @TenantIgnore
    @Mapping("/sys/accountCenter/mfa/bindInitByLogin")
    public CommonResult<DevMfaBindInitDTO> mfaBindInitByLogin(@Validated SysAccountMfaBindLoginParam param) {
        return CommonResult.data(accountCenterService.mfaBindInitByLogin(param));
    }

    /**
     * 登录流程中确认绑定，成功后前端回到登录页重新登录
     */
    @ApiOperation("登录时确认2FA绑定")
    @Post
    @PermitAll
    @TenantIgnore
    @Mapping("/sys/accountCenter/mfa/bindConfirmByLogin")
    public CommonResult<Void> mfaBindConfirmByLogin(@Validated SysAccountMfaBindLoginConfirmParam param) {
        accountCenterService.mfaBindConfirmByLogin(param);
        return CommonResult.ok();
    }

}
