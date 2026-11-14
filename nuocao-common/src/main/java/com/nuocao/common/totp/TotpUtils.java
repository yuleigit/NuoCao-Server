package com.nuocao.common.totp;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Locale;

/**
 * RFC 6238 (TOTP) / RFC 4226 (HOTP) 的最小实现。
 * <p>纯 JDK，无第三方依赖。服务端不需要任何"刷新线程"：
 * 验证码是 {@code HOTP(secret, 当前时间步)} 的纯函数计算结果，算完就丢。
 * <p>约定：
 * <ul>
 *   <li>{@code secretBase32} 是用户绑定时生成的 Base32 密钥；库里应是密文，先解密再传进来。</li>
 *   <li>时间相关的参数全部是"时间步"（time step），不是 epoch 秒。</li>
 *   <li>失败一律返回 {@code -1}（fail-closed），不会把异常抛给业务层。</li>
 * </ul>
 */
public final class TotpUtils {

    /** HMAC 算法。Google Authenticator 兼容性最好的是 SHA1。 */
    private static final String HMAC = "HmacSHA1";
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final int[] POW10 = {1, 10, 100, 1_000, 10_000, 100_000,
            1_000_000, 10_000_000, 100_000_000};

    /** 默认生成的 secret 字节数：20 字节 = 160 bit = 32 个 Base32 字符。 */
    public static final int DEFAULT_SECRET_BYTES = 20;
    /** 允许的最小 secret 字节数（RFC 4226 要求至少 128 bit）。历史短 secret 需要调小此值。 */
    public static final int MIN_SECRET_BYTES = 16;
    /** 允许的最大时间步偏移（1 即 ±30 秒）。 */
    public static final int MAX_WINDOW = 2;

    private static final SecureRandom RANDOM = new SecureRandom();

    private TotpUtils() {
    }

    // ======================= 校验 =======================

    /**
     * 校验动态码是否正确。
     *
     * @param secretBase32  用户绑定时生成的 Base32 密钥（库里应是密文，先解密再传进来）
     * @param code          用户提交的码，允许带空格/横线/全角数字
     * @param digits        位数，6 或 8
     * @param periodSeconds 时间步长，一般 30
     * @param window        容忍的时间步偏移，0 表示只认当前这一步，1 表示 ±30 秒
     */
    public static boolean validate(String secretBase32, String code,
                                   int digits, int periodSeconds, int window) {
        return verify(secretBase32, code, 0L, digits, periodSeconds, window) >= 0;
    }

    /**
     * 校验并返回<b>命中的时间步</b>，失败返回 -1。
     * <p>防重放必须用这个：同一个时间步只能成功一次，只返回 boolean 做不到。
     * <p>真实时间步约为 5.8e7，不可能等于 -1，所以 -1 是安全的失败哨兵。
     * <p>参数非法、secret 损坏或长度不足时同样返回 -1（fail-closed）；
     * 配置错误请在启动时用 {@link #validateConfig(int, int, int)} 提前暴露。
     *
     * @param currentStep 当前时间步；传 0 或负数表示按系统时钟自己算
     */
    public static long verify(String secretBase32, String code, long currentStep,
                              int digits, int periodSeconds, int window) {
        if (secretBase32 == null || code == null
                || digits < 6 || digits > 8
                || periodSeconds <= 0
                || window < 0 || window > MAX_WINDOW) {
            return -1L;
        }

        String input = normalizeCode(code);
        if (input.length() != digits) {
            return -1L;
        }

        byte[] key;
        try {
            key = base32Decode(secretBase32);
        } catch (IllegalArgumentException e) {
            // 数据/配置损坏：按失败处理，由业务层统一告警，不要把异常抛到接口层
            return -1L;
        }
        if (key.length < MIN_SECRET_BYTES) {
            return -1L;
        }

        long step = currentStep > 0
                ? currentStep
                : System.currentTimeMillis() / 1000L / periodSeconds;

        // 逐个候选时间步比对。用 isEqual 而不是 equals 是刻意的：
        // 字符串比较会在第一个不同的字符处提前返回，理论上可被用来做时序侧信道。
        for (int offset = -window; offset <= window; offset++) {
            long candidateStep = step + offset;
            if (candidateStep < 0) {
                continue;
            }
            String candidate = hotp(key, candidateStep, digits);
            if (MessageDigest.isEqual(
                    candidate.getBytes(StandardCharsets.US_ASCII),
                    input.getBytes(StandardCharsets.US_ASCII))) {
                return candidateStep;
            }
        }
        return -1L;
    }

