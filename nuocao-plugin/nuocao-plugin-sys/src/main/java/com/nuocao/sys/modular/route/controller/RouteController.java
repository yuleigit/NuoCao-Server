package com.nuocao.sys.modular.route.controller;

import com.nuocao.common.pojo.CommonResult;
import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;
import com.nuocao.sys.modular.role.service.SysRoleService;
import com.nuocao.sys.modular.route.result.MenuRouteResult;
import com.nuocao.sys.modular.route.result.UserRouteResult;
import com.nuocao.sys.modular.route.service.RouteService;
import com.nuocao.sys.modular.user.service.SysUserCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.validation.annotation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路由控制器
 * 根据用户权限获取该用户可用路由
 **/
@Api(tags = "路由控制器")
@Controller
@Valid
public class RouteController {

    @Inject
    private RouteService routeService;


    /**
     * 获取用户可用菜单
     */
    @ApiOperation("获取用户可用菜单")
    @Get
    @Mapping("/route/getUserRoutes")
    public CommonResult<UserRouteResult> getUserRoutes() {

        return CommonResult.data(routeService.getLoginUserRouteResult());
    }


    /**
     * 获取静态路由，静态路由无需权限，为通用
     */
    @ApiOperation("获取静态路由")
    @Get
    @Mapping("/route/getConstantRoutes")
    public CommonResult<List<MenuRouteResult>> getConstantRoutes() {
        return CommonResult.data(routeService.getConstantRoutes());
    }

    /**
     * 获取静态路由，静态路由无需权限，为通用
     */
    @ApiOperation("获取静态路由")
    @Get
    @Mapping("/route/isRouteExist")
    public CommonResult<Boolean> isRouteExist(String routeName) {
        return CommonResult.data(routeService.isRouteExists(routeName));
    }
}
