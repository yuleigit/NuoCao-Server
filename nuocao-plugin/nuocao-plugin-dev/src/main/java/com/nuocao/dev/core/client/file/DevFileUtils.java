package com.nuocao.dev.core.client.file;

import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件相关工具类
 */
public class DevFileUtils {


    /**
     * 根据文件名获取ContentType
     **/
    public static String getFileContentType(String key) {
        // 根据文件名获取contentType
        String contentType = "application/octet-stream";
        if (key.contains(".")) {
            contentType = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(key);
        }
        return contentType;
    }

    /**
     * 获取InputStream大小 - 通过读取整个流
     * 这是最可靠的方法，适用于任何InputStream
     */
    public static long getInputStreamSize(InputStream inputStream) {
        if (inputStream == null) {
            return 0;
        }

        long size = 0;
        byte[] buffer = new byte[8192]; // 8KB缓冲区
        int bytesRead;

        try {
            // 如果流支持标记，先标记以便重置
            boolean canReset = inputStream.markSupported();
            if (canReset) {
                inputStream.mark(Integer.MAX_VALUE);
            }

            // 读取整个流来计算大小
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                size += bytesRead;
            }

            // 重置流到起始位置
            if (canReset) {
                inputStream.reset();
            } else {
                // 如果不支持重置，流已经读完，无法再次使用
                throw new IOException("InputStream does not support resetting and cannot be used again after reading");
            }

            return size;

        } catch (IOException e) {
            throw new RuntimeException("Error occurred when calculating InputStream size", e);
        }
        }
}
