package com.nuocao.data.access.datascope.rule;

import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryTable;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;

import java.util.Set;

/**
 * 数据权限规则接口，所有规则以这个接口为实现
 * 如：org组织权限或者扩展其他如公司、部门权限都可以
 */
public interface DataScopeRule {

    /**
     * 返回需要生效的表名数组
     * 为什么需要该方法？Data Permission 数组基于 SQL 重写，通过 Where 返回只有权限的数据
     *
     * 如果需要基于实体名获得表名，可调用 } 获得
     *
     * @return 表名数组
     */
    Set<String> getTableNames();

    /**
     * 是否需要验证数据权限
     * @param tableName
     * @return
     */
    boolean isVerifyByTableName(String tableName);

    /**
     * 根据表名和别名，生成对应的 WHERE / OR 过滤条件
     * @param schema schema默认为null,一般postgreSQL才有，默认为public
     * @param tableName 表名
     * @param tableAlias 别名，可能为空
     * @return 过滤条件 Condition 表达式
     */
    QueryCondition getQueryCondition(String schema,String tableName, String tableAlias);

    /**
     * 根据表名和别名，生成对应的 WHERE / OR 过滤条件
     *
     * @param tableName 表名
     * @param tableAlias 别名，可能为空
     * @return 过滤条件 Condition 表达式
     */
    QueryCondition getQueryCondition(String tableName, String tableAlias);

    /**
     * 根据表名和别名，生成对应的 WHERE / OR 过滤条件
     *
     * @param queryTable 查询表信息
     * @return 过滤条件 Condition 表达式
     */
    QueryCondition getQueryCondition(QueryTable queryTable);
}
