package com.nuocao.sys.modular.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.pojo.CommonValidList;
import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;
import com.nuocao.sys.modular.user.entity.SysUser;
import com.nuocao.sys.modular.user.enums.SysUserSourceFromTypeEnum;
import com.nuocao.sys.modular.user.service.SysUserCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
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



}
