package com.nuocao.common.config;

import lombok.Getter;
import lombok.Setter;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * 缓存基础配置

 */

@Inject("${nuocao.config}")
@Configuration
public class NuocaoConfig {

    /** 前端地址 */
    private static String frontUrl;

    /** 后端地址 */
    private static String backendUrl;

    /** 程序版本 */
    private static String version;

    /** 名称 */
    private static String name;

    public void setFrontUrl(String frontUrl){
        NuocaoConfig.frontUrl=frontUrl;
    }
    public static String getFrontUrl(){
        return NuocaoConfig.frontUrl;
    }

    public void setBackendUrl(String backendUrl){
        NuocaoConfig.backendUrl=backendUrl;
    }
    public static String getBackendUrl(){
        return NuocaoConfig.backendUrl;
    }

    public void setVersion(String version){
        NuocaoConfig.version=version;
    }
    public static String getVersion(){
        return NuocaoConfig.version;
    }

    public void setName(String name){
        NuocaoConfig.name=name;
    }
    public static String getName(){
        return NuocaoConfig.name;
    }
}
