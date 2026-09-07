package com.nuocao.common.utils;


import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

/**
 * 常用工具类
 */
public class NumberUtils {
    /**
     * 是否大于0
     * @param value
     */
    public static boolean isGtZero(Long value)
    {
        return value!=null && value.compareTo(0L)>0;
    }
    public static boolean isLeZero(Long value)
    {
        return value==null || value.compareTo(0L)<=0;
    }
    public static boolean isLtZero(Long value)
    {
        return value==null || value.compareTo(0L)<0;
    }
    public static boolean isLeZero(Integer value)
    {
        return value==null || value.compareTo(0)<=0;
    }
    public static boolean isGtZero(Integer value)
    {
        return value!=null && value.compareTo(0)>0;
    }
    public static boolean isLtZero(Integer value)
    {
        return value==null || value.compareTo(0)<0;
    }
    /**
     * 是否大于0
     * @param value
     */
    public static boolean isGtZero(BigDecimal value)
    {
        return value!=null && value.compareTo(BigDecimal.ZERO)>0;
    }
    public static boolean isLeZero(BigDecimal value)
    {
        return value==null || value.compareTo(BigDecimal.ZERO)<=0;
    }


    /**
     * 是否为0或null
     * @param value
     */
    public static boolean isZero(BigDecimal value)
    {
        return value==null || value.equals(BigDecimal.ZERO);
    }
    public static boolean isZero(Integer value)
    {
        return value==null || value.equals(0);
    }

    public static boolean isZero(Long value)
    {
        return value==null || value.equals(0L);
    }


}
