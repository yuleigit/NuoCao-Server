package com.nuocao.sys.core.config;

import com.github.xiaoymin.knife4j.solon.extension.OpenApiExtensionResolver;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.models.ApiInfo;

@Configuration
public class SysConfigure {

    @Inject
    private OpenApiExtensionResolver openApiExtensionResolver;

    /**
     * API文档分组配置
     *
     **/
    @Bean("sysDocApi")
    public DocDocket sysDocApi() {
        return new DocDocket()
                .info(new ApiInfo()
                        .title("系统管理")
                        .description("系统管理接口")
                        .version("1.0.0"))
                .groupName("系统管理")
                .apis("com.nuocao.sys")
                .basicAuth(openApiExtensionResolver.getSetting().getBasic())
                .vendorExtensions(openApiExtensionResolver.buildExtensions());
    }
}
