package com.nuocao.sys.modular.user.pojo.param;

import com.mybatisflex.annotation.Id;
import com.nuocao.sys.modular.user.pojo.SysUserBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import org.noear.solon.validation.annotation.Email;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

import javax.validation.constraints.Pattern;

@Getter
@Setter
public class SysUserEditParam extends SysUserBase {

    /**
     * id
     */
    @ApiModelProperty(value = "userId", position = 1)
    @Id
    private Long id;


}
