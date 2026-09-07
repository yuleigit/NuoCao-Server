package com.nuocao.data.access.tenant.filter;

import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.auth.api.dto.SaUserLoginInfoDTO;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.utils.ConvertUtils;
import com.nuocao.common.utils.LocalDateTimeUtils;
import com.nuocao.common.utils.SolonFilterUtils;
import com.nuocao.data.access.tenant.annotation.TenantIgnore;
import com.nuocao.data.access.tenant.config.TenantConfig;
import com.nuocao.data.access.tenant.constants.TenantConstants;
import com.nuocao.data.access.tenant.context.TenantContext;
import com.nuocao.sys.api.SysTenantApi;
import com.nuocao.sys.api.SysUserApi;
import com.nuocao.sys.api.dto.SysTenantDTO;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;
import org.smartboot.http.common.utils.AntPathMatcher;


import java.util.List;

import static com.nuocao.data.access.tenant.constants.TenantConstants.TENANT_ID_HEADER;


/**
 * 租户请求过滤
 * 主要用于初始化 租户相关信息、租户角色认证、权限过滤等
 */
@Component(index=2)
@Slf4j
public class TenantFilter implements Filter {

    @Inject
    private TenantConfig tenantConfig;

    @Inject
    private SysUserApi sysUserApi;

    @Inject
    private SysTenantApi sysTenantApi;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        // 0. 放行OPTIONS预检请求,否则会导致
        if (SolonFilterUtils.isOptionsRequest(ctx)) {
            chain.doFilter(ctx);
            return;
        }
        //未启用多租户则直接退出
        if(tenantConfig.getTenantEnable()==null || !tenantConfig.getTenantEnable()){
            chain.doFilter(ctx);
            return;
        }

        //默认不忽略租户
        TenantContext.setIgnore(false);
        // 检查方法或类上是否有 @TenantIgnore 注解 hasTenantIgnoreAnnotation中得不到action，所以会一直处于验证租户状态，目前写法无意义，咱是注释掉
//        if (hasTenantIgnoreAnnotation(ctx)) {
//            TenantContext.setIgnore(true);
//            chain.doFilter(ctx); //执行完以后继续限制左虎
//            TenantContext.setIgnore(false);
//            return;
//        }


        SaUserLoginInfoDTO userInfo = null;

        String ctxPath = ctx.path();
        // 检查是否匹配忽略路径
        boolean shouldIgnore = tenantConfig.getIgnorePathList().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern,  ctxPath));
        if(!tenantConfig.getTenantEnable() || shouldIgnore){
            TenantContext.setIgnore(true);
            chain.doFilter(ctx); //执行完以后继续限制左虎
            TenantContext.setIgnore(false);
            return ;
        }
        //1.得到header中的租户ID
        Long tenantId = ConvertUtils.toLong(ctx.header(TENANT_ID_HEADER),0L);

        if(tenantId.compareTo(0L)<=0){
            log.info("[TenantFilter] -> 此次请求未携带租户id ip:{},method:{},uri:{}", ctx.realIp(), ctx.method(), ctx.uri());
            SolonFilterUtils.writeResult(ctx, CommonResult.error("请携带租户ID:Tenant-Id"));
            return;
        }
        TenantContext.setCurrentTenant(tenantId);

        userInfo = StpLoginUserUtils.getLoginUser();

        if (userInfo == null) {
            SolonFilterUtils.writeResult(ctx,CommonResult.error("未登录"));
            return;
        }

        if (!userInfo.getTenantId().equals(tenantId)) {
            log.info("[TenantFilter] -> 无该租户访问权限user_id:{},tenant_id:{} ip:{},method:{},uri:{}", userInfo.getId(), tenantId, ctx.realIp(), ctx.method(), ctx.uri());
            SolonFilterUtils.writeResult(ctx,CommonResult.error("无权访问此租户信息"));
            return;
        }
        //log.info("tenant:{},{},{}",TenantContext.getCurrentTenant(),TenantContext.isIgnore(),ctx.path());
        //系统租户直接放行
        if(tenantId.equals(TenantConstants.TENANT_SYSTEM_ID))
        {
            chain.doFilter(ctx); //直接放行
            return;
        }

        //验证租户状态
        SysTenantDTO tenant = sysTenantApi.getTenant(tenantId);
        if(tenant==null){
            SolonFilterUtils.writeResult(ctx,CommonResult.error("不存在的租户"));
            return;
        }

        if(LocalDateTimeUtils.toTimestamp(tenant.getExpireTime())<= LocalDateTimeUtils.currentTimestamp()){
            SolonFilterUtils.writeResult(ctx,CommonResult.error("您的账户所属单位已过期"));
            return;
        }
        chain.doFilter(ctx);

    }

    /**
     * 检查是否含有忽略注解
     * @param ctx
     * @return
     */
    private boolean hasTenantIgnoreAnnotation(Context ctx) {
        // 1. 获取当前请求的处理器方法
        Action action = ctx.action();

        if (action != null) {
            // 2.1 检查方法上的注解
            if (action.method().isAnnotationPresent(TenantIgnore.class)) {
                TenantContext.setIgnore(true);
                return true;
            }
            // 2.2 检查类上的注解
            if (action.controller().clz().isAnnotationPresent(TenantIgnore.class)) {
                TenantContext.setIgnore(true);
                return true;
            }
        }

        return false;
    }


}
