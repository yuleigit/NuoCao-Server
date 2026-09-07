package com.nuocao.sys.modular.role.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.nuocao.common.pojo.CommonEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色实体
 **/
@Getter
@Setter
@Table(value = "sys_role")
public class SysRole extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    @Id
    private Long id;

    /** 组织id */
    @ApiModelProperty(value = "组织id", position = 2)
    private Long orgId;

    /** 名称 */
    @ApiModelProperty(value = "名称", position = 3)
    private String name;

    /** 编码 */
    @ApiModelProperty(value = "编码", position = 4)
    private String code;

    /** 分类 */
    @ApiModelProperty(value = "分类", position = 5)
    private String category;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 6)
    private Integer sortCode;

    /** 角色权重，最小为0，在给用户授权时，只能授权权重低于自己的 */
    @ApiModelProperty(value = "角色权重", position = 6)
    private Integer weight;


    @ApiModelProperty(value="租户ID")
    @Column(tenantId = true)
    private Long tenantId;
}
