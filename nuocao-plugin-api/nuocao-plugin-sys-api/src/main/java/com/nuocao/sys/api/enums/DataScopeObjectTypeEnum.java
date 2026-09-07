package com.nuocao.sys.api.enums;

import com.nuocao.common.enums.StrArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 值超限处理方式
 */
@Getter
@AllArgsConstructor
public enum DataScopeObjectTypeEnum implements StrArrayValuable {
    USER("user","用户"),
    ROLE("role","角色");

    private final String type;

    private final String name;

    public static final List<String> ARRAYS = Arrays.stream(values()).map(DataScopeObjectTypeEnum::getType).collect(Collectors.toList());

    @Override
    public String[] array() {
        return ARRAYS.toArray(String[]::new);
    }
}
