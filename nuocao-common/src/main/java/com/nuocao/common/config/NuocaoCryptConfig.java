package com.nuocao.common.config;


import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * 加密配置类，配置参数内容请与前端一直
 */
@Inject("${nuocao.crypt}")
@Configuration
public class NuocaoCryptConfig {

    /**
     * sm2公钥
     */

    private static String sm2PublicKey;

    /**
     * sm2私钥
     */
    private static String sm2PrivateKey;



    /**
     * sm2公钥
     */
    private static String sm4Key;

    /**
     * sm2私钥
     */
    private static String sm4Iv;


    public void setSm2PublicKey(String sm2PublicKey){
        NuocaoCryptConfig.sm2PublicKey=sm2PublicKey;
    }

    public static String getSm2PublicKey(){
        return NuocaoCryptConfig.sm2PublicKey;
    }

    public void setSm2PrivateKey(String sm2PrivateKey){
        NuocaoCryptConfig.sm2PrivateKey=sm2PrivateKey;
    }

    public static String getSm2PrivateKey(){
        return NuocaoCryptConfig.sm2PrivateKey;
    }

    public void setSm4Iv(String sm4Iv) {
        NuocaoCryptConfig.sm4Iv = sm4Iv;
    }

    public static String getSm4Iv() {
        return NuocaoCryptConfig.sm4Iv;
    }

    public void setSm4Key(String sm4Key) {
        NuocaoCryptConfig.sm4Key = sm4Key;
    }

    public static String getSm4Key() {
        return NuocaoCryptConfig.sm4Key;
    }
}
