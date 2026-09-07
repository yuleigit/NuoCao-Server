package com.nuocao.data.access.datascope.annotation;


import com.nuocao.data.access.datascope.rule.DataScopeRule;

import java.lang.annotation.*;

/**
 * 数据权限注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Deprecated
public @interface  DataScope {
    /**
     * 当前类或方法是否开启数据权限
     * 即使不添加 @DataScope 注解，默认关闭
     * 可通过设置 enable 为 false 禁用
     */
    boolean enable() default true;

    /**
     * 生效的数据权限规则数组，优先级高于 {@link #excludeRules()}
     */
    Class<? extends DataScopeRule>[] includeRules() default {};

    /**
     * 排除的数据权限规则数组，优先级最低
     */
    Class<? extends DataScopeRule>[] excludeRules() default {};
}
