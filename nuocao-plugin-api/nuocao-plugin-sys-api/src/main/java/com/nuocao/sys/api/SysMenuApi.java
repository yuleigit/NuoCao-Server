package com.nuocao.sys.api;

import com.nuocao.sys.api.dto.menu.SysMenuDTO;
import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;

import java.util.List;

/**
 * 系统菜单 API
 *
 **/
public interface SysMenuApi {


    /**
     * 获取菜单tree
     * @param topId
     * @return
     */
    List<SysMenuTreeResultDTO> getMenuTree(Long topId);

    default List<SysMenuTreeResultDTO> getMenuTree(){
        return getMenuTree(null);
    }

    /**
     * 获取上级分类名称路径，缓存读取
     * @param parentId
     * @return
     */
    String getTitlePathByCache(Long parentId);


    /**
     * 获取所有，菜单、按钮集合
     * @param menuIds
     * @return
     */
    List<SysMenuDTO> getMenuDtoList(List<Long> menuIds);

    /**
     * 获取所有菜单权限code列表
     * @param menuIds
     * @return
     */
    List<String> getMenuCodeList(List<Long> menuIds);

    /**
     * 生成代码是自动生成菜单
     * @param parentId 上级菜单ID
     * @param businessName 业务名，如user
     * @param modelName 模块名，如sys
     * @param functionName 功能名，如 用户
     * @param path 路由路径
     * @return
     */
    Long addMenuForGen(Long parentId,String businessName,String modelName,String functionName,String path);


    /**
     *
     * @param menuId
     * @param businessName
     * @param modelName
     * @param functionName
     * @return
     */
    Long addButtonFormGen(Long menuId,String businessName,String modelName,String functionName);


}
