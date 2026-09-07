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
}
