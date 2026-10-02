package com.nuocao.sys.modular.tenantpackage.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import com.nuocao.common.pojo.CommonEntity;
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
public class SysTenantPackage extends CommonEntity {

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

}
