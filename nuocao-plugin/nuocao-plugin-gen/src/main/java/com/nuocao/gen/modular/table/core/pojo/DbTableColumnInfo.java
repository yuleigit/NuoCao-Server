package com.nuocao.gen.modular.table.core.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 数据库 表字段信息
 */
@Getter
@Setter
public class DbTableColumnInfo {

    /** 字段名称 */
    @ApiModelProperty(value = "字段名称", position = 1)
    private String columnName;

    /** 字段类型 */
    @ApiModelProperty(value = "字段类型", position = 2)
    private String columnType;

    /** 字段注释 */
    @ApiModelProperty(value = "字段注释", position = 3)
    private String columnRemark;

    @ApiModelProperty(value = "最大长度或数字最大值", position = 3)
    private Integer sizeMax;

    @ApiModelProperty(value = "最小长度或数字最小值", position = 3)
    private Integer sizeMin;

    @ApiModelProperty(value = "是否允许为空", position = 3)
    private Integer nullable;
}
