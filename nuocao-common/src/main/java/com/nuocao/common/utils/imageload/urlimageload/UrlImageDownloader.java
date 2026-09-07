package com.nuocao.common.utils.imageload.urlimageload;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * URL图片安全下载器
 * 专门用于安全下载图片，防止下载非图片文件或恶意内容
 */
@Slf4j
@Component
public class UrlImageDownloader implements AutoCloseable {

    // 配置参数
    private static final int CONNECT_TIMEOUT = 10000; // 10秒连接超时
    private static final int READ_TIMEOUT = 30000;    // 30秒读取超时
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB最大图片大小
    private static final int MAX_REDIRECTS = 3; // 最大重定向次数

    // 支持的图片Content-Type
    private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif",
            "image/bmp", "image/webp", "image/svg+xml", "image/tiff"
    ));

    // 支持的图片文件扩展名
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "tiff", "tif"
    ));

    // 用户代理
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    /**
     * 安全下载图片
     *
     * @param imageUrl 图片URL
     * @return 图片下载结果
     */
    public UrlImageDownloadResult downloadImage(String imageUrl) {
        return downloadImage(imageUrl, true);
    }

    /**
     * 安全下载图片
     *
     * @param imageUrl 图片URL
     * @param strictMode 严格模式（进行完整安全验证）
     * @return 图片下载结果
     */
    public UrlImageDownloadResult downloadImage(String imageUrl, boolean strictMode) {
        if (StrUtil.isBlank(imageUrl)) {
            return UrlImageDownloadResult.failed("图片URL不能为空");
        }

        if (!isValidUrl(imageUrl)) {
            return UrlImageDownloadResult.failed("无效的URL格式: " + getSafeUrlForLog(imageUrl));
        }

        try {
            if (strictMode) {
                return downloadWithFullSecurity(imageUrl);
            } else {
                return downloadWithBasicSecurity(imageUrl);
            }
        } catch (Exception e) {
            log.error("图片下载异常: {}", getSafeUrlForLog(imageUrl), e);
            return UrlImageDownloadResult.failed("下载异常: " + e.getMessage());
        }
    }

    /**
     * 完整安全验证下载
     */
    private UrlImageDownloadResult downloadWithFullSecurity(String imageUrl) {
        // 第一步：预验证（HEAD请求）
        PreValidationResult preValidation = preValidateUrl(imageUrl);
        if (!preValidation.isValid()) {
            return UrlImageDownloadResult.failed(preValidation.getErrorMessage());
        }

        // 第二步：下载并验证内容
        return downloadAndValidateContent(imageUrl, preValidation);
    }

    /**
     * 基础安全验证下载
     */
    private UrlImageDownloadResult downloadWithBasicSecurity(String imageUrl) {
        try (HttpResponse response = createGetRequest(imageUrl).execute()) {
            if (!response.isOk()) {
                return UrlImageDownloadResult.failed("HTTP响应异常: " + response.getStatus());
            }

            // 基础Content-Type验证
            String contentType = response.header("Content-Type");
            if (!isValidContentType(contentType)) {
                return UrlImageDownloadResult.failed("不支持的Content-Type: " + contentType);
            }

            byte[] imageData = response.bodyBytes();
            if (imageData == null || imageData.length == 0) {
                return UrlImageDownloadResult.failed("图片数据为空");
            }

            // 基础大小验证
            if (imageData.length > MAX_IMAGE_SIZE) {
                return UrlImageDownloadResult.failed("图片大小超过限制: " + imageData.length + " bytes");
            }

            // 基础魔数验证
            if (!isValidImageMagicNumber(imageData)) {
                return UrlImageDownloadResult.failed("图片格式无效");
            }

            InputStream stream = new ByteArrayInputStream(imageData);
            String format = extractFormatFromContentType(contentType);

            log.info("图片基础安全下载成功: {}, 大小: {} bytes", getSafeUrlForLog(imageUrl), imageData.length);
            return UrlImageDownloadResult.success(stream, imageData.length, format, imageUrl);

        } catch (Exception e) {
            return UrlImageDownloadResult.failed("下载失败: " + e.getMessage());
        }
    }

    /**
     * 预验证URL
     */
    private PreValidationResult preValidateUrl(String imageUrl) {
        try (HttpResponse response = createHeadRequest(imageUrl).execute()) {
            // 检查HTTP状态
            if (!response.isOk()) {
                return PreValidationResult.failed("HTTP响应异常: " + response.getStatus());
            }

            // 检查Content-Type
            String contentType = response.header("Content-Type");
            if (StrUtil.isBlank(contentType)) {
                return PreValidationResult.failed("无法获取Content-Type");
            }

            if (!isValidContentType(contentType)) {
                return PreValidationResult.failed("不支持的Content-Type: " + contentType);
            }

            // 检查文件大小
            String contentLengthHeader = response.header("Content-Length");
            long contentLength = 0;
            if (StrUtil.isNotBlank(contentLengthHeader)) {
                try {
                    contentLength = Long.parseLong(contentLengthHeader);
                    if (contentLength > MAX_IMAGE_SIZE) {
                        return PreValidationResult.failed("文件大小超过限制: " + contentLength + " bytes");
                    }
                    if (contentLength == 0) {
                        return PreValidationResult.failed("文件大小为0");
                    }
                } catch (NumberFormatException e) {
                    log.warn("无法解析Content-Length: {}", contentLengthHeader);
                }
            }

            // 检查URL扩展名（辅助验证）
            if (!hasValidExtension(imageUrl)) {
                log.warn("URL扩展名不常见: {}", imageUrl);
                // 这里只是警告，不阻止下载，因为有些URL可能没有扩展名
            }

            String format = extractFormatFromContentType(contentType);
            return PreValidationResult.success(contentType, contentLength, format);

        } catch (Exception e) {
            return PreValidationResult.failed("预验证失败: " + e.getMessage());
        }
    }

    /**
     * 下载并验证内容
     */
    private UrlImageDownloadResult downloadAndValidateContent(String imageUrl, PreValidationResult preValidation) {
        try (HttpResponse response = createGetRequest(imageUrl).execute()) {
            if (!response.isOk()) {
                return UrlImageDownloadResult.failed("下载响应异常: " + response.getStatus());
            }

            byte[] imageData = response.bodyBytes();
            if (imageData == null || imageData.length == 0) {
                return UrlImageDownloadResult.failed("图片数据为空");
            }

            // 详细安全验证
            SecurityValidationResult securityResult = validateImageSecurity(imageData, preValidation);
            if (!securityResult.isValid()) {
                return UrlImageDownloadResult.failed(securityResult.getErrorMessage());
            }

            // 创建结果
            InputStream stream = new ByteArrayInputStream(imageData);
            String format = securityResult.getDetectedFormat();

            log.info("图片安全下载成功: {}, 大小: {} bytes, 格式: {}",
                    getSafeUrlForLog(imageUrl), imageData.length, format);

            return UrlImageDownloadResult.success(stream, imageData.length, format, imageUrl)
                    .withContentType(preValidation.getContentType())
                    .withSecurityScore(securityResult.getSecurityScore());

        } catch (Exception e) {
            return UrlImageDownloadResult.failed("内容下载失败: " + e.getMessage());
        }
    }

    /**
     * 图片安全验证
     */
    private SecurityValidationResult validateImageSecurity(byte[] imageData, PreValidationResult preValidation) {
        // 1. 大小验证
        if (imageData.length > MAX_IMAGE_SIZE) {
            return SecurityValidationResult.failed("图片大小超过限制: " + imageData.length + " bytes");
        }

        // 2. 魔数验证
        if (!isValidImageMagicNumber(imageData)) {
            return SecurityValidationResult.failed("图片魔数验证失败");
        }

        // 3. 检测实际格式
        String detectedFormat = detectImageFormatFromMagicNumber(imageData);
        if (detectedFormat == null) {
            return SecurityValidationResult.failed("无法识别图片格式");
        }

        // 4. 格式一致性验证
        String declaredFormat = preValidation.getFormat();
        if (!isFormatConsistent(declaredFormat, detectedFormat)) {
            log.warn("声明格式与实际格式不一致: {} vs {}", declaredFormat, detectedFormat);
            // 这里可以根据业务需求决定是否严格验证
        }

        // 5. 计算安全评分
        int securityScore = calculateSecurityScore(imageData, detectedFormat, preValidation);

        return SecurityValidationResult.success(detectedFormat, securityScore);
    }

    // ========== 验证方法 ==========

    private boolean isValidUrl(String url) {
        return StrUtil.isNotBlank(url) &&
                (url.startsWith("http://") || url.startsWith("https://"));
    }

    private boolean isValidContentType(String contentType) {
        if (StrUtil.isBlank(contentType)) return false;

        // 检查是否包含允许的Content-Type
        String lowerType = contentType.toLowerCase();
        for (String allowedType : ALLOWED_CONTENT_TYPES) {
            if (lowerType.contains(allowedType)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasValidExtension(String url) {
        if (StrUtil.isBlank(url)) return false;

        String lowerUrl = url.toLowerCase();
        for (String ext : ALLOWED_EXTENSIONS) {
            if (lowerUrl.endsWith("." + ext) || lowerUrl.contains("." + ext + "?")) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidImageMagicNumber(byte[] data) {
        if (data == null || data.length < 8) return false;

        // JPEG
        if (data[0] == (byte) 0xFF && data[1] == (byte) 0xD8 && data[2] == (byte) 0xFF) {
            return true;
        }
        // PNG
        if (data[0] == (byte) 0x89 && data[1] == (byte) 0x50 && data[2] == (byte) 0x4E &&
                data[3] == (byte) 0x47) {
            return true;
        }
        // GIF
        if (data[0] == 'G' && data[1] == 'I' && data[2] == 'F' && data[3] == '8') {
            return true;
        }
        // BMP
        if (data[0] == 'B' && data[1] == 'M') {
            return true;
        }
        // WebP
        if (data.length >= 12 && data[0] == 'R' && data[1] == 'I' && data[2] == 'F' && data[3] == 'F' &&
                data[8] == 'W' && data[9] == 'E' && data[10] == 'B' && data[11] == 'P') {
            return true;
        }
        // SVG (文本格式，检查开头)
        if (data.length >= 5 && new String(data, 0, 5).toLowerCase().contains("<svg")) {
            return true;
        }

        return false;
    }

    private String detectImageFormatFromMagicNumber(byte[] data) {
        if (data == null || data.length < 8) return null;

        // JPEG
        if (data[0] == (byte) 0xFF && data[1] == (byte) 0xD8 && data[2] == (byte) 0xFF) return "jpg";
        // PNG
        if (data[0] == (byte) 0x89 && data[1] == (byte) 0x50 && data[2] == (byte) 0x4E && data[3] == (byte) 0x47) return "png";
        // GIF
        if (data[0] == 'G' && data[1] == 'I' && data[2] == 'F' && data[3] == '8') return "gif";
        // BMP
        if (data[0] == 'B' && data[1] == 'M') return "bmp";
        // WebP
        if (data.length >= 12 && data[0] == 'R' && data[1] == 'I' && data[2] == 'F' && data[3] == 'F' &&
                data[8] == 'W' && data[9] == 'E' && data[10] == 'B' && data[11] == 'P') return "webp";
        // SVG
        if (data.length >= 100) {
            String header = new String(data, 0, Math.min(100, data.length)).toLowerCase();
            if (header.contains("<svg")) return "svg";
        }

        return null;
    }

    private boolean isFormatConsistent(String declaredFormat, String detectedFormat) {
        if (StrUtil.isBlank(declaredFormat) || StrUtil.isBlank(detectedFormat)) {
            return true; // 如果无法确定，认为一致
        }

        // 处理同义格式
        if ("jpeg".equals(declaredFormat) && "jpg".equals(detectedFormat)) return true;
        if ("jpg".equals(declaredFormat) && "jpeg".equals(detectedFormat)) return true;

        return declaredFormat.equals(detectedFormat);
    }

    private int calculateSecurityScore(byte[] imageData, String format, PreValidationResult preValidation) {
        int score = 100;

        // 大小合理性检查
        if (imageData.length < 100) score -= 20; // 图片太小
        if (imageData.length > 5 * 1024 * 1024) score -= 10; // 图片太大

        // 格式一致性
        if (!isFormatConsistent(preValidation.getFormat(), format)) score -= 30;

        // 扩展名检查
        if (!hasValidExtension(preValidation.getContentType())) score -= 10;

        return Math.max(0, score);
    }

    // ========== 工具方法 ==========

    private HttpRequest createHeadRequest(String url) {
        return HttpRequest.head(url)
                .setConnectionTimeout(CONNECT_TIMEOUT)
                .setReadTimeout(READ_TIMEOUT)
                .header("User-Agent", USER_AGENT)
                .header("Accept", "image/*")
                .setMaxRedirectCount(MAX_REDIRECTS);
    }

    private HttpRequest createGetRequest(String url) {
        return HttpRequest.get(url)
                .setConnectionTimeout(CONNECT_TIMEOUT)
                .setReadTimeout(READ_TIMEOUT)
                .header("User-Agent", USER_AGENT)
                .header("Accept", "image/*")
                .setMaxRedirectCount(MAX_REDIRECTS);
    }

    private String extractFormatFromContentType(String contentType) {
        if (StrUtil.isBlank(contentType)) return "jpg";

        String lowerType = contentType.toLowerCase();
        if (lowerType.contains("jpeg")) return "jpg";
        if (lowerType.contains("png")) return "png";
        if (lowerType.contains("gif")) return "gif";
        if (lowerType.contains("bmp")) return "bmp";
        if (lowerType.contains("webp")) return "webp";
        if (lowerType.contains("svg")) return "svg";
        if (lowerType.contains("tiff")) return "tiff";

        return "jpg";
    }

    private String getSafeUrlForLog(String url) {
        if (StrUtil.isBlank(url)) return "null";
        if (url.length() > 100) return url.substring(0, 100) + "...";
        return url;
    }

    @Override
    public void close() {
        // Hutool 不需要显式关闭
    }

    // ========== 内部结果类 ==========

    /**
     * 预验证结果
     */
    private static class PreValidationResult {
        private final String contentType;
        private final long contentLength;
        private final String format;
        private final boolean valid;
        private final String errorMessage;

        private PreValidationResult(String contentType, long contentLength, String format,
                                    boolean valid, String errorMessage) {
            this.contentType = contentType;
            this.contentLength = contentLength;
            this.format = format;
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public static PreValidationResult success(String contentType, long contentLength, String format) {
            return new PreValidationResult(contentType, contentLength, format, true, null);
        }

        public static PreValidationResult failed(String errorMessage) {
            return new PreValidationResult(null, 0, null, false, errorMessage);
        }

        // Getters
        public String getContentType() { return contentType; }
        public long getContentLength() { return contentLength; }
        public String getFormat() { return format; }
        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
    }

    /**
     * 安全验证结果
     */
    private static class SecurityValidationResult {
        private final String detectedFormat;
        private final int securityScore;
        private final boolean valid;
        private final String errorMessage;

        private SecurityValidationResult(String detectedFormat, int securityScore,
                                         boolean valid, String errorMessage) {
            this.detectedFormat = detectedFormat;
            this.securityScore = securityScore;
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public static SecurityValidationResult success(String detectedFormat, int securityScore) {
            return new SecurityValidationResult(detectedFormat, securityScore, true, null);
        }

        public static SecurityValidationResult failed(String errorMessage) {
            return new SecurityValidationResult(null, 0, false, errorMessage);
        }

        // Getters
        public String getDetectedFormat() { return detectedFormat; }
        public int getSecurityScore() { return securityScore; }
        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
    }
}