package com.nuocao.sys.modular.menu.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 菜单按钮信息
 */
@Getter
@Setter
public class SysMenuButton {

    @ApiModelProperty(value = "菜单ID",notes = "新增时不传")
    private Long id;

    @ApiModelProperty("权限编码")
    @NotBlank
    private String code;

    @ApiModelProperty("权限标题")
    @NotBlank
    private String title;
}
