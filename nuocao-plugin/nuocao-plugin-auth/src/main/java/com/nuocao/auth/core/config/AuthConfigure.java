package com.nuocao.auth.core.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import com.github.xiaoymin.knife4j.solon.extension.OpenApiExtensionResolver;
import com.nuocao.auth.api.AuthUserLoginApi;
import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.auth.core.enums.SaLoginTypeEnum;
import com.nuocao.common.cache.CacheUtils;
import com.nuocao.common.pojo.CommonHttpCodes;
import com.nuocao.common.utils.ConvertUtils;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.models.ApiInfo;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * SaToken鉴权配置
 **/
@Configuration
public class AuthConfigure {

    /** 文档分组名 */
    private static final String GROUP_NAME = "认证授权";

    /** 分组描述 */
    private static final String GROUP_DESC = "认证授权模块接口文档";

    /** 文档版本 */
    private static final String DOC_VERSION = "1.0.0";

    /** 该分组扫描的根包路径 */
    private static final String SCAN_PACKAGE = "com.nuocao.auth";

    /**
     * 注册Sa-Token的注解拦截器，打开注解式鉴权功能
     * <p>
     * 注解的方式有以下几中，注解既可以加在接口方法上，也可加在Controller类上：
     * 1.@SaCheckLogin: 登录认证 —— 只有登录之后才能进入该方法（常用）
     * 2.@SaCheckRole("admin"): 角色认证 —— 必须具有指定角色标识才能进入该方法（常用）
     * 3.@SaCheckPermission("user:add"): 权限认证 —— 必须具有指定权限才能进入该方法（常用）
     * 4.@SaCheckSafe: 二级认证校验 —— 必须二级认证之后才能进入该方法
     * 5.@SaCheckBasic: HttpBasic认证 —— 只有通过 Basic 认证后才能进入该方法
     * <p>
     * 在Controller中创建一个接口，默认不需要登录也不需要任何权限都可以访问的，只有加了上述注解才会校验
     **/
    @Bean
    public StpLogic getStpLogic() {
        // 重写Sa-Token的StpLogic，默认客户端类型为B
        return new StpLogic(SaLoginTypeEnum.ADMIN.getValue());
    }



    /**
     * 获取当前用户id放到容器里边
     */
    @Bean("user_id_bean")
    public Supplier<String> userIdBean() {
        return () -> {
            Object loginId = StpUtil.getLoginId();
            if (null == loginId) {
                return null;
            } else {
                return String.valueOf(loginId);
            }
        };
    }

    /**
     * API文档分组配置
     */
    @Bean
    public DocDocket authDocApi(@Inject OpenApiExtensionResolver extensionResolver) {
        return new DocDocket()
                .info(new ApiInfo()
                        .title(GROUP_NAME)
                        .description(GROUP_DESC)
                        .termsOfService("https://www.nuocao.com")
                        .contact("NUOCAO_TREM", "https://www.nuocao.com", "yulei6@gamil.com")
                        .version(DOC_VERSION))
                .globalResponseCodes(new CommonHttpCodes())
                .groupName(GROUP_NAME)
                .apis(SCAN_PACKAGE)
                .basicAuth(extensionResolver.getSetting().getBasic())
                .vendorExtensions(extensionResolver.buildExtensions());
    }
}
