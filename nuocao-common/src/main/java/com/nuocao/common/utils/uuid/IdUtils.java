package com.nuocao.common.utils.uuid;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * ID生成器工具类
 *
 * @author qslinks
 */
public class IdUtils {
    /**
     * 获取随机UUID
     *
     * @return 随机UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线
     *
     * @return 简化的UUID，去掉了横线
     */
    public static String simpleUUID() {
        return UUID.randomUUID().toString(true);
    }

    /**
     * 获取随机UUID，使用性能更好的ThreadLocalRandom生成UUID
     *
     * @return 随机UUID
     */
    public static String fastUUID() {
        return UUID.fastUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成UUID
     *
     * @return 简化的UUID，去掉了横线
     */
    public static String fastSimpleUUID() {
        return UUID.fastUUID().toString(true);
    }

    /**
     * 获取雪花算法id
     *
     * @return
     */
    public static Long snowflakeId() {
        return getSnowflake().nextId();
    }

    /**
     * 获取雪花算法id 字符串
     *
     * @return
     */
    public static String snowflakeIdStr() {
        return getSnowflake().nextIdStr();
    }

    /**
     * 获取雪花算法id
     *
     * @return
     */
    public static Snowflake getSnowflake() {
//        long workId = QsLinksIdConfig.getWorkId()!=null?QsLinksIdConfig.getWorkId():1L;
//        long datacenterId = QsLinksIdConfig.getDatacenterId()!=null?QsLinksIdConfig.getDatacenterId():1L;
        long workId=1L;
        long datacenterId=1L;
        return IdUtil.getSnowflake(workId,datacenterId);
    }
}
