package com.nuocao.common.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.LambdaGetter;
import com.nuocao.common.mybatis.MyBatisUtils;
import com.nuocao.common.page.PageParam;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.page.SortPageParam;
import com.nuocao.common.page.SortingField;
import com.nuocao.common.utils.ConvertUtils;
import com.nuocao.common.utils.LocalDateTimeUtils;
import com.nuocao.common.utils.collection.CollectionUtils;
import org.apache.ibatis.annotations.Param;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 基础mapper扩展
 * @param <T>
 */
public interface BaseMapperX<T> extends BaseMapper<T> {

    /**
     * 修改信息
     * @param updata
     * @return
     */
    default int updateById(T updata){
       return this.update(updata);
    }

    default PageResult<T> selectPage(SortPageParam pageParam, QueryWrapper queryWrapper) {
        return selectPage(pageParam, pageParam.getSortingFields(), queryWrapper);
    }

    default PageResult<T> selectPage(PageParam pageParam, QueryWrapper queryWrapper) {
        return selectPage(pageParam, null, queryWrapper);
    }


    /**
     * 分页查询工具
     * @param pageParam
     * @param sortingFields
     * @param queryWrapper
     * @return
     */
    default PageResult<T> selectPage(PageParam pageParam, Collection<SortingField> sortingFields, QueryWrapper queryWrapper) {
        // 特殊：不分页，直接查询全部
        if (PageParam.PAGE_SIZE_NONE.equals(pageParam.getSize())) {
            if(CollectionUtils.isNotEmpty(sortingFields)) {
                MyBatisUtils.buildQueryOrderSafe(queryWrapper, sortingFields, getEntityType());
            }
            List<T> list = selectListByQuery(queryWrapper);
            return new PageResult<>(list, (long) list.size());
        }
        // mybatis-flex 分页
        Page<T> objectPage = new Page<>(pageParam.getCurrent(), pageParam.getSize());
        if(CollectionUtils.isNotEmpty(sortingFields)) {
            MyBatisUtils.buildQueryOrderSafe(queryWrapper, sortingFields, getEntityType());
        }
        this.paginate(objectPage, queryWrapper);
        // 转换返回
        return new PageResult<>(objectPage.getRecords(), objectPage.getTotalRow());
    }

    /**
     * joinpag
     * @param pageParam
     * @param queryWrapper
     * @return
     */
    default <D> PageResult<D> selectJoinPage(SortPageParam pageParam, Class<D> clazz, QueryWrapper queryWrapper) {
        return selectJoinPage(pageParam, pageParam.getSortingFields(),clazz, queryWrapper);
    }

    default <D> PageResult<D> selectJoinPage(PageParam pageParam, Collection<SortingField> sortingFields, Class<D> clazz, QueryWrapper queryWrapper) {
        // 特殊：不分页，直接查询全部
        if (PageParam.PAGE_SIZE_NONE.equals(pageParam.getSize())) {
            if(CollectionUtils.isNotEmpty(sortingFields)) {
                MyBatisUtils.buildQueryOrderSafe(queryWrapper, sortingFields, getEntityType());
            }
            List<D> list = selectListByQueryAs(queryWrapper,clazz);
            return new PageResult<>(list, (long) list.size());
        }
        // mybatis-flex 分页
        Page<D> objectPage = new Page<>(pageParam.getCurrent(), pageParam.getSize());
        if(CollectionUtils.isNotEmpty(sortingFields)) {
            MyBatisUtils.buildQueryOrderSafe(queryWrapper, sortingFields, getEntityType());
        }        this.paginateAs(objectPage, queryWrapper,clazz);
        // 转换返回
        return new PageResult<>(objectPage.getRecords(), objectPage.getTotalRow());
    }




    default int save(@Param("$$entity") T var1){
        return this.insert(var1,true);
    }

    /**
     * 删除
     * @param field
     * @param value
     * @return
     */
    default int delete(String field, Object value) {
        return this.deleteByQuery(new QueryWrapper().eq(field, value));
    }


    default int delete(LambdaGetter<T> field, Object value) {
        return this.deleteByQuery(new QueryWrapper().eq(field, value));
    }


    default T selectOne(String field, Object value) {
        return this.selectOneByQuery(new QueryWrapper().eq(field, value));
    }

    default T selectOne(LambdaGetter<T> field, Object value) {
        return selectOneByQuery(new QueryWrapper().eq(field, value));
    }

    default T selectOne(String field1, Object value1, String field2, Object value2) {
        return selectOneByQuery(new QueryWrapper().eq(field1, value1).eq(field2, value2));
    }

    default T selectOne(LambdaGetter<T> field1, Object value1, LambdaGetter<T> field2, Object value2) {
        return selectOneByQuery(new QueryWrapper().eq(field1, value1).eq(field2, value2));
    }

    default boolean exists(LambdaGetter<T> field, Object value)
    {
        return selectOne(field,value)!=null;
    }

    default boolean exists(LambdaGetter<T> field1, Object value1, LambdaGetter<T> field2, Object value2)
    {
        return selectOne(field1,value1,field2,value2)!=null;
    }


