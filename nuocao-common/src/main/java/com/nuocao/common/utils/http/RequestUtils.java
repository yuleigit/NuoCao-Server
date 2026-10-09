package com.nuocao.common.utils.http;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.Browser;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.nuocao.common.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.handle.Context;

/**
 * Http Request工具类
 */
@Slf4j
public class RequestUtils {

    /**
     * 从请求中中获取参数
     **/
    public static String getParamFromRequest(String paramName) {
        Context request = getCurrentRequest();

        // 1. 尝试从请求体里面读取
        String paramValue = request.param(paramName);

        // 2. 尝试从header里读取
        if (ObjectUtil.isEmpty(paramValue)) {
            paramValue = request.header(paramName);
        }
        // 3. 尝试从cookie里读取
        if (ObjectUtil.isEmpty(paramValue)) {
            paramValue = request.cookie(paramName);
        }
        // 4. 返回
        return paramValue;
    }


    /**
     * 获取当前用户的Request 上下文Context
     * @return
     */
    public static Context getCurrentRequest() {
        Context context;
        try {
            context = Context.current();
        } catch (Exception e) {
            log.error(">>> Non web context cannot be obtained Request：", e);
            throw new CommonException("Non web context cannot be obtained Request");
        }
        if (context == null) {
            throw new CommonException("Non web context cannot be obtained Request");
        } else {
            return context;
        }
    }

    public static boolean isWeb() {
        return Context.current() != null;
    }


    /**
     * 获取当前用户浏览器
     * @return
     */
    public static String getCurrentBrowser(){
        return getBrowser(getCurrentRequest());
    }

    /**
     * 获取当前用户浏览器
     * @return
     */
    public static String getCurrentOs(){
        return getOs(getCurrentRequest());
    }
    /**
     * 获取当前用户浏览器
     * @return
     */
    public static UserAgent getCurrentUserAgent(){
        return getUserAgent(getCurrentRequest());
    }

    /**
     * 解析请求的 UserAgent，供多处复用。
     *
     * <p>UserAgent 解析是正则匹配，有一定开销；同时需要浏览器和操作系统信息时，
     * 请解析一次后分别调用 {@link #getBrowser(UserAgent)} / {@link #getOs(UserAgent)}，
     * 不要重复调用 {@link #getBrowser(Context)} 与 {@link #getOs(Context)}。</p>
     */
    public static UserAgent parseUserAgent(Context request) {
        return getUserAgent(request);
    }

    /**
     * 获取客户端浏览器
     */
    public static String getBrowser(Context request) {
        return getBrowser(getUserAgent(request));
    }

    /**
     * 获取客户端浏览器（复用已解析的 UserAgent）
     */
    public static String getBrowser(UserAgent userAgent) {
        if (ObjectUtil.isEmpty(userAgent)) {
            return StrUtil.DASHED;
        } else {
            String browser = userAgent.getBrowser().toString();
            if (StrUtil.isNotBlank(browser) && browser.length() > 250) {
                browser = browser.substring(0, 250);
            }
            return "Unknown".equals(browser) ? StrUtil.DASHED : browser;
        }
    }

    /**
     * 获取客户端操作系统
     */
    public static String getOs(Context request) {
        return getOs(getUserAgent(request));
    }

    /**
     * 获取客户端操作系统（复用已解析的 UserAgent）
     */
    public static String getOs(UserAgent userAgent) {
        if (ObjectUtil.isEmpty(userAgent)) {
            return StrUtil.DASHED;
        } else {
            String os = userAgent.getOs().toString();
            if (StrUtil.isNotBlank(os) && os.length() > 250) {
                os = os.substring(0, 250);
            }
            return "Unknown".equals(os) ? StrUtil.DASHED : os;
        }
    }

    /**
     * 获取请求代理头
     */
    private static UserAgent getUserAgent(Context request) {
        String userAgentStr = request.userAgent();
        UserAgent userAgent = UserAgentUtil.parse(userAgentStr);
        if (ObjectUtil.isNotEmpty(userAgentStr)) {
            if ("Unknown".equals(userAgent.getBrowser().getName())) {
                userAgent.setBrowser(new Browser(userAgentStr, null, ""));
            }
        }
        return userAgent;
    }

    /**
     * 获取当前请求url，如不存在则为空
     * @return
     */
    public static String getReqUrl(){
        String url="";
        try {
            Context context = Context.current();
            if (context != null) {
                url= context.url();
            }
        }catch (Exception ex){
            url="";
        }
        return url;
    }
}
