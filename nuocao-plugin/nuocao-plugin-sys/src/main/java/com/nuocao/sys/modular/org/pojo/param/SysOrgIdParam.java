package com.nuocao.sys.modular.org.pojo.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import org.noear.solon.validation.annotation.NotNull;

/**
 * 组织Id参数
 **/
@Getter
@Setter
public class SysOrgIdParam {

    /** orgId */
    @ApiModelProperty(value = "orgId", required = true)
    @NotNull
    private Long id;
}
