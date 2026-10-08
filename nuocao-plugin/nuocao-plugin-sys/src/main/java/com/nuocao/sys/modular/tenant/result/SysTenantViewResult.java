package com.nuocao.sys.modular.tenant.result;

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
public class SysTenantViewResult extends SysTenantBaseResult {



    @ApiModelProperty("账号个数统计")
    private Integer accountCount=0;




}
