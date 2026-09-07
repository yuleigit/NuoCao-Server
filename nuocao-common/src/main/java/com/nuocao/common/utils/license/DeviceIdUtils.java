package com.nuocao.common.utils.license;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * DeviceIdUtils
 *
 * 获取设备唯一标识 device-id
 * - 优先使用硬盘序列号 / 分区 UUID
 * - 兼容 Windows / Linux / macOS
 * - 兼容 JDK 1.8 ~ 17+
 */
public class DeviceIdUtils {

    /**
     * 获取设备唯一 ID（32位Hex字符串）
     */
    public static String getDeviceId() {
        try {
            String raw = getDiskSerial() + System.getProperty("os.name") + System.getProperty("user.name");
            return sha256Hex(raw).substring(0, 32);
        } catch (Exception e) {
            e.printStackTrace();
            return "UNKNOWN_DEVICE";
        }
    }

    /**
     * 获取硬盘序列号/分区UUID
     */
    private static String getDiskSerial() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                return getWindowsDiskSerial();
            } else if (os.contains("linux")) {
                return getLinuxDiskUuid();
            } else if (os.contains("mac")) {
                return getMacDiskUuid();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "UNKNOWN_DISK";
    }

    // Windows 获取 C 盘卷序列号
    private static String getWindowsDiskSerial() throws Exception {
        Process process = Runtime.getRuntime().exec(new String[]{"cmd", "/c", "vol C:"});
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(process.getInputStream(), Charset.forName("GBK")))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("卷的序列号") || line.contains("Volume Serial Number")) {
                    String[] parts = line.split("是|is");
                    if (parts.length > 1) {
                        return parts[1].trim().replace("-", "");
                    }
                }
            }
        }
        return "WIN_UNKNOWN_DISK";
    }

    // Linux 获取根分区 UUID
    private static String getLinuxDiskUuid() throws Exception {
        String[] cmd = {"/bin/bash", "-c", "blkid -o value -s UUID $(df / | tail -1 | awk '{print $1}')"};
        Process process = Runtime.getRuntime().exec(cmd);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String uuid = br.readLine();
            if (uuid != null && !uuid.isEmpty()) return uuid.trim();
        }
        return "LINUX_UNKNOWN_DISK";
    }

    // macOS 获取根分区 UUID
    private static String getMacDiskUuid() throws Exception {
        String[] cmd = {"/bin/bash", "-c", "diskutil info / | grep 'Volume UUID'"};
        Process process = Runtime.getRuntime().exec(cmd);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line = br.readLine();
            if (line != null && line.contains(":")) {
                return line.split(":")[1].trim();
            }
        }
        return "MAC_UNKNOWN_DISK";
    }

    // 生成 SHA-256 HEX
    private static String sha256Hex(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

}
