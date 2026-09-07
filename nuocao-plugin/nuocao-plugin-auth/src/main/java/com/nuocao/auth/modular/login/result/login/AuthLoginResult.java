package com.nuocao.auth.modular.login.result.login;

import lombok.Data;

/**
 * 登录结果
 */
@Data
public class AuthLoginResult {
    private Long tenantId;

    private String token;

    /**
     * 登录的账号,非必要,仅loginToTenant使用
     */
    private String username;

    /**
     * 登录人姓名，非必要，仅loginToTenant使用
     */
    private String name;

    public AuthLoginResult(String token,Long tenantId){
        this.tenantId=tenantId;
        this.token=token;
    }
}
