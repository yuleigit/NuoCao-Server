package com.nuocao.dev.modular.sms.provider;

import com.nuocao.common.utils.json.JsonUtils;
import com.nuocao.dev.modular.sms.DevSmsHelper;
import com.nuocao.plugin.dev.api.DevSmsApi;
import com.nuocao.plugin.dev.api.enums.sms.SmsChannelEnum;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.LinkedHashMap;

/**
 * 短信接口实现
 */
@Component
public class DevApiProvider implements DevSmsApi {

    @Inject
    private DevSmsHelper devSmsHelper;

    @Override
    public void sendSms(String phoneNumbers, String templateCodeOrId, LinkedHashMap<String,String> paramMap) {
        devSmsHelper.sendSms(phoneNumbers,null,templateCodeOrId,paramMap);
    }

    @Override
    public void sendSms(SmsChannelEnum channel, String phoneNumbers, String templateCode, String templateParam) {
        devSmsHelper.sendSms(phoneNumbers,channel.getValue(),templateCode, JsonUtils.parseObject(templateParam, LinkedHashMap.class));
    }

    @Override
    public String sendSmsCode(SmsChannelEnum channel, String group, String phoneNumber,String templateCode) {
        return devSmsHelper.sendSmsCode(phoneNumber,channel.getValue(),templateCode);
    }

    @Override
    public String sendSmsCode(String group, String phoneNumber,String templateCode) {
        return devSmsHelper.sendSmsCode(group,phoneNumber,templateCode);
    }

    @Override
    public void validateSmsCode(String group, String phone, String reqId, String inputCode) {
        devSmsHelper.validateSmsCode(group,phone,reqId,inputCode);
    }
}
