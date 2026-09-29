package com.nuocao.sys.modular.user.pojo.param;

import com.nuocao.sys.modular.user.pojo.SysUserBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

import java.util.List;

/**
 * 设置用户角色请求参数
 **/
@Getter
@Setter
public class SysUserSetRolesParam  {


    @ApiModelProperty(value = "用户ID",required = true)
    @NotNull
    private Long userId;

    @ApiModelProperty(value = "用户角色Id集合")
    private List<Long> roleIds;

}
