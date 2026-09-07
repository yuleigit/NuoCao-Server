package com.nuocao.common.mapper;

import com.mybatisflex.core.util.LambdaGetter;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapperLambdaUtils {
    private static final Map<Method, Class<?>> METHOD_RETURN_TYPE_CACHE = new ConcurrentHashMap<>();

    /**
     * 得到列明
     * @param fn
     * @return
     * @param <T>
     */
    public static <T> String getColumnName(LambdaGetter<T> fn) {
        try {
            Object serializedLambda = getSerializedLambda(fn);
            String methodName = invokeMethod(serializedLambda, "getImplMethodName");

            // 方法名转字段名
            String propertyName = methodName.replaceFirst("^(get|is)", "");
            propertyName = propertyName.substring(0, 1).toLowerCase()
                    + propertyName.substring(1);

            // 驼峰转下划线
            return propertyName.replaceAll("([A-Z])", "_$1").toLowerCase();

        } catch (Exception e) {
            throw new RuntimeException("解析Lambda失败", e);
        }
    }


    // **自动匹配数值类型**
    public static Number convertNumber(Number value) {
        if (value instanceof Integer) {
            return value.intValue();
        } else if (value instanceof Long) {
            return value.longValue();
        } else if (value instanceof Double) {
            return value.doubleValue();
        } else if (value instanceof Float) {
            return value.floatValue();
        } else {
            return new BigDecimal(value.toString()); // 其他情况使用 BigDecimal 处理
        }
    }

    // **返回默认 0 值**
    public static Number defaultZeroValue() {
        return BigDecimal.ZERO; // 默认返回 BigDecimal，调用方可以强转
    }

    private static Object getSerializedLambda(Object fn) throws Exception {
        Method writeReplace = fn.getClass().getDeclaredMethod("writeReplace");
        writeReplace.setAccessible(true);
        return writeReplace.invoke(fn);
    }

    private static String invokeMethod(Object obj, String methodName) throws Exception {
        Method method = obj.getClass().getMethod(methodName);
        return (String) method.invoke(obj);
    }

    /**
     * 方法名转属性名（"getUserName" -> "userName"）
     */
    private static String methodToProperty(String methodName) {
        if (methodName.startsWith("get")) {
            return firstToLower(methodName.substring(3));
        } else if (methodName.startsWith("is")) {
            return firstToLower(methodName.substring(2));
        }
        return methodName;
    }

    /**
     * 首字母小写
     */
    private static String firstToLower(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * 驼峰转下划线
     */
    private static String camelToUnderline(String str) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                result.append("_").append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
