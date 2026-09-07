package com.nuocao.common.language;

import java.util.Locale;
import java.util.Map;

/**
 * 多语言上下文
 */
public class LanguageContext {

    private static final ThreadLocal<String> CURRENT_LOCAlE_CODE = new ThreadLocal<>();

    private static Map<String,Locale> LOCALE_MAP = Map.of("cn",Locale.CHINESE,"en",Locale.ENGLISH);
    /**
     * 设置语言表示
     * @param localeCode
     */
    public static void setCurrentLocaleCode(String localeCode) {
        //不存在则设为
        if(localeCode==null || !LOCALE_MAP.containsKey(localeCode)){
            CURRENT_LOCAlE_CODE.set(LOCALE_MAP.keySet().iterator().next());
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
