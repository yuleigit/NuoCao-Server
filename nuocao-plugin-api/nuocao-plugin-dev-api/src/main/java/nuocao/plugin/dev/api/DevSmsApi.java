package nuocao.plugin.dev.api;

import cn.hutool.json.JSONObject;
import nuocao.plugin.dev.api.enums.sms.SmsChannelEnum;
import org.noear.solon.annotation.Component;

import java.util.LinkedHashMap;

/**
 * 短信API
 **/
@Component
public interface DevSmsApi {

    /**
     * 动态发送短信（使用系统配置的默认短信引擎）
     *
     * @param phoneNumbers 手机号
     * @param templateCodeOrId 模板id或编码
     * @param paramMap 发送参数
     */
    void sendSms(String phoneNumbers, String templateCodeOrId, LinkedHashMap<String,String> paramMap);

    /* =========使用指定引擎发送短信========= */

    /**
     * 发送短信，使用指定通道
     * @param channel 发送通道，为null则使用默认通道
     * @param phoneNumbers 手机号码，支持对多个手机号码发送短信，手机号码之间以半角逗号（,）分隔。
     *                     上限为1000个手机号码。批量调用相对于单条调用及时性稍有延迟。
     * @param templateCode 短信服务控制台配置且审核通过的模板编码
     * @param templateParam 短信模板变量对应的实际值，JSON格式。支持传入多个参数，示例：{"name":"张三","number":"15038****76"}
     **/
    void sendSms(SmsChannelEnum channel,String phoneNumbers,  String templateCode, String templateParam);

    /**
     * 发送短信验证码
     * @param channel
     * @param phoneNumber
     * @param group 分組，为空则使用default
     * @param templateCode 使用的模板编码
     * @return 验证码请求id
     */
    String sendSmsCode(SmsChannelEnum channel,String group,String phoneNumber,String templateCode);

    /**
     * 发送短信验证码
     * @param phoneNumber
     * @param group 分組，为空则使用default
     * @return 验证码请求id
     */
    String sendSmsCode(String group,String phoneNumber,String templateCode);

    /**
     * 验证短信验证码是否正确
     * @param group 分组，为空则为default
     * @param reqId sendSmsCode返回的请求ID
     * @param inputCode 用户输入的验证码
     */
    void validateSmsCode(String group,String phone, String reqId, String inputCode);

}
