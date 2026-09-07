package com.nuocao.core.config;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.ContentType;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.FlexGlobalConfig;
import com.nuocao.common.annotation.CommonNoRepeat;
import com.nuocao.common.cache.CacheUtils;
import com.nuocao.common.language.LanguageUtils;
import com.nuocao.common.pojo.CommonEntity;
import com.nuocao.common.pojo.CommonResult;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;


import java.lang.reflect.Method;

/**
 * 全局配置配置
 **/
@Configuration
public class GlobalConfigure {

    private static final String COMMON_REPEAT_SUBMIT_CACHE_KEY = "dev-repeatSubmit:";

    @Inject
    private CacheUtils cacheUtils;

    /**
     * 静态资源映射
     **/
    @Bean
    public void addResourceHandlers() {
        StaticMappings.add("/doc.html", new ClassPathStaticRepository("/META-INF/resources/"));
        StaticMappings.add("/webjars/", new ClassPathStaticRepository("/META-INF/resources/webjars/"));
    }

    /**
     * 添加节流防抖拦截器
     **/
    @Bean
    public void addInterceptors() {
        Solon.app().routerInterceptor((ctx, mainHandler, chain) -> {
            Action action = ctx.action();
            if (action != null) {
                Method method = action.method().getMethod();
                CommonNoRepeat annotation = method.getAnnotation(CommonNoRepeat.class);
                if (ObjectUtil.isNotEmpty(annotation)) {
                    JSONObject repeatSubmitJsonObject = this.isRepeatSubmit(ctx, annotation);
                    if (repeatSubmitJsonObject.getBool("repeat")) {
                        ctx.charset(CharsetUtil.UTF_8);
                        ctx.contentType(ContentType.JSON.toString());
                        ctx.outputAsJson(JSONUtil.toJsonStr(CommonResult.error(LanguageUtils.getMessage("common.request.repeat",repeatSubmitJsonObject.getStr("time")))));
                        return;
                    }
                }
            }
            chain.doIntercept(ctx, mainHandler);
        });
    }

    protected JSONObject isRepeatSubmit(Context ctx, CommonNoRepeat annotation) {
        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.set("repeatParam", JSONUtil.toJsonStr(ctx.paramMap()));
        jsonObject.set("repeatTime", DateUtil.current());
        String url = ctx.url();

        // 获取该接口缓存的限流数据
        Object cacheObj = cacheUtils.get(COMMON_REPEAT_SUBMIT_CACHE_KEY + url);
        if (ObjectUtil.isNotEmpty(cacheObj)) {
            JSONObject cacheJsonObject = JSONUtil.parseObj(cacheObj);
            if (cacheJsonObject.containsKey(url)) {
                JSONObject existRepeatJsonObject = cacheJsonObject.getJSONObject(url);
                // 如果与上次参数一致，且时间间隔小于要求的限流时长，则判定为重复提交
                if (jsonObject.getStr("repeatParam").equals(existRepeatJsonObject.getStr("repeatParam"))) {
                    long interval = jsonObject.getLong("repeatTime") - existRepeatJsonObject.getLong("repeatTime");
                    if (interval < annotation.interval()) {
                        long secondsParam = (annotation.interval() - interval) / 1000;
                        if (secondsParam == 0) {
                            return JSONUtil.createObj().set("repeat", false);
                        } else {
                            return JSONUtil.createObj().set("repeat", true).set("time", (secondsParam));
                        }
                    }
                }
            }
        }
        // 缓存最新的该接口的限流数据，为防止缓存的数据过多，缓存时效为1小时
        cacheUtils.setObject(COMMON_REPEAT_SUBMIT_CACHE_KEY + url, JSONUtil.createObj().set(url, jsonObject), 60 * 60);
        return JSONUtil.createObj().set("repeat", false);
    }


    /**
     * 全局配置flex监听
     */
    @Init
    public void registerListenerByFlex() {
        CustomMetaObjectListener customMetaObjectListener = new CustomMetaObjectListener();
        FlexGlobalConfig config = FlexGlobalConfig.getDefaultConfig();

        config.setDeletedValueOfLogicDelete(1);
        config.setNormalValueOfLogicDelete(0);

        config.registerInsertListener(customMetaObjectListener, CommonEntity.class);
        config.registerUpdateListener(customMetaObjectListener, CommonEntity.class );
    }

}
