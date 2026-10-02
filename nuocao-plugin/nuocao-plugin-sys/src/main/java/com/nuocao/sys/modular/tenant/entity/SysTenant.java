package com.nuocao.sys.modular.tenant.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.nuocao.common.pojo.CommonEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.Pattern;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 客户机构实体
 *
 * @author Lei
 * @date  2025/06/20 14:26
 **/
@Getter
@Setter
@Table(value = "sys_tenant")
public class SysTenant extends CommonEntity {

    /** 租户ID */
    @Id
    @ApiModelProperty(value = "租户ID", position = 1)
    private Long id;

    @ApiModelProperty(value = "分组ID", position = 1)
    private Long orgId;

    /** 租户名称 */
    @ApiModelProperty(value = "租户名称", position = 2)
    private String tenantName;

    /** 套餐ID */
    @ApiModelProperty(value = "套餐ID", position = 3)
    private Long packageId;

    /** 数据规则形式 */
    @ApiModelProperty(value = "数据规则形式", position = 4)
    private String dataRule;

    /** 租户编码;不重复，数字字母组合、主要生成数据库用 */
    @ApiModelProperty(value = "租户编码;不重复，数字字母组合、主要生成数据库用", position = 5)
    @Pattern(value="^[a-z][a-z0-9_]*$",message = "编码只能是字母开头，字母、数字、下划线的组合，且字母必须为小写")
    private String tenantCode;

    /** 联系人 */
    @ApiModelProperty(value = "联系人", position = 6)
    private String contactName;

    /** 联系电话 */
    @ApiModelProperty(value = "联系电话", position = 7)
    private String contactTel;

    /** 地址 */
    @ApiModelProperty(value = "地址", position = 8)
    private String contactAddress;

    /** 系统名称 */
    @ApiModelProperty(value = "系统名称", position = 9)
    private String sysName;

    /** 租户管理用户ID */
    @ApiModelProperty(value = "租户管理用户ID", position = 10)
    private Long tenantAdminId;

    /** 到期时间 */
    @ApiModelProperty(value = "到期时间", position = 11)
    private LocalDateTime expireTime;

    /** 可用状态 */
    @ApiModelProperty(value = "可用状态", position = 12)
    private String enableStatus;

}
