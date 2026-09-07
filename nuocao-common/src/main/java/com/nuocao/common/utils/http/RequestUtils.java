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
     * 获取客户端浏览器
     */
    public static String getBrowser(Context request) {
        UserAgent userAgent = getUserAgent(request);
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
        UserAgent userAgent = getUserAgent(request);
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
}
