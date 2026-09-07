package com.nuocao.dev.modular.config.param;

import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 配置查询参数
 */
@Getter
@Setter
public class DevConfigPageParam extends SortPageParam {

    /** 配置键 */
    @ApiModelProperty(value = "配置键")
    private String configKey;
}
