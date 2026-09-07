package com.nuocao.sys.modular.account.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 简单的用户信息
 *
 **/
@Getter
@Setter
public class SysAccountPageResult {

    /** 账号ID */
    @ApiModelProperty(value = "账号ID")

    private Long id;
    /** 用户账号 */
    @ApiModelProperty(value = "用户账号")
    private String username;

    /** 用户姓名 */
    @ApiModelProperty(value = "用户姓名")
    private String nickname;


    /** 用户姓名 */
    @ApiModelProperty(value = "手机号码")
    private String phone;
}
