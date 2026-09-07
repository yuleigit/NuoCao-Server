package com.nuocao.sys.api.dto;

import lombok.Data;

import java.util.Date;

/**
 * 用户数据 pojo
 */
@Data
public class SysUserDTO {

    private Long id;

    private String username ="";

    private String name="";

    private String nickname="";

    private String phone="";

    /** 最新登录时间 */
    private Date latestLoginTime;

    /** 用户状态 */
    private Integer status;

    private Long tenantId;
}
