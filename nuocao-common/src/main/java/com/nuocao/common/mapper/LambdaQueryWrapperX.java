package com.nuocao.common.mapper;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.common.utils.collection.ArrayUtils;
import com.nuocao.common.utils.collection.CollectionUtils;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * mybatis-flex QueryWrapper Lambda工具
 * @param <T>
 */
public class LambdaQueryWrapperX<T> extends QueryWrapper {


    // ========== 核心技巧：强制类型转换 ==========

    @SuppressWarnings("unchecked")
    private LambdaQueryWrapperX<T> self() {
        return this;
    }

    // ========== 条件方法 ==========

    /**
     * LIKE 条件（如果值存在且不为空）
     */
    public LambdaQueryWrapperX<T> likeIfPresent(LambdaGetter<T> column, String val) {
        if (StringUtils.hasText(val)) {
            super.like(column,val);
        }
        return self();
    }

    /**
     * LIKE 条件（右模糊，如果值存在且不为空）
     */
    public LambdaQueryWrapperX<T> likeRightIfPresent(LambdaGetter<T> column, String val) {
        if (StringUtils.hasText(val)) {
            super.likeRight(column,val);
        }
        return self();
    }

    /**
     * LIKE 条件（左模糊，如果值存在且不为空）
     */
    public LambdaQueryWrapperX<T> likeLeftIfPresent(LambdaGetter<T> column, String val) {
        if (StringUtils.hasText(val)) {
            super.likeLeft(column, val);
        }
        return self();
    }

    /**
     * IN 条件（如果集合存在且不为空）
     */
    public LambdaQueryWrapperX<T> inIfPresent(LambdaGetter<T> column, Collection<?> values) {
        if (!CollectionUtils.isEmpty(values)) {
            super.in(column,values);
        }
        return self();
    }

    /**
     * IN 条件（如果数组存在且不为空）
     */
    public LambdaQueryWrapperX<T> inIfPresent(LambdaGetter<T> column, Object... values) {
        if (!ArrayUtil.isEmpty(values)) {
            super.in(column,values);
        }
        return self();
    }

    /**
     * 等于条件（如果值存在）
     * 注意：MyBatis-Flex 中空字符串也会被处理
     */
    public LambdaQueryWrapperX<T> eqIfPresent(LambdaGetter<T> column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
           super.eq(column,val);
        }
        return self();
    }


    /**
     * 不等于条件（如果值存在）
     */
    public LambdaQueryWrapperX<T> neIfPresent(LambdaGetter<T> column, Object val) {
        if (ObjectUtil.isNotEmpty(val)) {
           super.ne(column,val);
        }
        return self();
    }

    /**
     * 大于条件（如果值存在）
     */
    public LambdaQueryWrapperX<T> gtIfPresent(LambdaGetter<T> column, Object val) {
        if (val != null) {
           super.gt(column,val);
        }
        return self();
    }

    /**
     * 大于等于条件（如果值存在）
     */
    public LambdaQueryWrapperX<T> geIfPresent(LambdaGetter<T> column, Object val) {
        if (val != null) {
          super.ge(column,val);
        }
        return self();
    }

    /**
     * 小于条件（如果值存在）
     */
    public LambdaQueryWrapperX<T> ltIfPresent(LambdaGetter<T> column, Object val) {
        if (val != null) {
          super.lt(column,val);
        }
        return self();
    }

    /**
     * 小于等于条件（如果值存在）
     */
    public LambdaQueryWrapperX<T> leIfPresent(LambdaGetter<T> column, Object val) {
        if (val != null) {
            super.le(column,val);
        }
        return self();
    }

    /**
     * BETWEEN 条件（如果值存在）
     * 如果只有一个值存在，则使用 >= 或 <=
     */
    public LambdaQueryWrapperX<T> betweenIfPresent(LambdaGetter<T> column, Object val1, Object val2) {
        if (val1 != null && val2 != null) {
            super.between(column,val1, val2);
            return self();
        }
        if (val1 != null) {
            super.ge(column,val1);
            return self();
        }
        if (val2 != null) {
            super.le(column,val2);
        }
        return self();
    }

    /**
     * BETWEEN 条件（数组形式）
     */
    public LambdaQueryWrapperX<T> betweenIfPresent(LambdaGetter<T> column, Object[] values) {
        Object val1 = ArrayUtils.get(values, 0);
        Object val2 = ArrayUtils.get(values, 1);
        return betweenIfPresent(column, val1, val2);
    }


    // ========== 基础 X 版本 (用于强制约束 T 类型的 Lambda) ==========

    public LambdaQueryWrapperX<T> eqX(LambdaGetter<T> column, Object val) {
        super.eq(column, val);
        return self();
    }

    public LambdaQueryWrapperX<T> neX(LambdaGetter<T> column, Object val) {
        super.ne(column, val);
        return self();
    }

    public LambdaQueryWrapperX<T> gtX(LambdaGetter<T> column, Object val) {
        super.gt(column, val);
        return self();
    }

    public LambdaQueryWrapperX<T> geX(LambdaGetter<T> column, Object val) {
        super.ge(column, val);
        return self();
    }

    public LambdaQueryWrapperX<T> ltX(LambdaGetter<T> column, Object val) {
        super.lt(column, val);
        return self();
    }

    public LambdaQueryWrapperX<T> leX(LambdaGetter<T> column, Object val) {
        super.le(column, val);
        return self();
    }

    public LambdaQueryWrapperX<T> likeX(LambdaGetter<T> column, Object val) {
        super.like(column, val);
        return self();
    }

    public LambdaQueryWrapperX<T> inX(LambdaGetter<T> column, Object... values) {
        super.in(column, values);
        return self();
    }

    public LambdaQueryWrapperX<T> inX(LambdaGetter<T> column, Collection<?> values) {
        super.in(column, values);
        return self();
    }




    //************select 字段方法 *******
    @SafeVarargs
    public final LambdaQueryWrapperX<T> selectX(LambdaGetter<T>... lambdaGetters) {
        super.select(lambdaGetters);
        return self();
    }






    // ========== 链式调用支持 ==========

    @Override
    public LambdaQueryWrapperX<T> where(QueryCondition condition) {
        super.where(condition);
        return self();
    }


    @Override
    public LambdaQueryWrapperX<T> and(QueryCondition condition) {
        super.and(condition);
        return self();
    }

    @Override
    public LambdaQueryWrapperX<T> or(QueryCondition condition) {
        super.or(condition);
        return self();
    }


    public LambdaQueryWrapperX<T> orderBy(QueryColumn columns) {
        super.orderBy(columns,true);
        return self();
    }

    public LambdaQueryWrapperX<T> orderBy(LambdaGetter<T> column, boolean asc) {
        super.orderBy(column, asc);
        return self();
    }

    public LambdaQueryWrapperX<T> limit(int rows) {
        super.limit(rows);
        return self();
    }

    public LambdaQueryWrapperX<T> offset(int offset) {
        super.offset(offset);
        return self();
    }

    public LambdaQueryWrapperX<T> groupBy(QueryColumn... columns) {
        super.groupBy(columns);
        return self();
    }

    public LambdaQueryWrapperX<T> having(QueryCondition condition) {
        super.having(condition);
        return self();
    }

}
