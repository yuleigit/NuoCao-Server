package com.nuocao.data.access.tenant.annotation;

import com.nuocao.data.access.tenant.aop.TenantAspect;
import org.noear.solon.annotation.Around;

import java.lang.annotation.*;

/**
 * 忽略租户注解
 */
@Around(TenantAspect.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TenantIgnore {
}
