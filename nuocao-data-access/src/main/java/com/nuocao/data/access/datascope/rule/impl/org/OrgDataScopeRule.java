package com.nuocao.data.access.datascope.rule.impl.org;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.table.TableInfoFactory;
import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.auth.api.dto.SaUserLoginInfoDTO;
import com.nuocao.common.pojo.CommonEntity;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.data.access.datascope.context.DataScopeContext;
import com.nuocao.data.access.datascope.rule.DataScopeRule;
import com.nuocao.sys.api.SysDataScopeApi;
import com.nuocao.sys.api.dto.OrgDataScopeDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 组织机构权限规则实现
 */
@Slf4j
@AllArgsConstructor
public class OrgDataScopeRule implements DataScopeRule {


    private final SysDataScopeApi sysDataScopeApi;

    /**
     * LoginUser 的 Context 缓存 Key
     */
    protected static final String CONTEXT_KEY = OrgDataScopeRule.class.getSimpleName();

    /**
     * 默认组织字段名
     */
    private static final String ORG_COLUMN_NAME = "org_id";
    /**
     * 默认用户字段名
     */
    private static final String USER_COLUMN_NAME = "create_user";

    /**
     * 基于部门的表字段配置
     * 一般情况下，每个表的部门编号字段是 dept_id，通过该配置自定义。
     *
     * key：表名
     * value：字段名
     */
    private final Map<String, String> deptColumns = new HashMap<>();
    /**
     * 基于用户的表字段配置
     * 一般情况下，每个表的部门编号字段是 dept_id，通过该配置自定义。
     *
     * key：表名
     * value：字段名
     */
    private final Map<String, String> userColumns = new HashMap<>();
    /**
     * 所有表名，是 {@link #deptColumns} 和 {@link #userColumns} 的合集
     */
    private final Set<String> TABLE_NAMES = new HashSet<>();

    @Override
    public Set<String> getTableNames() {
        return TABLE_NAMES;
    }

    @Override
    public QueryCondition getQueryCondition(QueryTable queryTable) {
        if(queryTable==null) return null;
        return getQueryCondition(queryTable.getSchema(),queryTable.getName(),queryTable.getAlias());
    }

    @Override
    public QueryCondition getQueryCondition(String tableName, String tableAlias) {
        return getQueryCondition(null,tableName,tableAlias);
    }

    @Override
    public QueryCondition getQueryCondition(String schema,String tableName, String tableAlias) {

        //忽略数据权限
        if(DataScopeContext.isIgnore()){
            return null;
        }
        //不在表中不处理
        if(!isVerifyByTableName(tableName)){
            return null;
        }

        //得到用户
        SaUserLoginInfoDTO loginUser= null;
        try{
            loginUser= StpLoginUserUtils.getLoginUser();
        }catch (Exception ex){

        }
        if(loginUser==null){
            return null;
        }

        OrgDataScopeDTO dataScope =loginUser.getContext(CONTEXT_KEY, OrgDataScopeDTO.class);
        if(dataScope==null){//权限为空则从数据库中读取
            dataScope = sysDataScopeApi.getOrgDataScopeByUserId(loginUser.getId());
            if (dataScope == null) {
                log.error("[getExpression][LoginUser({}) 获取数据权限为 null]", JSONObject.toJSONString(loginUser));
                throw new NullPointerException(String.format("LoginUser(%s) Table(%s/%s) 未返回数据权限",
                        loginUser.getId(), tableName, tableAlias));
            }
            // 添加到上下文中，避免重复计算
            loginUser.setContext(CONTEXT_KEY, dataScope);
        }

        //情况1.拥有所有权限
        if(dataScope.getAll()){
            return null;
        }
        // 情况二，既没有自己的权限，也有没有部门权限，返回一个不可能的条件
        if (Boolean.FALSE.equals(dataScope.getSelf())
            && CollectionUtil.isEmpty(dataScope.getOrgIds())
        ) {

            return QueryCondition.create(createColumn(schema,tableName,"1",tableAlias),"2"); // WHERE 1 = 2，可以保证返回的数据为空
        }
        // 情况三，拼接 Dept 和 User 的条件，最后组合
        QueryCondition userCondition =buildUserCondition(schema,tableName,tableAlias,dataScope.getSelf(), loginUser.getId());
        QueryCondition orgCondition =buildOrgCondition(schema,tableName,tableAlias, dataScope.getOrgIds());

        if (userCondition == null && orgCondition == null) {
            // TODO YN：获得不到条件的时候，暂时不抛出异常，而是不返回数据
            log.warn("[getExpression][LoginUser({}) Table({}/{}) DeptDataPermission({}) 构建的条件为空]",
                    JSONObject.toJSONString(loginUser), tableName, tableAlias, JSONObject.toJSONString(dataScope));
            return null; //这个时发你一个默认and null的，保证没设置权限的人无法访问，现在@DataPermission注解无效了，所以先这么写，让刚注册没分配账号的角色能有权限使用
            //后期一定改回来
        }

        if(userCondition==null){
            return orgCondition;
        }

        if(orgCondition==null){
            return userCondition;
        }




        //if()
        return orgCondition.or(userCondition);
    }


    private QueryCondition buildOrgCondition(String schema,String tableName,String tableAlias,Set<Long> orgIds){
        String columnName = deptColumns.get(tableName);
        if (StringUtils.isBlank(columnName)) {
            return null;
        }
        // 如果为空，则无条件
        if (CollectionUtils.isEmpty(orgIds)) {
            return null;
        }

        return QueryCondition.create(createColumn(schema,tableName,columnName,tableAlias), SqlConsts.IN,orgIds);

    }

    private QueryCondition buildUserCondition(String schema,String tableName,String tableAlias,boolean isSelf,Long userId){
        if (Boolean.FALSE.equals(isSelf)) {
            return null;
        }
        String columnName = userColumns.get(tableName);
        if (StringUtils.isBlank(columnName)) {
            return null;
        }
        return QueryCondition.create(createColumn(schema,tableName,columnName,tableAlias),userId);
    }

    private QueryColumn createColumn(String schema,String tableName,String columnName,String tableAlias){
        return new QueryColumn(schema,tableName,columnName,tableAlias);
    }

    // ==================== 添加配置 ====================

    public void addOrgColumn(Class<? extends CommonEntity> entityClass) {
        addOrgColumn(entityClass, ORG_COLUMN_NAME);
    }

    public void addOrgColumn(Class<? extends CommonEntity> entityClass, String columnName) {
        String tableName = TableInfoFactory.ofEntityClass(entityClass).getTableName();
        addOrgColumn(tableName, columnName);
    }

    public void addOrgColumn(String tableName, String columnName) {
        deptColumns.put(tableName.toUpperCase(), columnName);
        TABLE_NAMES.add(tableName.toUpperCase());
    }

    public void addUserColumn(Class<? extends CommonEntity> entityClass) {
        addUserColumn(entityClass, USER_COLUMN_NAME);
    }

    public void addUserColumn(Class<? extends CommonEntity> entityClass, String columnName) {
        String tableName = TableInfoFactory.ofEntityClass(entityClass).getTableName();
        addUserColumn(tableName, columnName);
    }

    public void addUserColumn(String tableName, String columnName) {
        userColumns.put(tableName.toUpperCase(), columnName);
        TABLE_NAMES.add(tableName.toUpperCase());
    }


    @Override
    public boolean isVerifyByTableName(String tableName) {
        if(StringUtils.isBlank(tableName) || getTableNames().isEmpty()) return false;
        return getTableNames().contains(tableName.toUpperCase());
    }
}
