package com.nuocao.sys.modular.tenant.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.sys.api.utils.SysOrgApiUtils;
import com.nuocao.sys.api.utils.SysTenantPackageApiUtils;
import com.nuocao.sys.modular.user.pojo.result.SaSysLoginUser;
import com.nuocao.sys.modular.user.service.SysUserService;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SysTenantBaseResult {

    /** 租户ID */
    @ApiModelProperty(value = "租户ID", position = 1)
    private Long id;

    private Long orgId;

    private String orgName;

    /** 租户名称 */
    @ApiModelProperty(value = "租户名称", position = 2)
    private String tenantName;

    /** 套餐ID */
    @ApiModelProperty(value = "套餐ID", position = 3)
    private Long packageId;

    private String packageName;

    /** 数据规则形式 */
    @ApiModelProperty(value = "数据规则形式", position = 4)
    private String dataRule;

    /** 租户编码;不重复，数字字母组合、主要生成数据库用 */
    @ApiModelProperty(value = "租户编码;不重复，数字字母组合、主要生成数据库用", position = 5)
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

    private String tenantAdminName;


    /** 到期时间 */
    @ApiModelProperty(value = "到期时间", position = 11)
    private Date expireTime;

    /** 可用状态 */
    @ApiModelProperty(value = "可用状态", position = 12)
    private String enableStatus;

    /** 创建人 */
    @ApiModelProperty(value = "创建人", position = 14)
    private Long createdUser;

    /** 创建时间 */
    @ApiModelProperty(value = "创建时间", position = 15)
    private Date createdTime;


    @JsonProperty
    public String getPackageName(){
        return SysTenantPackageApiUtils.getNameByCache(this.packageId);
    }

    @JsonProperty
    public String getTenantAdminName(){
        SaSysLoginUser user= BeanUtils.getBean(SysUserService.class).getLoginUserById(this.tenantAdminId);
        return user==null?"":user.getName()+"("+user.getUsername()+")";
    }
    @JsonProperty
    public String getOrgName(){
        return SysOrgApiUtils.getName(this.orgId);
    }
}
