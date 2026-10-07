package com.nuocao.auth.modular.login.controller;

import com.nuocao.data.access.datascope.annotation.DataScopeIgnore;
import io.swagger.annotations.Api;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.Valid;

/**
 * 账号登录 控制器
 */
@Api(tags = "账号登录 控制器")
@Controller
@Valid
@DataScopeIgnore
public class AuthMemberController {
    //可以自行扩转会员登录、client登录等，这个可以以account+member注册的形式，形成多变关系
    //目前系统中时基于account登录+user使用的模式，为了方便用户改为只有user一层用户登录模式，所以所以在设计时将密码存了两份，实际使用中自行扩展，是删除还是保留
}
