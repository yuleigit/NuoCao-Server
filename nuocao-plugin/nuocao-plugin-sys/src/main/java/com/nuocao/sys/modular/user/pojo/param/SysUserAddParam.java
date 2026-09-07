package com.nuocao.sys.modular.user.pojo.param;

import com.nuocao.common.validation.Mobile;
import com.nuocao.sys.modular.user.pojo.SysUserBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import org.noear.solon.validation.annotation.Email;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * 用户添加参数
 **/
@Getter
@Setter
public class SysUserAddParam  extends SysUserBase {



    @ApiModelProperty(value = "用户角色")
    private List<Long> roleIds;

    @ApiModelProperty(value = "用户登录名")
    @NotBlank
    private String username;

    @ApiModelProperty(value = "密码")
    @NotBlank
    private String password;
}
