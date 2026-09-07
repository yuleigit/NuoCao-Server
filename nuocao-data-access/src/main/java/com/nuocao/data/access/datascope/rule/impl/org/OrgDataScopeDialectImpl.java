package com.nuocao.data.access.datascope.rule.impl.org;

import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.*;
import com.mybatisflex.core.table.TableInfo;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.data.access.datascope.context.DataScopeContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.mybatisflex.core.constant.SqlConsts.AND;

/**
 * 组织机构数据权限自定义方言
 */
@AllArgsConstructor
@Slf4j
public class OrgDataScopeDialectImpl extends CommonsDialectImpl {


    private final OrgDataScopeRule dataScopeRule;

    @Override
    public void prepareAuth(QueryWrapper queryWrapper, OperateType operateType) {
        if(DataScopeContext.isIgnore()){
            return;
        }
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        if (queryTables == null || queryTables.isEmpty()) {
            return;
        }
        //得到
        List<QueryCondition> queryConditions = new ArrayList<>();
        for (QueryTable queryTable : queryTables) {
            QueryCondition cond = dataScopeRule.getQueryCondition(queryTable);
            if(cond!=null){
                queryConditions.add(cond);
            }
        }
        if(!queryConditions.isEmpty()){
            for (QueryCondition q:
                    queryConditions ) {
                queryWrapper.and(q);
            }

            super.prepareAuth(queryWrapper, operateType);
        }
    }

    @Override
    public void prepareAuth(String schema, String tableName, StringBuilder sql, OperateType operateType) {
        if (dataScopeRule.isVerifyByTableName(tableName)) {
            String newSql = buildConditionSql(dataScopeRule.getQueryCondition(schema,tableName,null));
            if(StringUtils.isNotBlank(newSql)){
                sql.append(AND).append("("+newSql+")");
            }
            //sql.append(AND).append(wrap("insert_user_id")).append(EQUALS).append(1);
        }
        super.prepareAuth(schema, tableName, sql, operateType);
    }

    @Override
    public void prepareAuth(TableInfo tableInfo, StringBuilder sql, OperateType operateType) {
        if (dataScopeRule.isVerifyByTableName(tableInfo.getTableName())) {
            String newSql = buildConditionSql(dataScopeRule.getQueryCondition(tableInfo.getSchema(),tableInfo.getTableName(),null));
            if(StringUtils.isNotBlank(newSql)){
                sql.append(AND).append("("+newSql+")");
            }
        }
        super.prepareAuth(tableInfo, sql, operateType);
    }

    /**
     * 将 QueryCondition 转换为 SQL 字符串
     */
    private String buildConditionSql(QueryCondition condition) {
        if(condition==null) return null;
        QueryWrapper query =QueryWrapper.create().and(condition);
        String fullSql= query.toSQL();
        int whereIndex = fullSql.toUpperCase().indexOf("WHERE");
        if (whereIndex != -1) {
            return fullSql.substring(whereIndex + 5).trim();
        }
        return null;
    }


}
