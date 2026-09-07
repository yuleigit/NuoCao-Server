package com.nuocao.common.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 时间工具类
 * 
 * @author ruoyi
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils
{
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";
    /**
     * 秒转换成毫秒
     */
    public static final long SECOND_MILLIS = 1000;

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", 
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前Date型日期
     * 
     * @return Date() 当前日期
     */
    public static Date getNowDate()
    {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     * 
     * @return String
     */
    public static String getDate()
    {
        return dateTimeNow(YYYY_MM_DD);
    }

    /**
     * 获取今天的日期
     * @return
     */
    public static Date GetCurrentData(){return parseDate(getDate());}

    public static final String getTime()
    {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow()
    {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format)
    {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date)
    {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static String toRFC3339WithMillis(Date date) {
        return DateTimeFormatter.ISO_INSTANT.format(date.toInstant());
    }

    public static final String parseDateToStr(final String format, final Date date)
    {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts)
    {
        try
        {
            return new SimpleDateFormat(format).parse(ts);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }


    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }
    /**
     * 根据公式化获取时间
     */
    public static final Date getNowDate(String format)
    {
        Date now = new Date();
        return parseDate( DateFormatUtils.format(now, format));
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str)
    {
        if (str == null)
        {
            return null;
        }
        try
        {
            return parseDate(str.toString(), parsePatterns);
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    /**
     * 将时间转换为当前的开始时间
     * @param dateStr
     * @return
     */
    public static Date parseDateStart(Object dateStr){
        if (dateStr == null)
        {
            return null;
        }

       return parseDateStart(parseDate(dateStr));

    }

    public static Date parseDateStart(Date date){
        if(date==null) return null;
        Date temp = parseDate(parseDateToStr(YYYY_MM_DD,date));
        return temp;
    }

    /**
     * 将时间转换为当天就得结束时间
     * @param dateStr
     * @return
     */
    public static Date parseDateEnd(Object dateStr){

        Date start = parseDateStart(dateStr);
        if(start!=null){
            Date end = addDays(start,1); //一天后
            end=addMilliseconds(end,-1); //减一秒就是今天的最后一一刻
            return end;
        }
        return null;

    }

    /**
     * 将时间转换为当天就得结束时间
     * @param date
     * @return
     */
    public static Date parseDateEnd(Date date){

        Date start = parseDateStart(date);
        if(start!=null){
            Date end = addDays(start,1); //一天后
            end=addMilliseconds(end,-1); //减一秒就是今天的最后一一刻
            return end;
        }
        return null;

    }


    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate()
    {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算时间差
     *
     * @param endDate 最后时间
     * @param startTime 开始时间
     * @return 时间差（天/小时/分钟）
     */
    public static String timeDistance(Date endDate, Date startTime)
    {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - startTime.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }
    /**
     * 计算两个时间的毫秒差
     */
    public static long getMillisecondsDiff(Date startDate, Date endDate) {
        return endDate.getTime() - startDate.getTime();
    }

    /**
     * 计算两个时间的秒差
     */
    public static long getSecondsDiff(Date startDate, Date endDate) {
        return TimeUnit.MILLISECONDS.toSeconds(getMillisecondsDiff(startDate, endDate));
    }

    /**
     * 计算两个时间的分钟差
     */
    public static long getMinutesDiff(Date startDate, Date endDate) {
        return TimeUnit.MILLISECONDS.toMinutes(getMillisecondsDiff(startDate, endDate));
    }

    /**
     * 计算两个时间的天数差
     */
    public static long getDaysDiff(Date startDate, Date endDate) {
        return TimeUnit.MILLISECONDS.toDays(getMillisecondsDiff(startDate, endDate));
    }

    /**
     * 计算两个时间的月差（基于年月计算）
     */
    public static int getMonthsDiff(Date startDate, Date endDate) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.setTime(startDate);
        end.setTime(endDate);

        int yearDiff = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
        int monthDiff = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);

        return yearDiff * 12 + monthDiff;
    }
    /**
     * 增加 LocalDateTime ==> Date
     */
    public static Date toDate(LocalDateTime temporalAccessor)
    {
        ZonedDateTime zdt = temporalAccessor.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 增加 LocalDate ==> Date
     */
    public static Date toDate(LocalDate temporalAccessor)
    {
        LocalDateTime localDateTime = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 将毫秒级时间戳转换为 LocalDateTime（默认系统时区）
     * @param timestamp 毫秒级时间戳
     * @return LocalDateTime 对象
     */
    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault()
        );
    }

    /**
     * 将毫秒级时间戳转换为指定时区的 LocalDateTime
     * @param timestamp 毫秒级时间戳
     * @param zoneId 时区ID，如 "Asia/Shanghai"
     * @return LocalDateTime 对象
     */
    public static LocalDateTime timestampToLocalDateTime(long timestamp, String zoneId) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.of(zoneId)
        );
    }

    /**
     * 验证字符串是否为时间格式
     * @param time
     * @return
     */
    public static boolean isTime(String time){
        if (time == null || time.isEmpty()) {
            return false;
        }
        return time.matches("^([01]\\d|2[0-3]):[0-5]\\d$");
    }

    /**
     * 验证是否时日期时间格式
     * @param dateTime
     * @return
     */
    public static boolean isDateTime(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return false;
        }
        return dateTime.matches("^\\d{4}-\\d{2}-\\d{2} [0-2]\\d:[0-5]\\d:[0-5]\\d(\\.\\d{1,3})?$");
    }

    /**
     * 获取某个日期的 00:00:00 时间（用于比较日期）
     */
    public static Date getStartOfDay(Date date) {
        return new Date(date.getYear(), date.getMonth(), date.getDate(), 0, 0, 0);
    }


    /**
     * 是是否为上午
     * @param timeStr
     * @return
     */
    public static  boolean isAM(String timeStr){
        Date dateTime =DateUtils.parseDate(dateTimeNow(YYYY_MM_DD)+" "+timeStr);
        LocalTime dtim = LocalTime.of(dateTime.getHours(),dateTime.getMinutes(),dateTime.getSeconds());
        return dtim.isBefore(LocalTime.NOON);
    }

    public static Date mergeDateTime(String date,String time){
        String dateTimeStr = date+" "+time;
        return parseDate(dateTimeStr);
    }

    public static boolean isToDay(Date date){
        if (date == null) {
            return false;
        }
        String today = getDate();
        String dateStr =parseDateToStr(YYYY_MM_DD,date);
        return today.equals(dateStr);
    }

    /**
     * 将时间戳转换为Date对象
     * @param timestamp 时间戳
     * @param isSeconds 是否为秒级时间戳
     * @return Date对象
     */
    public static Date timestampToDate(long timestamp, boolean isSeconds) {
        long milliseconds = isSeconds ? timestamp * 1000 : timestamp;
        return new Date(milliseconds);
    }

    /**
     * 将时间戳转换为Date对象
     * @param timestamp 时间戳【毫秒级】
     * @return Date对象
     */
    public static Date timestampToDate(long timestamp) {
        return timestampToDate(timestamp,false); //Date的getTime默认为毫秒级
    }

    /**
     * 获取毫秒级时间戳
     * @return
     */
    public static Long getTimestampMillis(){
        return System.currentTimeMillis();
    }
    /**
     * 获取毫秒级时间戳
     * @return
     */
    public static Integer getTimestamp(){
        return (int)(System.currentTimeMillis()/1000L);
    }

    public static String timestampMillisToBeginTime(Long timestamp){
        return parseDateToStr("yyyy-MM-dd HH:mm:ss.000",new Date(timestamp));
    }
    public static String timestampMillisToEndTime(Long timestamp){
        return parseDateToStr("yyyy-MM-dd HH:mm:ss.9999",new Date(timestamp));
    }
}
