package com.nuocao.dev.core.integration;

import com.nuocao.common.annotation.CommonLog;
import com.nuocao.dev.core.aop.DevLogInterceptor;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

public class PluginImpl implements Plugin {
    @Override
    public void start(AppContext context) {
        context.beanInterceptorAdd(CommonLog.class, new DevLogInterceptor());
    }
}
