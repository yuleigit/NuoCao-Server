package com.nuocao.sys.api.dto;

import lombok.Data;

/**
 * 角色数据 pojo
 */
@Data
public class SysRoleDTO {

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 角色权重，
     */
    private Integer weight;

}
