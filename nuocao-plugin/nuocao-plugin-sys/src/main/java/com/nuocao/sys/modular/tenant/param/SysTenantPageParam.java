package com.nuocao.sys.modular.tenant.param;

import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 客户机构查询参数
 *
 * @author Lei
 * @date  2025/06/20 14:26
 **/
@Getter
@Setter
public class SysTenantPageParam extends SortPageParam {

    /** 关键词 */
    @ApiModelProperty(value = "关键词")
    private String searchKey;

    /** 租户名称 */
    @ApiModelProperty(value = "orgId")
    private Long orgId;
    /** 租户名称 */
    @ApiModelProperty(value = "套餐ID")
    private Long packageId;

    /** 租户名称 */
    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    /** 联系人 */
    @ApiModelProperty(value = "联系人")
    private String contactName;

    /** 系统名称 */
    @ApiModelProperty(value = "系统名称")
    private String sysName;

    /** 到期时间开始 */
    @ApiModelProperty(value = "到期时间开始")
    private String startExpireTime;

    /** 到期时间结束 */
    @ApiModelProperty(value = "到期时间结束")
    private String endExpireTime;

    /** 可用状态 */
    @ApiModelProperty(value = "可用状态")
    private String enableStatus;

}
