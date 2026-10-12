package com.nuocao.gen.modular.table.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * 代码生成器 表编辑基础参数
 */
@Getter
@Setter
public class GenTableAddEditCommonParam {

    /** 主表名称 */
    @ApiModelProperty(value = "主表名称", required = true, position = 1)
    @NotBlank(message = "gen.table.edit.tableNameNotBlank")
    private String tableName;

    /** 表主键 */
    @ApiModelProperty(value = "表主键", required = true, position = 2)
    @NotBlank(message = "gen.table.edit.pkColumnNameNotBlank")
    private String pkColumnName;

    /** 插件名 */
    @ApiModelProperty(value = "插件名", required = true, position = 3)
    @NotBlank(message = "sys.param.title.existsChar")
    private String pluginName;

    /** 模块名 */
    @ApiModelProperty(value = "模块名", required = true, position = 4)
    @NotBlank(message = "gen.table.edit.moduleNameNotBlank")
    private String moduleName;

    /** 表前缀移除 */
    @ApiModelProperty(value = "表前缀移除", required = true, position = 5)
    @NotNull(message = "gen.table.edit.isRemoveTablePrefixNotNull")
    private Integer removeTablePrefix;

    /** 生成方式 */
    @ApiModelProperty(value = "生成方式", required = true, position = 7)
    @NotNull(message = "generateType不能为空")
    private String generateType;

    /** 所属模块 */
    @ApiModelProperty(value = "所属模块", required = true, position = 8)
    @NotNull(message = "module不能为空")
    private String module;

    /** 上级目录 */
    @ApiModelProperty(value = "上级目录", required = true, position = 9)
    @NotNull(message = "menuPid不能为空")
    private String menuPid;

    /** 移动端所属模块 */
    @ApiModelProperty(value = "移动端所属模块", required = true, position = 10)
    private String mobileModule;

    /** 功能名 */
    @ApiModelProperty(value = "功能名", required = true, position = 11)
    @NotNull(message = "functionName不能为空")
    private String functionName;

    /** 业务名 */
    @ApiModelProperty(value = "业务名", required = true, position = 12)
    @NotNull(message = "busName不能为空")
    private String busName;

    /** 类名 */
    @ApiModelProperty(value = "类名", required = true, position = 13)
    @NotNull(message = "className不能为空")
    private String className;

    /** 表单布局 */
    @ApiModelProperty(value = "表单布局", required = true, position = 14)
    @NotNull(message = "formLayout不能为空")
    private String formLayout;

    /** 使用栅格 */
    @ApiModelProperty(value = "使用栅格", required = true, position = 15)
    @NotNull(message = "gridWhether不能为空")
    private String gridWhether;

    /** 排序 */
    @ApiModelProperty(value = "排序", required = true, position = 16)
    @NotNull(message = "sortCode不能为空")
    private Integer sortCode;

    /** 作者名 */
    @ApiModelProperty(value = "作者名", required = true, position = 17)
    @NotNull(message = "authorName不能为空")
    private String authorName;

    /** 包名 */
    @ApiModelProperty(value = "包名 */", required = true, position = 17)
    private String packageName;

}
