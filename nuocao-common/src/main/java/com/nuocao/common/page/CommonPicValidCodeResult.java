package com.nuocao.common.page;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 图片验证码结果类，公用部分
 */
@Getter
@Setter
public class CommonPicValidCodeResult {
    /** 验证码图片，Base64 */
    @ApiModelProperty(value = "验证码图片，Base64")
    private String image;

    /** 验证码请求号 */
    @ApiModelProperty(value = "验证码请求编号")
    private String validCodeReqId;

    public static CommonPicValidCodeResult craete(String reqId,String imageBase64){

        CommonPicValidCodeResult result = new CommonPicValidCodeResult();
        result.setImage(imageBase64);
        result.setValidCodeReqId(reqId);
        return result;
    }
}