    /**
     * 算出指定时间步的验证码。参数是 <b>时间步</b>，不是 epoch 秒。
     *
     * @throws IllegalArgumentException secret 非法或长度不足、digits 不在 6~8
     */
    public static String generate(String secretBase32, long timeStep, int digits) {
        if (secretBase32 == null) {
            throw new IllegalArgumentException("secretBase32 不能为 null");
        }
        byte[] key = base32Decode(secretBase32);
        if (key.length < MIN_SECRET_BYTES) {
            throw new IllegalArgumentException("secret 长度不足: " + key.length + " 字节");
        }
        return hotp(key, timeStep, digits);
    }

    /** 当前时间步（便于业务层使用与校验一致的时钟语义，也方便写测试）。 */
    public static long currentTimeStep(int periodSeconds) {
        if (periodSeconds <= 0) {
            throw new IllegalArgumentException("periodSeconds 必须大于 0");
        }
        return System.currentTimeMillis() / 1000L / periodSeconds;
    }

    // ======================= 绑定相关 =======================

    /** 生成新密钥（Base32，无填充），默认 20 字节。 */
    public static String generateSecret() {
        return generateSecret(DEFAULT_SECRET_BYTES);
    }

    /**
     * 生成新密钥（Base32，无填充）。不足 {@link #DEFAULT_SECRET_BYTES} 时按 20 字节生成。
     */
    public static String generateSecret(int bytes) {
        byte[] buf = new byte[Math.max(bytes, DEFAULT_SECRET_BYTES)];
        RANDOM.nextBytes(buf);
        return base32Encode(buf);
    }

