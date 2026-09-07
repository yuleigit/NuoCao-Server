package com.nuocao.dev.core.client.sms.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.dev.core.client.file.impl.MinIoFileStorageClientImpl;
import com.nuocao.dev.core.client.sms.SmsChannelClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import nuocao.plugin.dev.api.DevConfigApi;
import nuocao.plugin.dev.api.enums.sms.SmsChannelEnum;
import org.dromara.sms4j.aliyun.config.AlibabaConfig;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.dromara.sms4j.javase.config.SEInitializer;
import org.dromara.sms4j.provider.config.SmsConfig;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.validation.annotation.Date;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 阿里云短信发送 实现
 * 参考 https://next.api.aliyun.com/api-tools/sdk/Dysmsapi?version=2018-05-01&language=java-async-tea
 */
@Component
@Slf4j
public class AliyunSmsChannelClientImpl implements SmsChannelClient {

    private static SmsBlend smsBlend;

    private static AliyunConfig config;

    private static final String SMS_ALIYUN_ACCESS_KEY_ID_KEY = "sms_aliyun_access_key_id";
    private static final String SMS_ALIYUN_ACCESS_KEY_SECRET_KEY = "sms_aliyun_access_key_secret";
    private static final String SMS_ALIYUN_DEFAULT_SIGN_NAME_KEY = "sms_aliyujn_default_sign_name";

    @Data
    private static class AliyunConfig {
        private String accessKeyId;
        private String accessKeySecret;
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
        return StringUtils.isNotBlank(config.accessKeyId) && StringUtils.isNotBlank(config.getAccessKeySecret())
                && StringUtils.isNotBlank(config.getDefaultSignName());
    }

    private void validConfig(){
        if(!this.isReady()){
            throw new CommonException("Alibaba SMS configuration incomplete");
        }
    }

    @Override
    @Init
    public void reloadConfig() {
        DevConfigApi api = BeanUtils.getBean(DevConfigApi.class);
        AliyunConfig newConfig = new AliyunConfig();

        newConfig.setAccessKeyId(api.getValueByKey(SMS_ALIYUN_ACCESS_KEY_ID_KEY));
        newConfig.setAccessKeySecret(api.getValueByKey(SMS_ALIYUN_ACCESS_KEY_SECRET_KEY));
        newConfig.setDefaultSignName(api.getValueByKey(SMS_ALIYUN_DEFAULT_SIGN_NAME_KEY));
        config=newConfig;

        smsBlend=null; //清理之前的
        if(!isSetConfig()){
            return;
        }

        try {
            AlibabaConfig alibabaConfig = new AlibabaConfig();
            alibabaConfig.setConfigId(newConfig.getAccessKeyId());
            alibabaConfig.setAccessKeyId(newConfig.getAccessKeyId());
            alibabaConfig.setAccessKeySecret(newConfig.getAccessKeySecret());
            alibabaConfig.setSignature(getDefaultSignName());
            SEInitializer.initializer().fromConfig(new SmsConfig(), CollectionUtil.newArrayList(alibabaConfig));
            smsBlend = SmsFactory.getSmsBlend(alibabaConfig.getConfigId());
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
                    throw new CommonException(responseData.getStr("Message"));
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
