package com.nuocao.sys.modular.menu.service;

import com.nuocao.sys.modular.menu.pojo.SysMenuButton;

import java.util.List;

/**
 * 菜单按钮 service
 */
public interface SysMenuButtonService {


    /**
     * 根据菜单ID获取包含的所有按钮
     * @param menuId
     * @return
     */
    List<SysMenuButton> selectMenuButton(Long menuId);

    /**
     * 更新菜单的按钮
     * @param menuId
     * @param newButtons
     */
    void updateSyncMenuButtons(Long menuId,List<SysMenuButton> newButtons);

    /**
     * 删除菜单下的所有按钮
     * @param menuIds
     */
    void deleteMenuButton(List<Long> menuIds);


    void validButtons(List<SysMenuButton> buttons);

}
