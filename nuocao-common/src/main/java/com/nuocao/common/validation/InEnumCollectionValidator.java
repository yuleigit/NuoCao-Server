package com.nuocao.common.validation;

import cn.hutool.core.util.StrUtil;
import com.nuocao.common.enums.IntArrayValuable;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

import java.util.*;
import java.util.stream.Collectors;

public class InEnumCollectionValidator implements Validator<InEnum> {
    public static final InEnumCollectionValidator instance = new InEnumCollectionValidator();

    List<Integer> values = new ArrayList<>();
    @Override
    public String message(InEnum anno) {
        return anno.message();
    }

    @Override
    public Class<?>[] groups(InEnum anno) {
        return anno.groups();
    }

    /**
     * 校验实体的字段（注解在参数或字段上有效，即注入后的校验）
     * */
    @Override
    public Result validateOfValue(InEnum anno, Object val0, StringBuilder tmp) {
        if (val0 != null && !(val0 instanceof Integer)) {
            return Result.failure();
        }
        Integer val = (Integer) val0;
        if (!verify(anno, val)) {
            return Result.failure();
        } else {
            return Result.succeed();
        }
    }

    /**
     * 校验上下文的参数（注解在方法上有效，即注入前的校验）
     * */
    @Override
    public Result validateOfContext(Context ctx, InEnum anno, String name, StringBuilder tmp) {

        Object val = ctx.param(name);
        if (!verify(anno, val)) {
            return Result.failure();
        } else {
            return Result.succeed();
        }
    }

    private boolean verify(InEnum anno, Object val) {
        //如果为空，算通过（交由 @NotNull 或 @NotEmpty 或 @NotBlank 进一步控制）
        if(val==null) return true;
        IntArrayValuable[] valueAry = anno.value().getEnumConstants();

        List<Integer> allowedValues= null;
        if (valueAry.length == 0) {
            allowedValues = Collections.emptyList();
        } else {
            allowedValues = Arrays.stream(valueAry[0].array())
                    .boxed()
                    .collect(Collectors.toList());
        }
        this.values=allowedValues;
        if (val instanceof Collection) {
            return validateCollection(anno,(Collection<?>) val, allowedValues);
        } else if (val.getClass().isArray()) {
            return validateArray(anno,val, allowedValues);
        } else if (val instanceof String) {
            return validateString(anno,(String) val, allowedValues);
        }else if(val instanceof Integer){
            return allowedValues.contains((Integer) val);
        }

        return false;
    }


    private boolean validateCollection(InEnum anno,Collection<?> collection, List<Integer> allowedValues) {
        if (collection.isEmpty()) {
            return true;
        }
        return collection.stream()
                .allMatch(item -> validateSingleValue(anno,item, allowedValues));
    }

    private boolean validateArray(InEnum anno, Object array, List<Integer> allowedValues) {
        int length = java.lang.reflect.Array.getLength(array);
        if (length == 0) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            Object item = java.lang.reflect.Array.get(array, i);
            if (!validateSingleValue(anno, item, allowedValues)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateString(InEnum anno, String val, List<Integer> allowedValues) {
        if (StrUtil.isBlank(val)) {
            return true;
        }

        // 支持逗号分隔的多个值
        if (StrUtil.isNotBlank(anno.split())) {
            return Arrays.stream(val.split(anno.split()))
                    .map(String::trim)
                    .allMatch(v -> validateSingleValue(anno, v, allowedValues));
        }

        return validateSingleValue(anno, val, allowedValues);
    }

    private boolean validateSingleValue(InEnum anno, Object value, List<Integer> allowedValues) {
        if (value == null) {
            return true;
        }

        String strValue = value.toString();

        return allowedValues.contains(strValue);
    }



}

