package com.nuocao.common.utils.imageload;

import java.io.IOException;
import java.io.InputStream;

/**
 * 图片加载结果封装
 * 实现AutoCloseable，支持try-with-resources自动资源管理
 */
public class ImageLoadResult implements AutoCloseable {
    private final InputStream inputStream;
    private final long size;
    private final String format;
    private final ImageSource source;
    private final String contentType;
    private final boolean success;
    private final String errorMessage;

    private ImageLoadResult(InputStream inputStream, long size, String format, ImageSource source,
                            String contentType, boolean success, String errorMessage) {
        this.inputStream = inputStream;
        this.size = size;
        this.format = format;
        this.source = source;
        this.contentType = contentType;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static ImageLoadResult success(InputStream inputStream, long size, String format, ImageSource source) {
        return new ImageLoadResult(inputStream, size, format, source, null, true, null);
    }

    public static ImageLoadResult failed(String errorMessage) {
        return new ImageLoadResult(null, 0, null, null, null, false, errorMessage);
    }

    public ImageLoadResult withContentType(String contentType) {
        return new ImageLoadResult(inputStream, size, format, source, contentType, success, errorMessage);
    }

    // Getter方法
    public InputStream getInputStream() { return inputStream; }
    public long getSize() { return size; }
    public String getFormat() { return format; }
    public ImageSource getSource() { return source; }
    public String getContentType() { return contentType; }
    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }

    /**
     * 安全关闭输入流
     */
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

