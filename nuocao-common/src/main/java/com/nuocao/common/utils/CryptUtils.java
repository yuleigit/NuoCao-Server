package com.nuocao.common.utils;

import com.antherd.smcrypto.sm2.Keypair;
import com.antherd.smcrypto.sm2.Sm2;
import com.antherd.smcrypto.sm3.Sm3;
import com.antherd.smcrypto.sm4.Sm4;
import com.antherd.smcrypto.sm4.Sm4Options;
import com.nuocao.common.config.NuocaoCryptConfig;
import com.nuocao.common.pojo.KeyValue;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

/**
 * 国密算法工具类，统一收口 SM2 / SM3 / SM4 的加解密能力。
 * <p>
 * 密钥、IV、工作模式等参数均从 {@link NuocaoCryptConfig} 读取，支持运行时调整；
 * 上层业务只需调用本类暴露的静态方法，不直接依赖 sm-crypto 的具体实现。
 */
@Slf4j
public final class CryptUtils {

    /** SM4 默认工作模式 */
    private static final String DEFAULT_SM4_MODE = "cbc";

    /** SM4 分组长度（字节），CBC/CTR 模式下 IV 与之保持一致 */
    private static final int SM4_BLOCK_BYTES = 16;

    /** GCM 模式推荐的 IV 长度（字节） */
    private static final int GCM_IV_BYTES = 12;

    private CryptUtils() {
    }

    // ==================== SM2 非对称 ====================

    /**
     * 使用 SM2 公钥对明文加密。
     * 适用于前后端分离场景：后端下发公钥，前端加密敏感字段后再提交。
     *
     * @param raw 待加密明文
     * @return 十六进制密文
     */
    public static String sm2Encrypt(String raw) {
        return Sm2.doEncrypt(raw, NuocaoCryptConfig.getSm2PublicKey());
    }

    /**
     * 使用 SM2 私钥对密文解密。
     * 若后续接入硬件加密机，可在此处捕获异常并降级返回原值。
     *
     * @param cipher 待解密密文
     * @return 解密后的明文
     */
    public static String sm2Decrypt(String cipher) {
        return Sm2.doDecrypt(cipher, NuocaoCryptConfig.getSm2PrivateKey());
    }

    /**
     * 使用 SM2 私钥对数据签名。
     *
     * @param raw 待签名数据
     * @return 签名结果
     */
    public static String signature(String raw) {
        return Sm2.doSignature(raw, NuocaoCryptConfig.getSm2PrivateKey());
    }

    /**
     * 使用 SM2 公钥校验签名。
     *
     * @param raw       原始数据
     * @param signature 待校验的签名值
     * @return 校验通过返回 {@code true}
     */
    public static boolean verifySignature(String raw, String signature) {
        return Sm2.doVerifySignature(raw, signature, NuocaoCryptConfig.getSm2PublicKey());
    }

    // ==================== SM4 对称 ====================

    /**
     * 使用 SM4（CBC 模式）对明文加密。
     *
     * @param raw 待加密明文
     * @return 密文
     */
    public static String sm4CbcEncrypt(String raw) {
        return Sm4.encrypt(raw, NuocaoCryptConfig.getSm4Key(),
                sm4Options(NuocaoCryptConfig.getSm4Iv()));
    }

    /**
     * 使用 SM4（CBC 模式）对密文解密。
     * 解密失败时按原值透传，避免历史脏数据阻断整条业务链路。
     *
     * @param cipher 待解密密文
     * @return 明文；解密失败时返回原密文
     */
    public static String sm4CbcDecrypt(String cipher) {
        String plain = Sm4.decrypt(cipher, NuocaoCryptConfig.getSm4Key(),
                sm4Options(NuocaoCryptConfig.getSm4Iv()));
        if (plain == null || plain.isEmpty()) {
            log.warn("SM4 解密未命中，按原值透传：{}", cipher);
            return cipher;
        }
        return plain;
    }

    // ==================== SM3 摘要 ====================

    /**
     * 使用 SM3 杂凑算法计算哈希值，用于数据完整性保护。
     *
     * @param raw 待摘要数据
     * @return 十六进制哈希值
     */
    public static String hashValue(String raw) {
        return Sm3.sm3(raw);
    }

    // ==================== 密钥生成 ====================

    /**
     * 按指定模式生成 SM4 的密钥与 IV。
     * <p>
     * Key 恒为 128bit；IV 长度由工作模式决定：CBC/CTR 取 16 字节，GCM 取 12 字节，ECB 无需 IV。
     *
     * @param mode 工作模式（CBC / CTR / GCM / ECB），大小写不敏感
     * @return 键为 key、值为 iv
     */
    public static KeyValue<String, String> generateSm4keyIv(String mode) {
        String key = randomHex(SM4_BLOCK_BYTES);
        return new KeyValue<>(key, ivHex(mode));
    }

    /**
     * 按默认模式（CBC）生成 SM4 的密钥与 IV，并打印提示信息。
     *
     * @return 键为 key、值为 iv
     */
    public static KeyValue<String, String> generateSm4keyIv() {
        KeyValue<String, String> keyIv = generateSm4keyIv(DEFAULT_SM4_MODE);
        log.info("SM4 密钥对已生成，请同步更新配置 nuocao.crypt：key={}, iv={}",
                keyIv.getKey(), keyIv.getValue());
        return keyIv;
    }

    /**
     * 生成 SM2 的公私钥对。
     *
     * @return 键为公钥、值为私钥；生成失败时返回 {@code null}
     */
    public static KeyValue<String, String> generateSmKeyPairHex() {
        Keypair keyPair = Sm2.generateKeyPairHex();
        if (keyPair == null) {
            log.warn("SM2 密钥对生成失败");
            return null;
        }
        log.info("SM2 密钥对已生成，请同步更新配置 nuocao.crypt 与前端文件");
        return new KeyValue<>(keyPair.getPublicKey(), keyPair.getPrivateKey());
    }

    // ==================== 内部实现 ====================

    /** 构造 SM4 参数，统一走配置读取 */
    private static Sm4Options sm4Options(String iv) {
        Sm4Options options = new Sm4Options();
        options.setMode(DEFAULT_SM4_MODE);
        options.setIv(iv);
        return options;
    }

    /** 根据模式推导 IV 的十六进制表示 */
    private static String ivHex(String mode) {
        return switch (mode.toUpperCase()) {
            case "CBC", "CTR" -> randomHex(SM4_BLOCK_BYTES);
            case "GCM" -> randomHex(GCM_IV_BYTES);
            case "ECB" -> "";
            default -> throw new IllegalArgumentException("不支持的 SM4 模式：" + mode);
        };
    }

    /** 生成指定字节长度的强随机十六进制字符串 */
    private static String randomHex(int byteLength) {
        byte[] seed = new byte[byteLength];
        new SecureRandom().nextBytes(seed);
        StringBuilder hex = new StringBuilder(byteLength * 2);
        for (byte b : seed) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
