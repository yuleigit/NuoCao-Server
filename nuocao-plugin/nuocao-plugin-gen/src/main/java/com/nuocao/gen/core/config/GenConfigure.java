package com.nuocao.gen.core.config;

import com.github.xiaoymin.knife4j.solon.extension.OpenApiExtensionResolver;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.models.ApiInfo;

@Configuration
public class GenConfigure {

    @Inject
    private OpenApiExtensionResolver openApiExtensionResolver;

    /**
     * API文档分组配置
     *
     **/
    @Bean("genDocApi")
    public DocDocket genDocApi() {
        return new DocDocket()
                .info(new ApiInfo()
                        .title("代码生成GEN")
                        .description("代码生成GEN")
                        .version("1.0.0"))
                .groupName("代码生成GEN")
                .apis("com.nuocao.gen")
                .basicAuth(openApiExtensionResolver.getSetting().getBasic())
                .vendorExtensions(openApiExtensionResolver.buildExtensions());
    }
}
