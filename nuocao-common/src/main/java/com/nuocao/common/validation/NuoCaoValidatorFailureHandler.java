package com.nuocao.common.validation;


import com.nuocao.common.language.LanguageUtils;
import com.nuocao.common.utils.ConvertUtils;
import com.nuocao.common.utils.StringUtils;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.expression.snel.SnelParser;
import org.noear.solon.i18n.I18nUtil;
import org.noear.solon.validation.ValidatorException;
import org.noear.solon.validation.ValidatorFailureHandler;
import org.noear.solon.validation.ValidatorFailureHandlerI18n;
import org.noear.solon.validation.annotation.Length;
import org.noear.solon.validation.annotation.Pattern;

import javax.validation.constraints.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 验证配置，自定载入valid message {@link ValidatorFailureHandlerI18n}
 */
public class NuoCaoValidatorFailureHandler implements ValidatorFailureHandler {
    private final SnelParser SNEL;
    private final Set<String> VALIDATION_I18N_LENETN_SIZE_NAME_SET=Set.of("Length","Size");

    public NuoCaoValidatorFailureHandler(int cahceCapacity) {
        //解析消息体中的变量
        this.SNEL = new SnelParser(cahceCapacity, '#', '{');
    }

    public NuoCaoValidatorFailureHandler(SnelParser snelParser) {
        Objects.requireNonNull(snelParser, "snelParser");
        this.SNEL = snelParser;
    }

    public boolean onFailure(Context ctx, Annotation anno, Result rst, String msg) throws Throwable {

       msg = this.format(anno, rst, msg);
       throw new ValidatorException(rst.getCode(), msg, anno, rst);
    }

    //由于我这个format需要按模块提示，所以主要是改了LangageUtils的方法
    public String format(Annotation anno, Result rst, String msg) {
        String annoName= anno.annotationType().getSimpleName();
        if(StringUtils.isBlank(msg)){ //如果消息是空的，则使用默认注解的题似乎处理，默认注解题似乎采用SNEL解析格式

        }
        //1.如果消息为空，则启用默认注解消息体处理
        if (Utils.isEmpty(msg)) {
            msg = "{common.validation." + annoName + "}";

//            if (anno != null) {
//                if (Utils.isEmpty(rst.getDescription())) {
//                    msg = (new StringBuilder(100)).append("@").append(anno.annotationType().getSimpleName()).append(" verification failed").toString();
//                } else {
//                    msg = (new StringBuilder(100)).append("@").append(anno.annotationType().getSimpleName()).append(" verification failed: ").append(rst.getDescription()).toString();
//                }
//            }
        } //else
        //2.是i18n的接口消息，则从i18n中得到消息替换他
        if (isI18nMsg(msg)) {
            Context ctx = Context.current();
            String i18nKey= StringUtils.substring(msg,1,msg.length()-1);
            //获取i18n配置，如果不存在则返回空字符
            msg=LanguageUtils.getMessageOrDefault(ctx,i18nKey,"");
        }
        //3.处理后，消息仍为空，则使用系统默认错误处理
        if(StringUtils.isBlank(msg)){
            if (anno != null) {
                if (Utils.isEmpty(rst.getDescription())) {
                    msg = (new StringBuilder(100)).append("@").append(anno.annotationType().getSimpleName()).append(" verification failed").toString();
                } else {
                    msg = (new StringBuilder(100)).append("@").append(anno.annotationType().getSimpleName()).append(" verification failed: ").append(rst.getDescription()).toString();
                }
            }
            return msg;
        }

        //4.处理SNEL消息解析
        if(!this.SNEL.hasMarker(msg)){
            return msg;
        }

        //4.1得到anno中携带的变量
        Map<String,Object> valueMap =getAnnoAttributes(anno);
        //4.1.1返回属性为为空、不是SNEL参数消息，则直接返回
        if(valueMap.isEmpty() ){
            return msg;
        }
        //如果是min-max区间配置，则获取上线下线（支持Size，Length)
//        if(VALIDATION_I18N_LENETN_SIZE_NAME_SET.contains(annoName))
//        {
//            Integer min = (Integer) valueMap.get("min");
//            Integer max = (Integer) valueMap.get("max");
//            if(min!=null && min.equals(0) && max!=null && max!=Integer.MAX_VALUE){
//
//            }
//        }
        //写入属性名称
        valueMap.put("name",rst.getDescription());

        if(!valueMap.isEmpty() && this.SNEL.hasMarker(msg)){
            msg = (String)this.SNEL.forTmpl().parse(msg).eval((key) -> {
                String k = key.toString();
                // 1. 优先从注解属性里找 (比如 min, max, value)
                if (valueMap.containsKey(k)) {
                    return String.valueOf(valueMap.get(k));
                }
                return "";
            });
        }

        return msg;
    }

    /**
     * 约定，验证注解中使用多语言标记时，以{开头，}结尾
     * @param msg
     * @return
     */
    private boolean isI18nMsg(String msg){
       return msg != null && msg.startsWith("{") && msg.endsWith("}");
    }

    /**
     * 获取Annotation携带的值，用于SNEL解析参数
     * @param anno 验证注解
     * @return  Map<Key,Value></Key,Value>
     */
    public static Map<String, Object> getAnnoAttributes(Annotation anno) {
        Map<String, Object> attrs = new HashMap<>();

        // 提取注解的简单类名，如 "Length"
        // attrs.put("annoName", anno.annotationType().getSimpleName());

        Method[] methods = anno.annotationType().getDeclaredMethods();
        for (Method method : methods) {
            try {
                String methodName= method.getName();
                // 排除掉值解析时不需要的属性
                if ("message".equals(methodName) || "groups".equals(methodName) || "payload".equals(methodName)) {
                    continue;
                }
                attrs.put(method.getName(), method.invoke(anno));
            } catch (Exception ignored) {}
        }

        return attrs;
        /* 老版本，不方便维护
        if (anno instanceof Length) {
            Length l = (Length) anno;
            attrs.put("min", l.min());
            attrs.put("max", l.max());
        } else if (anno instanceof Max) {
            attrs.put("value", ((Max) anno).value());
        } else if (anno instanceof Min) {
            attrs.put("value", ((Min) anno).value());
        } else if (anno instanceof DecimalMax) {
            attrs.put("value", ((DecimalMax) anno).value());
        } else if (anno instanceof DecimalMin) {
            attrs.put("value", ((DecimalMin) anno).value());
        } else if (anno instanceof Pattern) {
            attrs.put("value", ((Pattern) anno).value());
        } else if (anno instanceof Size) {
            Size s = (Size) anno;
            attrs.put("min", s.min());
            attrs.put("max", s.max());
        }
        // 对于 NotNull, NotBlank, Email 等无参注解，通常只需要类名即可

        return attrs;

         */
    }
}
