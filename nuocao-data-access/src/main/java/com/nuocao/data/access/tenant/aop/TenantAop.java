package com.nuocao.data.access.tenant.aop;

import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;
import com.nuocao.data.access.tenant.annotation.TenantIgnore;
import com.nuocao.data.access.tenant.context.TenantContext;
import lombok.RequiredArgsConstructor;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nuocao.data.access.tenant.constants.TenantConstants.TENANT_ID_COLUMN_NAME;


@Component
@RequiredArgsConstructor
@Deprecated
public class TenantAop implements Interceptor {
    @Inject("${tenant.enable}")
    private boolean tenantEnable;

    @Inject("${tenant.table-ignore}")
    private List<String> ignoreTableList;

    private boolean shouldSkip(TenantIgnore methodAnno, TenantIgnore classAnno) {
        if(!tenantEnable){
            return true; //未开启
        }
        if(TenantContext.isIgnore()){
            return true;
        }

        return methodAnno !=null || classAnno!=null; // 有忽略注解，则跳过
    }

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        //1.读取忽略注解
        TenantIgnore methodAnnotation = inv.getMethodAnnotation(TenantIgnore.class);
        TenantIgnore classAnnotation  = inv.getTargetAnnotation(TenantIgnore.class);



        return inv.invoke();
    }

    private  void  handleQueryWrapper(QueryWrapper queryWrapper, Long tenantId) {
        // 1. 获取主表信息
        List<QueryTable> collect = CPI.getQueryTables(queryWrapper)
                .stream().collect(Collectors.toList());

        //boolean hasTablePermission = collect.stream().map(QueryTable::getName).anyMatch(tableName::contains);

        //2.得到表明对应查询信息
        Map<String, QueryTable> map = collect.stream().collect(Collectors.groupingBy(QueryTable::getName,Collectors.collectingAndThen(Collectors.toList(), v->v.get(0))));

//        for (QueryTable queryTable : collect) {
//            System.err.println(queryTable);
//
//        }
        //3. 循环拼接sql条件
        for (String tableName :  map.keySet()){
            if(ignoreTableList.contains(tableName.toLowerCase())){
                continue; //忽略表不处理
            }
            String whereSql= TENANT_ID_COLUMN_NAME+"="+tenantId;
            //组合
            QueryTable queryTables = map.get(tableName);
            if (StrUtil.isNotEmpty(queryTables.getAlias())){
                queryWrapper.and(queryTables.getAlias()+"."+whereSql);
            }else {
                queryWrapper.and(whereSql);
            }

        }
    }


}
