package com.nuocao.common.captcha;

import lombok.Getter;

/**
 * 验证码分组类型，一般为需要用验证码的业务
 */
@Getter
public enum CaptchaGroupEnum {

    AUTH("auth"),
    ACCOUNT("account");

    String value;

    CaptchaGroupEnum(String value){
        this.value=value;
    }
}
