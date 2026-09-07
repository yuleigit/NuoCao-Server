package com.nuocao.sys.api.dto;

import com.mybatisflex.annotation.Id;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 组织tree 实体
 */
@Setter
@Getter
public class SysOrgTreeResult {
    @ApiModelProperty(value = "id", position = 1)
    @Id
    private Long id;

    /** 父id */
    @ApiModelProperty(value = "父id", position = 2)
    private Long parentId;

    /** 主管id */
    @ApiModelProperty(value = "主管id", position = 3)
    private Long directorUserId;

    /** 名称 */
    @ApiModelProperty(value = "名称", position = 4)
    private String orgName;

    /** 编码 */
    @ApiModelProperty(value = "编码", position = 5)
    private String orgCode;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 7)
    private Integer sortCode;

    /** 组织状态
     *  {@link com.nuocao.common.enums.CommonEnableStatusEnum}
     * */
    @ApiModelProperty(value = "排序码", position = 7)
    private Integer status;

    private List<SysOrgTreeResult> children ;

}