    /** 判断 secret 是否是格式正确、长度足够的 Base32（绑定/导入时校验用）。 */
    public static boolean isWellFormedSecret(String secretBase32) {
        if (secretBase32 == null) {
            return false;
        }
        try {
            byte[] key = base32Decode(secretBase32);
            return key.length >= MIN_SECRET_BYTES;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 构造 otpauth URI（前端可用 qrcode.js 等直接渲染成二维码）。
     * <pre>
     * otpauth://totp/{issuer}:{account}?secret=...&amp;issuer=...&amp;algorithm=SHA1&amp;digits=6&amp;period=30
     * </pre>
     *
     * @throws IllegalArgumentException issuer/account 为空、secret 非法、参数非法
     */
    public static String buildOtpAuthUri(String issuer, String account, String secretBase32,
                                         int digits, int periodSeconds) {
        if (issuer == null || issuer.trim().isEmpty()) {
            throw new IllegalArgumentException("issuer 不能为空");
        }
        if (account == null || account.trim().isEmpty()) {
            throw new IllegalArgumentException("account 不能为空");
        }
        if (!isWellFormedSecret(secretBase32)) {
            throw new IllegalArgumentException("secret 不是合法的 Base32 密钥");
        }
        validateConfig(digits, periodSeconds, 0);

        String canonicalSecret = secretBase32.trim()
                .replace(" ", "").replace("-", "").replace("=", "")
                .toUpperCase(Locale.ROOT);
        String label = urlEncode(issuer) + ":" + urlEncode(account);
        return "otpauth://totp/" + label
                + "?secret=" + canonicalSecret
                + "&issuer=" + urlEncode(issuer)
                + "&algorithm=SHA1"
                + "&digits=" + digits
                + "&period=" + periodSeconds;
    }

    /** 启动时校验一次配置，避免运行时静默失败。 */
    public static void validateConfig(int digits, int periodSeconds, int window) {
        if (digits < 6 || digits > 8) {
            throw new IllegalArgumentException("digits 只支持 6~8，当前: " + digits);
        }
        if (periodSeconds <= 0) {
            throw new IllegalArgumentException("periodSeconds 必须大于 0，当前: " + periodSeconds);
        }
        if (window < 0 || window > MAX_WINDOW) {
            throw new IllegalArgumentException("window 只支持 0~" + MAX_WINDOW + "，当前: " + window);
        }
    }

    /**
     * 清洗用户输入：只保留数字，"123 456"、"123-456"、全角"１２３４５６"都能通过。
     */
    public static String normalizeCode(String code) {
        if (code == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(code.length());
        for (int i = 0; i < code.length(); i++) {
            char digit = toAsciiDigit(code.charAt(i));
            if (digit != 0) {
                sb.append(digit);
            }
        }
        return sb.toString();
    }

    // ======================= 内部实现 =======================

    /** ASCII 数字原样返回；全角数字转半角；其它字符返回 0（作为忽略标记）。 */
    private static char toAsciiDigit(char c) {
        if (c >= '0' && c <= '9') {
            return c;
        }
        if (c >= '０' && c <= '９') {
            return (char) ('0' + (c - '０'));
        }
        return 0;
    }

    private static String hotp(byte[] key, long counter, int digits) {
        if (digits < 6 || digits > 8) {
            throw new IllegalArgumentException("digits 只支持 6~8");
        }
        byte[] data = new byte[8];
        long value = counter;
        for (int i = 7; i >= 0; i--) {
            data[i] = (byte) (value & 0xFF);
            value >>>= 8;
        }
        try {
            Mac mac = Mac.getInstance(HMAC);
            mac.init(new SecretKeySpec(key, HMAC));
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0x0F;          // 动态截断
            int binary = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);

            // 必须补前导零：约 10% 的码以 0 开头，直接 toString 会变成 5 位，用户永远输不对
            return String.format(Locale.ROOT, "%0" + digits + "d", binary % POW10[digits]);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HMAC-SHA1 不可用", e);
        }
    }

    static byte[] base32Decode(String secretBase32) {
        String value = secretBase32.trim()
                .replace(" ", "").replace("-", "").replace("=", "")
                .toUpperCase(Locale.ROOT);
        if (value.isEmpty()) {
            throw new IllegalArgumentException("密钥为空");
        }
        int outLen = value.length() * 5 / 8;
        if (outLen == 0) {
            throw new IllegalArgumentException("密钥长度不合法");
        }
        byte[] out = new byte[outLen];
        int buffer = 0;
        int bits = 0;
        int index = 0;
        for (int i = 0; i < value.length(); i++) {
            int val = ALPHABET.indexOf(value.charAt(i));
            if (val < 0) {
                // 不要把 secret 字符写进异常信息，避免泄进日志
                throw new IllegalArgumentException("非法 Base32 字符（位置 " + i + "）");
            }
            buffer = (buffer << 5) | val;
            bits += 5;
            if (bits >= 8) {
                out[index++] = (byte) ((buffer >> (bits - 8)) & 0xFF);
                bits -= 8;
            }
        }
        return out;
    }

    static String base32Encode(byte[] data) {
        StringBuilder sb = new StringBuilder((data.length * 8 + 4) / 5);
        int buffer = 0;
        int bits = 0;
        for (byte b : data) {
            buffer = (buffer << 8) | (b & 0xFF);
            bits += 8;
            while (bits >= 5) {
                sb.append(ALPHABET.charAt((buffer >> (bits - 5)) & 0x1F));
                bits -= 5;
            }
        }
        if (bits > 0) {
            sb.append(ALPHABET.charAt((buffer << (5 - bits)) & 0x1F));
        }
        return sb.toString();
    }

    private static String urlEncode(String value) {
        try {
            // 用 Java 8 兼容的 String 重载；URLEncoder 把空格编成 '+'，这里转回 %20
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name()).replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 不可用", e);
        }
    }
}