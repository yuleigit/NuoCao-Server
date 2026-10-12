package com.nuocao.dev.core.config;

import com.github.xiaoymin.knife4j.solon.extension.OpenApiExtensionResolver;
import com.nuocao.dev.core.client.file.FileStorageClient;
import com.nuocao.dev.core.client.file.FileStorageFactory;
import com.nuocao.dev.core.client.sms.SmsChannelClient;
import com.nuocao.dev.core.client.sms.SmsChannelFactory;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.models.ApiInfo;

import java.util.List;

@Configuration
public class DevConfigure {

    /** 文档分组名 */
    private static final String GROUP_NAME = "开发配置";

    /** 分组描述 */
    private static final String GROUP_DESC = "开发配置模块接口文档";

    /** 文档版本 */
    private static final String DOC_VERSION = "1.0.0";

    /** 该分组扫描的根包路径 */
    private static final String SCAN_PACKAGE = "com.nuocao.dev";

    /**
     * API文档分组配置
     *
     * <p><b>必须显式写 bean 名，不能写成裸 {@code @Bean}</b>：knife4j 的文档分组 id 就是 bean 名，
     * 前端按 {@code /swagger/v2?group=<beanName>} 拉取分组。裸 {@code @Bean} 在 Solon 里
     * 拿不到方法名、bean 名为空，会被分组过滤器丢掉，doc.html 就什么都显示不出来。</p>
     **/
    @Bean("devDocApi")
    public DocDocket devDocApi(@Inject OpenApiExtensionResolver extensionResolver) {
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

    /**
     *会自动去容器寻找 List<FileStorageClient>
     */
    @Bean
    public FileStorageFactory fileStorageFactory(@Inject List<FileStorageClient> clients, AppContext context) {
        FileStorageFactory factory = new FileStorageFactory();
        if (clients != null) {
            clients.forEach(factory::registerEngine);
        }
        return factory;
    }


    /**
     * 注册短信通道
     */
    @Bean
    public SmsChannelFactory smsChannelFactory(@Inject List<SmsChannelClient> clients, AppContext context) {
        SmsChannelFactory factory = new SmsChannelFactory();
        if (clients != null) {
            clients.forEach(factory::registerChannel);
        }
        return factory;
    }
}
