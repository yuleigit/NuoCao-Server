package com.nuocao.auth.api.dto;

import lombok.Data;

/**
 * 登录结果通用DTO
 */
@Data
public class AuthLoginResultDTO {
    private Long tenantId;

    private String token;

    /**
     * 登录的账号,非必要,仅loginToTenant使用
     */
    private String account;

    /**
     * 登录人姓名，非必要，仅loginToTenant使用
     */
    private String name;

    public AuthLoginResultDTO(String token, Long tenantId){
        this.tenantId=tenantId;
        this.token=token;
    }
}