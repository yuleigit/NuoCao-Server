package com.nuocao.core.config;

import com.nuocao.common.config.CacheConfig;
import com.nuocao.common.config.NuoCaoRedisConfig;
import com.nuocao.common.constants.Constants;
import com.nuocao.common.utils.NumberUtils;
import com.nuocao.common.utils.StringUtils;
import org.noear.redisx.RedisClient;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.cache.redisson.RedissonCacheService;
import org.noear.solon.data.cache.CacheService;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.solon.RedissonClientOriginalSupplier;

/**
 * @author noear 2022/12/18 created
 */
@Configuration
public class RedisClientConfigure {

    /**
     * 配置solon redisx
     * @param redisClient
     * @return
     */
//    @Bean
//    public RedisClient redisClient(@Inject("${nuocao.redis}") RedisClient redisClient){
//        return redisClient;
//    }
//
//    //构建 Cache Service（给 @Cache 用）
//    @Bean
//    public CacheService cacheService(@Inject RedisClient fileclient,@Inject("${nuocao.cache}") CacheConfig cacheConfig){
//        RedisCacheService cacheService =new RedisCacheService(fileclient, cacheConfig.getExpireSeconds());
//        if(cacheConfig.getMd5Key()){
//            cacheService.enableMd5key(true);
//        }
//        return cacheService;
//    }

    /**
     * 配置Redisson
     * @param //supplier
     * @return
     */
//    @Bean //@Bean("redisson1") 如果bean指定name后，可以参考如下方式使用，支持多个redisson
//    public RedissonClient redissonClient(@Inject("${nuocao.redission}") RedissonClientOriginalSupplier supplier){
//        RedissonClientOriginalSupplier supplier2 = new RedissonClientOriginalSupplier();
//        supplier2.conf
//        return supplier.get();
//    }

    /**
     * 使用自定义redisconfig配置，方便统一写法
     * @param redisConfig yml中配置的nuocao.redis信息
     * @return
     */
    @Bean
    public RedissonClient redissonClient(@Inject("${nuocao.redis}") NuoCaoRedisConfig redisConfig){
        Config redissionConfig = new Config();
        SingleServerConfig single = redissionConfig.useSingleServer()
                .setDatabase(redisConfig.getDb())
                .setTimeout((int)redisConfig.getTimeout().toMillis());

        String address= redisConfig.getServer();
        if (!address.startsWith(Constants.REDIS) && !address.startsWith(Constants.REDISS)) {
            address=Constants.REDIS+address;
        }
       single.setAddress(address);



        if (StringUtils.isNotBlank(redisConfig.getPassword())) {
            single.setPassword(redisConfig.getPassword());
        }

        if(StringUtils.isNotBlank(redisConfig.getUser())){
            single.setUsername(redisConfig.getUser());
        }

        if(NumberUtils.isGtZero(redisConfig.getConnectionTimeout())){
            single.setConnectTimeout(redisConfig.getConnectionTimeout());
        }
        //保持最小连接空闲，避免突发请求时频繁创建连接
        if(NumberUtils.isGtZero(redisConfig.getMinIdle())){
            single.setConnectionMinimumIdleSize(redisConfig.getMinIdle());
        }
        //最大连接池大小,控制 Redis 客户端并发能力
        if(NumberUtils.isGtZero(redisConfig.getMaxTotal())){
            single.setConnectionPoolSize(redisConfig.getMaxTotal());
        }
        //重试次数
        if(NumberUtils.isGtZero(redisConfig.getMaxAttempts())){
            single.setRetryAttempts(redisConfig.getMaxAttempts());
        }

        return Redisson.create(redissionConfig);
    }

    //缓存redisson
    @Bean(value = "cache1",typed = true)
    public CacheService cache1(@Inject RedissonClient client,
            @Inject("${nuocao.cache}") CacheConfig cacheConfig
                ){
        RedissonCacheService service = new  RedissonCacheService(client,cacheConfig.getKeyHeader(),cacheConfig.getExpireSeconds());
        service.enableMd5key(cacheConfig.getMd5Key());
        return service;
    }



}
