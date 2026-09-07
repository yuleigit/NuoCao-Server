package com.nuocao.common.utils;

import com.antherd.smcrypto.sm2.Keypair;
import com.antherd.smcrypto.sm2.Sm2;
import com.antherd.smcrypto.sm3.Sm3;
import com.antherd.smcrypto.sm4.Sm4;
import com.antherd.smcrypto.sm4.Sm4Options;
import com.nuocao.common.config.NuocaoConfig;
import com.nuocao.common.config.NuocaoCryptConfig;
import com.nuocao.common.pojo.KeyValue;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;


@Slf4j
public class CommonCryptogramUtils {

    private static final String SM4_MODE="cbc";

    /**
     * 加密方法（Sm2 的专门针对前后端分离，非对称秘钥对的方式，暴露出去的公钥，对传输过程中的密码加个密）
     */
    public static String doSm2Encrypt(String str) {
        return Sm2.doEncrypt(str, NuocaoCryptConfig.getSm2PublicKey());
    }

    /**
     * 解密方法
     * 如果采用加密机的方法，用try catch 捕捉异常，返回原文值即可
     */
    public static String doSm2Decrypt(String str) {
        // 解密
        return Sm2.doDecrypt(str, NuocaoCryptConfig.getSm2PrivateKey());
    }

    /**
     * 加密方法
     */
    public static String doSm4CbcEncrypt(String str) {
        // SM4 加密  cbc模式
        Sm4Options sm4Options4 = new Sm4Options();
        sm4Options4.setMode("cbc");
        sm4Options4.setIv(NuocaoCryptConfig.getSm4Iv());
        return Sm4.encrypt(str, NuocaoCryptConfig.getSm4Key(), sm4Options4);
    }

    /**
     * 解密方法
     * 如果采用加密机的方法，用try catch 捕捉异常，返回原文值即可
     */
    public static String doSm4CbcDecrypt(String str) {
        // 解密，cbc 模式，输出 utf8 字符串
        Sm4Options sm4Options8 = new Sm4Options();
        sm4Options8.setMode(SM4_MODE);
        sm4Options8.setIv(NuocaoCryptConfig.getSm4Iv());
        String docString =  Sm4.decrypt(str, NuocaoCryptConfig.getSm4Key(), sm4Options8);
        if ("".equals(docString)) {
            log.warn(">>> 字段解密失败，返回原文值：{}", str);
            return str;
        } else {
            return docString;
        }
    }

    /**
     * 纯签名
     */
    public static String doSignature(String str) {
        return Sm2.doSignature(str, NuocaoCryptConfig.getSm2PrivateKey());
    }

    /**
     * 验证签名结果
     */
    public static boolean doVerifySignature(String originalStr, String str) {
        return Sm2.doVerifySignature(originalStr, str, NuocaoCryptConfig.getSm2PublicKey());
    }

    /**
     * 通过杂凑算法取得hash值，用于做数据完整性保护
     */
    public static String doHashValue(String str) {
        return Sm3.sm3(str);
    }


    /**
     * 创建sm4秘钥与iv
     * @param mode
     * @return key=key,value=iv
     */
    public static KeyValue<String,String> generateSm4keyIv(String mode){
        //统一大写
        String modeUpper = mode.toUpperCase();

        // 1. 无论什么模式，SM4 的 Key 永远是 128 bit (16字节 -> 32位十六进制)
        String key = generateRandomHex(16);
        String iv = "";
        // 2. 根据模式确定 IV 长度
        switch (modeUpper) {
            case "CBC":
            case "CTR":
                // CBC/CTR 模式要求 IV 必须与块大小一致：128 bit (16字节)
                iv= generateRandomHex(16);
                break;
            case "GCM":
                // GCM 模式官方推荐长度为 96 bit (12字节 -> 24位十六进制)
                // 这样效率最高且最安全
                iv= generateRandomHex(12);
                break;
            case "ECB":
                // ECB 模式不需要 IV
                iv="";
                break;
            default:
                throw new IllegalArgumentException("不支持的模式: " + mode);
        }

        return new KeyValue<>(key,iv);
    }

    /**
     * 生成强随机十六进制字符串
     * @param byteLength 字节长度
     */
    private static String generateRandomHex(int byteLength) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[byteLength];
        random.nextBytes(bytes);

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    /**
     *生成SM4的key和iv，使用默认模式
     * @return key=key,value=iv
     */
    public static KeyValue<String,String> generateSm4keyIv(){
        KeyValue<String,String> keyiv = generateSm4keyIv(SM4_MODE);
        log.info("======生成sm4KeyIv成功，请同步更新项目配置nuocao.crypt");
        log.info("sm4Key：{}",keyiv.getKey());
        log.info("sm4Iv：{}",keyiv.getValue());
        return keyiv;
    }

    /**
     * 创建一个公钥、私钥键值对，
     * @return key=公钥，value=私钥
     */
    public static KeyValue<String,String> generateSmKeyPairHex(){
        Keypair key = Sm2.generateKeyPairHex();
        if(key==null){
            log.warn("创建SM2keypair失败");
            return null;
        }
        log.info("======生成sm2Keypair成功，请同步更新项目配置nuocao.crypt和前端文件");
        log.info("公钥publicKey，可公开：{}",key.getPublicKey());
        log.info("私钥privateKey，仅用于后端服务：{}",key.getPrivateKey());
        return new KeyValue<>(key.getPublicKey(),key.getPrivateKey());
    }

}
