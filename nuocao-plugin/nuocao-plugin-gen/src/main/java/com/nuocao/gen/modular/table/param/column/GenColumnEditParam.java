package com.nuocao.gen.modular.table.param.column;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 代码生成字段编辑参数
 *
 * @author yubaoshan
 * @date 2022/10/25 22:33
 **/
@Getter
@Setter
public class GenColumnEditParam extends GenColumnAddEditCommonParams {

    /** id */
    @ApiModelProperty(value = "id", required = true, position = 1)
    @NotNull
    private Long id;

}
