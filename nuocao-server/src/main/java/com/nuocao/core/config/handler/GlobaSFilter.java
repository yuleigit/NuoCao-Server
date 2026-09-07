package com.nuocao.core.config.handler;

import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.utils.SolonFilterUtils;
import com.nuocao.common.utils.license.LicenseVerifyUtils;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;

/**
 * 授权过滤
 * @author LEI
 */
@Slf4j
@Component(index=1)
public class GlobaSFilter implements Filter {
    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        // 0. 放行OPTIONS预检请求
        if (SolonFilterUtils.isOptionsRequest(ctx)) {
            chain.doFilter(ctx);
            return;
        }
        try {
            LicenseVerifyUtils.verify();
        }catch (Exception e){
            SolonFilterUtils.writeResult(ctx, CommonResult.error(404552,e.getMessage()));
            return;
        }
        chain.doFilter(ctx);
    }
}