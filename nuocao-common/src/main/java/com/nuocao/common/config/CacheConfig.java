package com.nuocao.common.config;

import lombok.Getter;
import lombok.Setter;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * 缓存基础配置

 */
@Getter
@Setter
@Inject("${nuocao.cache}")
@Configuration
public class CacheConfig {

    /** 是否启用key的md5加密 */
    private Boolean md5Key;

    /** 默认缓存时长（秒） */
    private Integer expireSeconds=30*60;

    /**
     * key的前缀
     */
    private String keyHeader="cache";

}
