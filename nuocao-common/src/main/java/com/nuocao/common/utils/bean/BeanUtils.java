package com.nuocao.common.utils.bean;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.utils.collection.CollectionUtils;
import org.noear.solon.Solon;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Bean 工具类
 *
 * @Autor LEI
 */
public class BeanUtils {



    /** Bean方法名中属性名开始的下标 */
    private static final int BEAN_METHOD_PROP_INDEX = 3;

    /** * 匹配getter方法的正则表达式 */
    private static final Pattern GET_PATTERN = Pattern.compile("get(\\p{javaUpperCase}\\w*)");

    /** * 匹配setter方法的正则表达式 */
    private static final Pattern SET_PATTERN = Pattern.compile("set(\\p{javaUpperCase}\\w*)");


    /**
     * 加载注入的bean，solon
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T getBean(Class<T> clazz){
        return Solon.context().getBean(clazz);
    }

    /**
     * 加载注入的bean，solon
     * @param name
     * @return
     * @param <T>
     */
    public static <T> T getBean(String name){
        return Solon.context().getBean(name);
    }


    /**
     * Bean属性复制工具方法。
     *
     * @param dest 目标对象
     * @param src 源对象
     */
    public static void copyBeanProp(Object dest, Object src)
    {
        try
        {
            copyProperties(src, dest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获取对象的setter方法。
     *
     * @param obj 对象
     * @return 对象的setter方法列表
     */
    public static List<Method> getSetterMethods(Object obj)
    {
        // setter方法列表
        List<Method> setterMethods = new ArrayList<Method>();

        // 获取所有方法
        Method[] methods = obj.getClass().getMethods();

        // 查找setter方法

        for (Method method : methods)
        {
            Matcher m = SET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 1))
            {
                setterMethods.add(method);
            }
        }
        // 返回setter方法列表
        return setterMethods;
    }

    /**
     * 获取对象的getter方法。
     *
     * @param obj 对象
     * @return 对象的getter方法列表
     */

    public static List<Method> getGetterMethods(Object obj)
    {
        // getter方法列表
        List<Method> getterMethods = new ArrayList<Method>();
        // 获取所有方法
        Method[] methods = obj.getClass().getMethods();
        // 查找getter方法
        for (Method method : methods)
        {
            Matcher m = GET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 0))
            {
                getterMethods.add(method);
            }
        }
        // 返回getter方法列表
        return getterMethods;
    }

    /**
     * 检查Bean方法名中的属性名是否相等。<br>
     * 如getName()和setName()属性名一样，getName()和setAge()属性名不一样。
     *
     * @param m1 方法名1
     * @param m2 方法名2
     * @return 属性名一样返回true，否则返回false
     */

    public static boolean isMethodPropEquals(String m1, String m2)
    {
        return m1.substring(BEAN_METHOD_PROP_INDEX).equals(m2.substring(BEAN_METHOD_PROP_INDEX));
    }

    /**
     * 将一个类转换成另一个实体
     * @param source
     * @param targetClass
     * @return
     * @param <T>
     */
    public static <T> T toBean(Object source, Class<T> targetClass) {
        return BeanUtil.toBean(source, targetClass);
    }

    /**
     * 转换类型并对结果复制
     * peek用法obj->obj.setName()
     * @param source
     * @param targetClass
     * @param peek
     * @return
     * @param <T>
     */
    public static <T> T toBean(Object source, Class<T> targetClass, Consumer<T> peek) {
        T target = toBean(source, targetClass);
        if (target != null) {
            peek.accept(target);
        }
        return target;
    }

    /**
     * 转换List为新类型
     * @param source
     * @param targetType
     * @return
     * @param <S>
     * @param <T>
     */
    public static <S, T> List<T> toBean(List<S> source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        return CollectionUtils.convertList(source, s -> toBean(s, targetType));
    }

    public static <S, T> List<T> toBean(List<S> source, Class<T> targetType, Consumer<T> peek) {
        List<T> list = toBean(source, targetType);
        if (list != null) {
            list.forEach(peek);
        }
        return list;
    }

    /**
     * 转换为page
     * @param source
     * @param targetType
     * @return
     * @param <S>
     * @param <T>
     */
    public static <S, T> PageResult<T> toBean(PageResult<S> source, Class<T> targetType) {
        return toBean(source, targetType, null);
    }


    public static <S, T> PageResult<T> toBean(PageResult<S> source, Class<T> targetType, Consumer<T> peek) {
        if (source == null) {
            return null;
        }
        List<T> list = toBean(source.getRecords(), targetType);
        if (peek != null) {
            list.forEach(peek);
        }
        return new PageResult<>(list, source.getTotal());
    }

    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        BeanUtil.copyProperties(source, target, false);
    }

    /**
     * 获取集合中重复的属性值列表
     *
     * @param list      数据集合
     * @param keyMapper 属性映射函数
     * @param <T>       集合元素类型
     * @param <K>       属性类型
     * @param gtRepeatNum 重复次数大于这个数字，如果是0则是全部去重后的结果
     * @return 重复值的列表
     */
    public static <T, K> List<K> getGroupValues(List<T> list, Function<T, K> keyMapper, int gtRepeatNum) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        // 找出数量大于1的重复属性
        // 找到重复的属性值
        Map<K, Long> valueCounts = list.stream()
                .filter(Objects::nonNull)
                .map(keyMapper)
                .filter(Objects::nonNull) // 忽略空值
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return valueCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > gtRepeatNum)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    /**
     * 获取存在重复的项目
     *
     * @param list      数据集合
     * @param keyMapper 属性映射函数
     * @param <T>       集合元素类型
     * @param <K>       属性类型
     * @return 重复值的列表
     */
    public static <T, K> List<K> getRepeatValues(List<T> list, Function<T, K> keyMapper){
        return getGroupValues(list,keyMapper,1);
    }

    /**
     * 获取集合中重复的属性值及重复次数
     *
     * @param list      数据集合
     * @param keyMapper 属性映射函数
     * @param <T>       集合元素类型
     * @param <K>       属性类型
     * @param gtRepeatNum 重复次数大于这个数字，如果是0则是全部去重后的结果
     * @return 重复值及其出现次数的 Map
     */
    public static <T, K> Map<K, Long> getGroupCounts(List<T> list, Function<T, K> keyMapper,int gtRepeatNum) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyMap();
        }

        // 找到重复的属性值及其次数
        return list.stream()
                .filter(Objects::nonNull)
                .map(keyMapper)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > gtRepeatNum)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 获取重复次数统计
     *
     * @param list      数据集合
     * @param keyMapper 属性映射函数
     * @param <T>       集合元素类型
     * @param <K>       属性类型
     * @return 重复值及其出现次数的 Map
     */
    public static <T, K> Map<K, Long> getGroupCounts(List<T> list, Function<T, K> keyMapper)
    {
        return getGroupCounts(list,keyMapper,0);
    }


}
