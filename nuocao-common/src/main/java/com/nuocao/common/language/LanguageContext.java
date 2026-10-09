package com.nuocao.common.language;

import java.util.Locale;
import java.util.Map;

/**
 * 多语言上下文
 */
public class LanguageContext {

    /** 默认语言标识（显式定义，避免依赖 Map 无序的“第一个 key”） */
    private static final String DEFAULT_LOCALE_CODE = "cn";

    private static final ThreadLocal<String> CURRENT_LOCAlE_CODE = new ThreadLocal<>();

    private static final Map<String,Locale> LOCALE_MAP = Map.of("cn",Locale.CHINESE,"en",Locale.ENGLISH);
    /**
     * 设置语言表示
     * @param localeCode
     */
    public static void setCurrentLocaleCode(String localeCode) {
        //不存在则设为默认语言
        if(localeCode==null || !LOCALE_MAP.containsKey(localeCode)){
            CURRENT_LOCAlE_CODE.set(DEFAULT_LOCALE_CODE);
        }
        else {
            CURRENT_LOCAlE_CODE.set(localeCode);
        }
    }

    /**
     * 获取获取语言标识
     * @return
     */
    public static String getCurrentLocaleCode() {
        return CURRENT_LOCAlE_CODE.get();
    }

    /**
     * 获取当前语言
     * @return
     */
    public static Locale getLocale(){
        if(CURRENT_LOCAlE_CODE.get()==null || !LOCALE_MAP.containsKey(CURRENT_LOCAlE_CODE.get())) return Locale.CHINESE;
        return LOCALE_MAP.get(CURRENT_LOCAlE_CODE.get());
    }

}
