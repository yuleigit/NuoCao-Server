package com.nuocao.common.utils.license;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * License 工具类（SM4 + RSA 版）
 *
 * 功能：
 * 1. 根据 privateKey 生成 license（包含设备ID和过期时间）
 * 2. 使用 SM4-CBC 对内容加密，RSA 对签名
 * 3. 离线验证签名 + 解密 + 到期时间
 */
public class LicenseSM4Utils {

    private static final String SIGN_ALGORITHM = "SHA256withRSA";
    private static final String SM4_ALGORITHM = "SM4/CBC/PKCS5Padding";
    private static final String PROVIDER = "BC";

    private static final String DECRYPT_ERROR="decrypt code error";
    private static final String SIGN_ERROR="licence sign error！";
    private static final String LICENSE_FRM_ERROR="License format error";

    private static final String EXRPIE_ERROR="License expire！";
    private static final String DEVICE_ID_ERROR="device id error";


    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 生成 License（SM4加密 + RSA签名）
     *
     * @param privateKeyStr Base64 私钥
     * @param sm4Key        16字节密钥（建议随机生成后内置或服务器分配）
     * @param deviceId      设备ID
     * @param expireTime    到期时间
     * @return license文本（加密后）
     */
    public static String generateLicense(String privateKeyStr, String sm4Key, String deviceId, Date expireTime) throws Exception {
        PrivateKey privateKey = loadPrivateKey(privateKeyStr);

        // License 基础数据
        Map<String, String> data = new LinkedHashMap<>();
        data.put("deviceId", deviceId);
        data.put("expireTime", new SimpleDateFormat("yyyy-MM-dd 23:59:59").format(expireTime));

        String payload = toText(data);

        // 使用私钥签名
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(payload.getBytes(StandardCharsets.UTF_8));
        String sign = Base64.getEncoder().encodeToString(signature.sign());

        // 拼接完整内容（未加密）
        String fullText = payload + "\nsignature=" + sign + "\n";

        // 使用SM4加密
        byte[] encrypted = sm4Encrypt(fullText.getBytes(StandardCharsets.UTF_8), sm4Key);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * 验证 License
     *
     * @param publicKeyStr Base64 公钥
     * @param sm4Key       SM4密钥
     * @param licenseBase64 License Base64 内容
     * @return "" 表示成功，否则为错误信息
     */
    public static String verifyLicense(String publicKeyStr, String sm4Key, String licenseBase64, String currentDeviceId) {
        String licenseText = "";
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(licenseBase64);
            licenseText = new String(sm4Decrypt(encryptedBytes, sm4Key), StandardCharsets.UTF_8);
        }catch (Exception ex){
           return (DECRYPT_ERROR+ex.getMessage());
        }

        try {
            PublicKey publicKey = loadPublicKey(publicKeyStr);

            // 解密 license




            Map<String, String> map = parseText(licenseText);
            String deviceId = map.get("deviceId");
            String expireTimeStr = map.get("expireTime");
            String signatureBase64 = map.get("signature");

            if (deviceId == null || expireTimeStr == null || signatureBase64 == null) {
                throw new IllegalArgumentException(LICENSE_FRM_ERROR);
            }

            // 验证签名
            String payload = "deviceId=" + deviceId + "\n" + "expireTime=" + expireTimeStr;
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(payload.getBytes(StandardCharsets.UTF_8));

            byte[] signBytes = Base64.getDecoder().decode(signatureBase64);
            if (!signature.verify(signBytes)) {
                throw new Exception(SIGN_ERROR);
            }
            // 4. 校验设备ID
            if (!deviceId.equals(currentDeviceId)) {
                throw new Exception(DEVICE_ID_ERROR);
            }

            // 验证时间
            Date expireDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expireTimeStr);
            if (new Date().after(expireDate)) {
                throw new Exception(EXRPIE_ERROR);
            }

            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    // ======================= SM4 实现 =======================

    private static byte[] sm4Encrypt(byte[] data, String key) throws Exception {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] iv = Arrays.copyOfRange(keyBytes, 0, 16); // 简单使用 key 前16位作为 IV

        Cipher cipher = Cipher.getInstance(SM4_ALGORITHM, PROVIDER);
        SecretKeySpec sm4Key = new SecretKeySpec(keyBytes, "SM4");
        cipher.init(Cipher.ENCRYPT_MODE, sm4Key, new IvParameterSpec(iv));
        return cipher.doFinal(data);
    }

    private static byte[] sm4Decrypt(byte[] encryptedData, String key) throws Exception {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] iv = Arrays.copyOfRange(keyBytes, 0, 16);

        Cipher cipher = Cipher.getInstance(SM4_ALGORITHM, PROVIDER);
        SecretKeySpec sm4Key = new SecretKeySpec(keyBytes, "SM4");
        cipher.init(Cipher.DECRYPT_MODE, sm4Key, new IvParameterSpec(iv));
        return cipher.doFinal(encryptedData);
    }

    // ======================= Key 处理 ========================

    private static PrivateKey loadPrivateKey(String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    private static PublicKey loadPublicKey(String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    // ======================= 工具函数 ========================

    private static Map<String, String> parseText(String text) {
        Map<String, String> map = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] kv = line.split("=", 2);
                if (kv.length == 2) map.put(kv[0].trim(), kv[1].trim());
            }
        } catch (IOException ignored) {
        }
        return map;
    }

    private static String toText(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        map.forEach((k, v) -> sb.append(k).append("=").append(v).append("\n"));
        return sb.toString().trim();
    }
}