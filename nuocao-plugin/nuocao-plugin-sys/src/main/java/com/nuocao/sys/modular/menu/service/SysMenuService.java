package com.nuocao.sys.modular.menu.service;

import com.nuocao.common.page.PageResult;
import com.nuocao.sys.api.dto.menu.SysMenuDTO;
import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;
import com.nuocao.sys.modular.menu.entity.SysMenu;

import com.nuocao.sys.modular.menu.pojo.SysMenuButton;
import com.nuocao.sys.modular.menu.pojo.params.*;
import com.nuocao.sys.modular.menu.pojo.result.SysMenuDetailResult;

import java.util.List;

/**
 * 菜单Service接口
 **/
public interface SysMenuService {


    /**
     * 获取菜单分页
     *
     */
    PageResult<SysMenu> page(SysMenuPageParam pageParam);

    /**
     * 获取菜单树，所欲的，不光是菜单
     * @return 含按钮及
     */
    List<SysMenuTreeResultDTO> tree(SysMenuTreeParam treeParam);

    /**
     * 获取菜单树,不含按钮
     * @return 含按钮及
     */
    List<SysMenuTreeResultDTO> treeMenu(SysMenuTreeParam treeParam);

    /**
     * 获取菜单树,去掉按钮级的只保留可用目录、菜单
     * @param menuIds 含有所有需要读取的菜单ID
     * @return 含按钮及
     */
    List<SysMenuTreeResultDTO> treeByIds(List<Long> menuIds);

    List<SysMenuTreeResultDTO> treeSelector(SysMenuTreeParam treeParam);


    List<SysMenuTreeResultDTO> buildTree(List<SysMenu> menuList);

    /**
     * 添加菜单

     */
    Long add(SysMenuAddParam addParam);


    /**
     * 编辑菜单
     */
    Long edit(SysMenuEditParam sysMenuEditParam);

    /**
     * 删除菜单
     *
     */
    void deleteByIds(List<Long> menuIds);


    /**
     * 删除菜单
     *
     */
    void delete(List<SysMenuIdParam> menuIdParams);

    /**
     * 获取菜单详情
     */
    SysMenuDetailResult detail(SysMenuIdParam menuIdParam);

    /**
     * 获取菜单详情
     */
    SysMenu queryEntity(Long menuId);
    /**
     * 获取所有下级id集合
     * @param menuId
     * @return
     */
    List<Long> getMenuChildIds(Long menuId);


    /**
     * 获得所有菜单DTO集合
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
     * 获取菜单ID集合
     * @return
     */
    List<Long> getAllMenuIdList();

    /**
     * 获取所有菜单权限code集合
     * @return
     */
    List<String> getAllMenuCodeList();

    /**
     * 菜单名是否存在
     * @param name
     * @return
     */
    boolean existsName(String name);

    /**
     * 获取上级分类路径
     * @param parentId
     * @return
     */
    String getTitlePathByCache(Long parentId);





}
