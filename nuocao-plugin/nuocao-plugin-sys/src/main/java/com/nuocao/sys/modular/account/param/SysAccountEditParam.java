package com.nuocao.sys.modular.account.param;

import com.nuocao.common.validation.Mobile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.Email;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;


/**
 * 系统账户 编辑参数
   用于管理员强制修改用户信息。一般不要用
 **/
@Getter
@Setter
public class SysAccountEditParam {

    @ApiModelProperty(value="账户ID")
    @NotNull(message = "ID必须填写")
    private Long id;


    /** 姓名 */
    @ApiModelProperty(value = "昵称", required = true, position = 2)
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    /** 手机 */
    @ApiModelProperty(value = "手机", position = 23)
    @NotBlank(message = "手机号必须填写")
    @Mobile
    private String phone;

    /** 邮箱 */
    @ApiModelProperty(value = "邮箱", position = 24)
    @Email
    private String email;

}
