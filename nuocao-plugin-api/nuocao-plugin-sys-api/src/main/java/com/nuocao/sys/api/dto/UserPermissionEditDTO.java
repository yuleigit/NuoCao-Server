package com.nuocao.sys.api.dto;


import com.nuocao.common.validation.InStrEnum;
import com.nuocao.sys.api.enums.DataScopeCategoryEnum;
import com.nuocao.sys.api.enums.DataScopeObjectTypeEnum;
import com.nuocao.sys.api.enums.DataScopeTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.Length;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

import java.util.List;
import java.util.Set;

/**
 * 用户权限 维护实体
 */
@Getter
@Setter
public class UserPermissionEditDTO {



    /** 对象类型
     *
     * {@link DataScopeObjectTypeEnum}
     * */
    @ApiModelProperty(value = "对象类型",notes = "user/role",required = true, position = 5)
    @NotBlank
    @InStrEnum(value= DataScopeObjectTypeEnum.class)
    private String objectType;

    /** 权限对象ID */
    @ApiModelProperty(value = "权限对象ID",notes = "",required = true, position = 5)
    @NotNull
    private Long objectId;

    /** 菜单/按钮等ID集合 */
    @ApiModelProperty(value = "菜单/按钮等ID集合", required = true, position = 6)
    private Set<Long> menuIds;

}
