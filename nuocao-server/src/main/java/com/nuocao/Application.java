package com.nuocao;


import com.nuocao.common.config.NuocaoConfig;
import com.nuocao.common.utils.StrFormatUtils;
import com.nuocao.common.utils.license.LicenseVerifyUtils;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.scheduling.annotation.EnableAsync;

/**
 * 启动类

 */
@Slf4j
@Controller
@EnableAsync
public class Application {

    /**
     * 主启动函数
     */
    public static void main(String[] args) {
        //验证授权
        Solon.start(Application.class, args, app->{
            LicenseVerifyUtils.verify();
        });
        log.info(">>> {}", Application.class.getSimpleName().toUpperCase() + " STARTING SUCCESS");
       // CommonCryptogramUtils.generateSm4keyIv();
       // CommonCryptogramUtils.generateSmKeyPairHex();
       // String ens=CommonCryptogramUtils.doSm2Encrypt("1234");
       // String des = CommonCryptogramUtils.doSm2Decrypt(ens);
    }

    /**
     * 首页
     **/
    @Get@Post
    @Mapping("/")
    public String index() {
        return StrFormatUtils.doFormat("Welcome to {},version:{}", NuocaoConfig.getName(),NuocaoConfig.getVersion());
    }
}
