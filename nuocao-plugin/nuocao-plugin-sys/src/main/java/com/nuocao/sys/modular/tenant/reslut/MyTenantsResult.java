package com.nuocao.sys.modular.tenant.reslut;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nuocao.sys.api.dto.SysTenantDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MyTenantsResult {

    /**
     * 当前租户
     */
    SysTenantDTO current;

    /**
     * 租户列表
     */
    List<SysTenantDTO> tenants;


}
