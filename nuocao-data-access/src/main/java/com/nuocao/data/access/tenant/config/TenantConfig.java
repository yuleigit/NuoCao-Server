package com.nuocao.data.access.tenant.config;

import lombok.Getter;
import lombok.Setter;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.List;

/**
 * 多租户配置类
 */
@Component
@Setter
@Getter
public class TenantConfig {
    @Inject("${tenant.path-ignore}")
    private List<String> ignorePathList;

    @Inject("${tenant.enable}")
    private Boolean tenantEnable;
}
