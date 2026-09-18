package com.nuocao.dev.core.config;

import com.github.xiaoymin.knife4j.solon.extension.OpenApiExtensionResolver;
import com.nuocao.dev.core.client.file.FileStorageClient;
import com.nuocao.dev.core.client.file.FileStorageFactory;
import com.nuocao.dev.core.client.sms.SmsChannelClient;
import com.nuocao.dev.core.client.sms.SmsChannelFactory;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.models.ApiInfo;

import java.util.List;

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

    /**
     *会自动去容器寻找 List<FileStorageClient>
     */
    @Bean
    public FileStorageFactory fileStorageFactory(@Inject List<FileStorageClient> clients) {
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
    public SmsChannelFactory smsChannelFactory(@Inject List<SmsChannelClient> clients) {
        SmsChannelFactory factory = new SmsChannelFactory();
        if (clients != null) {
            clients.forEach(factory::registerChannel);
        }
        return factory;
    }
}
