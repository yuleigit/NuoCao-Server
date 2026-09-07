package com.nuocao.dev.core.client.sms;

import cn.hutool.json.JSONObject;
import com.nuocao.common.constants.Constants;
import com.nuocao.common.utils.ConvertUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 短信通道客户端
 */
public interface SmsChannelClient {

    /**
     * 得到通道编码
     * @return
     */
    String getChannelCode();

    /**
     * 获取默认签名
     * @return
     */
    String getDefaultSignName();

    /**
     * 是否已经载入配置
     * @return
     */
    boolean isReady();

    /**
     * 重载配置，一般在修改万配置文件后调用
     */
    void reloadConfig();


    /**
     * 发送模板短信
     * @param phoneNumbers 手机号
     * @param apiTemplateId 签名接口端模板ID
     * @param templateParams 模板参数{code:1234}
     * @return
     */
    String sendSms(List<String> phoneNumbers,  String apiTemplateId, LinkedHashMap<String,String> templateParams);

    /**
     * 直接发送短信
     * @param phoneNumbers
     * @param content 短信内容
     * @return
     */
    String sendSmsContent(List<String> phoneNumbers, String content);

    /**
     * 发送模板短信，
     * @param phoneNumber 手机号码，以,分开
     * @param apiTemplateId api端模板地址
     * @param templateParams 模板参数{code:1234}
     * @return
     */
    default String sendSms(String phoneNumber, String apiTemplateId, LinkedHashMap<String,String> templateParams ){
        return sendSms(List.of(ConvertUtils.toStrArray(Constants.SPLIT_STR,phoneNumber)),apiTemplateId,templateParams);
    }



}
