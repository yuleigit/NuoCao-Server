package com.nuocao.dev.core.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码工具：生成可直接用于 img src 的 Base64 PNG
 **/
public class QrCodeUtils {

    private static final int DEFAULT_SIZE = 240;

    private static final String PNG_DATA_URL_PREFIX = "data:image/png;base64,";

    private QrCodeUtils() {
    }

    /**
     * 生成二维码图片（PNG，Base64 data url）
     *
     * @param content 二维码内容
     */
    public static String toPngDataUrl(String content) {
        return toPngDataUrl(content, DEFAULT_SIZE);
    }

    /**
     * 生成二维码图片（PNG，Base64 data url）
     *
     * @param content 二维码内容
     * @param size    图片边长（像素）
     */
    public static String toPngDataUrl(String content, int size) {
        int imageSize = size > 0 ? size : DEFAULT_SIZE;
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, imageSize, imageSize, hints);

            int width = matrix.getWidth();
            int height = matrix.getHeight();
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                ImageIO.write(image, "png", output);
                return PNG_DATA_URL_PREFIX + Base64.getEncoder().encodeToString(output.toByteArray());
            }
        } catch (Exception e) {
            throw new IllegalStateException("生成二维码失败", e);
        }
    }
}
