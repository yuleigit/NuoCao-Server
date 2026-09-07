package com.nuocao.sys.api.enums;

import com.nuocao.common.enums.StrArrayValuable;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门权限
 */
@Getter
public enum DataScopeTypeEnum implements StrArrayValuable {
    ALL("all", "全部数据"),
    CUSTOM("custom", "自定义数据"),
    ONLY("only", "本部门数据"),
    CHILD("child", "本部门级子部门数据"),
    SELF("self","仅本人自己数据");

    private final String value;
    private final String name;

    public static final List<String> ARRAYS = Arrays.stream(values()).map(DataScopeTypeEnum::getValue).collect(Collectors.toList());

    @Override
    public String[] array() {
        return ARRAYS.toArray(String[]::new);
    }
    DataScopeTypeEnum(String value, String name)
    {
        this.value = value;
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public String getName()
    {
        return name;
    }

}
