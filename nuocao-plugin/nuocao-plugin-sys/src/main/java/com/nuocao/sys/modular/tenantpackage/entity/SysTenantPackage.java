package com.nuocao.sys.modular.tenantpackage.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 租户套餐实体
 *
 * @author Lei
 * @date  2025/06/19 16:30
 **/
@Getter
@Setter
@Table(value = "sys_tenant_package")
public class SysTenantPackage {

    /** 租户套餐ID */
    @Id
    @ApiModelProperty(value = "租户套餐ID", position = 1)
    private Long id;

    /** 套餐名称 */
    @ApiModelProperty(value = "套餐名称", position = 2)
    private String name;

    /** 备注说明 */
    @ApiModelProperty(value = "备注说明", position = 3)
    private String remark;


    /** 备注说明 */
    @ApiModelProperty(value = "启用状态", position = 3)
    private Integer enableStatus;


    /** 被授权的菜单ID集合 */
    @ApiModelProperty(value = "被授权的菜单ID集合", position = 3)
    @Column(typeHandler = Fastjson2TypeHandler.class)
    private List<Long> menuIds;

    /** 乐观锁 */
    @ApiModelProperty(value = "乐观锁", position = 4)
    private Integer deleteFlag;

    /** 创建人 */
    @ApiModelProperty(value = "创建人", position = 5)
    private String createdUser;

    /** 创建时间 */
    @ApiModelProperty(value = "创建时间", position = 6)
    private Date createdTime;

    /** 更新人 */
    @ApiModelProperty(value = "更新人", position = 7)
    private String updatedUser;

    /** 更新时间 */
    @ApiModelProperty(value = "更新时间", position = 8)
    private Date updatedTime;
}
