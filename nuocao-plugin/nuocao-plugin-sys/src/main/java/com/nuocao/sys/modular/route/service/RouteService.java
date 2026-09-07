package com.nuocao.sys.modular.route.service;

import com.nuocao.sys.modular.route.result.MenuRouteResult;
import com.nuocao.sys.modular.route.result.UserRouteResult;

import java.util.List;

/**
 * 路由Service
 * 该模块主要结合用户权限得到用户可用路由返回给前端服务
 * 为了兼容Soybean，所以单独做一个
 */
public interface RouteService {


    /**
     * 获取登录用户的前端路由信息，返回实体
     * @return
     */
    UserRouteResult getLoginUserRouteResult();


    /**
     * 获取登录用户的前端路由信息列表
     * @return
     */
    List<MenuRouteResult> getLoginUserRoutes();



    /**
     * 获取所有静态路由，静态路由无需权限
     * @return
     */
    List<MenuRouteResult> getConstantRoutes();


    /**
     * 验证路由是否存在
     * @param name
     * @return
     */
    boolean isRouteExists(String routeName);


}
