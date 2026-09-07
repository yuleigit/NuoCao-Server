package com.nuocao.data.access.tenant.config;

import com.mybatisflex.core.tenant.TenantFactory;
import com.nuocao.data.access.tenant.aop.NuoCaoTenantFactory;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;



/**
 * 多租户 自动配置 mybatis-flex tenantFaction

 **/
@Configuration
public class TenantFactoryConfigure {

    @Bean
    public TenantFactory tenantFactory(){
        TenantFactory tenantFactory = new NuoCaoTenantFactory();
        return tenantFactory;
    }

}
