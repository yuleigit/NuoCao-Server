package com.nuocao.core.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoForRedisson;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.solon.integration.SaTokenInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import com.nuocao.common.annotation.PublicMapping;
import com.nuocao.core.config.handler.GlobalExceptionUtil;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.Routing;
import org.noear.solon.core.route.VersionedTarget;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * SaToken配置
 *
 * <p>关于「无需登录的接口」共有两个来源：
 * <ol>
 *     <li>{@link #NO_LOGIN_PATH_ARR} —— 静态路径白名单（登录接口、静态资源等）；</li>
 *     <li>{@link PublicMapping} 注解 —— 标在 Controller 类或方法上，从 Solon 路由表里反查出来。</li>
 * </ol>
 * 两者在 {@link #initAnonUrls()} 里合并为一份「匿名放行 URI 列表」（{@link #anonUrls}），
 * 只在<b>启动期</b>计算一次；请求链路上只做一次 {@code notMatch} 判断，不再重复扫描。
 *
 * <p>注意：注解白名单是「按最终 URI」匹配的，所以必须枚举<b>路由表</b>拿全路径，
 * 不能用「Controller 类上的 {@code @Mapping} 前缀」去匹配 —— 前缀 `/dev/config`
 * 并不等于最终 URI `/dev/config/sysConfigList`（见 {@code PathMatcher} 语义）。
 **/
@Configuration
public class SaTokenConfigure {

    private static final Logger log = LoggerFactory.getLogger(SaTokenConfigure.class);

    /**
     * 无需登录的接口地址集合（静态部分）
     */
    private static final String[] NO_LOGIN_PATH_ARR = {
            /* 主入口 */
            "/",
            /* 静态资源 */
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**",
            "/v2/api-docs",
            "/v2/api-docs/**",
            "/doc.html",
            "/v2/api-docs-ext",
            "/configuration/ui",
            "/configuration/security",
            "/ureport/**",
            "/druid/**",
            "/images/**",
            /*账号登录公开接口  */
            "/auth/account/**"
    };

    /**
     * 匿名放行的最终 URI 列表 = 静态白名单 + {@link PublicMapping} 扫描结果。
     *
     * <p>由 {@link #initAnonUrls()} 在启动期一次性算好：{@code @Init} 注册为 index=1 的生命周期节点，
     * HTTP 端口监听是 index=99，升序执行 ⇒ 首个请求进来时本字段必然已就绪。
     * 请求链路上只读不写，用 {@code volatile} 保证跨线程可见性。
     */
    private volatile List<String> anonUrls = Collections.emptyList();

    /**
     * 注册鉴权拦截器
     */
    @Bean(index = -100)
    public SaTokenInterceptor saTokenInit() {
        return new SaTokenInterceptor()
                // 指定拦截路由
                .addInclude("/**")
                .addExclude("/swagger-resources", "/swagger/*") //todo: 用于支持 swagger-resouces

                // 设置鉴权的接口
                .setAuth(r -> {
                    SaRouter.match("/**")
                            // anonUrls 已在启动期算好：静态白名单与 @PublicMapping 已合并为一份列表，
                            // 此处只引用、不计算（notMatch 的参数表达式在每次请求都会被求值）。
                            .notMatch(anonUrls)
                            .check(r1 -> StpUtil.checkLogin());
                })
                // 前置函数：在每次认证函数之前执行
                .setBeforeAuth(obj -> {

                    // ---------- 设置跨域响应头 ----------
                    SaHolder.getResponse()

                            // 是否可以在iframe显示视图： DENY=不可以 | SAMEORIGIN=同域下可以 | ALLOW-FROM uri=指定域名下可以
                            // .setHeader("X-Frame-Options", "SAMEORIGIN")

                            // 是否启用浏览器默认XSS防护： 0=禁用 | 1=启用 | 1; mode=block 启用, 并在检查到XSS攻击时，停止渲染页面
                            .setHeader("X-XSS-Protection", "1; mode=block")
                            // 禁用浏览器内容嗅探
                            .setHeader("X-Content-Type-Options", "nosniff")
                            // 允许指定域访问跨域资源
                            .setHeader("Access-Control-Allow-Origin", "*")
                            // 允许所有请求方式
                            .setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
                            // 有效时间
                            .setHeader("Access-Control-Max-Age", "3600")
                            // 允许的header参数
                            .setHeader("Access-Control-Allow-Headers", "*");

                    // 如果是预检请求，则立即返回到前端
                    SaRouter.match(SaHttpMethod.OPTIONS)
                            // OPTIONS预检请求，不做处理
                            .free(r -> {
                            })
                            .back();
                }).setError(e -> {
                    // 由于过滤器中抛出的异常不进入全局异常处理，所以必须提供[异常处理函数]来处理[认证函数]里抛出的异常
                    // 在[异常处理函数]里的返回值，将作为字符串输出到前端，此处统一转为JSON输出前端
                    SaResponse saResponse = SaHolder.getResponse();
                    saResponse.setHeader(Header.CONTENT_TYPE.getValue(), ContentType.JSON + ";charset=" + CharsetUtil.UTF_8);
                    return GlobalExceptionUtil.getCommonResult((Exception) e);
                });
    }

//    @Bean //redisx配置
//    public SaTokenDao saTokenDaoInit(@Inject("${sa-token.redis}") SaTokenDaoForRedisx saTokenDao) {
//        return saTokenDao;
//    }

//    @Bean //redisson配置
//    public SaTokenDao saTokenDaoInit(@Inject("${sa-token.redis}") SaTokenDaoForRedisson saTokenDaoForRedisson){
//        return saTokenDaoForRedisson;
//    }
    @Bean
    public SaTokenDao saTokenDaoInit(@Inject RedissonClient redissonClient) {
        // 手动构造并传入 RedissonClient
        return new SaTokenDaoForRedisson(redissonClient);
    }

    /**
     * 启动期收集「匿名放行 URI」：静态白名单 + 路由表上标了 {@link PublicMapping} 的接口。
     *
     * <p><b>必须是同步 {@code @Init}，禁止改成 {@code @Init(async = true)}。</b>
     * Solon 把 {@code @Init} 注册为 index=1 的生命周期节点（{@code @Init#index()} 默认 0，
     * 注册时取 {@code index() + 1}），端口监听由 {@code SmHttpPlugin} 注册为 index=99，
     * 生命周期按 index 升序执行 ⇒ 本方法一定跑在端口对外服务之前。一旦标成 {@code async}，
     * 它会被挪到 {@code executeAsyncInitTasks} 里延后执行（发生在端口监听之后），保证失效。
     *
     * <p>另外 {@code @Init} 的执行时机（{@code AppContext.start()}）晚于 {@code beanScan}，
     * 此刻 Controller 路由已注册完整，所以能扫全。注意<b>不要</b>把扫描放进
     * {@code saTokenInit()} 的 {@code @Bean} 方法里 —— 那发生在 {@code beanScan} 期间，
     * 与 Controller 注册的先后顺序不确定，可能扫不全。
     */
    @Init
    public void initAnonUrls() {
        Set<String> urls = new LinkedHashSet<>(Arrays.asList(NO_LOGIN_PATH_ARR));
        try {
            urls.addAll(scanPublicUrlsFromAnnotations());
        } catch (Exception e) {
            // 旁路能力不影响启动主链路：扫不到就退回静态白名单
            // （失败方向是「多要一次登录」，不会误放行，可接受）
            log.error("[SaToken] 扫描 {} 失败，本次仅使用静态白名单", PublicMapping.class.getSimpleName(), e);
        }
        this.anonUrls = Collections.unmodifiableList(new ArrayList<>(urls));
        log.info("[SaToken] 匿名放行 URI（{} 个）: {}", anonUrls.size(), anonUrls);
    }

    /**
     * 扫描路由表，收集类或方法上标注了 {@link PublicMapping} 的最终 URI。
     *
     * <p>数据来源是 Solon 的<b>路由表</b>（{@code Solon.app().router().findAll()}）而不是 Controller 的类注解：
     * 路由表里的 {@code path()} 已经是「类上的 {@code @Mapping} 前缀 + 方法上的 {@code @Mapping}」拼好的最终 URI，
     * 并且天然覆盖「多路径、路径参数 {@code {id}}、继承的父类 Controller」等情况。
     *
     * @return 公开访问的 URI 列表
     */
    private List<String> scanPublicUrlsFromAnnotations() {
        Set<String> urls = new LinkedHashSet<>();

        Collection<Routing<Handler>> routings = Solon.app().router().findAll();
        if (routings != null) {
            for (Routing<Handler> routing : routings) {
                Collection<VersionedTarget<Handler>> targets = routing.targets();
                if (targets == null) {
                    continue;
                }
                for (VersionedTarget<Handler> versionedTarget : targets) {
                    Handler handler = versionedTarget.getTarget();
                    // 注：本模块 source level 为 11，不能用 instanceof 模式匹配
                    if (handler instanceof Action && isPublicAction((Action) handler)) {
                        urls.add(routing.path());
                        break;
                    }
                }
            }
        }

        return new ArrayList<>(urls);
    }

    /**
     * 判断该 Action 是否允许匿名访问：方法上或所属 Controller 类上有 {@link PublicMapping} 即算公开。
     */
    private boolean isPublicAction(Action action) {
        // 1. 方法级注解
        if (action.method().isAnnotationPresent(PublicMapping.class)) {
            return true;
        }
        // 2. 类级注解
        return action.controller().clz().isAnnotationPresent(PublicMapping.class);
    }

}
