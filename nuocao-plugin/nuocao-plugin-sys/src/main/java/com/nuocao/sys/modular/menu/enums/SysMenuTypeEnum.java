package com.nuocao.sys.modular.menu.enums;

import com.nuocao.common.enums.StrArrayValuable;
import lombok.Getter;

import java.util.Arrays;

/**
 * 菜单类型 枚举
 */
@Getter
public enum SysMenuTypeEnum implements StrArrayValuable {
    MODULE("module","模块"),
    DIR("dir","目录"),
    MENU("param","菜单"),
    IFRAME("iframe","框架"),
    LINK("link","外链"),
    BUTTON("button","功能按键");

    private String name;

    private String type;

    private static final String[] ARRAY = Arrays.stream(SysMenuTypeEnum.values()).map(o->o.type).toArray(String[]::new);
    SysMenuTypeEnum(String type,String name){
        this.type=type;
        this.name=name;
    }

    @Override
    public String[] array() {
        return ARRAY;
    }
}
