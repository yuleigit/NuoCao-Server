package com.nuocao.auth.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 租户中登录可用的用户列表
 */
@Getter
@Setter
public class AuthLoginUserDTO  {

    /** id */
    @ApiModelProperty(value = "用户ID", position = 1)
    private Long id;

    /** 账号 */
    @ApiModelProperty(value = "账号", position = 3)
    private String username;

    /** 姓名 */
    @ApiModelProperty(value = "姓名", position = 4)
    private String name;

    /** 昵称 */
    @ApiModelProperty(value = "昵称", position = 5)
    private String nickname;

    /** 最新登录时间 */
    @ApiModelProperty(value = "最新登录时间", position = 18)
    private Date latestLoginTime;

    /** 用户状态 */
    @ApiModelProperty(value = "用户状态", position = 20)
    private Integer status;

    @ApiModelProperty(value="租户ID", position = 22)
    private Long tenantId;

}
