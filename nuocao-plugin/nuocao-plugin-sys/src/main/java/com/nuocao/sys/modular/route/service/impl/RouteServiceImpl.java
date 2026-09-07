package com.nuocao.sys.modular.route.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;
import com.nuocao.sys.modular.menu.enums.SysIconTypeEnum;
import com.nuocao.sys.modular.menu.enums.SysMenuTypeEnum;
import com.nuocao.sys.modular.menu.service.SysMenuService;
import com.nuocao.sys.modular.route.result.MenuRouteResult;
import com.nuocao.sys.modular.route.result.UserRouteResult;
import com.nuocao.sys.modular.route.service.RouteService;
import com.nuocao.sys.modular.user.service.SysUserCenterService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 路由Service 实现
 */
@Component
public class RouteServiceImpl implements RouteService {

    @Inject
    private SysUserCenterService sysUserCenterService;
    @Inject
    private SysMenuService sysMenuService;

    @Override
    public UserRouteResult getLoginUserRouteResult() {
        UserRouteResult result = new UserRouteResult();
        List<MenuRouteResult> menuTree = getLoginUserRoutes();
        result.setRoutes(menuTree);
        String onePath = getOneMenuPath(menuTree);
        if(StringUtils.isNotBlank(onePath)){
            result.setHome(onePath);
        }
        return result;
    }

    /**
     * 获取第一个类型为menu的菜单路径
     * @param menuTree
     * @return
     */
    private String getOneMenuPath(List<MenuRouteResult> menuTree)
    {
        if (menuTree == null || menuTree.isEmpty()) {
            return null;
        }

        for (MenuRouteResult menu : menuTree) {
            // 如果没有子菜单，说明是叶子节点，直接返回路径
            if (menu.getChildren() == null || menu.getChildren().isEmpty()) {
                return menu.getPath();
            } else {
                // 如果有子菜单，递归进去找
                String path = getOneMenuPath(menu.getChildren());
                if (path != null) {
                    return path;
                }
            }
        }
        return null;
    }

    @Override
    public List<MenuRouteResult> getLoginUserRoutes() {
        List<SysMenuTreeResultDTO> menuTree=  sysUserCenterService.getLoginMenuTree();

        return convertRoutes(menuTree);
    }

    @Override
    public List<MenuRouteResult> getConstantRoutes() {

        return new ArrayList<>();
    }

    @Override
    public boolean isRouteExists(String routeName) {
        return sysMenuService.existsName(routeName);
    }

    /**
     * 将菜单tree转为前端路路由格式
     * @param menuTree
     * @return
     */
    private List<MenuRouteResult> convertRoutes(List<SysMenuTreeResultDTO> menuTree){
        List<MenuRouteResult> result = new ArrayList<>();
        if(CollectionUtils.isEmpty(menuTree)){
            return result;
        }
        for (SysMenuTreeResultDTO menu:menuTree
             ) {
            MenuRouteResult route = new MenuRouteResult();
            route.setName(menu.getName());
            route.setPath(menu.getPath());
            route.setComponent(menu.getComponent());
            List<SysMenuTreeResultDTO> subMenu = menu.getChildren()==null ?new ArrayList<>():menu.getChildren();

            //找到下级内容，这个buttonList可能在nuocao模式下并不需要，等弄完前端如果不需要就清理掉
            List<SysMenuTreeResultDTO> children = subMenu.stream().filter(o->!o.getType().equals(SysMenuTypeEnum.BUTTON.getType())).collect(Collectors.toList());
            //写入下级
            if(CollectionUtils.isNotEmpty(children)) {
                route.setChildren(convertRoutes(children));
            }
            route.setMeta(buildMeta(menu,subMenu));
            result.add(route);
        }
        return result;
    }

    private MenuRouteResult.MenuRouteMetaResult buildMeta(SysMenuTreeResultDTO menu,List<SysMenuTreeResultDTO> subMenu){
        List<SysMenuTreeResultDTO> buttonList = subMenu.stream().filter(o->o.getType().equals(SysMenuTypeEnum.BUTTON.getType())).collect(Collectors.toList());
        MenuRouteResult.MenuRouteMetaResult meta =BeanUtils.toBean(menu,MenuRouteResult.MenuRouteMetaResult.class,(o)->{
            o.setOrder(menu.getSortCode());
            if(menu.getIconType().equals(SysIconTypeEnum.LOCAL.getType())){
                o.setLocalIcon(o.getIcon());
                o.setIcon("");
            }
        });

//        if (CollectionUtils.isNotEmpty(buttonList)) {
//            meta.setButtons(buttonList.stream()
//                    .map(m -> new MenuRouteResult.MenuRouteButtonResult(m.getCode(), m.getTitle()))
//                    .collect(Collectors.toList()));
//        }else{
//            meta.setButtons(new ArrayList<>());
//        }
        return meta;
    }
}
