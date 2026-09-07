package com.nuocao.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import org.noear.solon.validation.ValidUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern PATTERN_MOBILE = Pattern.compile("^(?:(?:\\+|00)86)?1(?:(?:3[\\d])|(?:4[0,1,4-9])|(?:5[0-3,5-9])|(?:6[2,5-7])|(?:7[0-8])|(?:8[\\d])|(?:9[0-3,5-9]))\\d{8}$");

    private static final Pattern PATTERN_URL = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    private static final Pattern PATTERN_XML_NCNAME = Pattern.compile("[a-zA-Z_][\\-_.0-9_a-zA-Z$]*");

    private static final Pattern PATTERN_LOWER_NUMBER = Pattern.compile("[a-z][_a-z0-9]*");
    /**
     * 邮箱正则表达式
     * 规则：名称允许字母、数字、下划线、中划线、点，域名允许字母、数字、中划线
     */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    public static boolean isMobile(String mobile) {
        return StrUtil.isNotBlank(mobile)
                && PATTERN_MOBILE.matcher(mobile).matches();
    }

    public static boolean isURL(String url) {
        return StrUtil.isNotBlank(url)
                && PATTERN_URL.matcher(url).matches();
    }

    public static boolean isXmlNCName(String str) {
        return StrUtil.isNotBlank(str)
                && PATTERN_XML_NCNAME.matcher(str).matches();
    }
    public static void validate(Object object, Class<?>... groups) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Assert.notNull(validator);
        validate(validator, object, groups);
    }
    public static void validate(Validator validator, Object object, Class<?>... groups) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (CollUtil.isNotEmpty(constraintViolations)) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }
    /**
     * 验证是否为合法邮箱
     */
    public static boolean isEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    public static void validateEntity(Object obj, Class<?>... groups){
        ValidUtils.validateEntity(obj,groups);
    }


    /**
     * 验证是否为小写字母+数字的组合
     * @param str
     */
    public static boolean isLowerNumber(String str){
        return StrUtil.isNotBlank(str)
                && PATTERN_LOWER_NUMBER.matcher(str).matches();
    }
}
