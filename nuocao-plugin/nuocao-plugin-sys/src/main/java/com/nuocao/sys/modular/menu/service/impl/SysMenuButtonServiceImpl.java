package com.nuocao.sys.modular.menu.service.impl;

import com.nuocao.common.constants.Constants;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.mapper.LambdaQueryWrapperX;
import com.nuocao.common.utils.LockUtils;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.sys.modular.menu.entity.SysMenu;
import com.nuocao.sys.modular.menu.enums.SysMenuTypeEnum;
import com.nuocao.sys.modular.menu.mapper.SysMenuMapper;
import com.nuocao.sys.modular.menu.pojo.SysMenuButton;
import com.nuocao.sys.modular.menu.service.SysMenuButtonService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Transaction;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SysMenuButtonServiceImpl implements SysMenuButtonService {

    private final static String MENU_BUTTON_UPDATE_LOCK_KEY ="menu:update-buttons:{}";

    @Inject
    private LockUtils lockUtils;

    @Inject
    private SysMenuMapper sysMenuMapper;

    private void add(SysMenu parentMenu, String code, String title,int sortCode) {

        SysMenu menu = new SysMenu();
        menu.setParentId(parentMenu.getId());
        menu.setActiveMenu("");
        menu.setName("");
        menu.setType(SysMenuTypeEnum.BUTTON.getType());
        menu.setTitle(title);
        menu.setCode(code);
        menu.setSortCode(sortCode);
        menu.setI18nKey("");
        menu.setComponent("");
        menu.setPath("");
        menu.setSortCode(sortCode);
        menu.setEnable(parentMenu.getEnable());
        menu.setTopId(parentMenu.getTopId());
        menu.setAncestors(parentMenu.getAncestors()+ Constants.SPLIT_STR+parentMenu.getId());
        sysMenuMapper.insert(menu);

    }

    private void editByMenuAndCode(SysMenu parentMenu, String code, String title,int sortCode) {
        SysMenu menu = sysMenuMapper.selectOne(SysMenu::getParentId,parentMenu.getId(),SysMenu::getCode,code);
        if(menu==null){
            return;
        }
        SysMenu up = new SysMenu();
        up.setId(menu.getId());
        up.setTitle(title);
        up.setSortCode(sortCode);
        up.setTopId(parentMenu.getTopId());
        up.setEnable(parentMenu.getEnable());
        menu.setAncestors(parentMenu.getAncestors()+ Constants.SPLIT_STR+parentMenu.getId());
        sysMenuMapper.updateById(up);
    }


    @Override
    public List<SysMenuButton> selectMenuButton(Long menuId) {
        List<SysMenuButton> buttons =  sysMenuMapper.selectListByQueryAs(
                new LambdaQueryWrapperX<SysMenu>()
                        .selectX(SysMenu::getTitle,SysMenu::getCode,SysMenu::getId)
                        .eqX(SysMenu::getParentId,menuId)
                        .eqX(SysMenu::getType,SysMenuTypeEnum.BUTTON.getType())
                ,SysMenuButton.class
        );
        return buttons==null?new ArrayList<>():buttons;
    }

    @Override
    public void updateSyncMenuButtons(Long menuId, List<SysMenuButton> newButtons) {
        if(menuId==null){
            return;
        }
        lockUtils.lock(StringUtils.format(MENU_BUTTON_UPDATE_LOCK_KEY,menuId),()->updateSyncButtonsDo(menuId,newButtons));

    }
    @Transaction
    public void updateSyncButtonsDo(Long menuId, List<SysMenuButton> newButtons){
        //得到上级菜单信息
        SysMenu parent= sysMenuMapper.selectOne(SysMenu::getId,menuId);
        CommonExceptionI18n.notNull(parent,"sys.menu.parent.NotExists",menuId);

        if(CollectionUtils.isEmpty(newButtons)){
            //为空则删除所欲按钮
            sysMenuMapper.deleteByQuery(new LambdaQueryWrapperX<SysMenu>()
                    .eqX(SysMenu::getParentId,menuId)
                    .eqX(SysMenu::getType, SysMenuTypeEnum.BUTTON.getType()));
        }
        //得到所有下级按钮
        List<SysMenuButton> oldButtons = selectMenuButton(menuId);


        //得到所有新按钮的code集合
        List<String> codes = newButtons.stream().map(SysMenuButton::getCode).collect(Collectors.toList());

        //如果新提交的菜单中不存在，则移除
        List<SysMenuButton> removeButtons = oldButtons.stream().filter(o->!codes.contains(o.getCode())).collect(Collectors.toList());
        //更新
        int newButtonIndex=0;
        for (SysMenuButton btn:
                newButtons) {
            //存在菜单位则修改
            SysMenuButton oldBtn = oldButtons.stream().filter(o->o.getCode().equals(btn.getCode())).findFirst().orElse(null);
            if(oldBtn==null){
                add(parent,btn.getCode(),btn.getTitle(),1000+newButtonIndex);
            }else{
                editByMenuAndCode(parent,btn.getCode(),btn.getTitle(),1000+newButtonIndex);
            }
            newButtonIndex++;
        }

        //移除不存在的菜单
        if(CollectionUtils.isNotEmpty(removeButtons)){
            sysMenuMapper.deleteByQuery(new LambdaQueryWrapperX<SysMenu>()
                    .eqX(SysMenu::getParentId,menuId)
                    .inX(SysMenu::getCode,CollectionUtils.convertList(removeButtons,SysMenuButton::getCode))
            );
        }

    }
    @Override
    public void deleteMenuButton(List<Long> menuIds) {
        sysMenuMapper.deleteByQuery(new LambdaQueryWrapperX<SysMenu>().inX(SysMenu::getParentId,menuIds).eqX(SysMenu::getType,SysMenuTypeEnum.BUTTON.getType()));
    }

    @Override
    public void validButtons(List<SysMenuButton> buttons) {
        if(CollectionUtils.isEmpty(buttons)) return;
        //得到所有code
        List<String> codeList = buttons.stream().map(SysMenuButton::getCode).collect(Collectors.toList());
        Set<String> tempSet = new HashSet<>();
        Optional<String> duplicateCode = codeList.stream().filter(c -> !tempSet.add(c)).findFirst();

        if (duplicateCode.isPresent()) {
            throw new CommonExceptionI18n("sys.menu.button.code.nonRepeat");
        }
    }
}
