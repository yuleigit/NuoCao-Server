package com.nuocao.sys.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * 用户数据范围组
 */
@Getter
@Setter
public class OrgDataScopeDTO {

    /**
     * 是否拥有全部权限
     */
    private Boolean all;

    /**
     * 是否可查看自己的数据
     */
    private Boolean self;
    /**
     * 部门ID集合
     */
    private Set<Long> orgIds;

    public OrgDataScopeDTO() {
        this.all = false;
        this.self = false;
        this.orgIds = new HashSet<>();
    }
}