    default Long selectCount() {
        return ConvertUtils.toLong(this.selectCountByQuery(new QueryWrapper()),0L);
    }

    default Long selectCount(String field, Object value) {
        return ConvertUtils.toLong(this.selectCountByQuery( new QueryWrapper().eq(field, value)));
    }

    default Long selectCount(LambdaGetter<T> field, Object value,LambdaGetter<T> field2, Object value2) {
        return ConvertUtils.toLong(this.selectCountByQuery(new QueryWrapper().eq(field, value).eq(field2, value2)));
    }
    default Long selectCount(LambdaGetter<T> field, Object value) {
        return ConvertUtils.toLong(this.selectCountByQuery(new QueryWrapper().eq(field, value)));
    }

    default  <R extends Number> R selectSum(String sumField,LambdaGetter<T> queryField,Object value){
        return selectSum(sumField,new QueryWrapper().eq(queryField,value));
    }

    default  <R extends Number> R selectSum(LambdaGetter<T> sumField,LambdaGetter<T> queryField,Object value,LambdaGetter<T> queryField2,Object value2){
        return selectSum(sumField,new QueryWrapper().eq(queryField,value).eq(queryField2,value2));
    }

    default  <R extends Number> R selectSum(LambdaGetter<T> sumField,LambdaGetter<T> queryField,Object value){
        return selectSum(sumField,new QueryWrapper().eq(queryField,value));
    }

    default <R extends Number> R selectSum(LambdaGetter<T> field, QueryWrapper lamdaQuery){
        String column = MapperLambdaUtils.getColumnName(field); // 获取数据库字段名
        return selectSum(column,lamdaQuery);
    }

    default <R extends Number> R selectSum(String column, QueryWrapper lambdaQuery) {
        // 列名白名单校验：只允许合法 SQL 标识符，杜绝拼接注入
        if (column == null || !column.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException("非法的聚合列名: " + column);
        }
        QueryWrapper query = lambdaQuery.clone();
        // 使用 mybatis-flex 聚合 API，避免手写 "SUM(" + column + ")" 字符串拼接
        query.select(QueryMethods.sum(column).as("sumResult"));

        Map<String, Object> resultMap = this.selectObjectByQueryAs(query,Map.class);

        if (resultMap != null && resultMap.containsKey("sumResult")) {
            Object sumValue = resultMap.get("sumResult");

            if (sumValue == null) {
                return (R) MapperLambdaUtils.defaultZeroValue();
            }

            // 确保 sumValue 是数值类型
            if (!(sumValue instanceof Number)) {
                throw new IllegalArgumentException("SUM 返回的值不是数值类型: " + sumValue.getClass().getName());
            }

            return (R) MapperLambdaUtils.convertNumber((Number) sumValue);
        }

        return (R) MapperLambdaUtils.defaultZeroValue();
    }


    default List<T> selectList() {
        return selectListByQuery(new QueryWrapper());
    }

    default List<T> selectList(String field, Object value) {
        return selectListByQuery(new QueryWrapper().eq(field, value));
    }

    default List<T> selectList(LambdaGetter<T> field, Object value) {
        return selectListByQuery(new QueryWrapper().eq(field, value));
    }

    default List<T> selectList(LambdaGetter<T> field, Object value,LambdaGetter<T> field2, Object value2) {
        return selectListByQuery(new QueryWrapper().eq(field, value).eq(field2, value2));
    }

    default List<T> selectList(String field, Collection<?> values) {
        return selectListByQuery(new QueryWrapper().in(field, values));
    }

    default List<T> selectList(LambdaGetter<T> field, Collection<?> values) {
        return selectListByQuery(new QueryWrapper().in(field, values));
    }

    /**
     * 取得两个值区间的的值
     * @param field
     * @param beginValue
     * @param endValue
     * @return
     */
    default List<T> selectListRange(LambdaGetter<T> field,  Object beginValue,Object endValue) {
        return selectListByQuery(new QueryWrapper().le(field, endValue).ge(field, beginValue));
    }


    /**
     * 获取泛型 T 的真实类型
     */
    @SuppressWarnings("unchecked")
    default Class<T> getEntityType() {
        Long startTime = LocalDateTimeUtils.currentTimestamp();
        // 获取当前子类（如 UserMapper）实现的接口列表
        Type[] interfaces = this.getClass().getGenericInterfaces();

        for (Type type : interfaces) {
            // 检查是否是参数化类型（即带泛型的类型）
            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) type;

                // 确保我们找的是 BaseMapperX 这个接口上的泛型
                if (pType.getRawType().equals(BaseMapperX.class)) {
                    // 返回第一个泛型参数 T
                    return  (Class<T>) pType.getActualTypeArguments()[0];
                }
            }
        }

        // 2. 如果第一层没找到（可能存在多层继承），可以尝试递归或抛出异常
        throw new RuntimeException("无法通过反射确定实体类类型，请在子类中重写 getEntityType()");
    }


}
