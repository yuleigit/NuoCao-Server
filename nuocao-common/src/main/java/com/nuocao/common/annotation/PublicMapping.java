package com.nuocao.common.annotation;

import java.lang.annotation.*;

/**
 * 公开mapping注解，用户不需要登录即可访问
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface PublicMapping {
}
