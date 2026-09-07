package com.nuocao.common.utils.imageload.urlimageload;

import java.io.IOException;
import java.io.InputStream;

/**
 * 图片下载结果
 */
public class UrlImageDownloadResult implements AutoCloseable {
    private final InputStream inputStream;
    private final long size;
    private final String format;
    private final String originalUrl;
    private final String contentType;
    private final Integer securityScore;
    private final boolean success;
    private final String errorMessage;

    private UrlImageDownloadResult(InputStream inputStream, long size, String format, String originalUrl,
                                String contentType, Integer securityScore, boolean success, String errorMessage) {
        this.inputStream = inputStream;
        this.size = size;
        this.format = format;
        this.originalUrl = originalUrl;
        this.contentType = contentType;
        this.securityScore = securityScore;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static UrlImageDownloadResult success(InputStream inputStream, long size, String format, String originalUrl) {
        return new UrlImageDownloadResult(inputStream, size, format, originalUrl, null, null, true, null);
    }

    public static UrlImageDownloadResult failed(String errorMessage) {
        return new UrlImageDownloadResult(null, 0, null, null, null, null, false, errorMessage);
    }

    public UrlImageDownloadResult withContentType(String contentType) {
        return new UrlImageDownloadResult(inputStream, size, format, originalUrl, contentType, securityScore, success, errorMessage);
    }

    public UrlImageDownloadResult withSecurityScore(Integer securityScore) {
        return new UrlImageDownloadResult(inputStream, size, format, originalUrl, contentType, securityScore, success, errorMessage);
    }

    // Getters
    public InputStream getInputStream() { return inputStream; }
    public long getSize() { return size; }
    public String getFormat() { return format; }
    public String getOriginalUrl() { return originalUrl; }
    public String getContentType() { return contentType; }
    public Integer getSecurityScore() { return securityScore; }
    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }

    @Override
    public void close() {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                // 忽略关闭异常
            }
        }
    }
}