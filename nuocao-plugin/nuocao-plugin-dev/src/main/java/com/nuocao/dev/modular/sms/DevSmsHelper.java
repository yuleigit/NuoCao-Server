package com.nuocao.dev.modular.sms;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.excel.util.StringUtils;
import com.nuocao.common.ObjectUtils;
import com.nuocao.common.cache.CacheUtils;
import com.nuocao.common.captcha.CaptchaGroupEnum;
import com.nuocao.common.enums.CommonUserTypeEnum;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.utils.LocalDateTimeUtils;
import com.nuocao.common.utils.StrFormatUtils;
import com.nuocao.common.utils.json.JsonUtils;
import com.nuocao.common.utils.uuid.IdUtils;
import com.nuocao.dev.core.client.sms.SmsChannelClient;
import com.nuocao.dev.core.client.sms.SmsChannelFactory;
import com.nuocao.dev.modular.sms.constants.SmsConstants;
import com.nuocao.dev.modular.sms.log.param.DevSmsLogAddParam;
import com.nuocao.dev.modular.sms.log.service.DevSmsLogService;
import com.nuocao.dev.modular.sms.template.service.DevSmsTemplateService;
import nuocao.plugin.dev.api.DevConfigApi;
import nuocao.plugin.dev.api.dto.sms.DevSmsTemplateDTO;
import nuocao.plugin.dev.api.enums.sms.DevSmsTypeEnum;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 短信工具
 */
@Component
public class DevSmsHelper {

    //通道工厂
    @Inject
    private SmsChannelFactory smsChannelFactory;

    @Inject
    private  DevConfigApi devConfigApi ;

    @Inject
    private CacheUtils cacheUtils;

    @Inject
    private DevSmsTemplateService devSmsTemplateService;

    @Inject
    private DevSmsLogService devSmsLogService;

    private static final String DEFAULT_GROUP="default"; //验证码默认业务分组

    /**
     * 短信验证码缓存key，参数为group,phoneNumber,reqId
     */
    private static final String SMS_CODE_CACHE_PREFIX= "sms-valid-code:{}:{}:{}";

    /**
     * 发送短信验证码
     * @param phoneNumber 手机号码
     * @param group 业务分组 {@link  CaptchaGroupEnum}
     * @param userId 发送给的用户
     * @param userType 发送给的用户类型{@link CommonUserTypeEnum}
     * @param templateCode 模板编码
     * @param useChannelCode 使用通道 {@link nuocao.plugin.dev.api.enums.sms.SmsChannelEnum}
     * @param extParams 扩展模板参数，用于自定义短信验证码环境，一般不用传入
     * @return reqId，请求号，使用验证码时需要携带
     */
    private String doSendSmsCode(String phoneNumber,Long userId,String userType, String group, String useChannelCode, String templateCode, LinkedHashMap<String,String> extParams){

        useChannelCode = useChannel(useChannelCode);
        if(StringUtils.isBlank(group)){
            group=DEFAULT_GROUP;
        }
        //获取发送客户端
        SmsChannelClient client = smsChannelFactory.getClient(useChannelCode);
        //创建一个验证码
        String smsCode = RandomUtil.randomNumbers(6);
        //生成请求ID
        String smsReqId = IdUtils.snowflakeIdStr();

        LinkedHashMap<String,String> paramsMap = new LinkedHashMap<>();

        if(ObjectUtils.isNotEmpty(extParams)){
            paramsMap.putAll(extParams);
        }
        paramsMap.put("code",smsCode);


        DevSmsTemplateDTO smsTemplate = devSmsTemplateService.getAndValidDtoByChannelAndCode(client.getChannelCode(),templateCode);

        //验证模板参数是否符合预期
        devSmsTemplateService.validParams(smsTemplate,paramsMap);

        //发送短信，返回发送结果，一般时成功的消息
        String resultMessage = "";
        Integer resultCode= CommonResult.CODE_SUCCESS;
        try {
            resultMessage = client.sendSms(phoneNumber, smsTemplate.getApiTemplateId(), paramsMap);
        }catch (Exception ex){
            resultMessage = ex.getMessage();
            resultCode=CommonResult.CODE_ERROR;
        }

        //写入验证码缓存
        String cacheCodeKey = getCodeCacheKey(group,phoneNumber,smsReqId);
        cacheUtils.setObject(cacheCodeKey,smsCode,60*10); //10分钟有效

        createLog(DevSmsTypeEnum.CODE, client.getChannelCode(),templateCode,smsTemplate.getContent(),
                phoneNumber,JsonUtils.toJsonString(paramsMap),userType,userId,smsReqId,resultCode.toString(),resultMessage);

        return smsReqId;
    }

