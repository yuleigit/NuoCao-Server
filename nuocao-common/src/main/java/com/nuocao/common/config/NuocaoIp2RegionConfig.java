package com.nuocao.common.config;

import lombok.Getter;
import lombok.Setter;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * ip2region 离线 IP 归属地查询配置（对应 {@code nuocao.ip2region}）。
 *
 * <p><b>本类只承载参数，不含任何装配或初始化逻辑。</b>xdb 的加载、查询服务构建、
 * 注册到 {@link com.nuocao.common.utils.http.IpUtils}，以及服务关闭时的资源释放，
 * 统一由 {@link NuocaoIp2RegionConfigure} 负责——与项目里
 * 「{@code *Config} 只存参数、{@code *Configure} 负责装配」的约定保持一致。</p>
 *
 * <h2>为什么数据文件放在 jar 外</h2>
 * {@code ip2region_v4.xdb}（约 11 MB）与 {@code ip2region_v6.xdb}（约 37 MB）合计约 48 MB，
 * 若打进 jar 会显著增大制品体积、每次发版都要重新打包上传；而这两个文件属于「低频变更的静态数据」，
 * 更新频率远低于代码。因此改为<b>部署时外部放置 + 本配置指定路径</b>。
 * 路径的解析规则（绝对 / 相对 / {@code classpath:} 前缀、跨平台差异）见 {@link NuocaoIp2RegionConfigure}。
 *
 * <h2>缓存策略</h2>
 * <ul>
 *     <li>{@code vector-index}（默认，官方推荐用法）：只常驻向量索引（约 1 MB），查询时按需读盘；</li>
 *     <li>{@code buffer}：全量载入内存（约 48 MB），零磁盘 IO，内部为单实例并发安全查询器；</li>
 *     <li>{@code file}：完全不缓存，磁盘 IO 最多。</li>
 * </ul>
 *
 * <h2>容错</h2>
 * 这是旁路能力：<b>初始化失败不会中断启动</b>（{@code fail-fast: false} 时），
 * 只记录 error 日志并让归属地查询统一返回「未知」，不影响主业务链路。
 * 若希望配置错误时立刻暴露，可把 {@code fail-fast} 设为 {@code true}。
 */
@Inject("${nuocao.ip2region}")
@Configuration
@Setter
@Getter
public class NuocaoIp2RegionConfig {

    /** 是否启用。为 false 时归属地查询统一返回「未知」 */
    private boolean enable = true;

    /** ip2region v4 xdb 路径，解析规则见 {@link NuocaoIp2RegionConfigure} */
    private String v4Path;

    /** ip2region v6 xdb 路径，解析规则见 {@link NuocaoIp2RegionConfigure} */
    private String v6Path;

    /** 缓存策略：vector-index（默认）/ buffer / file */
    private String cachePolicy = "vector-index";

    /** 查询器数量，仅 vector-index / file 策略生效（buffer 策略下内部固定单实例） */
    private int searchers = 15;

    /** 初始化失败时是否中断启动。默认 false：降级为「未知」并记录 error 日志 */
    private boolean failFast = false;
}
