package com.nuocao.sys.modular.account.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.nuocao.common.handler.CommonSm4CbcTypeHandler;
import com.nuocao.common.pojo.CommonEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

/**
 * C端用户实体
 *
 * @author LEI
 **/
@Getter
@Setter
@Table(value = "sys_account")
public class SysAccount extends CommonEntity {

    /**
     * 账户ID
     */
    @ApiModelProperty(value = "账户ID", position = 1)
    @Id
    private Long id;


    /**
     * 账号
     */
    @ApiModelProperty(value = "账号", position = 4)
    private String username;

    /**
     * 密码
     */
    @JsonIgnore
    @ApiModelProperty(value = "密码", position = 5)
    private String password;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名", position = 6)
    private String name;

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
     * 上次登录ip
     */
    @ApiModelProperty(value = "上次登录ip", position = 30)
    private String lastLoginIp;

    /**
     * 上次登录地点
     */
    @ApiModelProperty(value = "上次登录地点", position = 31)
    private String lastLoginAddress;

    /**
     * 上次登录时间
     */
    @ApiModelProperty(value = "上次登录时间", position = 32)
    private LocalDateTime lastLoginTime;

    /**
     * 上次登录设备
     */
    @ApiModelProperty(value = "上次登录设备", position = 33)
    private String lastLoginDevice;

    /**
     * 最新登录ip
     */
    @ApiModelProperty(value = "最新登录ip", position = 34)
    private String latestLoginIp;

    /**
     * 最新登录地点
     */
    @ApiModelProperty(value = "最新登录地点", position = 35)
    private String latestLoginAddress;

    /**
     * 最新登录时间
     */
    @ApiModelProperty(value = "最新登录时间", position = 36)
    private LocalDateTime latestLoginTime;

    /**
     * 最新登录设备
     */
    @ApiModelProperty(value = "最新登录设备", position = 37)
    private String latestLoginDevice;

    /**
     * 用户状态
     */
    @ApiModelProperty(value = "用户状态", position = 38)
    private Integer status;


    /**
     * 用户来源
     * {@see FromSource}
     */
    @ApiModelProperty(value="用户来源")
    private String fromSource;

}
