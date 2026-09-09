package com.nuocao.dev.core.config;

import com.github.xiaoymin.knife4j.solon.extension.OpenApiExtensionResolver;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.models.ApiInfo;

@Configuration
public class DevConfigure {

    @Inject
    private OpenApiExtensionResolver openApiExtensionResolver;

    /**
     * API文档分组配置
     *
     **/
    @Bean("devDocApi")
    public DocDocket sysDocApi() {
        return new DocDocket()
                .info(new ApiInfo()
                        .title("开发配置")
                        .description("开发配置接口")
                        .version("1.0.0"))
                .groupName("开发配置")
                .apis("com.nuocao.dev")
                .basicAuth(openApiExtensionResolver.getSetting().getBasic())
                .vendorExtensions(openApiExtensionResolver.buildExtensions());
    }
}
