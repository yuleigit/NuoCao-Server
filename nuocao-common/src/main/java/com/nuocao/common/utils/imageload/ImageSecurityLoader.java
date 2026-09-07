package com.nuocao.common.utils.imageload;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * 图片安全加载工具类（Hutool版本）
 * 自动识别Base64和URL，包含严格的安全验证
 */
@Slf4j
@Component
public class ImageSecurityLoader implements AutoCloseable {

    // 正则表达式模式
    private static final Pattern URL_PATTERN = Pattern.compile("^https?://.*", Pattern.CASE_INSENSITIVE);
    private static final Pattern BASE64_IMAGE_PATTERN = Pattern.compile("^data:image/(\\w+);base64,", Pattern.CASE_INSENSITIVE);
    private static final Pattern BASE64_SIMPLE_PATTERN = Pattern.compile("^[A-Za-z0-9+/]*={0,2}$");

    // 配置参数
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB最大图片大小
    private static final int CONNECT_TIMEOUT = 10000; // 10秒连接超时
    private static final int READ_TIMEOUT = 30000;    // 30秒读取超时

    // 支持的图片类型
    private static final String[] SUPPORTED_IMAGE_TYPES = {"jpeg", "jpg", "png", "gif", "bmp", "webp"};

    /**
     * 安全加载图片 - 主入口方法
     *
     * @param input 输入内容（Base64或URL）
     * @return 图片加载结果，使用后必须关闭
     */
    public ImageLoadResult loadImage(String input) {
        if (StrUtil.isBlank(input)) {
            return ImageLoadResult.failed("输入内容为空");
        }

        try {
            if (isBase64Image(input)) {
                log.debug("检测到Base64图片输入");
                return loadFromBase64(input);
            } else if (isUrl(input)) {
                log.debug("检测到URL图片输入: {}", getSafeUrlForLog(input));
                return loadFromUrl(input);
            } else {
                return ImageLoadResult.failed("不支持的输入格式，仅支持Base64和URL");
            }
        } catch (Exception e) {
            log.error("图片加载失败: {}", getSafeInputForLog(input), e);
            return ImageLoadResult.failed("加载失败: " + e.getMessage());
        }
    }