    public String sendSmsCode(String phoneNumber, String group, String useChannelCode, String templateCode, LinkedHashMap<String,String> extParams)
    {
        return doSendSmsCode(phoneNumber,null,null,group,useChannelCode,templateCode,extParams);
    }
    public String sendSmsCode(String phoneNumber, String group, String templateCode, LinkedHashMap<String,String> extParams)
    {
        return doSendSmsCode(phoneNumber,null,null,group,null,templateCode,extParams);
    }

    public String sendSmsCode(String phoneNumber, String group, String templateCode)
    {
        return doSendSmsCode(phoneNumber,null,null,group,null,templateCode,null);
    }

    public String sendSmsCodeToUser(String phoneNumber,Long userId,String userType, String group, String useChannelCode, String templateCode, LinkedHashMap<String,String> extParams){
        return doSendSmsCode(phoneNumber,userId,userType,group,useChannelCode,templateCode,extParams);
    }
    public String sendSmsCodeToUser(String phoneNumber,Long userId,String userType, String group, String templateCode, LinkedHashMap<String,String> extParams){
        return doSendSmsCode(phoneNumber,userId,userType,group,null,templateCode,extParams);
    }
    public String sendSmsCodeToUser(String phoneNumber,Long userId,String userType, String group, String templateCode)
    {
        return doSendSmsCode(phoneNumber,userId,userType,group,null,templateCode,null);
    }
    /**
     * 验证验证码是否正确，错误则抛出异常
     * @param group
     * @param phoneNumber
     * @param smsValidCodeReqId 发送验证码时生成的请求ID
     * @param inputCode
     */
    public void validateSmsCode(String group, String phoneNumber, String smsValidCodeReqId, String inputCode){
        if(StringUtils.isBlank(smsValidCodeReqId) || StringUtils.isBlank(phoneNumber) || StringUtils.isBlank(inputCode)){
            throw new CommonExceptionI18n("dev.sms.code.error");
        }
        String cacheCodeKey = getCodeCacheKey(StringUtils.isBlank(group)?DEFAULT_GROUP:group
                ,phoneNumber,smsValidCodeReqId);
        //获取缓存
        String smsCode = cacheUtils.get(cacheCodeKey,String.class);
        if(inputCode.equals(smsCode)){
            return ;
        }
        throw new CommonExceptionI18n("dev.sms.code.error");
    }


    /**
     * 发送短信验证码
     * @param phoneNumber 手机号码
     * @param userId 发送给的用户
     * @param userType 发送给的用户类型 {@link CommonUserTypeEnum}
     * @param templateCode 模板编码
     * @param useChannelCode 使用通道 {@link nuocao.plugin.dev.api.enums.sms.SmsChannelEnum}
     * @param templateParams 扩展模板参数，用于自定义短信验证码环境，一般不用传入
     * @return reqId，请求号，使用验证码时需要携带
     */
    private String doSendSms(String phoneNumber,Long userId,String userType, String useChannelCode, String templateCode, LinkedHashMap<String,String> templateParams){

        useChannelCode = useChannel(useChannelCode);
        //获取发送客户端
        SmsChannelClient client = smsChannelFactory.getClient(useChannelCode);
        //生成请求ID
        String smsReqId = IdUtils.snowflakeIdStr();
        DevSmsTemplateDTO smsTemplate = devSmsTemplateService.getAndValidDtoByChannelAndCode(client.getChannelCode(),templateCode);

        //验证模板参数是否符合预期
        devSmsTemplateService.validParams(smsTemplate,templateParams);

        //发送短信，返回发送结果，一般时成功的消息
        String resultMessage = "";
        Integer resultCode= CommonResult.CODE_SUCCESS;
        try {
            resultMessage = client.sendSms(phoneNumber, smsTemplate.getApiTemplateId(), templateParams);
        }catch (Exception ex){
            resultMessage = ex.getMessage();
            resultCode=CommonResult.CODE_ERROR;
        }
        createLog(DevSmsTypeEnum.NOTIFY, client.getChannelCode(),templateCode,smsTemplate.getContent(),
                phoneNumber,JsonUtils.toJsonString(templateParams),userType,userId,smsReqId,resultCode.toString(),resultMessage);

        return smsReqId;
    }

    /**
     * 发送短信验证码
     * @param phoneNumber 手机号码
     * @param userId 发送给的用户
     * @param userType 发送给的用户类型
     * @param templateCode 模板编码
     * @param templateParams 扩展模板参数，用于自定义短信验证码环境，一般不用传入
     * @return reqId，请求号，使用验证码时需要携带
     */
    public String sendSmsToUser(String phoneNumber,Long userId,String userType, String templateCode, LinkedHashMap<String,String> templateParams){
        return doSendSms(phoneNumber,userId,userType,null,templateCode,templateParams);
    }

