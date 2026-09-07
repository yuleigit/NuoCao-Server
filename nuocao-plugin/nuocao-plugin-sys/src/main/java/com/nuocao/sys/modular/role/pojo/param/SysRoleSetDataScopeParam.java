package com.nuocao.sys.modular.role.pojo.param;

import com.nuocao.common.validation.InStrEnum;
import com.nuocao.sys.api.enums.DataScopeTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.Length;
import org.noear.solon.validation.annotation.NotBlank;

import java.util.Set;

/**
 * 角色授权数据权限参数

 **/
@Getter
@Setter
public class SysRoleSetDataScopeParam {

    /** 角色id */
    @ApiModelProperty(value = "角色id", required = true, position = 1)
    @NotBlank
    private Long id;

    /** 权限范围ALL,ONLY,CHILD,CUSTOM,SELF */
    @ApiModelProperty(value = "权限范围",notes = "ALL,ONLY,CHILD,CUSTOM,SELF",required = true, position = 5)
    @NotBlank
    @Length(max=20)
    @InStrEnum(value= DataScopeTypeEnum.class)
    private String scopeType;


    /** 权限ID集合 */
    @ApiModelProperty(value = "权限ID集合", required = true, position = 6)
    private Set<Long> ids;
}
