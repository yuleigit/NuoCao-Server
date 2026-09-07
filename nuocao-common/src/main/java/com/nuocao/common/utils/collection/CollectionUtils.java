package com.nuocao.common.utils.collection;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

/**
 * Collection 工具类
 * 提供基于 Java Stream API 的常用集合操作封装，减少样板代码
 *
 * @author LEI
 */
public class CollectionUtils {

    /**
     * 判断目标对象是否包含在指定的参数列表中
     * @usage
     * boolean isAllowed = CollectionUtils.containsAny("ALIYUN", "ALIYUN", "TENCENT"); // true
     */
    public static boolean containsAny(Object source, Object... targets) {
        return asList(targets).contains(source);
    }

    /**
     * 验证多个集合中是否存在空集合
     * @usage
     * boolean hasEmpty = CollectionUtils.isEmpty(list1, list2);
     */
    public static boolean isEmpty(Collection<?>... collections) {
        return Arrays.stream(collections).anyMatch(CollectionUtil::isEmpty);
    }

    /**
     * 验证所有提供的集合是否都不为空
     * @usage
     * if (CollectionUtils.isNotEmpty(orderList, userList)) { ... }
     */
    public static boolean isNotEmpty(Collection<?>... collections) {
        return Arrays.stream(collections).noneMatch(CollectionUtil::isEmpty);
    }

    /**
     * 检查集合中是否存在匹配给定断言的元素
     * @usage
     * boolean hasAdmin = CollectionUtils.anyMatch(userList, user -> user.getRole().equals("ADMIN"));
     */
    public static <T> boolean anyMatch(Collection<T> from, Predicate<T> predicate) {
        return from.stream().anyMatch(predicate);
    }

