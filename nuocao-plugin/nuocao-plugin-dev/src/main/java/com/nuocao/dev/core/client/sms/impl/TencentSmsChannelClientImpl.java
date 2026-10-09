package com.nuocao.dev.core.client.sms.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.dev.core.client.sms.SmsChannelClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.nuocao.plugin.dev.api.DevConfigApi;
import com.nuocao.plugin.dev.api.enums.sms.SmsChannelEnum;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.dromara.sms4j.javase.config.SEInitializer;
import org.dromara.sms4j.provider.config.SmsConfig;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 腾讯云 短信发送 实现
 * 参考 https://next.api.aliyun.com/api-tools/sdk/Dysmsapi?version=2018-05-01&language=java-async-tea
 */
@Component
@Slf4j
public class TencentSmsChannelClientImpl implements SmsChannelClient {

    private static SmsBlend smsBlend;

    private static TencentConfig config;

    private static final String SMS_TENCENT_SECRET_ID_KEY = "sms_tencent_secret_id";
    private static final String SMS_TENCENT_SECRET_KEY_KEY = "sms_tencent_secret_key";
    private static final String SMS_TENCENT_DEFAULT_SDK_APP_ID_KEY = "sms_tencent_default_sdk_app_id";
    private static final String SMS_TENCENT_DEFAULT_SIGN_NAME_KEY = "sms_tencent_default_sign_name";


    @Data
    private static class TencentConfig {
        private String secretId;
        private String secretKey;
        private String defaultSdkAppId;
        private String defaultSignName;
    }


    @Override
    public String getChannelCode() {
        return SmsChannelEnum.ALIYUN.getValue();
    }

    @Override
    public String getDefaultSignName() {
        return config.getDefaultSignName();
    }

    @Override
    public boolean isReady() {
        return smsBlend!=null;
    }

    private boolean isSetConfig(){
        return config!=null && StringUtils.isNotBlank(config.secretId) && StringUtils.isNotBlank(config.getSecretKey())
                && StringUtils.isNotBlank(config.getDefaultSignName())
                && StringUtils.isNotBlank(config.getDefaultSdkAppId());
    }
    private void validConfig(){
        if(!this.isReady()){
            throw new CommonException("TENCENT SMS configuration incomplete");
        }
    }
    @Override
    @Init
    public void reloadConfig() {
        DevConfigApi api = BeanUtils.getBean(DevConfigApi.class);
        TencentConfig newConfig = new TencentConfig();

        newConfig.setSecretId(api.getValueByKey(SMS_TENCENT_SECRET_ID_KEY));
        newConfig.setSecretKey(api.getValueByKey(SMS_TENCENT_SECRET_KEY_KEY));
        newConfig.setDefaultSignName(api.getValueByKey(SMS_TENCENT_DEFAULT_SIGN_NAME_KEY));
        newConfig.setDefaultSdkAppId(api.getValueByKey(SMS_TENCENT_DEFAULT_SDK_APP_ID_KEY));
        config=newConfig;

        smsBlend=null; //清理之前的
        if(!isSetConfig()){
            return;
        }

        try {
           org.dromara.sms4j.tencent.config.TencentConfig tencentConfig = new org.dromara.sms4j.tencent.config.TencentConfig();
            tencentConfig.setConfigId(config.getSecretId());
            tencentConfig.setAccessKeyId(config.getSecretId());
            tencentConfig.setAccessKeySecret(config.getSecretKey());
            tencentConfig.setSignature(config.getDefaultSignName());
            tencentConfig.setSdkAppId(config.getDefaultSdkAppId());
            SEInitializer.initializer().fromConfig(new SmsConfig(), CollectionUtil.newArrayList(tencentConfig));
            smsBlend = SmsFactory.getSmsBlend(tencentConfig.getConfigId());
        }catch (Exception ex){

        }

    }

    @Override
    public String sendSms(List<String> phoneNumbers,  String apiTemplateId, LinkedHashMap<String, String> templateParams) {
        validConfig();
        try {
            // 发送短信
            SmsResponse smsResponse = smsBlend.massTexting(phoneNumbers, apiTemplateId, templateParams);
            if(smsResponse.isSuccess()) {
                return JSONUtil.toJsonStr(smsResponse.getData());
            } else {
                String data = Convert.toStr(smsResponse.getData());
                if(JSONUtil.isTypeJSON(data)) {
                    JSONObject responseData = JSONUtil.parseObj(smsResponse.getData());
                    throw new CommonException(responseData.getStr("resInfo"));
                } else {
                    throw new CommonException(data);
                }
            }
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public String sendSmsContent(List<String> phoneNumbers, String content) {
        throw new CommonException("阿里云不支持直接发送内容");
    }
}
