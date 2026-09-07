package com.nuocao.sys.modular.user.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.noear.solon.validation.annotation.Email;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

import javax.validation.constraints.Pattern;

@Getter
@Setter
public class SysUserBase {
    /** 手机号 */
    @ApiModelProperty(value = "手机号码",required = true,position = 2)
    @NotBlank(message = "电话号码必须填写")
    private String phone;

    /** email邮箱地址 */
    @ApiModelProperty(value = "email邮箱地址",required = true,position = 2)
    @Email
    private String email;

    /** 姓名 */
    @ApiModelProperty(value = "姓名", required = true, position = 2)
    @NotBlank(message = "name不能为空")
    private String name;

    /** 昵称 */
    @ApiModelProperty(value = "昵称", position = 9)
    private String nickname;
    /** 组织id */
    @ApiModelProperty(value = "组织id", required = true, position = 3)
    @NotNull(message = "组织不能为空")
    private Long orgId;

    /** 性别 */
    @ApiModelProperty(value = "性别", position = 10)
    private Integer gender;

    /** 出生日期 */
    @ApiModelProperty(value = "出生日期", position = 12)
    @Pattern( regexp = "^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
            message = "生日格式必须为 yyyy-MM-dd")
    private String birthday;
}
