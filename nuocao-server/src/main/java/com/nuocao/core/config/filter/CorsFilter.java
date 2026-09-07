package com.nuocao.core.config.filter;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;

@Component(index = 0) // 确保优先级更高
public class CorsFilter implements Filter {
    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        ctx.headerSet("Access-Control-Allow-Origin", "*");
        ctx.headerSet("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        ctx.headerSet("Access-Control-Allow-Headers", "Tenant-Id, Content-Type");
        chain.doFilter(ctx);
    }
}