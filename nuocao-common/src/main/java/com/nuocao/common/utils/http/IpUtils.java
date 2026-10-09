package com.nuocao.common.utils.http;

import cn.hutool.core.net.Ipv4Util;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.service.Ip2Region;
import org.noear.solon.core.handle.Context;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 根据 IP 地址离线定位归属地工具类（ip2region 3.x）。
 *
 * <h2>与 2.x 的差异（升级要点）</h2>
 * <ul>
 *     <li>数据文件由单个 {@code ip2region.xdb} 拆分为 <b>{@code ip2region_v4.xdb} + {@code ip2region_v6.xdb}</b>；
 *         v3 xdb 的文件格式与 v2 不兼容（文件头首字节 3 表示 v3，2 表示 v2），旧库必须替换。</li>
 *     <li>3.2.0 起官方推荐使用 {@link Ip2Region} 查询服务（<b>双协议兼容</b>）：同一个
 *         {@link Ip2Region#search(String)} 会自动识别 IPv4 / IPv6，且<b>并发安全</b>，
 *         可以直接作为全局单例复用；不再需要自己维护 {@code Searcher} 或按线程创建实例。</li>
 *     <li>{@code Searcher.newWithBuffer} 的参数由 {@code byte[]} 改为 {@code LongByteArray}，
 *         并新增 {@code Version} 参数，用于规避超大 xdb 的 int 溢出。</li>
 * </ul>
 *
 * <h2>数据文件与初始化</h2>
 * xdb 文件<b>不再打包进 jar</b>（v4 约 11 MB + v6 约 37 MB，合计约 48 MB），改为部署时外部放置、
 * 由 {@code nuocao.ip2region.*} 指定路径，详见
 * {@link com.nuocao.common.config.NuocaoIp2RegionConfigure}。
 * 因此本类<b>不再自初始化</b>：查询服务由 {@code NuocaoIp2RegionConfigure} 在启动时构建，
 * 并通过 {@link #init(Ip2Region)} 注册进来。
 * <p>
 * 遵循「旁路能力不影响主链路」的原则：<b>服务未就绪（未配置、文件缺失、初始化失败）时，
 * 本类所有查询直接返回「未知」，绝不抛异常</b>。
 *
 * <h2>线程安全</h2>
 * {@link Ip2Region} 的查询 API 是并发安全的，因此这里用 {@code volatile} 静态字段做全局单例；
 * 只有整个服务关闭时才需要 {@link #close()}。
 *
 * <h2>安全注意</h2>
 * 3.x 的 {@code Searcher.search(String)} 内部走 {@code InetAddress.getByName} 解析，
 * <b>传入主机名会触发 DNS 解析</b>（实测可阻塞数秒）。而本类的 IP 通常来自
 * {@code request.realIp()}（即 {@code X-Forwarded-For} 等请求头原文，未经验证），
 * 攻击者可用伪造值把请求线程拖住。因此 {@link #getCityInfo(String)} 在查询前会先用
 * {@link #isIpLiteral(String)} 做纯本地校验，不是 IP 字面量就直接返回「未知」。
 */
@Slf4j
public class IpUtils {

    /** IPv4 环回地址 */
    private static final String LOCAL_HOST_V4 = Ipv4Util.LOCAL_IP;

    /** IPv6 环回地址（全写） */
    private static final String LOCAL_HOST_V6_FULL = "0:0:0:0:0:0:0:1";

    /** IPv6 环回地址（缩写） */
    private static final String LOCAL_HOST_V6_SHORT = "::1";

    /** 查询不到归属地时的占位文案 */
    private static final String UNKNOWN = "未知";

    /**
     * 双协议查询服务（IPv4 / IPv6 兼容），全局单例、并发安全。
     * 由 {@link com.nuocao.common.config.NuocaoIp2RegionConfigure} 在启动时注入；为 {@code null} 表示未启用/初始化失败。
     */
    private static volatile Ip2Region ip2Region;

    /** 未就绪告警只打印一次，避免每条日志都刷屏 */
    private static final AtomicBoolean NOT_READY_WARNED = new AtomicBoolean(false);

    /**
     * 私有构造函数防止实例化
     */
    private IpUtils() {
    }

    /**
     * 注册查询服务实例（由配置类在启动时调用；传 {@code null} 表示停用）。
     */
    public static void init(Ip2Region instance) {
        ip2Region = instance;
        if (instance == null) {
            NOT_READY_WARNED.set(false);
        }
    }

    /**
     * 释放查询服务资源（服务关闭时调用）。
     */
    public static void close() {
        Ip2Region instance = ip2Region;
        ip2Region = null;
        if (instance == null) {
            return;
        }
        try {
            instance.close();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn(">>> ip2region 关闭被中断", e);
        }
    }

    /**
     * 查询服务是否已就绪。
     */
    public static boolean isReady() {
        return ip2Region != null;
    }

    /**
     * 获取客户端 ip
     */
    public static String getIp(Context request) {
        if (ObjectUtil.isEmpty(request)) {
            return LOCAL_HOST_V4;
        } else {
            try {
                String remoteHost = request.realIp();
                return isLocalHost(remoteHost) ? LOCAL_HOST_V4 : remoteHost;
            } catch (Exception e) {
                log.error(">>> get client ip error：", e);
                return LOCAL_HOST_V4;
            }
        }
    }

    public static String getRequestIp() {
        return getIp(RequestUtils.getCurrentRequest());
    }

    /**
     * 根据 IP 地址离线获取城市
     *
     * @param ip IPv4 或 IPv6 地址（内部自动识别协议）
     * @return 归属地字符串（形如 {@code 中国|0|广东省|深圳市|电信|CN}）；
     *         空 IP、非法 IP、查询无结果、<b>查询服务未就绪</b>时统一返回「未知」，不抛异常
     */
    public static String getCityInfo(String ip) {
        if (StrUtil.isBlank(ip)) {
            return UNKNOWN;
        }
        Ip2Region service = ip2Region;
        if (service == null) {
            if (NOT_READY_WARNED.compareAndSet(false, true)) {
                log.warn(">>> ip2region 查询服务未就绪（未配置 xdb 路径或初始化失败），归属地查询统一返回「未知」");
            }
            return UNKNOWN;
        }
        String value = ip.trim();
        // 【安全】必须先确认是 IP 字面量：ip2region 内部用 InetAddress.getByName 解析，
        // 传入主机名会触发 DNS 解析（实测可阻塞数秒）；而本方法的入参来自 request.realIp()，
        // 即请求头原文，未经验证，绝不能让攻击者用一个伪造的 X-Forwarded-For 拖住请求线程。
        if (!isIpLiteral(value)) {
            log.debug(">>> 非法的 IP 字面量，跳过归属地查询：{}", value);
            return UNKNOWN;
        }
        try {
            // 3.x 的 search 会自动区分 IPv4 / IPv6；查不到时返回空串
            String region = service.search(value);
            if (StrUtil.isBlank(region)) {
                return UNKNOWN;
            }
            return region.replace("0|", "").replace("|0", "");
        } catch (Exception e) {
            // xdb 与协议不匹配等场景，统一按未知处理
            log.debug(">>> ip2region search fail, ip={}", ip, e);
            return UNKNOWN;
        }
    }

    /**
     * 判断是否为环回地址（IPv4 / IPv6 两种写法）
     */
    private static boolean isLocalHost(String ip) {
        return LOCAL_HOST_V4.equals(ip)
                || LOCAL_HOST_V6_FULL.equals(ip)
                || LOCAL_HOST_V6_SHORT.equals(ip);
    }

    /**
     * 判断是否为 IP 字面量（IPv4 或 IPv6）。
     *
     * <p>只做「字面量」判断，不涉及网络：不符合时直接返回 false，
     * 从而保证不会把主机名交给 {@code InetAddress.getByName} 触发 DNS。</p>
     */
    private static boolean isIpLiteral(String ip) {
        return isIpv4Literal(ip) || isIpv6Literal(ip);
    }

    /**
     * 是否为 IPv4 字面量（四段十进制，每段 0~255）
     */
    private static boolean isIpv4Literal(String ip) {
        String[] parts = ip.split("\\.", -1);
        if (parts.length != 4) {
            return false;
        }
        for (String part : parts) {
            if (part.isEmpty() || part.length() > 3) {
                return false;
            }
            for (int i = 0; i < part.length(); i++) {
                if (!Character.isDigit(part.charAt(i))) {
                    return false;
                }
            }
            if (Integer.parseInt(part) > 255) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否为 IPv6 字面量，支持 {@code ::} 压缩与 {@code ::ffff:1.2.3.4} 形式的 IPv4 映射写法。
     *
     * <p>不含裸 IPv4（那种情况由 {@link #isIpv4Literal(String)} 负责）。</p>
     */
    private static boolean isIpv6Literal(String ip) {
        if (ip.indexOf(':') < 0) {
            return false;
        }

        // IPv4 映射写法：把末尾的 IPv4 段折算成两个 16 进制组后再统一按组数判断
        int lastColon = ip.lastIndexOf(':');
        if (ip.indexOf('.', lastColon + 1) > lastColon) {
            if (!isIpv4Literal(ip.substring(lastColon + 1))) {
                return false;
            }
            ip = ip.substring(0, lastColon + 1) + "0:0";
        }

        int compress = ip.indexOf("::");
        if (compress != ip.lastIndexOf("::")) {
            // 出现多个 "::"，非法
            return false;
        }
        if (compress < 0) {
            // 未压缩写法必须正好 8 组
            return countHexGroups(ip) == 8;
        }
        // 压缩写法：head + tail 的组数必须少于 8（"::" 至少代表一组 0）
        int headGroups = countHexGroups(ip.substring(0, compress));
        int tailGroups = countHexGroups(ip.substring(compress + 2));
        return headGroups >= 0 && tailGroups >= 0 && headGroups + tailGroups < 8;
    }

    /**
     * 统计以 ":" 分隔的 16 进制组个数（每段 1~4 位十六进制）；
     * 出现空段或非法字符时返回 -1。
     */
    private static int countHexGroups(String part) {
        if (part.isEmpty()) {
            return 0;
        }
        int groups = 0;
        int groupLen = 0;
        for (int i = 0, n = part.length(); i < n; i++) {
            char c = part.charAt(i);
            if (c == ':') {
                if (groupLen == 0 || groupLen > 4) {
                    return -1;
                }
                groups++;
                groupLen = 0;
            } else if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')) {
                groupLen++;
            } else {
                return -1;
            }
        }
        if (groupLen == 0 || groupLen > 4) {
            return -1;
        }
        return groups + 1;
    }
}
