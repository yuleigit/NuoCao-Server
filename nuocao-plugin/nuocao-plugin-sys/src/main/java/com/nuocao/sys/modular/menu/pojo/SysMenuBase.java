package com.nuocao.sys.modular.menu.pojo;

import com.nuocao.common.enums.CommonYesNoEnum;
import com.nuocao.common.pojo.KeyValueData;
import com.nuocao.common.validation.InEnum;
import com.nuocao.sys.modular.menu.enums.SysIconTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.Length;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * 菜单基础 实体
 */
@Getter
@Setter
public class SysMenuBase {

    /** 父id */
    @ApiModelProperty(value = "父id", required = true, position = 1)
    @NotNull(message = "上级ID不能为空")
    private Long parentId;

    /** 标题 */
    @ApiModelProperty(value = "标题", required = true, position = 2)
    @NotBlank
    private String title;

    /** 别名（路由名称，英文） */
    @ApiModelProperty(value = "别名（路由名称，英文）", position = 7)
    private String name;
    /** 编码不能重复且只有菜单、按钮及才可用 */
    @ApiModelProperty(value = "编码不能重复且只有菜单、按钮及才可用", position = 4)
    private String code;
    /** 标题多语言KEY */
    @ApiModelProperty(value = "标题多语言KEY", required = true, position = 2)
    private String i18nKey;

    /** 菜单类型 */
    @ApiModelProperty(value = "菜单类型", required = true, position = 3)
    @NotBlank
    private String type;

    /** 路径 */
    @ApiModelProperty(value = "路径", required = true, position = 5)
    @NotBlank
    private String path;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", required = true, position = 6)
    @NotNull
    private Integer sortCode;

    /** 组件 */
    @ApiModelProperty(value = "组件", position = 8)
    private String component;

    /** 图标 */
    @ApiModelProperty(value = "图标", position = 9)
    private String icon;

    /** 图标类型 */
    @ApiModelProperty(value = "图标类型", position = 9)
    @NotNull
    @InEnum(SysIconTypeEnum.class)
    private Integer iconType = SysIconTypeEnum.ICONIFY.getType(); //写死了为icon，就是本地的，先不处理这个逻辑


    /** 是否可见 */
    @ApiModelProperty(value = "是否可见", position = 10)
    @NotNull
    @InEnum(CommonYesNoEnum.class)
    private Integer enable;

    /** 外链地址 */
    @ApiModelProperty(value = "外链地址", position = 7)
    private String href; //存在href为外链

    /** 是否可见 */
    @ApiModelProperty(value = "是否可见", position = 13)
    private Integer hideInMenu;

    /** 路由是否缓存
     * {@link com.nuocao.common.enums.CommonYesNoEnum}
     * */
    @ApiModelProperty(value = "路由是否缓存", position = 15)
    @NotNull
    @InEnum(CommonYesNoEnum.class)
    private Integer keepAlive;

    /** 当前路由对应的激活状态菜单 */
    @ApiModelProperty(value = "当前路由对应的激活状态菜单", position = 15)
    @Length(max = 250)
    private String activeMenu;

    /** 是否常量
     * {@link com.nuocao.common.enums.CommonYesNoEnum}
     * */
    @ApiModelProperty(value = "是否常量",notes = "常量菜单可不登录使用（要结合sa-token配置）", position = 15)
    @NotNull
    @InEnum(CommonYesNoEnum.class)
    private Integer constant=0;

    /** 是否支持多页签Tab */
    @ApiModelProperty(value = "是否支持多页签Tab", position = 15)
    @NotNull
    @InEnum(CommonYesNoEnum.class)
    private Integer multiTab=CommonYesNoEnum.YES.getValue();


    /** 固定在页签中的路由索引 */
    @ApiModelProperty(value = "固定在页签中的路由索引", position = 15)
    private Integer fixedIndexInTab;

    /** 菜单额外携带的参数 */
    @ApiModelProperty(value = "菜单额外携带的参数",notes = "json数组", position = 15)
    @Size(max = 10)
    private List<KeyValueData> query;

    /** 菜单额外携带的参数 */
    @ApiModelProperty(value = "菜单权限按钮",notes = "json数组", position = 15)
    @Size(max = 30)
    private List<SysMenuButton> buttons;

}