    /**
     * 发送短信验证码
     * @param phoneNumber 手机号码
     * @param userId 发送给的用户
     * @param userType 发送给的用户类型
     * @param useChannelCode 使用通道 {@link nuocao.plugin.dev.api.enums.sms.SmsChannelEnum}
     * @param templateCode 模板编码
     * @param templateParams 扩展模板参数，用于自定义短信验证码环境，一般不用传入
     * @return reqId，请求号，使用验证码时需要携带
     */
    public String sendSmsToUser(String phoneNumber,Long userId,String userType,String useChannelCode, String templateCode, LinkedHashMap<String,String> templateParams){
        return doSendSms(phoneNumber,userId,userType,useChannelCode,templateCode,templateParams);
    }



    /**
     * 发送短信验证码
     * @param phoneNumber 手机号码
     * @param templateCode 模板编码
     * @param templateParams 扩展模板参数，用于自定义短信验证码环境，一般不用传入
     * @return reqId，请求号，使用验证码时需要携带
     */
    public String sendSms(String phoneNumber,  String useChannelCode,String templateCode, LinkedHashMap<String,String> templateParams){
        return doSendSms(phoneNumber,null,null,useChannelCode,templateCode,templateParams);
    }
    /**
     * 发送短信验证码
     * @param phoneNumber 手机号码
     * @param templateCode 模板编码
     * @param templateParams 扩展模板参数，用于自定义短信验证码环境，一般不用传入
     * @return reqId，请求号，使用验证码时需要携带
     */
    public String sendSms(String phoneNumber,  String templateCode, LinkedHashMap<String,String> templateParams){
        return doSendSms(phoneNumber,null,null,null,templateCode,templateParams);
    }


    /**
     * 发送短信，根据语言选择模板通道
     * @return 返回通道编码，每个通道只能对应一种语言，配合sendSms方法可有选择性的使用通道发送多语言短信
     */
    public String getLangChannel(String lang){
        //根据语言获取发送短信的通道,key=短信通道，value为语言
        Map<String,Object> channelLangMap = devConfigApi.getValueToMap(SmsConstants.SYS_SMS_CHANNEL_LANG_KEY);
        //暂时只取第一个，可以自行扩展分布算法
        String returnChannel = "";
        if(channelLangMap!=null && !channelLangMap.isEmpty()){ //存在则查找
            for (String channelCode:
                    channelLangMap.keySet() ) {
                if(channelLangMap.get(channelCode).toString().equals(lang)){
                    returnChannel=channelCode;
                    break;
                }
            }
        }
        //没找到则返回第一个
        if(StringUtils.isBlank(returnChannel)){
            return devConfigApi.getValueByKey(SmsConstants.SYS_DEFAULT_SMS_CHANNEL_CODE_KEY);
        }
        return returnChannel;
    }



    private String getCodeCacheKey(String group,String phoneNumber,String smsReqId){
        return StrFormatUtils.doFormat(SMS_CODE_CACHE_PREFIX,group,phoneNumber,smsReqId);
    }


    private void createLog(DevSmsTypeEnum smsType,String channelCode,String templateCode,
                                  String content,String phoneNumber,String paramsMap,
                                  String userType,Long userId,String reqId,
                                  String resultCode,
                                  String resultMessage
                                  ){
        //创建短信发送记录
        DevSmsLogAddParam logAddParam = new DevSmsLogAddParam();
        logAddParam.setPhone(phoneNumber);
        logAddParam.setUserId(userId);
        logAddParam.setTemplateParam(JsonUtils.toJsonString(paramsMap));
        logAddParam.setChannelCode(channelCode);
        logAddParam.setTemplateCode(templateCode);
        logAddParam.setContent(content);
        logAddParam.setSendTime(LocalDateTimeUtils.now());
        logAddParam.setType(smsType.getValue());
        logAddParam.setUserType(userType);
        logAddParam.setApiRequestId(reqId);
        logAddParam.setApiResultCode(resultCode);
        logAddParam.setApiResultMsg(resultMessage);
        devSmsLogService.add(logAddParam);
    }


    /**
     * 使用通道编码，如果未传入转为默认，如默认也没有则报错
     * @param useChannelCode
     * @return
     */
    private String useChannel(String useChannelCode){
        if(StringUtils.isBlank(useChannelCode)){
            useChannelCode=devConfigApi.getValueByKey(SmsConstants.SYS_DEFAULT_SMS_CHANNEL_CODE_KEY);
        }
        if(StringUtils.isBlank(useChannelCode)){
            throw new CommonException("There is no channelCode available for sendSmScode. Please pass in the useChannelCode parameter or set the default SMS channel");
        }
        return useChannelCode;
    }

}
