package com.nuocao.auth.api.dto;

import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 租户登录后的用户信息，实际鉴权、登录状态存储
 */
@Getter
@Setter
public abstract class SaUserLoginInfoDTO implements Serializable {

    /** id */
    @ApiModelProperty(value = "用户ID", position = 1)
    private Long id;

    /** 头像 */
    @ApiModelProperty(value = "头像", position = 2)
    private String avatar;

    /** 账号 */
    @ApiModelProperty(value = "账号", position = 3)
    private String username;

    /** 姓名 */
    @ApiModelProperty(value = "姓名", position = 4)
    private String name;

    /** 昵称 */
    @ApiModelProperty(value = "昵称", position = 5)
    private String nickname;

    /** 性别 */
    @ApiModelProperty(value = "性别", position = 6)
    private Integer gender;

    /** 出生日期 */
    @ApiModelProperty(value = "出生日期", position = 7)
    private String birthday;
    /** 手机 */
    @ApiModelProperty(value = "手机", position = 8)
    private String phone;

    /** 邮箱 */
    @ApiModelProperty(value = "邮箱", position = 9)
    private String email;

    /** 组织id */
    @ApiModelProperty(value = "组织id", position = 10)
    private Long orgId;

    /** 用户状态 */
    @ApiModelProperty(value = "用户状态", position = 20)
    private Integer status;


    @ApiModelProperty(value="租户ID", position = 22)
    private Long tenantId;

    /** 权限码集合 */
    @ApiModelProperty(value = "权限码集合", position = 23, hidden = true)
    private List<String> permissions;

    /** 角色码集合 */
    @ApiModelProperty(value = "角色代码集合", position = 24, hidden = true)
    private List<String> roles;

    /** 用户密码hash值 */
    @JsonIgnore
    @ApiModelProperty(value = "用户密码hash值", position = 25)
    private String password;

    /** 是否可登录，由继承类实现 */
    public abstract Boolean getEnabled();

    // ========== 上下文 ==========
    /**
     * 上下文字段，不进行持久化【暂时先这么用不然改的太多了，上边DataScope基本没用了，相关的权限认证基本没用了】
     *
     * 1. 用于基于 LoginUser 维度的临时缓存
     */
    @JsonIgnore
    private Map<String, Object> context;

    public void setContext(String key, Object value) {
        if (context == null) {
            context = new HashMap<>();
        }
        context.put(key, value);
    }

    public <T> T getContext(String key, Class<T> type) {
        return MapUtil.get(context, key, type);
    }
}
