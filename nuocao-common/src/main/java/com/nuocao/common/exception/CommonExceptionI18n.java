package com.nuocao.common.exception;

import cn.hutool.core.util.StrUtil;
import com.nuocao.common.language.LanguageUtils;
import com.nuocao.common.utils.StrFormatUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 通用异常 处置工具类

 */
@Getter
@Setter
public class CommonExceptionI18n extends CommonException{


    public CommonExceptionI18n() {
        super(LanguageUtils.getMessage("common.request.error"));
        super.setCode(500);
        super.setMsg(super.getMessage());
    }

    public CommonExceptionI18n(String i18nKey, Object... arguments) {
        super(LanguageUtils.getMessage(i18nKey,arguments));
        super.setCode(500);
        super.setMsg(super.getMessage());
    }

    public CommonExceptionI18n(Integer code, String i18nKey, Object... arguments) {
        super(StrFormatUtils.doFormat(i18nKey, arguments));
        super.setCode(code);
        super.setMsg(super.getMessage());
    }

    private static void throwExceptionI18n(String i18nKey, Object... arguments){
        throw new CommonExceptionI18n(i18nKey,arguments);
    }

    /**
     * 不允许为空对象
     * @param obj
     * @param i18nKey
     * @param arguments
     */
    public static <T> void notNull(T obj,String i18nKey, Object... arguments){
        if(obj==null){
            throwExceptionI18n(i18nKey,arguments);
        }
    }

    /**
     * 验证必须是空
     * @param obj
     * @param i18nKey
     * @param arguments
     * @param <T>
     */
    public static <T> void isNull(T obj, String i18nKey, Object... arguments) {
        if (obj != null) {
            throwExceptionI18n(i18nKey, arguments);
        }
    }


    public static void notEmpty(String str, String i18nKey, Object... params) {
        if (str == null || str.length()<1) {
            throwExceptionI18n( i18nKey, params);
        }
    }

    /**
     * 不允许为空对象
     * @param obj
     * @param i18nKey
     * @param arguments
     */
    public static <T> void notEmpty(List<T> obj, String i18nKey, Object... arguments) {
        if (obj == null || obj.isEmpty()) {
            throwExceptionI18n( i18nKey, arguments);
        }
    }

    /**
     * 不允许为空字符串
     * @param str
     * @param i18nKey
     * @param arguments
     */
    public static void notBlank(String str,String i18nKey, Object... arguments){
        if(StrUtil.isBlank(str)){
            throwExceptionI18n(i18nKey,arguments);
        }
    }

    /**
     * 需要时真的
     * @param bo 值
     * @param i18nKey
     * @param arguments
     */
    public static void isTrue(Boolean bo,String i18nKey, Object... arguments){
        if(!bo){
            throwExceptionI18n(i18nKey,arguments);
        }
    }


}
