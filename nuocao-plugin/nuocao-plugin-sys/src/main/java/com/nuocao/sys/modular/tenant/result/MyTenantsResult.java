package com.nuocao.sys.modular.tenant.result;

import com.nuocao.sys.api.dto.SysTenantDTO;
import lombok.Data;

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
