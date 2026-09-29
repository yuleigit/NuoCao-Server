package com.nuocao.common.validation;


import org.noear.solon.validation.annotation.Validated;

import java.lang.annotation.*;

@Target({
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.PARAMETER,
        ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mobile {

    String message() default "手机号格式不正确";
    /**
     * 是否启用解密（默认为 false）
     */
    boolean encrypted() default false;
    Class<?>[] groups() default {};


}
