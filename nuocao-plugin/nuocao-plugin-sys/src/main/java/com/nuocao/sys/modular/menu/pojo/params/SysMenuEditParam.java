package com.nuocao.sys.modular.menu.pojo.params;

import com.nuocao.common.enums.CommonYesNoEnum;
import com.nuocao.common.pojo.KeyValueData;
import com.nuocao.common.validation.InEnum;
import com.nuocao.sys.modular.menu.enums.SysIconTypeEnum;
import com.nuocao.sys.modular.menu.pojo.SysMenuBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import org.noear.solon.validation.annotation.Length;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;
import org.noear.solon.validation.annotation.Size;

import java.util.List;

/**
 * 菜单编辑参数
 **/
@Getter
@Setter
public class SysMenuEditParam extends SysMenuBase {

    /** 菜单ID */
    @ApiModelProperty(value = "菜单ID", required = true, position = 1)
    @NotNull
    private Long id;


}
