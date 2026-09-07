package com.nuocao.common.config;

import lombok.Getter;
import lombok.Setter;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.time.Duration;

@Inject("${nuocao.redis}")
@Configuration
@Setter
@Getter
public class NuoCaoRedisConfig {

    /**
     * 数据库索引
     */
    private Integer db;

    /**
     * 服务器地址
     */
    private String server;

    /**
     * 密码
     */
    private String password;

    /**
     * 账号，redis 6.x 新加
     */
    private String user;

    /**
     * 超时时间
     */
    private Duration timeout = Duration.ofSeconds(10); //默认10秒

    /**
     * 最大连接数
     */
    private Integer maxTotal;
    /**
     * 最小空闲书（redission用）
     */
    private Integer minIdle;
    /**
     * 最大空闲数
     */
    private Integer maxIdle;

    /**
     * 最大建立连接等待时间
     */
    private Integer maxWaitMillis;

    /**
     * 最大重试，集群模式有效
     */
    private Integer maxAttempts;


    /**
     * 连接超时
     */
    private Integer connectionTimeout;
}
