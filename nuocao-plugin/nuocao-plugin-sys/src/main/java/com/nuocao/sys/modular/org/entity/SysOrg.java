package com.nuocao.sys.modular.org.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.nuocao.common.pojo.CommonEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * 组织实体
 **/
@Getter
@Setter
@Table(value = "SYS_ORG")
public class SysOrg extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    @Id
    private Long id;

    /** 父id */
    @ApiModelProperty(value = "父id", position = 2)
    private Long parentId;
    /**
     * 祖级集合，父级ID的集合，包括top=0;
     */
    @ApiModelProperty(value = "祖级集合")
    private String ancestors;


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


    @Column(tenantId = true)
    private Long tenantId;
}
