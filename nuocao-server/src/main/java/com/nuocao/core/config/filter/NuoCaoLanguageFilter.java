package com.nuocao.core.config.filter;

import com.nuocao.common.language.LanguageContext;
import com.nuocao.common.utils.ConvertUtils;
import com.nuocao.common.utils.SolonFilterUtils;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;
import java.util.List;


/**
 * 语言上下文，默认为中文
 */
@Component(index=0)
@Slf4j
public class NuoCaoLanguageFilter implements Filter {

    private static final String LANGUAGE_HEADER_NAME = "Language";
    //默认语言
    private static final String DEFAULT_LANGUAGE = "cn";

    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        // 0. 放行OPTIONS预检请求,否则会导致
        if (SolonFilterUtils.isOptionsRequest(ctx)) {
            chain.doFilter(ctx);
            return;
        }

        //1.得到语言header
        String language = ConvertUtils.toStr(ctx.header(LANGUAGE_HEADER_NAME),DEFAULT_LANGUAGE);
        LanguageContext.setCurrentLocaleCode(language);

        chain.doFilter(ctx);

    }
}
