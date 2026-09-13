package com.nuocao.common.annotation;

import com.nuocao.common.enums.CommonOperationTypeEnum;
import com.nuocao.common.enums.CommonOperatorTypeEnum;

import java.lang.annotation.*;

/**
 * 自定义日志注解
 * @author nuocao
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommonLog {

    /**
     * 操作标题
     */
    public String value() default "";

    /**
     * 操作
     */
    public CommonOperationTypeEnum operationType() default CommonOperationTypeEnum.OTHER;

    /**
     * 操作人类型
     */
    public CommonOperatorTypeEnum operatorType() default CommonOperatorTypeEnum.ADMIN;

    /**
     * 是否保存请求的参数
     */
    public boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的参数
     */
    public boolean isSaveResponseData() default true;

    /**
     * 排除指定的请求参数
     */
    public String[] excludeParamNames() default {};
}
