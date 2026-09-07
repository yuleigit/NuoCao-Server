package com.nuocao.sys.modular.route.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户菜单路由返回实体
 * 提供给soybean解析的
 */
@Setter
@Getter
public class UserRouteResult {

    private List<MenuRouteResult> routes;

    /**
     * 首页的路由名
     * 有这个可以在多模块扩展时，直接传入进入新模块时候需要默认打开的页面内容
     * 目前只是为了兼容soybean的接口
     */
    public String home="home";

}
