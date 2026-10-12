package com.nuocao.common.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StrFormatUtils {
    /**
     * 将错误编号对应的消息使用 param 进行格式化。
     *
     * @param code           错误编号
     * @param messagePattern 消息模版
     * @param params         参数
     * @return 格式化后的提示
     */
    public static String doFormat(int code, String messagePattern, Object... params) {
        StringBuilder sbuf = new StringBuilder(messagePattern.length() + 50);
        int i = 0;
        int j;
        int l;

        for (l = 0; l < params.length; l++) {
            j = messagePattern.indexOf("{}", i);
            if (j == -1) {
                log.error("[doFormat][参数过多：错误码({})|错误内容({})|参数({})", code, messagePattern, params);
                if (i == 0) {
                    return messagePattern;
                } else {
                    sbuf.append(messagePattern.substring(i));
                    return sbuf.toString();
                }
            } else {
                sbuf.append(messagePattern, i, j);
                sbuf.append(params[l]);
                i = j + 2;
            }
        }
        if (messagePattern.indexOf("{}", i) != -1) {
            log.error("[doFormat][参数过少：错误码({})|错误内容({})|参数({})", code, messagePattern, params);
        }
        sbuf.append(messagePattern.substring(i));
        return sbuf.toString();
    }

    /**
     * 将错误编号对应的消息使用 param 进行格式化。
     *
     * @param messagePattern 消息模版
     * @param params         参数
     * @return 格式化后的提示
     */
    public static String doFormat( String messagePattern, Object... params) {
        return doFormat(-404,messagePattern,params);
    }
}
