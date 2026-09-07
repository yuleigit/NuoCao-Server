package com.nuocao.data.access.datascope.aop;

import com.nuocao.data.access.datascope.annotation.DataScopeIgnore;
import com.nuocao.data.access.datascope.context.DataScopeContext;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

/**
 * 数据权限上线文监听
 */
@Component
public class DataScopeAspect implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {

        // 检查是否有忽略注解
        DataScopeIgnore methodAnnotation = inv.getMethodAnnotation(DataScopeIgnore.class);
        DataScopeIgnore classAnnotation = inv.getTargetAnnotation(DataScopeIgnore.class);
        boolean shouldIgnore = (methodAnnotation != null || classAnnotation != null);

        // 如果没有忽略注解，直接执行方法
        if (!shouldIgnore) {
            return inv.invoke();
        }

        // 有忽略注解：保存当前状态，设置忽略，执行后恢复
        boolean previousState = DataScopeContext.isIgnore();

        try {
            DataScopeContext.setIgnore(true);
            return inv.invoke();
        } finally {
            // 恢复之前的状态
            DataScopeContext.setIgnore(previousState);
        }
    }
}