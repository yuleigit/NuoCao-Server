package com.nuocao.auth.modular.login.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.auth.api.dto.SaUserLoginInfoDTO;
import com.nuocao.auth.modular.login.param.common.AuthGetPhoneValidCodeParam;
import com.nuocao.auth.modular.login.param.login.AuthAccountPasswordLoginParam;
import com.nuocao.auth.modular.login.param.login.AuthPhoneValidCodeLoginParam;
import com.nuocao.auth.modular.login.param.login.AuthSwitchTenantParam;
import com.nuocao.auth.modular.login.param.reg.AuthRegisterParam;
import com.nuocao.auth.modular.login.result.login.AuthLoginResult;
import com.nuocao.auth.modular.login.service.AuthAccountUserService;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.page.CommonPicValidCodeResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.data.access.datascope.annotation.DataScopeIgnore;
import com.nuocao.data.access.tenant.util.TenantUtils;
import com.nuocao.sys.api.SysAccountApi;
import com.nuocao.sys.api.SysTenantApi;
import com.nuocao.sys.api.dto.SysTenantDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.util.List;

/**
 * 账号登录 控制器
 * 目前系统中时基于account登录+user使用的模式，
 * 关于密码为了方便用户改为只有user一层用户登录模式， * 所以所以在设计时将密码存了两份，实际使用中自行扩展，是删除还是保留
 */
@Api(tags = "账号登录 控制器")
@Controller
@Valid
@DataScopeIgnore
public class AuthController {

    @Inject
    private AuthAccountUserService authAccountUserService;
    @Inject
    private SysTenantApi tenantApi;
    @Inject
    private SysAccountApi sysAccountApi;
    @Inject

    /**
     * 获取账户登录验证码
     **/
    @ApiOperation("账户登录获取图片验证码")
    @Get
    @SaIgnore
    @Mapping("/auth/account/getPicCaptcha")
    public CommonResult<CommonPicValidCodeResult> getPicCaptcha() {
        return CommonResult.data(authAccountUserService.getPicCaptcha());
    }

    /**
     * 账户登录获取手机验证码
     **/
    @ApiOperation("账户登录获取手机验证码")
    @Get
    @Mapping("/auth/account/getPhoneValidCode")
    public CommonResult<String> getPhoneValidCode(@Validated AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam) {
        return CommonResult.data(authAccountUserService.getPhoneLoginValidCode(authGetPhoneValidCodeParam));
    }

    /**
     * 账户登录账号密码登录
     **/
    @ApiOperation("账户登录账号密码登录")
    @Post
    @Mapping("/auth/account/login")
    public CommonResult<AuthLoginResult> doLogin(@Validated AuthAccountPasswordLoginParam authAccountPasswordLoginParam) {
        return CommonResult.data(authAccountUserService.doLogin(authAccountPasswordLoginParam));
    }

    /**
     * 账户登录手机验证码登录
     **/
    @ApiOperation("账户登录手机验证码登录")
    @Post
    @Mapping("/auth/account/loginByPhone")
    public CommonResult<AuthLoginResult> doLoginByPhone(@Validated AuthPhoneValidCodeLoginParam authPhoneValidCodeLoginParam) {
        AuthLoginResult result = authAccountUserService.doLoginByPhone(authPhoneValidCodeLoginParam);
        return CommonResult.data(result);
    }

    /**
     * 账户登录退出
     **/
    @ApiOperation("账户登录退出")
    @SaCheckLogin
    @Get
    @Mapping("/auth/account/logout")
    public CommonResult<String> doLogout() {
        StpUtil.logout();
        return CommonResult.ok();
    }


    /**
     * 账号注册获取手机验证码
     **/
    @ApiOperation("账号注册获取手机验证码")
    @Get
    @Mapping("/auth/account/getPhoneRegValidCode")
    public CommonResult<String> getPhoneRegValidCode(@Validated AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam) {
        return CommonResult.data(authAccountUserService.getPhoneRegValidCode(authGetPhoneValidCodeParam));
    }


    /**
     * 账号注册
     **/
    @ApiOperation(value = "账号注册",position = 9)
    @Post
    @Mapping("/auth/account/register")
    public CommonResult<String> register(@Validated AuthRegisterParam authRegisterParam) {
        authAccountUserService.register(authRegisterParam);
        return CommonResult.ok();
    }

    /**
     * 获取登录用户信息（获取的为当前租户的，每个租户的登录状态单独存储，只是用统一的account登录
     * 因为时使用租户信息，所以使用user前缀
     **/
    @ApiOperation(value = "获取登录用户信息",notes = "获取的为当前租户的，每个租户的登录状态单独存储，只是用统一的account登录",position = 9)
    @Get
    @SaCheckLogin
    @Mapping("/auth/user/getLoginUser")
    public CommonResult<SaUserLoginInfoDTO> getLoginUser() {
        return CommonResult.data(authAccountUserService.getLoginUser());
    }


    /**
     * 切换租户
     **/
    @ApiOperation(value = "切换租户,登录成功后的用户可用")
    @Post
    @Mapping("/auth/user/switchTenant")
    public CommonResult<AuthLoginResult> switchTenant(@Validated AuthSwitchTenantParam switchParam) {
        SaUserLoginInfoDTO loginUser= StpLoginUserUtils.getLoginUser();
        CommonExceptionI18n.notNull(loginUser,"auth.user.switchTenant.notLogin");
        List<SysTenantDTO> tenantList = tenantApi.getAccountTenantList(loginUser.getUsername());
        CommonExceptionI18n.notEmpty(tenantList,"auth.user.switchTenant.notTenant");
        SysTenantDTO toTenant = tenantList.stream().filter(o->o.getId().equals(switchParam.getTenantId())).findFirst().orElse(null);
        CommonException.notNull(toTenant,"auth.user.switchTenant.notExists");
        AuthLoginResult loginResult = TenantUtils.execute(toTenant.getId(),()->{
            return authAccountUserService.doLoginUser(loginUser.getUsername(),
                    switchParam.getDevice() ,switchParam.getClientId(),null);
        });
        return CommonResult.data(loginResult);
    }


}
