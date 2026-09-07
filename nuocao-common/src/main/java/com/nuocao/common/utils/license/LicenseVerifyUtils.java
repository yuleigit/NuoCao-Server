package com.nuocao.common.utils.license;

import cn.hutool.core.io.FileUtil;
import com.alibaba.excel.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 授权证书验证类
 */
public class LicenseVerifyUtils {

    //项目加密公钥，从授权系统中获得
    private final static String PUBLIC_KEY="";

    //该项目使用SM4加密授权文件license.lic。
    private final static String LICENSE_ENCRYPT_KEY="KNFKZf2fPYap7ipZ";

    private final static Long lastVerifyTime = 0L;
    private final static String lastVerifyMsg = "license error";

    public static void verify(){

        if(StringUtils.isBlank(PUBLIC_KEY)){
            return;
        }
        //读取授权文件
//        if(Solon.cfg().isDebugMode()){
//            //  return;
//        }

        //每个1小时验证一次
        String verifyMsg="";
        if(System.currentTimeMillis()- lastVerifyTime <60*60*1000L){
            verifyMsg=lastVerifyMsg;
        }else{
            //得到设备ID
            String deviceId = DeviceIdUtils.getDeviceId();
            //读取jar所在目录同目录的license.inc文件
            String license = readLicenseFile();
            //验证文件
            verifyMsg = LicenseSM4Utils.verifyLicense(PUBLIC_KEY,LICENSE_ENCRYPT_KEY, license,deviceId);
        }

        if(verifyMsg!=null && !verifyMsg.equals("")){
            //空字符串表示成功
            throw new RuntimeException(verifyMsg);
        }
    }

    /**
     * 读取 jar 所在目录的 license.inc 文件内容
     * @return 文件内容字符串（UTF-8 编码）
     */
    public static String readLicenseFile() {
        try {
            String jarPath = "/qslinks/";

            File dir;

            if (FileUtil.exist(jarPath)) {
                // 如果是 jar 包执行，取其所在目录
                dir = new File(jarPath);
            } else {
                // 如果是 IDE 调试运行（classes 目录）
                dir = new File(System.getProperty("user.dir"));
            }

            File licenseFile = new File(dir, "qslinks.lic");

            if (!licenseFile.exists()) {
                throw new FileNotFoundException("not find: " + licenseFile.getAbsolutePath());
            }

            // 读取文件内容
            return new String(Files.readAllBytes(Paths.get(licenseFile.toURI())), StandardCharsets.UTF_8);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
