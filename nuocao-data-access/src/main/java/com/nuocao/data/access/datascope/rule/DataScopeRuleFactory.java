package com.nuocao.data.access.datascope.rule;

import java.util.List;

/**
 * 权限规则处理需要实现的接口
 */
public interface DataScopeRuleFactory {

    /**
     * 获得所有数据权限规则数组
     *
     * @return 数据权限规则数组
     */
    List<DataScopeRule> getDataScopeRules();

    /**
     * 获得指定 Mapper 的数据权限规则数组
     *
     * @param mappedStatementId 指定 Mapper 的编号
     * @return 数据权限规则数组
     */
    List<DataScopeRule> getDataScopeRule(String mappedStatementId);

}
