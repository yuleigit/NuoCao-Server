package com.nuocao.common.validation;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.validation.ValidatorFailureHandler;

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
