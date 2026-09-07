package com.nuocao.data.access.datascope.rule.impl.org;

/**
 * {@link OrgDataScopeRule} 的自定义配置接口
 *
 * @author 芋道源码
 */
@FunctionalInterface
public interface OrgDataScopeRuleCustomizer {
    /**
     * 自定义该权限规则
     * 1. 调用 {@link OrgDataScopeRule#addOrgColumn(String, String)} (Class, String)} 方法，配置基于 org_id 的过滤规则
     * 2. 调用 {@link OrgDataScopeRule#addUserColumn(Class, String)} 方法，配置基于 user_id 的过滤规则
     *
     * @param rule 权限规则
     */
    void customize(OrgDataScopeRule rule);
}
