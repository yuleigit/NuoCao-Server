package com.nuocao.auth.modular.login.controller;

import com.nuocao.auth.modular.login.param.login.AuthMfaLoginParam;
import com.nuocao.auth.modular.login.result.login.AuthLoginResult;
import com.nuocao.auth.modular.login.service.AuthAccountUserService;
import com.nuocao.common.annotation.PermitAll;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.data.access.tenant.annotation.TenantIgnore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

/**
 * 2FA 动态码登录 控制器
 * <p>该接口属于登录流程的一部分，调用前必须已经通过账号密码校验拿到 mfaReqId。
 **/
@Api(tags = "2FA动态码登录 控制器")
@Controller
@Valid
public class AuthMfaController {

    @Inject
    private AuthAccountUserService authAccountUserService;

    /**
     * 校验 2FA 动态码并签发 token
     */
    @ApiOperation("2FA动态码登录验证")
    @Post
    @PermitAll
    @TenantIgnore
    @Mapping("/auth/mfa/loginVerify")
    public CommonResult<AuthLoginResult> loginVerify(@Validated AuthMfaLoginParam param) {
        return CommonResult.data(authAccountUserService.doMfaLogin(param));
    }
}
