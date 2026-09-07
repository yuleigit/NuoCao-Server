package com.nuocao.common.language;

import com.nuocao.common.utils.StrFormatUtils;
import com.nuocao.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.handle.Context;
import org.noear.solon.i18n.I18nBundle;
import org.noear.solon.i18n.I18nUtil;

/**
 * 多语言工具类
 * 为什么不用solon的I18nUtil，我是想实现一个方便扩展的自定义语言方法，每个模块独立存储语言包，采用auth，user等模块名开头，或common开头，方便管理
 */
@Slf4j
public class LanguageUtils {

    /**
     * 获取i18n消息内容，获取失败，则返回key
     * @param context 当前请求上下文
     * @param code i18n code
     * @param args 参数集合
     * @return 如果code存在，则返回语言包，否则直接输出字符串
     */
    public static String getMessage(Context context,String code,Object... args){
        try{
            //得到bundle
            I18nBundle bundle = I18nUtil.getBundle(getBundleName(code),context.getLocale());
            return args != null && args.length != 0 ? StringUtils.format(bundle.get(code),args) : bundle.get(code);
        }catch (Exception ex){
            log.warn("language message not found!! language:{} ;;; code==>>{}", LanguageContext.getLocale(),code);
            return StrFormatUtils.doFormat(code,args);
        }
    }

    /**
     * 获取i18n消息内容
     * @param code i18n code
     * @param args 参数集合
     * @return 如果code存在，则返回语言包，否则直接输出字符串
     */
    public static String getMessage(String code,Object... args){
        return getMessage(Context.current(),code, args);
    }



    /**
     * 获取i18n消息内容，如果失败了，返回空字符串
     * @param context 当前请求上下文
     * @param code i18n code
     * @param defaultMsg i18n 错误时返回的默认内容
     * @param args 参数集合
     * @return 如果code存在，则返回语言包，否则直接输出字符串
     */
    public static String getMessageOrDefault(Context context,String code,String defaultMsg,Object... args){
        try{
            //得到bundle
            I18nBundle bundle = I18nUtil.getBundle(getBundleName(code),context.getLocale());
            return args != null && args.length != 0 ? StringUtils.format(bundle.get(code),args) : bundle.get(code);
        }catch (Exception ex){
            log.warn("language message not found!! language:{} ;;; code==>>{}", LanguageContext.getLocale(),code);
            return defaultMsg;
        }
    }


    /**
     * 获取i18n消息内容，如果失败了，返回空字符串
     * @param code i18n code
     * @param defaultMsg i18n 错误时返回的默认内容
     * @param args 参数集合
     * @return 如果code存在，则返回语言包，否则直接输出字符串
     */
    public static String getMessageOrDefault(String code,String defaultMsg,Object... args) {
        return getMessageOrDefault(Context.current(),code,defaultMsg,args);
    }


    /**
     * 根据code获取bundleName，取值规则为.分隔的数组第一个为模块名
     * @param code
     * @return
     */
    private static String getBundleName(String code){
        String modulerName="";
        int dotIndex= StringUtils.indexOf(code,".");
        if(dotIndex>0){
            modulerName=StringUtils.left(code,dotIndex);
        }
        return StringUtils.isNotBlank(modulerName)?StringUtils.format("i18n.{}.message",modulerName):"i18n.message";
    }



}
