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
public class CommonException extends RuntimeException{

    private Integer code;

    private String msg;

    public CommonException() {
        super(LanguageUtils.getMessage("common.request.error"));
        this.code = 500;
        this.msg = super.getMessage();
    }

    public CommonException(String msg, Object... arguments) {
        super(StrFormatUtils.doFormat(msg, arguments));
        this.code = 500;
        this.msg = this.getMessage();
    }

    public CommonException(Integer code, String msg, Object... arguments) {
        super(StrFormatUtils.doFormat(msg, arguments));
        this.code = code;
        this.msg = super.getMessage();
    }

    public static void throwException(String msg, Object... arguments){
        throw new CommonException(msg,arguments);
    }

    /**
     * 不允许为空对象
     * @param obj
     * @param msg
     * @param arguments
     */
    public static <T> void notNull(T obj,String msg, Object... arguments){
        if(obj==null){
            throwException(msg,arguments);
        }
    }

    /**
     * 验证必须是空
     * @param obj
     * @param msg
     * @param arguments
     * @param <T>
     */
    public static <T> void isNull(T obj, String msg, Object... arguments) {
        if (obj != null) {
            throwException(msg, arguments);
        }
    }


    public static void notEmpty(String str, String msg, Object... params) {
        if (str == null || str.length()<1) {
            throwException( msg, params);
        }
    }

    /**
     * 不允许为空对象
     * @param obj
     * @param msg
     * @param arguments
     */
    public static <T> void notEmpty(List<T> obj, String msg, Object... arguments) {
        if (obj == null || obj.isEmpty()) {
            throwException( msg, arguments);
        }
    }

    /**
     * 不允许为空字符串
     * @param str
     * @param msg
     * @param arguments
     */
    public static void notBlank(String str,String msg, Object... arguments){
        if(StrUtil.isBlank(str)){
            throwException(msg,arguments);
        }
    }

    /**
     * 需要时真的
     * @param bo 值
     * @param msg
     * @param arguments
     */
    public static void isTrue(Boolean bo,String msg, Object... arguments){
        if(!bo){
            throwException(msg,arguments);
        }
    }

}
