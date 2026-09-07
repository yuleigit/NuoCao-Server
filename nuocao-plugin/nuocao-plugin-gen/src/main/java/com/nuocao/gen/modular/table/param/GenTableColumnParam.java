
package com.nuocao.gen.modular.table.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 数据源库字段列表参数
 *
 */
@Getter
@Setter
public class GenTableColumnParam {

    /** 表名称 */
    @ApiModelProperty(value = "表名称", required = true)
    @NotBlank(message = "表名称不能为空")
    private String tableName;
}
