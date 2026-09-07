package com.nuocao.sys.modular.role.pojo.param;

import com.nuocao.sys.modular.role.pojo.SysRoleBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import org.noear.solon.validation.annotation.Max;
import org.noear.solon.validation.annotation.Min;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 角色编辑参数
 **/
@Getter
@Setter
public class SysRoleEditParam extends SysRoleBase {

    /** id */
    @ApiModelProperty(value = "id", required = true, position = 1)
    @NotBlank
    private String id;

}
