package com.nuocao.dev.modular.file.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotNull;

import java.util.List;

/**
 * 文件列表参数
 */
@Getter
@Setter
public class DevFileUrlListParam {

    /** 文件路径集合 */
    @ApiModelProperty( "文件路径集合")
    @NotNull(message = "urlList不能为空")
    private List<String> urlList;
}