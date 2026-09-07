package com.nuocao.common.pojo;

import com.nuocao.common.enums.CommonExceptionEnum;
import com.nuocao.common.language.LanguageUtils;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 对Ajax请求返回Json格式数据的简易封装

 **/
public class CommonResult<T> implements Serializable{
    private static final long serialVersionUID = 1L;
    public static final int CODE_SUCCESS = CommonExceptionEnum.SUCCESS.getCode();
    public static final int CODE_ERROR = CommonExceptionEnum.ERROR500.getCode();

    @ApiModelProperty(value = "状态码")
    private int code;

    @ApiModelProperty(value = "提示语")
    private String msg;

    @ApiModelProperty(value = "返回数据")
    private T data;

    public CommonResult() {
    }

    public CommonResult(int code, String msg, T data) {
        this.setCode(code);
        this.setMsg(msg);
        this.setData(data);
    }

    /**
     * 获取code
     * @return code
     */
    public Integer getCode() {
        return this.code;
    }

    /**
     * 获取msg
     * @return msg
     */
    public String getMsg() {
        return this.msg;
    }
    /**
     * 获取data
     * @return data
     */
    public T getData() {
        return this.data;
    }

    /**
     * 给code赋值，连缀风格
     * @param code code
     * @return 对象自身
     */
    public CommonResult<T> setCode(int code) {
        this.code = code;
        return this;
    }

    /**
     * 给msg赋值，连缀风格
     * @param msg msg
     * @return 对象自身
     */
    public CommonResult<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    /**
     * 给data赋值，连缀风格
     * @param data data
     * @return 对象自身
     */
    public CommonResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    private static String t(String i18nCode){
        return LanguageUtils.getMessage(i18nCode);
    }

    // ============================  构建  ==================================

    // 构建成功
    public static <T> CommonResult<T> ok() {
        return new CommonResult<>(CODE_SUCCESS, t("common.request.success"), null);
    }
    public static <T> CommonResult<T> ok(String msg) {
        return new CommonResult<>(CODE_SUCCESS, msg, null);
    }
    public static <T> CommonResult<T> code(int code) {
        return new CommonResult<>(code, null, null);
    }
    public static <T> CommonResult<T> data(T data) {
        return new CommonResult<>(CODE_SUCCESS, t("common.request.success"), data);
    }

    // 构建失败
    public static <T> CommonResult<T> error() {
        return new CommonResult<>(CODE_ERROR, t("common.request.error"), null);
    }
    public static <T> CommonResult<T> error(String msg) {
        return new CommonResult<>(CODE_ERROR, msg, null);
    }
    public static <T> CommonResult<T> error(Integer code,String msg) {
        return new CommonResult<>(code, msg, null);
    }

    // 构建指定状态码
    public static <T> CommonResult<T> get(int code, String msg, T data) {
        return new CommonResult<>(code, msg, data);
    }

    /*
     * toString()
     */
    @Override
    public String toString() {
        return "{"
                + "\"code\": " + this.getCode()
                + ", \"msg\": \"" + this.getMsg() + "\""
                + ", \"data\": \"" + this.getData() + "\""
                + "}";
    }
}
