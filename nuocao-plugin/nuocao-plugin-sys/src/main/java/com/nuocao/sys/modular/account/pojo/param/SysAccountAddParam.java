
package com.nuocao.sys.modular.account.pojo.param;

import com.nuocao.common.validation.Mobile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.Email;
import org.noear.solon.validation.annotation.NotBlank;


/**
 * 系统账户添加类

 **/
@Getter
@Setter
public class SysAccountAddParam {

    /** 账号 */
    @ApiModelProperty(value = "账号", required = true, position = 1)
    @NotBlank(message = "account不能为空")
    private String username;

    /** 姓名 */
    @ApiModelProperty(value = "昵称", required = true, position = 2)
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @ApiModelProperty(value = "密码")
    private String password;

    /** 手机 */
    @ApiModelProperty(value = "手机", position = 23)
    @NotBlank(message = "手机号必须填写")
    @Mobile(message = "手机号码格式错误")
    private String phone;

    /** 邮箱 */
    @ApiModelProperty(value = "邮箱", position = 24)
    @Email
    private String email;

}
