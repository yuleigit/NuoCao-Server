package com.nuocao.sys.modular.tenant.reslut;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.sys.api.utils.SysOrgApiUtils;
import com.nuocao.sys.api.utils.SysTenantPackageApiUtils;
import com.nuocao.sys.modular.user.pojo.result.SaSysLoginUser;
import com.nuocao.sys.modular.user.service.SysUserService;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SysTenantViewResult {




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

    /** 租户编码;不重复，数字字母组合、主要生成数据库用 */
    @ApiModelProperty(value = "租户编码;不重复，数字字母组合、主要生成数据库用", position = 5)
    private String tenantCode;

    /** 联系人 */
    @ApiModelProperty(value = "联系人", position = 6)
    private String contactsUser;

    /** 联系电话 */
    @ApiModelProperty(value = "联系电话", position = 7)
    private String contactsPhone;

    /** 地址 */
    @ApiModelProperty(value = "地址", position = 8)
    private String contactsAddress;

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

    @ApiModelProperty("账号个数统计")
    private Integer accountCount=0;



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
