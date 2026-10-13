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

    /**
     * 忽略租户校验的接口路径（Ant 风格），与 Solon 路由同口径用 pathNew() 匹配。
     * required = false：配置缺失时退化为"空白名单"（所有请求都校验租户），属于安全方向。
     */
    @Inject(value = "${tenant.path-ignore}", required = false)
    private List<String> ignorePathList;

    /**
     * 是否启用多租户。
     * <p>字段保留包装类型是为了沿用既有的 {@code getTenantEnable()} 调用点；但配置缺失时不能为 null，
     * 因此这里显式给默认值 + {@code required = false}，避免 {@code !getTenantEnable()} 拆箱 NPE。
     * 新增代码请统一使用 {@link #isEnabled()}。
     */
    @Inject(value = "${tenant.enable}", required = false)
    private Boolean tenantEnable = Boolean.FALSE;

    /**
     * 空安全的"是否启用多租户"判断，所有内部使用点优先走这里。
     */
    public boolean isEnabled() {
        return Boolean.TRUE.equals(tenantEnable);
    }
}
