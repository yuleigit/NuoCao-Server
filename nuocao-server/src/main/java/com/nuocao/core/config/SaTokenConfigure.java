package com.nuocao.core.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoForRedisson;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.solon.integration.SaTokenInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import com.nuocao.common.annotation.PublicMapping;
import com.nuocao.common.pojo.KeyValue;
import com.nuocao.core.config.handler.GlobalExceptionUtil;
import io.swagger.models.HttpMethod;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.cache.redisson.RedissonClientOriginalSupplier;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.handle.MethodType;
import org.redisson.api.RedissonClient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SaToken配置
 **/
@Configuration
public class SaTokenConfigure {

    /**
     * 无需登录的接口地址集合
     */
    private static final String[] NO_LOGIN_PATH_ARR = {
            /* 主入口 */
            "/",
            /* 静态资源 */
            "/swagger-resources/**",
            "/v2/api-docs",
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
     * 注册跨域过滤器
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
                            .notMatch(CollectionUtil.newArrayList(NO_LOGIN_PATH_ARR))
                            .notMatch(getMappingPublicUrlsFromAnnotations())
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
     * 获得全部标记了MappingPublic的url，用于使用注解开放访问
     * @return
     */
    private List<String> getMappingPublicUrlsFromAnnotations() {
        Map<HttpMethod, List<String>> result = new HashMap<>();


        AppContext context = Solon.context();
        Set<String> publicEndpoints = new HashSet<>();


        // 扫描所有控制器
        context.beanForeach(beanWrap -> {
            Class<?> clazz = beanWrap.clz();

            // 检查类级别是否有 @MappingPublic 注解
            if (clazz.isAnnotationPresent(PublicMapping.class)) {
                KeyValue<String,MethodType[]> basePath = getControllerPath(clazz);
                if(basePath!=null) {
                    publicEndpoints.add(basePath.getKey());
                }
            }
        });


        return publicEndpoints.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 得到控制器地址和method
     * @param clazz
     * @return path,methods
     */
    private KeyValue<String, MethodType[]> getControllerPath(Class<?> clazz) {

        org.noear.solon.annotation.Mapping mappingAnno =
                clazz.getAnnotation(org.noear.solon.annotation.Mapping.class);
        if (mappingAnno != null) {
            return new KeyValue<>(mappingAnno.value(),mappingAnno.method());
        }
        return null;
    }


}
