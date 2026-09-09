package com.nuocao.sys.api.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nuocao.common.validation.InStrEnum;
import com.nuocao.sys.api.enums.DataScopeCategoryEnum;
import com.nuocao.sys.api.enums.DataScopeObjectTypeEnum;
import com.nuocao.sys.api.enums.DataScopeTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.Length;
import org.noear.solon.validation.annotation.NotBlank;

import java.util.Set;

/**
 * 数据权限维护实体
 */
@Getter
@Setter
public class DataScopeEditDTO {

    @ApiModelProperty("数据权限类型")
    @NotBlank(message = "数据权限类型不能为空")
    @InStrEnum(value= DataScopeCategoryEnum.class)
    @JsonIgnore
    private String category;

    /** 权限范围ALL,ONLY,CHILD,CUSTOM,SELF */
    @ApiModelProperty(value = "权限范围",notes = "ALL,ONLY,CHILD,CUSTOM,SELF",required = true, position = 5)
    @NotBlank(message = "权限范围不能为空")
    @Length(max=20,message="权限范围长度最多为20")
    @InStrEnum(value= DataScopeTypeEnum.class)
    private String scopeType;

    /** 权限范围ALL,ONLY,CHILD,CUSTOM,SELF */
    @ApiModelProperty(value = "对象类型",notes = "ALL,ONLY,CHILD,CUSTOM,SELF",required = true, position = 5)
    @NotBlank(message = "对象类型不能为空")
    @InStrEnum(value= DataScopeObjectTypeEnum.class)
    @JsonIgnore
    private String objectType;

    /** 权限范围ALL,ONLY,CHILD,CUSTOM,SELF */
    @ApiModelProperty(value = "权限对象ID不能为空",notes = "这里是角色或用户ID",required = true, position = 5)
    @NotBlank(message = "权限对象ID不能为空")
    @Length(max=32,message="对象ID不能为空")
    @JsonIgnore
    private Long objectId;

    /** 权限ID集合 */
    @ApiModelProperty(value = "权限ID集合", required = true, position = 6)
    private Set<Long> ids;

}
