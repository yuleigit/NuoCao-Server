package com.nuocao.common.mybatis;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.nuocao.common.page.SortingField;
import com.nuocao.common.utils.LocalDateTimeUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * mybatis 工具类
 */
@Slf4j
public class MyBatisUtils {
    public static QueryWrapper buildQueryOrderSafe(QueryWrapper query, Collection<SortingField> sortingFields,Class<?> entityClass) {
        if (CollectionUtil.isEmpty(sortingFields)) {
            return query;
        }
        Long startTime = LocalDateTimeUtils.currentTimestamp();
        List<String> allowedColumns = null;
        if (entityClass != null) {
            TableInfo tableInfo = TableInfoFactory.ofEntityClass(entityClass);
            if (tableInfo != null) {
                allowedColumns = List.of(tableInfo.getAllColumns());
            }
        }
        log.debug("获取可用字典列表-耗时：{}ms",LocalDateTimeUtils.currentTimestamp()-startTime);
        return buildQueryOrder(query, sortingFields, allowedColumns);
    }

    public static QueryWrapper buildQueryOrder(QueryWrapper query, Collection<SortingField> sortingFields,List<String> allowedColumns) {
        if (CollectionUtil.isEmpty(sortingFields)) {
            return query;
        }

        // 处理排序字段
        sortingFields.forEach(sortingField -> {
            String field = sortingField.getField();
            // 校验字段是否在白名单内
            if (allowedColumns.contains(field)) {
                boolean isAsc = SortingField.ORDER_ASC.equals(sortingField.getOrder());
                query.orderBy(field, isAsc);
            } else {
                // 记录非法尝试日志
                log.warn("检测到非法排序字段尝试: {}", field);
            }
        });


        return query;
    }

}
