package com.nuocao.sys.modular.menu.entity;


import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.JacksonTypeHandler;
import com.nuocao.common.pojo.CommonEntity;
import com.nuocao.common.pojo.KeyValue;
import com.nuocao.common.pojo.KeyValueData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 菜单实体
 *
 **/
@Getter
@Setter
@Table(value = "sys_menu")
public class SysMenu extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "menuId", position = 1)
    @Id
    private Long id;

    /** 父id */
    @ApiModelProperty(value = "父id", position = 2)
    private Long parentId;
    /** 顶级菜单ID */
    @ApiModelProperty(value = "顶级菜单ID", position = 2)
    private Long topId;
    /**
     * 祖级集合，父级ID的集合，包括top=0;
     */
    @ApiModelProperty(value = "祖级集合")
    private String ancestors;

    /** 标题 */
    @ApiModelProperty(value = "标题", position = 3)
    private String title;

    /** 多语言key（前端用） */
    @ApiModelProperty(value = "多语言key", position = 3)
    private String i18nKey;

    /** （路由名称，英文） */
    @ApiModelProperty(value = "别名（路由名称，英文）", position = 4)
    private String name;

    /** 编码不能重复且只有菜单、按钮及才可用 */
    @ApiModelProperty(value = "编码不能重复且只有菜单、按钮及才可用", position = 4)
    private String code;


    /** 菜单类型
     * {@link com.nuocao.sys.modular.menu.enums.SysMenuTypeEnum}
     * */
    @ApiModelProperty(value = "菜单类型", position = 8)
    private String type;

    /** 路径(路由路径） */
    @ApiModelProperty(value = "路径(路由路径）", position = 9)
    private String path;

    /** 组件路径 */
    @ApiModelProperty(value = "组件路径", position = 10)
    private String component;

    /** 图标 */
    @ApiModelProperty(value = "图标", position = 11)
    private String icon;

    /** 图标类型
     * {@link com.nuocao.sys.modular.menu.enums.SysIconTypeEnum }
     * */
    @ApiModelProperty(value = "图标类型", position = 11)
    private Integer iconType;

    /** 外链连接 */
    @ApiModelProperty(value = "外链连接", position = 12)
    private String href;

    /** 是否可见 */
    @ApiModelProperty(value = "是否可见", position = 13)
    private Integer hideInMenu;

    /** 排序码(前端ui使用的order，这个在tree给转一下，sortCode为了与系统其他模块统一 */
    @ApiModelProperty(value = "菜单排序", position = 14)
    private Integer sortCode;

    /** 路由是否缓存
     * {@link com.nuocao.common.enums.CommonYesNoEnum}
     * */
    @ApiModelProperty(value = "路由是否缓存", position = 15)
    private Integer keepAlive;

    /** 当前路由对应的激活状态菜单 */
    @ApiModelProperty(value = "当前路由对应的激活状态菜单", position = 15)
    private String activeMenu;

    /** 是否常量
     * {@link com.nuocao.common.enums.CommonYesNoEnum}
     * */
    @ApiModelProperty(value = "是否常量",notes = "常量菜单可不登录使用（要结合sa-token配置）", position = 15)
    private Integer constant;

    /** 是否支持多页签Tab */
    @ApiModelProperty(value = "是否支持多页签Tab", position = 15)
    private Integer multiTab;


    /** 固定在页签中的路由索引 */
    @ApiModelProperty(value = "固定在页签中的路由索引", position = 15)
    private Integer fixedIndexInTab;


    /** 菜单额外携带的参数 */
    @ApiModelProperty(value = "菜单额外携带的参数", position = 15)
    @Column(typeHandler = JacksonTypeHandler.class)
    private List<KeyValueData> query;

    /** 是否可用 */
    @ApiModelProperty(value = "是否可用", position = 10)
    private Integer enable;


}