    /**
     * 安全处理图片 - 自动资源管理
     *
     * @param input 输入内容
     * @param processor 图片处理器
     */
    public void processImage(String input, ImageProcessor processor) {
        try (ImageLoadResult result = loadImage(input)) {
            if (result.isSuccess()) {
                processor.process(result);
                log.info("图片处理成功，格式: {}, 大小: {} bytes",
                        result.getFormat(), result.getSize());
            } else {
                log.warn("图片加载失败: {}", result.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("图片处理异常", e);
        }
    }

    // ========== Base64加载 ==========

    private ImageLoadResult loadFromBase64(String base64) {
        // 1. 格式验证
        if (!isValidBase64Image(base64)) {
            return ImageLoadResult.failed("Base64格式无效或不是支持的图片类型");
        }

        // 2. 解码
        byte[] imageData = decodeBase64Image(base64);
        if (imageData == null || imageData.length == 0) {
            return ImageLoadResult.failed("Base64解码失败");
        }

        // 3. 大小验证
        if (imageData.length > MAX_IMAGE_SIZE) {
            return ImageLoadResult.failed("图片大小超过限制: " + imageData.length + " bytes");
        }

        // 4. 内容验证
        if (!isValidImageContent(imageData)) {
            return ImageLoadResult.failed("图片内容格式无效");
        }

        // 5. 创建结果
        InputStream stream = new ByteArrayInputStream(imageData);
        String format = extractImageFormatFromBase64(base64);

        log.debug("Base64图片加载成功，大小: {} bytes, 格式: {}", imageData.length, format);
        return ImageLoadResult.success(stream, imageData.length, format, ImageSource.BASE64);
    }

    // ========== URL加载 ==========

    private ImageLoadResult loadFromUrl(String url) {
        // 第一步：HEAD请求验证
        try (HttpResponse headResponse = createHeadRequest(url).execute()) {
            if (!headResponse.isOk()) {
                return ImageLoadResult.failed("URL响应异常: " + headResponse.getStatus());
            }

            String contentType = headResponse.header("Content-Type");
            String contentLengthStr = headResponse.header("Content-Length");
            long contentLength = StrUtil.isNotBlank(contentLengthStr) ? Long.parseLong(contentLengthStr) : 0;

            // 内容类型验证
            if (!isValidImageContentType(contentType)) {
                return ImageLoadResult.failed("不支持的Content-Type: " + contentType);
            }

            // 大小验证
            if (contentLength > MAX_IMAGE_SIZE) {
                return ImageLoadResult.failed("图片大小超过限制: " + contentLength + " bytes");
            }

            // 第二步：下载并验证内容
            return downloadAndValidateImage(url, contentType);

        } catch (Exception e) {
            return ImageLoadResult.failed("URL验证失败: " + e.getMessage());
        }
    }

    private ImageLoadResult downloadAndValidateImage(String url, String contentType) {
        try (HttpResponse response = createGetRequest(url).execute()) {
            if (!response.isOk()) {
                return ImageLoadResult.failed("下载失败: " + response.getStatus());
            }

            byte[] imageData = response.bodyBytes();
            if (imageData == null || imageData.length == 0) {
                return ImageLoadResult.failed("响应体为空");
            }

            // 内容验证
            if (!isValidImageContent(imageData)) {
                return ImageLoadResult.failed("图片内容无效");
            }

            InputStream stream = new ByteArrayInputStream(imageData);
            String format = extractImageFormatFromContentType(contentType);

            log.debug("URL图片加载成功，大小: {} bytes, 格式: {}", imageData.length, format);
            return ImageLoadResult.success(stream, imageData.length, format, ImageSource.URL)
                    .withContentType(contentType);

        } catch (Exception e) {
            return ImageLoadResult.failed("图片下载失败: " + e.getMessage());
        }
    }

    /**
     * 创建HEAD请求
     */
    private HttpRequest createHeadRequest(String url) {
        return HttpRequest.head(url)
                .setConnectionTimeout(CONNECT_TIMEOUT)
                .setReadTimeout(READ_TIMEOUT)
                .header("User-Agent", getUserAgent())
                .header("Accept", "image/*");
    }

    /**
     * 创建GET请求
     */
    private HttpRequest createGetRequest(String url) {
        return HttpRequest.get(url)
                .setConnectionTimeout(CONNECT_TIMEOUT)
                .setReadTimeout(READ_TIMEOUT)
                .header("User-Agent", getUserAgent())
                .header("Accept", "image/*");
    }

    private String getUserAgent() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    }

    // ========== 安全验证方法 ==========

    private boolean isValidBase64Image(String base64) {
        if (!isBase64Image(base64)) {
            return false;
        }

        // 检查是否是支持的图片类型
        String format = extractImageFormatFromBase64(base64);
        return isSupportedImageFormat(format);
    }

    private boolean isValidImageContent(byte[] imageData) {
        if (imageData == null || imageData.length < 8) {
            return false;
        }
        return isImageMagicNumber(imageData);
    }

    private boolean isValidImageContentType(String contentType) {
        if (StrUtil.isBlank(contentType)) return false;

        String lowerType = contentType.toLowerCase();
        for (String supportedType : SUPPORTED_IMAGE_TYPES) {
            if (lowerType.contains(supportedType)) {
                return true;
            }
        }
        return false;
    }

    // ========== 类型检测和解析 ==========

    private boolean isBase64Image(String input) {
        if (StrUtil.isBlank(input)) return false;
        return BASE64_IMAGE_PATTERN.matcher(input).find() ||
                (input.length() % 4 == 0 && BASE64_SIMPLE_PATTERN.matcher(input).matches());
    }

    private boolean isUrl(String input) {
        return StrUtil.isNotBlank(input) && URL_PATTERN.matcher(input).matches();
    }

    private boolean isSupportedImageFormat(String format) {
        if (StrUtil.isBlank(format)) return false;

        String lowerFormat = format.toLowerCase();
        for (String supportedType : SUPPORTED_IMAGE_TYPES) {
            if (supportedType.equals(lowerFormat)) {
                return true;
            }
        }
        return false;
    }

    private boolean isImageMagicNumber(byte[] header) {
        // JPEG: FF D8 FF
        if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8 && header[2] == (byte) 0xFF) {
            return true;
        }
        // PNG: 89 50 4E 47
        if (header[0] == (byte) 0x89 && header[1] == (byte) 0x50 && header[2] == (byte) 0x4E &&
                header[3] == (byte) 0x47) {
            return true;
        }
        // GIF: GIF8
        if (header[0] == 'G' && header[1] == 'I' && header[2] == 'F' && header[3] == '8') {
            return true;
        }
        // BMP: BM
        if (header[0] == 'B' && header[1] == 'M') {
            return true;
        }
        // WebP: RIFFxxxxWEBP
        if (header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F' &&
                header.length > 11 && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P') {
            return true;
        }

        return false;
    }

    private byte[] decodeBase64Image(String base64) {
        try {
            String base64Data = base64;
            if (base64.startsWith("data:image/")) {
                String[] parts = base64.split(",");
                if (parts.length > 1) {
                    base64Data = parts[1];
                }
            }

            return Base64.getDecoder().decode(base64Data);
        } catch (IllegalArgumentException e) {
            log.warn("Base64解码失败", e);
            return null;
        }
    }

    private String extractImageFormatFromBase64(String base64) {
        if (base64.startsWith("data:image/")) {
            String[] parts = base64.split("[/;]");
            if (parts.length > 2) {
                return parts[2];
            }
        }
        return "jpg"; // 默认格式
    }

    private String extractImageFormatFromContentType(String contentType) {
        if (StrUtil.isBlank(contentType)) return "jpg";

        if (contentType.contains("jpeg")) return "jpg";
        if (contentType.contains("png")) return "png";
        if (contentType.contains("gif")) return "gif";
        if (contentType.contains("bmp")) return "bmp";
        if (contentType.contains("webp")) return "webp";

        return "jpg";
    }

    // ========== 工具方法 ==========

    private String getSafeUrlForLog(String url) {
        if (StrUtil.isBlank(url)) return "null";
        if (url.length() > 100) {
            return url.substring(0, 100) + "...";
        }
        return url;
    }

    private String getSafeInputForLog(String input) {
        if (StrUtil.isBlank(input)) return "null";
        if (input.length() > 50) {
            return input.substring(0, 50) + "...";
        }
        return input;
    }

    @Override
    public void close() {
        // Hutool 的 HttpUtil 不需要显式关闭资源
        // 这里留空，为了保持 AutoCloseable 接口的一致性
    }
}