package com.nuocao.common.totp;

import com.nuocao.common.config.NuoCaoMfaConfig;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

/**
 * TOTP secret 落库加解密工具。
 * <p>算法：AES-256-GCM；存储格式：{@code v{version}:base64(iv || ciphertext || tag)}。
 * <p>密钥来自 {@code nuocao.mfa.secret-key}，支持多版本：新数据使用 active-version，旧数据按前缀版本解密。
 */
@Component
public class TotpSecretCipher {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String KEY_ALGORITHM = "AES";
    private static final int KEY_BYTES = 32;
    private static final int IV_BYTES = 12;
    private static final int TAG_BITS = 128;
    private static final String VERSION_PREFIX = "v";
    private static final String SEPARATOR = ":";

    private static final SecureRandom RANDOM = new SecureRandom();

    @Inject
    private NuoCaoMfaConfig mfaConfig;

    /**
     * 加密 secret，返回可直接落库的密文串。
     */
    public String encrypt(String plaintext) {
        if (plaintext == null) {
            throw new IllegalArgumentException("secret 不能为 null");
        }
        int version = mfaConfig.getSecretKey().getActiveVersion();
        byte[] key = resolveKey(version);

        byte[] iv = new byte[IV_BYTES];
        RANDOM.nextBytes(iv);

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, KEY_ALGORITHM),
                    new GCMParameterSpec(TAG_BITS, iv));
            byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            byte[] payload = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, payload, 0, iv.length);
            System.arraycopy(cipherText, 0, payload, iv.length, cipherText.length);
            return VERSION_PREFIX + version + SEPARATOR + Base64.getEncoder().encodeToString(payload);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("MFA secret 加密失败", e);
        }
    }

    /**
     * 解密落库的 secret 密文，格式非法或密钥缺失时直接抛异常（fail-closed）。
     */
    public String decrypt(String envelope) {
        if (envelope == null || envelope.isBlank()) {
            throw new IllegalArgumentException("secret 密文为空");
        }
        int splitIndex = envelope.indexOf(SEPARATOR);
        if (splitIndex <= 1 || !envelope.startsWith(VERSION_PREFIX)) {
            throw new IllegalArgumentException("secret 密文格式非法");
        }
        int version;
        try {
            version = Integer.parseInt(envelope.substring(VERSION_PREFIX.length(), splitIndex));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("secret 密文版本号非法");
        }

        byte[] payload;
        try {
            payload = Base64.getDecoder().decode(envelope.substring(splitIndex + 1));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("secret 密文编码非法");
        }
        if (payload.length <= IV_BYTES) {
            throw new IllegalArgumentException("secret 密文长度非法");
        }

        byte[] key = resolveKey(version);
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, KEY_ALGORITHM),
                    new GCMParameterSpec(TAG_BITS, payload, 0, IV_BYTES));
            byte[] plain = cipher.doFinal(payload, IV_BYTES, payload.length - IV_BYTES);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("MFA secret 解密失败", e);
        }
    }

    /**
     * 当前新数据写入使用的密钥版本，落库到 dev_mfa.cipher_version。
     */
    public int getActiveVersion() {
        return mfaConfig.getSecretKey().getActiveVersion();
    }

    private byte[] resolveKey(int version) {
        Map<Integer, String> keys = mfaConfig.getSecretKey().getKeys();
        String base64Key = keys == null ? null : keys.get(version);
        if (base64Key == null || base64Key.isBlank()) {
            throw new IllegalStateException("未配置 MFA secret 密钥版本: " + version);
        }
        byte[] key;
        try {
            key = Base64.getDecoder().decode(base64Key.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("MFA secret 密钥不是合法的 Base64", e);
        }
        if (key.length != KEY_BYTES) {
            throw new IllegalStateException("MFA secret 密钥长度必须为 " + KEY_BYTES + " 字节，当前: " + key.length);
        }
        return key;
    }
}
