package com.nuocao.common.validation;

import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import com.nuocao.common.utils.CryptUtils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

public class MobileValidator implements Validator<Mobile> {

    public static final MobileValidator instance = new MobileValidator();

    @Override
    public Result validateOfValue(Mobile anno, Object value, StringBuilder tmp) {
        if (value != null && value instanceof String == false) {
            return Result.failure();
        }

        String val = (String) value;

        return verify(anno,val);
    }



    @Override
    public Result validateOfContext(Context ctx, Mobile anno, String value, StringBuilder tmp) {



        return verify(anno,value);
    }

    private Result verify(Mobile anno,String value){
        if (StrUtil.isEmpty(value)) {
            return Result.failure();
        }
        String realPhone = value;
        // 如果注解中配置了需要解密，则进行 SM4 解密
        if (anno.encrypted()) {
            try {
                realPhone = CryptUtils.sm2Encrypt(value);
            } catch (Exception e) {
                // 解密失败（如密文格式错误、解密密钥不匹配等），直接视为校验不通过
                return Result.failure();
            }
        }
        // 校验手机
        return PhoneUtil.isMobile(value)?Result.succeed():Result.failure();
    }
}
