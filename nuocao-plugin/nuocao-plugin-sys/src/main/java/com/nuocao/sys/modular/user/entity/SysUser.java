package com.nuocao.sys.modular.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.nuocao.common.handler.CommonSm4CbcTypeHandler;
import com.nuocao.common.pojo.CommonEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 用户实体
 *
 * @author LEI
 **/
@Getter
@Setter
@Table(value = "sys_user")
public class SysUser extends CommonEntity {

    /**
     * id
     */
    @ApiModelProperty(value = "userId", position = 1)
    @Id
    private Long id;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像", position = 2)
    private String avatar;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号",notes = "与acction表保持一致", position = 4)
    private String username;

    /**
     * 密码
     */
    @JsonIgnore
    @ApiModelProperty(value = "密码",notes = "暂时保留，登录已移到acction",position = 5)
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
     * 性别
     */
    @ApiModelProperty(value = "性别", position = 8)
    private Integer gender;

    /**
     * 出生日期
     */
    @ApiModelProperty(value = "出生日期", position = 10)
    private String birthday;


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
     * 组织id
     */
    @ApiModelProperty(value = "组织id", position = 32)
    private Long orgId;


    /**
     * 上次登录ip
     */
    @ApiModelProperty(value = "上次登录ip", position = 37)
    private String lastLoginIp;

    /**
     * 上次登录地点
     */
    @ApiModelProperty(value = "上次登录地点", position = 38)
    private String lastLoginAddress;

    /**
     * 上次登录时间
     */
    @ApiModelProperty(value = "上次登录时间", position = 39)
    private Date lastLoginTime;

    /**
     * 上次登录设备
     */
    @ApiModelProperty(value = "上次登录设备", position = 40)
    private String lastLoginDevice;

    /**
     * 最新登录ip
     */
    @ApiModelProperty(value = "最新登录ip", position = 41)
    private String latestLoginIp;

    /**
     * 最新登录地点
     */
    @ApiModelProperty(value = "最新登录地点", position = 42)
    private String latestLoginAddress;

    /**
     * 最新登录时间
     */
    @ApiModelProperty(value = "最新登录时间", position = 43)
    private Date latestLoginTime;

    /**
     * 最新登录设备
     */
    @ApiModelProperty(value = "最新登录设备", position = 44)
    private String latestLoginDevice;

    /**
     * 用户状态
     */
    @ApiModelProperty(value = "用户状态", position = 45)
    private Integer status;

    @ApiModelProperty(value="租户ID")
    @Column(tenantId = true)
    private Long tenantId;

}
