package com.nuocao.sys.api.dto;

import com.mybatisflex.annotation.Column;
import com.nuocao.common.handler.CommonSm4CbcTypeHandler;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 系统账户接口数据
 */
@Getter
@Setter
public class SysAccountDTO {
    @ApiModelProperty(value = "账户ID", position = 1)
    private Long id;
    /**
     * 账号
     */
    @ApiModelProperty(value = "账号", position = 4)
    private String username;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称", position = 7)
    private String nickname;

    /**
     * 手机
     */
    @ApiModelProperty(value = "手机", position = 23)
    @Column(typeHandler = CommonSm4CbcTypeHandler.class)
    private String phone;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱", position = 24)
    private String email;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", position = 24)
    private String password;

    /**
     * 用户状态
     */
    @ApiModelProperty(value = "用户状态", position = 38)
    private Integer status;
}
