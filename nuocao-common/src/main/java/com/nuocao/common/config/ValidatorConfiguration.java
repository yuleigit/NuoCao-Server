package com.nuocao.common.config;

import com.nuocao.common.validation.Mobile;
import com.nuocao.common.validation.MobileValidator;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.validation.ValidatorFailureHandler;
import org.noear.solon.validation.ValidatorFailureHandlerI18n;
import org.noear.solon.validation.ValidatorManager;

/**
 * 验证配置
 */
@Configuration
public class ValidatorConfiguration {
    /**
     * 支持：message="{aaa} bb {ccc.ddd}"
     * */
    @Bean
    public ValidatorFailureHandler validatorFailureHandler() {
        return new NuoCaoValidatorFailureHandler(2048);
    }
}
