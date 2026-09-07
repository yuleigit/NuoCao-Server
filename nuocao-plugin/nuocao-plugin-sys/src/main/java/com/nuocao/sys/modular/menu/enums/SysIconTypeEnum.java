package com.nuocao.sys.modular.menu.enums;

import com.nuocao.common.enums.IntArrayValuable;
import com.nuocao.common.enums.StrArrayValuable;
import lombok.Getter;

import java.util.Arrays;

/**
 * 菜单类型 枚举
 */
@Getter
public enum SysIconTypeEnum implements IntArrayValuable {
    ICONIFY(1,"ICONIFY"),
    LOCAL(2,"本地图标，SVG"),
   ;

    private String name;

    private Integer type;

    private static final int[] ARRAY = Arrays.stream(SysIconTypeEnum.values()).mapToInt(o->o.type).toArray();
    SysIconTypeEnum(Integer type, String name){
        this.type=type;
        this.name=name;
    }

    @Override
    public int[] array() {
        return ARRAY;
    }
}
