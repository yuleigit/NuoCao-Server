package com.nuocao.sys.core.config;

import com.github.xiaoymin.knife4j.solon.extension.OpenApiExtensionResolver;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.models.ApiInfo;

@Configuration
public class SysConfigure {

    /** 文档分组名 */
    private static final String GROUP_NAME = "系统管理";

    /** 分组描述 */
    private static final String GROUP_DESC = "系统模块接口文档";

    /** 文档版本 */
    private static final String DOC_VERSION = "1.0.0";

    /** 该分组扫描的根包路径 */
    private static final String SCAN_PACKAGE = "com.nuocao.sys";

    @Bean
    public DocDocket systemDocApi(@Inject OpenApiExtensionResolver extensionResolver) {
        return new DocDocket()
                .info(new ApiInfo()
                        .title(GROUP_NAME)
                        .description(GROUP_DESC)
                        .version(DOC_VERSION))
                .groupName(GROUP_NAME)
                .apis(SCAN_PACKAGE)
                .basicAuth(extensionResolver.getSetting().getBasic())
                .vendorExtensions(extensionResolver.buildExtensions());
    }
}
