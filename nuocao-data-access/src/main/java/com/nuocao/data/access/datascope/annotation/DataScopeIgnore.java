package com.nuocao.data.access.datascope.annotation;

import com.nuocao.data.access.datascope.aop.DataScopeAspect;
import org.noear.solon.annotation.Around;

import java.lang.annotation.*;

/**
 * 忽略数据权限
 */
@Around(DataScopeAspect.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataScopeIgnore {
}
