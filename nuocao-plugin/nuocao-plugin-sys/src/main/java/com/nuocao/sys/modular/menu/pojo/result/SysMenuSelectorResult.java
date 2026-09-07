package com.nuocao.sys.modular.menu.pojo.result;

import com.mybatisflex.annotation.Id;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SysMenuSelectorResult {
    /** id */
    @ApiModelProperty(value = "menuId", position = 1)
    @Id
    private Long id;
    /** 父id */
    @ApiModelProperty(value = "菜单类型", position = 2)
    private String type;
    /** 父id */
    @ApiModelProperty(value = "父id", position = 2)
    private Long parentId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("标题")
    private String i18nKey;

    private List<SysMenuSelectorResult> children;
}
