package com.nuocao.common.validation;

import com.nuocao.common.enums.StrArrayValuable;

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

public @interface InStrEnum {

    /**
     * @return 实现 EnumValuable 接口的
     */
    Class<? extends StrArrayValuable> value();

    String message() default "必须在指定范围 {value}";

    Class<?>[] groups() default {};

    String split() default "";

    boolean ignoreCase() default  false;


}
