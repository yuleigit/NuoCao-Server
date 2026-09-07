package com.nuocao.sys.modular.role.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.Max;
import org.noear.solon.validation.annotation.Min;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 角色添加参数
 **/
@Getter
@Setter
public class SysRoleBase {

    /** 名称 */
    @ApiModelProperty(value = "名称", required = true, position = 1)
    @NotBlank
    private String name;

    /** 分类 */
    @ApiModelProperty(value = "分类", required = true, position = 2)
    private String category;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", required = true, position = 3)
    @NotNull
    private Integer sortCode;

    /** 编码，默认自动生成 */
    @ApiModelProperty(value = "编码，默认自动生成",hidden = true)
    private String code;

    /** 组织id */
    @ApiModelProperty(value = "组织id", position = 4)
    private String orgId;

    /** 角色权重 */
    @ApiModelProperty(value = "角色权重", position = 5)
    @NotNull
    @Max(value=99)
    @Min(value=0)
    private Integer weight;
}
