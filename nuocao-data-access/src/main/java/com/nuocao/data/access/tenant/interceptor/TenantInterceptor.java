package com.nuocao.data.access.tenant.interceptor;

import cn.hutool.core.util.StrUtil;
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
import com.nuocao.sys.api.dto.SysTenantDTO;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChain;
import org.smartboot.http.common.utils.AntPathMatcher;

import java.util.List;

import static com.nuocao.data.access.tenant.constants.TenantConstants.TENANT_ID_HEADER;

/**
 * 租户请求拦截。
 *
 * <p>与旧 {@code TenantFilter} 的关键差别：
 * <ol>
 *     <li>由 {@code Filter} 改为 {@code RouterInterceptor}。Solon 的过滤器跑在路由匹配之前，
 *     {@code ctx.action()} 恒为 {@code null}，注解校验形同虚设；路由拦截器在路由匹配之后执行，
 *     可以拿到 {@link Action}，因此 {@code @TenantIgnore} 才能真正决定"是否跳过请求级租户校验"。</li>
 *     <li>入口归零 + {@code try/finally} 归还 ThreadLocal，杜绝线程池复用时的上下文残留。</li>
 * </ol>
 *
 * <p>本类不再是过滤器，不参与 {@code Filter} 链的 index 排序；{@link Component#index()} 决定的是
 * 它在路由拦截器链内的位置（升序执行，越小越早）。
 *
 * <p><b>为什么 index 必须小于 -100：</b>{@code SaTokenConfigure#saTokenInit()} 用
 * {@code @Bean(index = -100)} 注册了 {@code SaTokenInterceptor}（它同样实现 {@code RouterInterceptor}），
 * 而 Sa-Token 的注解鉴权（{@code @SaCheckPermission}）是在 {@code chain.doIntercept()} <b>之前</b>执行的，
 * 会顺着 {@code StpInterface} → {@code SysUserPermissionServiceImpl} 去查 {@code sys_user} 等
 * 带 {@code @Column(tenantId = true)} 的表。若本拦截器排在 Sa-Token 之后，
 * 那次查询就会落在"无租户上下文"上，权限判定直接失效（查询被过滤成空 → 报"用户不存在"）。
 * 因此这里取 -200，保证"先立租户上下文，再做鉴权"，与旧 Filter 的执行次序一致。
 */
@Slf4j
@Component(index = -200)
public class TenantInterceptor implements RouterInterceptor {

    @Inject
    private TenantConfig tenantConfig;

    @Inject
    private SysTenantApi sysTenantApi;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        // 1. 未启用多租户 / OPTIONS 预检 → 直接放行
        if (!tenantConfig.isEnabled() || SolonFilterUtils.isOptionsRequest(ctx)) {
            chain.doIntercept(ctx, mainHandler);
            return;
        }

        // 2. 没有匹配到任何 handler（404 / 405 / 静态资源兜底）→ 交回 Solon 处理，
        //    否则会把 404 变成"请携带租户ID"
        if (mainHandler == null) {
            chain.doIntercept(ctx, mainHandler);
            return;
        }

        // 3. 请求级「忽略租户」判定：@TenantIgnore（方法 / 类） 或 路径白名单
        boolean ignore = hasTenantIgnoreAnnotation(ctx) || matchIgnorePath(ctx);

        // 4. 入口归零，防止线程复用带上一次请求的状态
        TenantContext.setIgnore(false);
        try {
            if (ignore) {
                TenantContext.setIgnore(true);
                chain.doIntercept(ctx, mainHandler);
                return;
            }

            // ---- 以下为原有校验逻辑，语义保持不变 ----
            Long tenantId = ConvertUtils.toLong(ctx.header(TENANT_ID_HEADER), 0L);
            if (tenantId.compareTo(0L) <= 0) {
                log.info("[TenantInterceptor] -> 此次请求未携带租户id ip:{},method:{},uri:{}", ctx.realIp(), ctx.method(), ctx.uri());
                SolonFilterUtils.writeResult(ctx, CommonResult.error("请携带租户ID:Tenant-Id"));
                return;
            }
            // 先落上下文：后续所有 DB 访问（含 sys_tenant 查询）都在租户上下文内执行
            TenantContext.setCurrentTenant(tenantId);

            SaUserLoginInfoDTO userInfo = StpLoginUserUtils.getLoginUser();
            if (userInfo == null) {
                SolonFilterUtils.writeResult(ctx, CommonResult.error("未登录"));
                return;
            }
            // 反写以避免 userInfo.getTenantId() 为 null 时 NPE；租户为空的用户不允许访问任何租户
            if (!tenantId.equals(userInfo.getTenantId())) {
                log.info("[TenantInterceptor] -> 无该租户访问权限user_id:{},tenant_id:{} ip:{},method:{},uri:{}", userInfo.getId(), tenantId, ctx.realIp(), ctx.method(), ctx.uri());
                SolonFilterUtils.writeResult(ctx, CommonResult.error("无权访问此租户信息"));
                return;
            }
            // 系统租户直接放行
            if (TenantConstants.TENANT_SYSTEM_ID.equals(tenantId)) {
                chain.doIntercept(ctx, mainHandler);
                return;
            }
            // 验证租户状态
            SysTenantDTO tenant = sysTenantApi.getTenant(tenantId);
            if (tenant == null) {
                SolonFilterUtils.writeResult(ctx, CommonResult.error("不存在的租户"));
                return;
            }
            // expireTime 为空 = 永不过期（原写法会在这里 NPE）
            if (tenant.getExpireTime() != null
                    && LocalDateTimeUtils.toTimestamp(tenant.getExpireTime()) <= LocalDateTimeUtils.currentTimestamp()) {
                SolonFilterUtils.writeResult(ctx, CommonResult.error("您的账户所属单位已过期"));
                return;
            }

            chain.doIntercept(ctx, mainHandler);
        } finally {
            // 5. 正常 / 异常 / 提前 return 一律归还线程上下文
            TenantContext.clear();
        }
    }

    /**
     * 方法或类上的 {@link TenantIgnore}。只有在路由匹配之后 {@code ctx.action()} 才有值。
     */
    private boolean hasTenantIgnoreAnnotation(Context ctx) {
        Action action = ctx.action();
        if (action == null) {
            return false;
        }
        return action.method().isAnnotationPresent(TenantIgnore.class)
                || action.controller().clz().isAnnotationPresent(TenantIgnore.class);
    }

    /**
     * 路径白名单。用 {@code pathNew()}（与 Solon 路由匹配同口径，已剔除 server.contextPath），
     * 若换成 {@code path()} 则在配置了 contextPath 后白名单会全部失配。
     */
    private boolean matchIgnorePath(Context ctx) {
        List<String> patterns = tenantConfig.getIgnorePathList();
        if (patterns == null || patterns.isEmpty()) {
            return false;
        }
        String path = ctx.pathNew();
        return patterns.stream()
                .filter(StrUtil::isNotBlank)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
