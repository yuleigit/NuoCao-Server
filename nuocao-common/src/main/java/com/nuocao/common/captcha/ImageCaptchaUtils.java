package com.nuocao.common.captcha;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import com.nuocao.common.cache.CacheUtils;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.pojo.KeyValue;
import com.nuocao.common.utils.NumberUtils;
import com.nuocao.common.utils.StrFormatUtils;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.common.utils.uuid.IdUtils;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

/**
 * 图片验证码工具
 */
@Component
public class ImageCaptchaUtils {

    private static final String IMAGE_CAPTCHA_CACHE_KEY="image_captcha:{}:{}"; //格式1：分组：类型2：请求ID

    @Inject
    private CacheUtils cacheUtils;

    /**
     * 创建图片验证码
     * @param group 分组，主要是业务标识
     * @param expire 过期时间，不能为0，默认为5分钟。
     * @return 返回keyValue ,key=请求编号，value=图片的base64
     */
    public KeyValue<String,String> get(String group,Integer expire){

        // 获取验证码失效时间（单位：秒）,没传入则使用默认值
        if(NumberUtils.isLeZero(expire)){
            expire=5*60;
        }
        // 生成验证码，随机4位字符
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(100, 38, 4, 10);
        // 获取验证码的值
        String validCode = circleCaptcha.getCode();
        // 获取验证码的base64
        String validCodeBase64 = circleCaptcha.getImageBase64Data();
        // 生成请求号,使用uuid
        String reqId = IdUtils.fastSimpleUUID();

        //创建缓存key
        String key = genCacheKey(group,reqId);
        // 将请求号作为key，验证码的值作为value放到redis，用于校验
        cacheUtils.setObject(key,validCode,expire);
        return new KeyValue<>(reqId,validCodeBase64);
    }

    public KeyValue<String,String> get(Integer expire){
        return get("",expire);
    }

    public KeyValue<String,String> get(){
        return get("",0);
    }
    public KeyValue<String,String> get(String group){
        return get(group,0);
    }

    /**
     * 验证验证码是否有效
     * @param group 分组，于get时输入的必须一致
     * @param reqId 请求编号，有get生成，请求时携带
     * @param inputCode 用户输入的code
     * @return 是否正确
     */
    private boolean check(String group,String reqId,String inputCode){
        String  validCode= cacheUtils.get(genCacheKey(group,reqId),String.class);
        if(StringUtils.isBlank(validCode)) {
            return false;
        }
        return validCode.equalsIgnoreCase(inputCode);
    }


    /**
     * 验证验证码是否有效，无效则报错
     * @param group
     * @param reqId
     * @param inputCode
     * @return
     */
    public void validate(String group, String reqId, String inputCode){
        if(!check(group,reqId,inputCode)){
            throw new CommonExceptionI18n("common.captcha.image.error");
        }
    }

    public void validate(String reqId, String inputCode){
        validate("",reqId,inputCode);
    }



    private String genCacheKey(String group,String reqId){
        if(StringUtils.isBlank(group)) {
            group="default"; //默认分组
        }
        return StrFormatUtils.doFormat(IMAGE_CAPTCHA_CACHE_KEY,group,reqId);
    }

}
