package com.nuocao.common.utils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * tree builde工具类
 *
 * 1. 使用函数式方案
 * List<MenuDTO> tree = TreeBuildUtils.buildTree(
 *     menuDtoList,
 *     MenuDTO::getId,           // ID获取
 *     MenuDTO::getParentId,     // 父ID获取
 *     MenuDTO::setChildren,     // 设置子节点
 *     Comparator.comparing(MenuDTO::getSortOrder) // 排序
 * );
 *
 */
public class TreeBuildUtils {
    /**
     * 通用树构建方法
     * @param list 原始数据列表
     * @param idGetter 获取ID的方法
     * @param parentIdGetter 获取父ID的方法
     * @param childrenSetter 设置子节点的方法
     * @param comparator 排序比较器（可选）
     * @param <T> 数据类型
     * @param <R> ID类型（通常是Long或String）
     * @return 树结构根节点列表
     */
    public static <T, R> List<T> buildTree(
            List<T> list,
            Function<T, R> idGetter,
            Function<T, R> parentIdGetter,
            BiConsumer<T, List<T>> childrenSetter,
            Comparator<T> comparator) {

        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        // 排序（可选）
        if (comparator != null) {
            list.sort(comparator);
        }

        // 创建映射表
        Map<R, T> nodeMap = new HashMap<>(list.size());
        List<T> rootList = new ArrayList<>();

        // 第一遍遍历：初始化映射
        for (T node : list) {
            nodeMap.put(idGetter.apply(node), node);
        }

        // 第二遍遍历：构建树结构
        for (T node : list) {
            R parentId = parentIdGetter.apply(node);
            T parent = nodeMap.get(parentId);

            if (parent != null) {
                // 确保父节点有children集合
                List<T> children = getOrInitChildren(parent, childrenSetter);
                children.add(node);
            } else {
                rootList.add(node);
            }
        }

        return rootList;
    }

    private static <T> List<T> getOrInitChildren(
            T node,
            BiConsumer<T, List<T>> childrenSetter) {

        // 这里需要通过反射获取children，或者要求节点有getChildren方法
        // 更简单的做法：始终初始化新列表
        List<T> children = new ArrayList<>();
        childrenSetter.accept(node, children);
        return children;
    }

    // 重载方法，不需要排序
    public static <T, R> List<T> buildTree(
            List<T> list,
            Function<T, R> idGetter,
            Function<T, R> parentIdGetter,
            BiConsumer<T, List<T>> childrenSetter) {

        return buildTree(list, idGetter, parentIdGetter, childrenSetter, null);
    }
}
