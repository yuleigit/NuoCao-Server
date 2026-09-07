package com.nuocao.sys.modular.route.result;

import com.mybatisflex.annotation.Id;
import com.nuocao.common.pojo.KeyValue;
import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户菜单路由返回实体
 * 提供给soybean解析的
 */
@Setter
@Getter
public class MenuRouteResult {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private Long id;

    /** （路由名称，英文） */
    @ApiModelProperty(value = "别名（路由名称，英文）", position = 4)
    private String name;

    /** 路径(路由路径） */
    @ApiModelProperty(value = "路径(路由路径）", position = 9)
    private String path;

    /** 组件路径 */
    @ApiModelProperty(value = "组件路径", position = 10)
    private String component;

    /**
     * 路由属性描述
     */
    private MenuRouteMetaResult meta =new MenuRouteMetaResult();

    private List<MenuRouteResult> children ;


    /**
     * 菜单meta信息
     */
    @Getter
    @Setter
    public static class MenuRouteMetaResult {
        /** 标题 */
        @ApiModelProperty(value = "标题", position = 3)
        private String title;

        /** 多语言key（前端用） */
        @ApiModelProperty(value = "多语言key", position = 3)
        private String i18nKey;


        /** 排序（前端用），对应sortCode */
        @ApiModelProperty(value = "排序（前端用）", position = 3)
        private Integer order;


        /** 路由是否缓存
         * {@link com.nuocao.common.enums.CommonYesNoEnum}
         * */
        @ApiModelProperty(value = "路由是否缓存", position = 15)
        private Boolean keepAlive;

        /** 是否常量
         * {@link com.nuocao.common.enums.CommonYesNoEnum}
         * */
//        @ApiModelProperty(value = "是否常量",notes = "常量菜单可不登录使用（要结合sa-token配置）", position = 15)
//        private Boolean constant;


        /** 图标 */
        @ApiModelProperty(value = "图标", position = 11)
        private String icon;

        /** 本地图标 */
        @ApiModelProperty(value = "图标", position = 11)
        private String localIcon;

        /** 外链连接 */
        @ApiModelProperty(value = "外链连接", position = 12)
        private String href;

        /** 是否可见 */
        @ApiModelProperty(value = "是否可见", position = 13)
        private Boolean hideInMenu;


        /** 当前路由对应的激活状态菜单 */
        @ApiModelProperty(value = "当前路由对应的激活状态菜单", position = 15)
        private String activeMenu;


        /** 是否支持多页签Tab */
        @ApiModelProperty(value = "是否支持多页签Tab", position = 15)
        private Boolean multiTab;


        /** 固定在页签中的路由索引 */
        @ApiModelProperty(value = "固定在页签中的路由索引", position = 15)
        private Integer fixedIndexInTab;


        /** 菜单额外携带的参数 */
        @ApiModelProperty(value = "菜单额外携带的参数", position = 15)
        private List<KeyValue<String,Object>> query;

        /**
         * 按钮集合
         */
//        private List<MenuRouteButtonResult> buttons;

    }

    /**
     * 菜单按钮
     */
    @Getter
    @Setter
    public static class MenuRouteButtonResult
    {
        /**
         * 按钮描述
         */
        private String desc;
        /**
         * 按钮权限标识
         */
        private String code;

        public MenuRouteButtonResult(String code ,String desc){
            this.desc=desc;
            this.code=code;
        }
    }
}
