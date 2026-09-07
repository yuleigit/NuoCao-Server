package com.nuocao.auth.modular.login.enums;

import lombok.Getter;

/**
 * 登录异常提示语枚举
 **/
@Getter
public enum AuthExceptionEnum {

    /** 验证码不能为空 */
    VALID_CODE_EMPTY("验证码不能为空","auth.validCode.empty"),

    /** 验证码请求号不能为空 */
    VALID_CODE_REQ_NO_EMPTY("验证码请求号不能为空","auth.validCode.reqId.empty"),

    /** 验证码过期 */
    VALID_CODE_EXPIRED("验证码过期","auth.validCode.expire"),

    /** 验证码错误 */
    VALID_CODE_ERROR("验证码错误","auth.validCode.error"),

    /** 账号错误 */
    ACCOUNT_ERROR("账号错误","auth.account.error"),

    /** 账号不存在租户用户 */
    ACCOUNT_NOT_EXISTS_USER("账号未加入任何组织，您可以创建新组织或加入","auth.account.notExistsUser"),

    /** 账号已停用 */
    ACCOUNT_DISABLED("账号已停用","auth.account.disable"),

    /** 密码错误 */
    PWD_ERROR("密码错误","auth.login.pwd.error"),

    /** 手机号格式错误 */
    PHONE_FORMAT_ERROR("手机号格式错误","auth.phone.format.error"),

    /** 邮箱格式错误 */
    EMAIL_FORMAT_ERROR("邮箱格式错误","auth.email.format.error"),

    /** 客户端类型不能为空 */
    CLIENT_TYPE_EMPTY("客户端类型不能为空","auth.device.empty"),

    /** 客户端类型错误 */
    CLIENT_TYPE_ERROR("客户端类型错误","auth.device.error"),

    /** 密码解密失败，请检查前端公钥 */
    PWD_DECRYPT_ERROR("密码解密失败，请检查前端公钥","auth.pwd.decrypt.error");

    private final String value;
    private final String i18nKey;

    AuthExceptionEnum(String value,String i18nKey) {
        this.value = value;
        this.i18nKey=i18nKey;
    }
}
