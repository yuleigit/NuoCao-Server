package com.nuocao.common.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LocalDateTime 工具类
 * 提供常用的日期时间操作方法
 */
public class LocalDateTimeUtils {

    // 常用格式化器缓存
    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();

    // 常用格式模式
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_TIME = "HH:mm:ss";
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATETIME_MS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String PATTERN_COMPACT_DATE = "yyyyMMdd";
    public static final String PATTERN_COMPACT_DATETIME = "yyyyMMddHHmmss";
    public static final String PATTERN_COMPACT_DATETIME_MS = "yyyyMMddHHmmssSSS";
    public static final String PATTERN_ISO = "yyyy-MM-dd'T'HH:mm:ss";

    // 默认时区
    public static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();
    public static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.ofHours(8); // 东八区

    private LocalDateTimeUtils() {
        throw new IllegalStateException("Utility class");
    }

    // ==================== 获取当前时间 ====================

    /**
     * 获取当前日期时间
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前日期
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * 获取当前时间
     */
    public static LocalTime currentTime() {
        return LocalTime.now();
    }

    /**
     * 获取当前时间戳（毫秒，默认时区）
     */
    public static long currentTimestamp() {
        return now().atZone(DEFAULT_ZONE).toInstant().toEpochMilli();
    }

    /**
     * 获取当前时间戳（毫秒，UTC）
     */
    public static long currentTimestampUTC() {
        return now().toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    /**
     * 获取当前时间戳（秒，默认时区）
     */
    public static long currentTimestampSeconds() {
        return now().atZone(DEFAULT_ZONE).toEpochSecond();
    }

    // ==================== 时间戳转换 ====================

    /**
     * 时间戳转LocalDateTime（默认时区）
     */
    public static LocalDateTime fromTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                DEFAULT_ZONE
        );
    }

    /**
     * 时间戳转LocalDateTime（指定时区）
     */
    public static LocalDateTime fromTimestamp(long timestamp, ZoneId zoneId) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                zoneId
        );
    }

    /**
     * LocalDateTime转时间戳（毫秒，默认时区）
     */
    public static long toTimestamp(LocalDateTime dateTime) {
        return dateTime.atZone(DEFAULT_ZONE).toInstant().toEpochMilli();
    }

    /**
     * LocalDateTime转时间戳（毫秒，指定时区）
     */
    public static long toTimestamp(LocalDateTime dateTime, ZoneId zoneId) {
        return dateTime.atZone(zoneId).toInstant().toEpochMilli();
    }

    /**
     * LocalDateTime转时间戳（毫秒，UTC）
     */
    public static long toTimestampUTC(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    /**
     * 秒级时间戳转LocalDateTime
     */
    public static LocalDateTime fromTimestampSeconds(long seconds) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(seconds),
                DEFAULT_ZONE
        );
    }

    // ==================== 格式化与解析 ====================

    /**
     * 格式化日期时间
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        return getFormatter(pattern).format(dateTime);
    }

    /**
     * 格式化日期
     */
    public static String formatDate(LocalDate date, String pattern) {
        if (date == null) {
            return null;
        }
        return getFormatter(pattern).format(date.atStartOfDay());
    }

    /**
     * 格式化时间
     */
    public static String formatTime(LocalTime time, String pattern) {
        if (time == null) {
            return null;
        }
        return getFormatter(pattern).format(time.atDate(LocalDate.now()));
    }

    /**
     * 默认格式化（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDefault(LocalDateTime dateTime) {
        return format(dateTime, PATTERN_DATETIME);
    }

    /**
     * 格式化日期（yyyy-MM-dd）
     */
    public static String formatDate(LocalDateTime dateTime) {
        return format(dateTime, PATTERN_DATE);
    }

    /**
     * 格式化时间（HH:mm:ss）
     */
    public static String formatTime(LocalDateTime dateTime) {
        return format(dateTime, PATTERN_TIME);
    }

    /**
     * 解析字符串为LocalDateTime
     */
    public static LocalDateTime parse(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, getFormatter(pattern));
    }

    /**
     * 解析字符串为LocalDate
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        LocalDateTime dateTime = parse(dateStr + " 00:00:00", pattern);
        return dateTime != null ? dateTime.toLocalDate() : null;
    }

    /**
     * 解析字符串为LocalTime
     */
    public static LocalTime parseTime(String timeStr, String pattern) {
        LocalDateTime dateTime = parse("1970-01-01 " + timeStr, pattern);
        return dateTime != null ? dateTime.toLocalTime() : null;
    }

    /**
     * 默认解析（yyyy-MM-dd HH:mm:ss）
     */
    public static LocalDateTime parseDefault(String dateTimeStr) {
        return parse(dateTimeStr, PATTERN_DATETIME);
    }

    // ==================== 时间计算 ====================

    /**
     * 增加年
     */
    public static LocalDateTime plusYears(LocalDateTime dateTime, long years) {
        return dateTime.plusYears(years);
    }

    /**
     * 减少年
     */
    public static LocalDateTime minusYears(LocalDateTime dateTime, long years) {
        return dateTime.minusYears(years);
    }

    /**
     * 增加月
     */
    public static LocalDateTime plusMonths(LocalDateTime dateTime, long months) {
        return dateTime.plusMonths(months);
    }

    /**
     * 减少月
     */
    public static LocalDateTime minusMonths(LocalDateTime dateTime, long months) {
        return dateTime.minusMonths(months);
    }

    /**
     * 增加周
     */
    public static LocalDateTime plusWeeks(LocalDateTime dateTime, long weeks) {
        return dateTime.plusWeeks(weeks);
    }

    /**
     * 减少周
     */
    public static LocalDateTime minusWeeks(LocalDateTime dateTime, long weeks) {
        return dateTime.minusWeeks(weeks);
    }

    /**
     * 增加天
     */
    public static LocalDateTime plusDays(LocalDateTime dateTime, long days) {
        return dateTime.plusDays(days);
    }

    /**
     * 减少天
     */
    public static LocalDateTime minusDays(LocalDateTime dateTime, long days) {
        return dateTime.minusDays(days);
    }

    /**
     * 增加小时
     */
    public static LocalDateTime plusHours(LocalDateTime dateTime, long hours) {
        return dateTime.plusHours(hours);
    }

    /**
     * 减少小时
     */
    public static LocalDateTime minusHours(LocalDateTime dateTime, long hours) {
        return dateTime.minusHours(hours);
    }

    /**
     * 增加分钟
     */
    public static LocalDateTime plusMinutes(LocalDateTime dateTime, long minutes) {
        return dateTime.plusMinutes(minutes);
    }

    /**
     * 减少分钟
     */
    public static LocalDateTime minusMinutes(LocalDateTime dateTime, long minutes) {
        return dateTime.minusMinutes(minutes);
    }

    /**
     * 增加秒
     */
    public static LocalDateTime plusSeconds(LocalDateTime dateTime, long seconds) {
        return dateTime.plusSeconds(seconds);
    }

    /**
     * 减少秒
     */
    public static LocalDateTime minusSeconds(LocalDateTime dateTime, long seconds) {
        return dateTime.minusSeconds(seconds);
    }

    // ==================== 获取特定时间 ====================

    /**
     * 获取一天的开始时间（00:00:00）
     */
    public static LocalDateTime startOfDay(LocalDateTime dateTime) {
        return dateTime.toLocalDate().atStartOfDay();
    }

    /**
     * 获取一天的开始时间（指定日期）
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * 获取一天的结束时间（23:59:59.999999999）
     */
    public static LocalDateTime endOfDay(LocalDateTime dateTime) {
        return dateTime.toLocalDate().atTime(LocalTime.MAX);
    }

    /**
     * 获取一天的结束时间（指定日期）
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }

    /**
     * 获取本周第一天（周一）
     */
    public static LocalDateTime startOfWeek(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .toLocalDate().atStartOfDay();
    }

    /**
     * 获取本周最后一天（周日）
     */
    public static LocalDateTime endOfWeek(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .toLocalDate().atTime(LocalTime.MAX);
    }

    /**
     * 获取本月第一天
     */
    public static LocalDateTime startOfMonth(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.firstDayOfMonth())
                .toLocalDate().atStartOfDay();
    }

    /**
     * 获取本月最后一天
     */
    public static LocalDateTime endOfMonth(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.lastDayOfMonth())
                .toLocalDate().atTime(LocalTime.MAX);
    }

    /**
     * 获取本季度第一天
     */
    public static LocalDateTime startOfQuarter(LocalDateTime dateTime) {
        int month = dateTime.getMonthValue();
        int quarterMonth = ((month - 1) / 3) * 3 + 1;
        return dateTime.withMonth(quarterMonth)
                .with(TemporalAdjusters.firstDayOfMonth())
                .toLocalDate().atStartOfDay();
    }

    /**
     * 获取本季度最后一天
     */
    public static LocalDateTime endOfQuarter(LocalDateTime dateTime) {
        int month = dateTime.getMonthValue();
        int quarterMonth = ((month - 1) / 3 + 1) * 3;
        return dateTime.withMonth(quarterMonth)
                .with(TemporalAdjusters.lastDayOfMonth())
                .toLocalDate().atTime(LocalTime.MAX);
    }

    /**
     * 获取本年度第一天
     */
    public static LocalDateTime startOfYear(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.firstDayOfYear())
                .toLocalDate().atStartOfDay();
    }

    /**
     * 获取本年度最后一天
     */
    public static LocalDateTime endOfYear(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.lastDayOfYear())
                .toLocalDate().atTime(LocalTime.MAX);
    }

    // ==================== 时间比较 ====================

    /**
     * 判断时间是否在范围内（包含边界）
     */
    public static boolean isBetween(LocalDateTime dateTime, LocalDateTime start, LocalDateTime end) {
        return !dateTime.isBefore(start) && !dateTime.isAfter(end);
    }

    /**
     * 判断时间是否在范围内（不包含边界）
     */
    public static boolean isBetweenExclusive(LocalDateTime dateTime, LocalDateTime start, LocalDateTime end) {
        return dateTime.isAfter(start) && dateTime.isBefore(end);
    }

    /**
     * 判断是否为同一天
     */
    public static boolean isSameDay(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.toLocalDate().isEqual(dateTime2.toLocalDate());
    }

    /**
     * 判断是否为同一月
     */
    public static boolean isSameMonth(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.getYear() == dateTime2.getYear() &&
                dateTime1.getMonth() == dateTime2.getMonth();
    }

    /**
     * 判断是否为同一年
     */
    public static boolean isSameYear(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.getYear() == dateTime2.getYear();
    }

    /**
     * 判断是否为工作日（周一到周五）
     */
    public static boolean isWorkday(LocalDateTime dateTime) {
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    /**
     * 判断是否为周末
     */
    public static boolean isWeekend(LocalDateTime dateTime) {
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    // ==================== 时间差计算 ====================

    /**
     * 计算相差天数
     */
    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 计算相差小时数
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * 计算相差分钟数
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * 计算相差秒数
     */
    public static long secondsBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.SECONDS.between(start, end);
    }

    /**
     * 计算相差毫秒数
     */
    public static long millisBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.MILLIS.between(start, end);
    }

    /**
     * 计算相差月数
     */
    public static long monthsBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.MONTHS.between(start, end);
    }

    /**
     * 计算相差年数
     */
    public static long yearsBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.YEARS.between(start, end);
    }

    // ==================== 年龄计算 ====================

    /**
     * 计算年龄
     */
    public static int calculateAge(LocalDate birthDate) {
        return calculateAge(birthDate, LocalDate.now());
    }

    /**
     * 计算年龄（指定参考日期）
     */
    public static int calculateAge(LocalDate birthDate, LocalDate referenceDate) {
        if (birthDate == null || referenceDate == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (birthDate.isAfter(referenceDate)) {
            return 0;
        }

        int age = referenceDate.getYear() - birthDate.getYear();
        if (referenceDate.getMonthValue() < birthDate.getMonthValue() ||
                (referenceDate.getMonthValue() == birthDate.getMonthValue() &&
                        referenceDate.getDayOfMonth() < birthDate.getDayOfMonth())) {
            age--;
        }
        return age;
    }

    // ==================== 其他常用方法 ====================

    /**
     * 获取两个时间的最大值
     */
    public static LocalDateTime max(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.isAfter(dateTime2) ? dateTime1 : dateTime2;
    }

    /**
     * 获取两个时间的最小值
     */
    public static LocalDateTime min(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.isBefore(dateTime2) ? dateTime1 : dateTime2;
    }

    /**
     * 判断是否为闰年
     */
    public static boolean isLeapYear(LocalDateTime dateTime) {
        return dateTime.toLocalDate().isLeapYear();
    }

    /**
     * 获取当月的天数
     */
    public static int lengthOfMonth(LocalDateTime dateTime) {
        return dateTime.toLocalDate().lengthOfMonth();
    }

    /**
     * 获取当年的天数
     */
    public static int lengthOfYear(LocalDateTime dateTime) {
        return dateTime.toLocalDate().lengthOfYear();
    }

    /**
     * 获取星期几（中文）
     */
    public static String getChineseDayOfWeek(LocalDateTime dateTime) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        return weekDays[dateTime.getDayOfWeek().getValue() % 7];
    }

    /**
     * 获取月份（中文）
     */
    public static String getChineseMonth(LocalDateTime dateTime) {
        String[] months = {"一月", "二月", "三月", "四月", "五月", "六月",
                "七月", "八月", "九月", "十月", "十一月", "十二月"};
        return months[dateTime.getMonthValue() - 1];
    }

    // ==================== 定时任务相关 ====================

    /**
     * 获取下一个整点时间
     */
    public static LocalDateTime nextHour(LocalDateTime dateTime) {
        return dateTime.plusHours(1).withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * 获取下一个整半点时间
     */
    public static LocalDateTime nextHalfHour(LocalDateTime dateTime) {
        int minute = dateTime.getMinute();
        if (minute < 30) {
            return dateTime.withMinute(30).withSecond(0).withNano(0);
        } else {
            return dateTime.plusHours(1).withMinute(0).withSecond(0).withNano(0);
        }
    }

    /**
     * 获取下一个5分钟倍数的时间
     */
    public static LocalDateTime nextFiveMinutes(LocalDateTime dateTime) {
        int minute = dateTime.getMinute();
        int nextMinute = ((minute / 5) + 1) * 5;
        if (nextMinute == 60) {
            return dateTime.plusHours(1).withMinute(0).withSecond(0).withNano(0);
        }
        return dateTime.withMinute(nextMinute).withSecond(0).withNano(0);
    }

    // ==================== 辅助方法 ====================

    /**
     * 获取或创建格式化器
     */
    private static DateTimeFormatter getFormatter(String pattern) {
        return FORMATTER_CACHE.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
    }

    /**
     * 安全的转换方法，避免空指针
     */
    public static LocalDateTime safeParse(String dateTimeStr, String pattern) {
        try {
            return parse(dateTimeStr, pattern);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 安全的格式化方法，避免空指针
     */
    public static String safeFormat(LocalDateTime dateTime, String pattern) {
        try {
            return format(dateTime, pattern);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 转换为Date
     */
    public static Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(DEFAULT_ZONE).toInstant());
    }

    /**
     * 从Date转换
     */
    public static LocalDateTime fromDate(Date date) {
        return date.toInstant().atZone(DEFAULT_ZONE).toLocalDateTime();
    }

    // ==================== 常用常量 ====================

    /**
     * 常用时间常量
     */
    public static final class Constants {
        public static final LocalDateTime MIN = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        public static final LocalDateTime MAX = LocalDateTime.of(2099, 12, 31, 23, 59, 59);

        private Constants() {}
    }

    // ==================== 测试示例 ====================

//    public static void main(String[] args) {
//        // 示例用法
//        LocalDateTime now = now();
//
//        // 1. 格式化
//        System.out.println("当前时间: " + formatDefault(now));
//        System.out.println("当前日期: " + formatDate(now));
//        System.out.println("当前时间戳: " + currentTimestamp());
//
//        // 2. 时间计算
//        LocalDateTime tomorrow = plusDays(now, 1);
//        System.out.println("明天: " + formatDefault(tomorrow));
//
//        LocalDateTime yesterday = minusDays(now, 1);
//        System.out.println("昨天: " + formatDefault(yesterday));
//
//        // 3. 获取特定时间
//        System.out.println("今天开始: " + formatDefault(startOfDay(now)));
//        System.out.println("今天结束: " + formatDefault(endOfDay(now)));
//        System.out.println("本月第一天: " + formatDefault(startOfMonth(now)));
//        System.out.println("本月最后一天: " + formatDefault(endOfMonth(now)));
//
//        // 4. 时间比较
//        System.out.println("是否为工作日: " + isWorkday(now));
//        System.out.println("是否为同一天: " + isSameDay(now, tomorrow));
//
//        // 5. 时间差
//        System.out.println("相差天数: " + daysBetween(yesterday, tomorrow));
//        System.out.println("相差小时: " + hoursBetween(now, tomorrow));
//
//        // 6. 年龄计算
//        LocalDate birthDate = LocalDate.of(1990, 1, 1);
//        System.out.println("年龄: " + calculateAge(birthDate));
//
//        // 7. 定时任务相关
//        System.out.println("下一个整点: " + formatDefault(nextHour(now)));
//        System.out.println("下一个半点: " + formatDefault(nextHalfHour(now)));
//    }
}