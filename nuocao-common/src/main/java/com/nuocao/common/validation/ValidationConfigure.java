package com.nuocao.common.validation;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.validation.ValidatorFailureHandler;
import org.noear.solon.validation.ValidatorManager;

/**
 * 验证器注册器
 * @author LEI
 */
@Configuration
public class ValidationConfigure {
    @Bean
    public void adapter() {
        //
        // 此处为注册验证器。如果有些验证器重写了，也是在此处注册
        //
        ValidatorManager.register(InStrEnum.class, new InStrEnumCollectionValidator());
        ValidatorManager.register(InEnum.class, new InEnumCollectionValidator());
        ValidatorManager.register(Mobile.class, new MobileValidator());
    }

    /**
     * 支持：message="{aaa} bb {ccc.ddd}"
     * */
    @Bean
    public ValidatorFailureHandler validatorFailureHandler() {
        return new NuoCaoValidatorFailureHandler(2048);
    }}
