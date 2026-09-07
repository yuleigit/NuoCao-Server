package com.nuocao.common.user;

import cn.hutool.core.util.RandomUtil;
import com.nuocao.common.utils.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;


public class CommonAvatarUtils {

    /**
     * 中文正则
     */
    public static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]+");

    /**
     * 预置颜色
     */
    private static final int[] BEAUTIFUL_COLORS = {
            0x7265E6,
            0xFCBF00,
            0x00A2AE,
            0xF56A00,
            0x1890FF,
            0x606D80
    };

    /**
     * 此工具类不可被实例化
     */
    private CommonAvatarUtils() {
    }

    /**
     * 绘制字体头像，如果是英文名，只显示首字母大写，
     * 如果是中文名，只显示最后两个字
     * @return png图像得byte[]
     **/
    public static byte[] generateImg(final String name) {
        final int width = 100;
        final int height = 100;
        String nameWritten = StringUtils.left(name, 1);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) bufferedImage.getGraphics();

        // 抗锯齿，让文字更圆滑
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setBackground(getRandomColor());
        g2.clearRect(0, 0, width, height);
        g2.setPaint(Color.WHITE);

        if (nameWritten.length() == 1) {
            if (isChinese(nameWritten)) {
                g2.setFont(new Font("微软雅黑", Font.PLAIN, 50));
                g2.drawString(nameWritten, 25, 70);
            } else {
                g2.setFont(new Font("微软雅黑", Font.PLAIN, 55));
                g2.drawString(nameWritten.toUpperCase(), 33, 67);
            }
        }

        // 释放资源
        g2.dispose();

        // 将图片转换为字节数组
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("生成头像图片字节流失败", e);
        }
    }

    /**
     * 获得随机颜色
     *
     **/
    private static Color getRandomColor() {
        return new Color(BEAUTIFUL_COLORS[RandomUtil.randomInt(BEAUTIFUL_COLORS.length)]);
    }

    /**
     * 判断字符串是否为中文
     **/
    private static boolean isChinese(String str) {
        return CHINESE_PATTERN.matcher(str).find();
    }
}
