package com.nuocao.common;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;

import java.util.Objects;

/**
 * 对象工具，对hutool等的封装
 */
public class ObjectUtils {

    public static boolean isNull(Object obj){
        return ObjectUtil.isNull(obj);
    }

    public static boolean isNotNull(Object obj){
        return ObjectUtil.isNotNull(obj);
    }

    public static boolean isEmpty(Object obj){
        return ObjectUtil.isEmpty(obj);
    }

    public static boolean isNotEmpty(Object obj){
        return ObjectUtil.isNotEmpty(obj);
    }

    public static boolean equals(Object obj1, Object obj2) {
        return equal(obj1, obj2);
    }

    public static boolean equal(Object obj1, Object obj2) {
        return ObjectUtil.equal(obj1,obj2);
    }

}
