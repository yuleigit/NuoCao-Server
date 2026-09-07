package com.nuocao.sys.modular.org.pojo;

import com.nuocao.common.enums.CommonEnableStatusEnum;
import com.nuocao.common.validation.InEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

@Getter
@Setter
public class SysOrgBase{
    /** 父id */
    @ApiModelProperty(value = "父id", required = true, position = 1)
    @NotNull
    private Long parentId;

    /** 名称 */
    @ApiModelProperty(value = "名称", required = true, position = 2)
    @NotBlank
    private String orgName;

    /** 名称 */
    @ApiModelProperty(value = "组织编码", required = true, position = 3)
    @NotBlank
    private String orgCode;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", required = true, position = 4)
    @NotNull
    private Integer sortCode;

    /** 名称 */
    @ApiModelProperty(value = "组织状态", required = true, position = 5)
    @NotNull
    @InEnum(CommonEnableStatusEnum.class)
    private Integer status;

    /** 主管id */
    @ApiModelProperty(value = "主管id", position = 6)
    private Long directorUserId;
}