    /**
     * 过滤集合
     * @usage
     * List<User> activeUsers = CollectionUtils.filterList(userList, User::getEnabled);
     */
    public static <T> List<T> filterList(Collection<T> from, Predicate<T> predicate) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * 根据指定 Key 进行去重
     * @usage
     * List<User> distinctUsers = CollectionUtils.distinct(userList, User::getMobile);
     */
    public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }
        return distinct(from, keyMapper, (t1, t2) -> t1);
    }

    /**
     * 根据指定 Key 进行去重，并指定冲突时的覆盖策略
     * @usage
     * List<User> distinctUsers = CollectionUtils.distinct(userList, User::getMobile, (oldValue, newValue) -> newValue);
     */
    public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper, BinaryOperator<T> cover) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(convertMap(from, keyMapper, Function.identity(), cover).values());
    }

    /**
     * 数组转换：将数组转换为另一种类型的 List
     * @usage
     * List<String> names = CollectionUtils.convertList(new Long[]{1L, 2L}, String::valueOf);
     */
    public static <T, U> List<U> convertList(T[] from, Function<T, U> func) {
        if (ArrayUtil.isEmpty(from)) {
            return new ArrayList<>();
        }
        return convertList(Arrays.asList(from), func);
    }

    /**
     * 集合转换：将 Collection 转换为另一种类型的 List，自动过滤 null 值
     * @usage
     * List<Long> ids = CollectionUtils.convertList(userList, User::getId);
     */
    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().map(func).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 集合转换（带过滤）：转换前先根据条件过滤
     * @usage
     * List<String> names = CollectionUtils.convertList(userList, User::getName, u -> u.getAge() > 18);
     */
    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().filter(filter).map(func).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 扁平化转换：将集合中的每个元素映射为一个 Stream，然后合并成一个 List
     * @usage
     * List<Tag> allTags = CollectionUtils.convertListByFlatMap(userList, u -> u.getTags().stream());
     */
    public static <T, U> List<U> convertListByFlatMap(Collection<T> from,
                                                      Function<T, ? extends Stream<? extends U>> func) {
        if (CollUtil.isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().filter(Objects::nonNull).flatMap(func).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 提取字段：从集合中提取某个属性生成新 List (如 List<User> -> List<Long> ids)
     * @usage
     * List<Long> configIds = CollectionUtils.toList(paramList, DevConfigIdParam::getConfigId);
     */
    public static <T, R> List<R> toList(Collection<T> collection, Function<? super T, ? extends R> mapper) {
        if (collection == null || collection.isEmpty()) {
            return List.of();
        }
        return collection.stream()
                .map(mapper)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 合并 Map 中的所有 List 值为一个单一的 List
     * @usage
     * List<Value> allValues = CollectionUtils.mergeValuesFromMap(groupMap);
     */
    public static <K, V> List<V> mergeValuesFromMap(Map<K, List<V>> map) {
        return map.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * 将集合转换为 Set
     * @usage
     * Set<Long> roleIds = CollectionUtils.convertSet(userList, User::getRoleId);
     */
    public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func) {
        if (CollUtil.isEmpty(from)) {
            return new HashSet<>();
        }
        return from.stream().map(func).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    /**
     * 将集合转换为 Map (Key -> 对象本身)
     * @usage
     * Map<Long, User> userMap = CollectionUtils.convertMap(userList, User::getId);
     */
    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }
        return convertMap(from, keyFunc, Function.identity());
    }

    /**
     * 将集合转换为 Map (Key -> Value)
     * @usage
     * Map<Long, String> nameMap = CollectionUtils.convertMap(userList, User::getId, User::getName);
     */
    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }
        return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1);
    }

    /**
     * 将集合转换为 Map，并指定冲突处理逻辑（Merge）
     * @usage
     * Map<String, User> map = CollectionUtils.convertMap(userList, User::getMobile, u -> u, (o, n) -> n);
     */
    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream().collect(Collectors.toMap(keyFunc, valueFunc, mergeFunction));
    }

    /**
     * 将集合转换为 MultiMap (一对多映射：一个 Key 对应一个 List)
     * @usage
     * Map<String, List<Config>> groupMap = CollectionUtils.convertMultiMap(list, Config::getGroup);
     */
    public static <T, K> Map<K, List<T>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc) {
        if (CollUtil.isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.toList()));
    }

    /**
     * 对比新老列表，识别差异项
     * @usage
     * List<List<T>> diff = CollectionUtils.diffList(oldList, newList, (o, n) -> o.getId().equals(n.getId()));
     * List<T> added = diff.get(0); List<T> updated = diff.get(1); List<T> deleted = diff.get(2);
     * @return 返回包含三个 List 的嵌套 List：[0]新增项, [1]修改项, [2]删除项
     */
    public static <T> List<List<T>> diffList(Collection<T> oldList, Collection<T> newList,
                                             BiFunction<T, T, Boolean> sameFunc) {
        List<T> createList = new LinkedList<>(newList);
        List<T> updateList = new ArrayList<>();
        List<T> deleteList = new ArrayList<>();

        for (T oldObj : oldList) {
            T foundObj = null;
            for (Iterator<T> iterator = createList.iterator(); iterator.hasNext(); ) {
                T newObj = iterator.next();
                if (!sameFunc.apply(oldObj, newObj)) {
                    continue;
                }
                iterator.remove();
                foundObj = newObj;
                break;
            }
            if (foundObj != null) {
                updateList.add(foundObj);
            } else {
                deleteList.add(oldObj);
            }
        }
        return asList(createList, updateList, deleteList);
    }

    /**
     * 查找集合中符合条件的第一个元素，并进行转换输出
     * @usage
     * String name = CollectionUtils.findFirst(userList, u -> u.getId() == 1L, User::getName);
     */
    public static <T, U> U findFirst(Collection<T> from, Predicate<T> predicate, Function<T, U> func) {
        if (CollUtil.isEmpty(from)) {
            return null;
        }
        return from.stream().filter(predicate).findFirst().map(func).orElse(null);
    }

    /**
     * 获取集合中某个字段的最大值
     * @usage
     * Integer maxAge = CollectionUtils.getMaxValue(userList, User::getAge);
     */
    public static <T, V extends Comparable<? super V>> V getMaxValue(Collection<T> from, Function<T, V> valueFunc) {
        if (CollUtil.isEmpty(from)) {
            return null;
        }
        return from.stream().max(Comparator.comparing(valueFunc)).map(valueFunc).orElse(null);
    }

    /**
     * 将多个 List 合并为一个新 ArrayList
     * @usage
     * List<T> flatList = CollectionUtils.newArrayList(listOfList);
     */
    public static <T> List<T> newArrayList(List<List<T>> list) {
        return list.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }
}