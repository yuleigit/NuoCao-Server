package com.nuocao.gen.modular.table.param.column;

import com.mybatisflex.annotation.Id;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

import java.math.BigDecimal;

@Getter
@Setter
public class GenColumnAddEditCommonParams {

    /** 所属表ID */
    @ApiModelProperty(value = "表ID",position = 0)
    @NotNull
    private Long tableId;

    /** 是否为表格的key * */
    @ApiModelProperty(value = "是否为表格的key",position = 0)
    @NotNull
    private Integer isPk;

    /** 字段名 */
    @ApiModelProperty(value = "字段名",position = 0)
    @NotBlank
    private String columnName;

    /** 字段描述 */
    @ApiModelProperty(value = "字段描述",position = 0)
    @NotBlank
    private String columnRemark;

    /** 字段类型 */
    @ApiModelProperty(value = "字段类型",position = 0)
    @NotBlank
    private String columnType;

    /** 字段对应java类型 */
    @ApiModelProperty(value = "字段对应java类型",position = 0)
    private String javaType;

    /** 字段对应java变量名 */
    @ApiModelProperty(value = "字段对应java变量名",position = 0)
    private String javaName;

    /** 字段类型 */
    @ApiModelProperty(value = "html组件类型",position = 0)
    private String htmlType;

    /** 如果使用字典则选择字典类型 */
    @ApiModelProperty(value = "字典类型",notes = "如果使用字典则选择字典类型",position = 0)
    private String dictType;

    /** 是否列表返回*/
    @ApiModelProperty(value = "是否列表返回",position = 0)
    private Integer isList;

    /** 是否忽略列表显示 */
    @ApiModelProperty(value = "是否忽略列表显示",position = 0)
    private Integer isIgnore;

    /** 是否详情显示 */
    @ApiModelProperty(value = "是否详情显示",position = 0)
    private Integer isView;

    /** 是否新增填写 */
    @ApiModelProperty(value = "是否新增填写",position = 0)
    private Integer isAdd;

    /** 是否编辑填写 */
    @ApiModelProperty(value = "是否编辑填写",position = 0)
    private Integer isEdit;

    /** 是否必填项 */
    @ApiModelProperty(value = "是否必填项",position = 0)
    private Integer isRequired;

    /** 是否唯一 */
    @ApiModelProperty(value = "是否唯一",position = 0)
    private Integer isOnly;

    /** 是否作为查询项 */
    @ApiModelProperty(value = "是否作为查询项",position = 0)
    private Integer isQuery;

    /** 查询类型 */
    @ApiModelProperty(value = "查询类型",position = 0)
    private Integer queryType;

    /** 值的最小长度或值 */
    @ApiModelProperty(value = "值的最小长度或值",position = 0)
    private BigDecimal valueSizeMin;

    /** 值的最小大度或值 */
    @ApiModelProperty(value = "值的最打长度或值",position = 0)
    private BigDecimal valueSizeMax;


    /** 验证的注解，以@开始 */
    @ApiModelProperty(value = "验证的注解，以@开始",position = 0)
    private String validAnnotation;


    /** 排序编码 */
    @ApiModelProperty(value = "排序编码",position = 0)
    private Integer sortCode;
}
