package com.nuocao.gen.modular.table.entity;


import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.nuocao.common.pojo.CommonEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 代码生成 表格实体
 **/
@Getter
@Setter
@Table(value = "gen_table")
public class GenTable extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    @Id
    private Long id;
    /** 主表名称 */
    @ApiModelProperty(value = "数据源名称", position = 2)
    private String dataSourceName;
    /** 主表名称 */
    @ApiModelProperty(value = "主表名称", position = 2)
    private String tableName;

    /** 主表主键 */
    @ApiModelProperty(value = "主表主键",notes = "默认为主键", position = 3)
    private String pkColumnName;

    /** 插件名 */
    @ApiModelProperty(value = "插件名",notes = "如：nuocao-plugin-sys",position = 4)
    private String pluginName;

    /** 模块名 */
    @ApiModelProperty(value = "模块名",notes = "如：com.nuocao.sys得sys就是模块名",position = 5)
    private String moduleName;

    /** 表前缀移除 */
    @ApiModelProperty(value = "表前缀移除",notes = "生成时是否移除表前缀，一般同模块的表会使用相同前缀，单独创建模块后可以移除", position = 6)
    private String removeTablePrefix;

    /** 生成方式 */
    @ApiModelProperty(value = "生成方式",notes = "zip和local", position = 7)
    private String generateType;

    /** 所属模块 */
    @ApiModelProperty(value = "所属模块ID", position = 8)
    private Long moduleId;

    /** 上级目录 */
    @ApiModelProperty(value = "上级目录ID", position = 9)
    private Long menuParentId;

    /** 功能名 */
    @ApiModelProperty(value = "功能名", position = 11)
    private String functionName;

    /** 业务名 */
    @ApiModelProperty(value = "业务名", position = 12)
    private String businessName;

    /** 类名 */
    @ApiModelProperty(value = "类名", position = 13)
    private String className;

    /** 表单布局 */
    @ApiModelProperty(value = "表单布局", position = 14)
    private String formLayout;

    /** 使用栅格 */
    @ApiModelProperty(value = "使用栅格",notes ="1=是，0=否", position = 15)
    private Integer isGrid;


    /** 包名 */
    @ApiModelProperty(value = "包名", position = 17)
    private String packageName;

    /** 作者 */
    @ApiModelProperty(value = "作者", position = 18)
    private String authorName;

    /** 排序 */
    @ApiModelProperty(value = "排序", position = 16)
    private Integer sortCode;

}

