package com.nuocao.auth.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统账户信息 （仅用于登录数值传递，不实际参与鉴权）
 *

 */
@Getter
@Setter
public  class AccountLoginInfoDTO implements Serializable {

    /** id */
    @ApiModelProperty(value = "账户ID", position = 1)
    private Long id;


    /** 账号 */
    @ApiModelProperty(value = "账号", position = 2)
    private String username;


    /** 昵称 */
    @ApiModelProperty(value = "昵称", position = 2)
    private String nickname;


    /** 电话 */
    @ApiModelProperty(value = "电话", position = 3)
    private String phone;

    /** email */
    @ApiModelProperty(value = "email", position = 3)
    private String email;

    /** 上次登录ip */
    @ApiModelProperty(value = "上次登录ip", position = 4)
    private String lastLoginIp;

    /** 上次登录地点 */
    @ApiModelProperty(value = "上次登录地点", position = 5)
    private String lastLoginAddress;

    /** 上次登录时间 */
    @ApiModelProperty(value = "上次登录时间", position = 6)
    private Date lastLoginTime;

    /** 上次登录设备 */
    @ApiModelProperty(value = "上次登录设备", position = 7)
    private String lastLoginDevice;

    /** 最新登录ip */
    @ApiModelProperty(value = "最新登录ip", position = 8)
    private String latestLoginIp;

    /** 最新登录地点 */
    @ApiModelProperty(value = "最新登录地点", position = 9)
    private String latestLoginAddress;

    /** 最新登录时间 */
    @ApiModelProperty(value = "最新登录时间", position = 10)
    private Date latestLoginTime;

    /** 最新登录设备 */
    @ApiModelProperty(value = "最新登录设备", position = 11)
    private String latestLoginDevice;

    /** 用户状态 */
    @ApiModelProperty(value = "用户状态", position = 12)
    private Integer status;

    /** 用户密码hash值 */
    @JsonIgnore
    @ApiModelProperty(value = "用户密码hash值", position = 13)
    private String password;



}
