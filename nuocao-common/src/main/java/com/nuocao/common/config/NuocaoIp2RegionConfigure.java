package com.nuocao.common.config;

import cn.hutool.core.util.StrUtil;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.utils.http.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.ConfigBuilder;
import org.lionsoul.ip2region.service.Ip2Region;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Destroy;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * ip2region 装配器：读 {@link NuocaoIp2RegionConfig} → 加载 xdb → 把查询服务注册到 {@link IpUtils}。
 *
 * <p>参数本体在 {@link NuocaoIp2RegionConfig} 里，本类只负责「把配置变成可用的能力」，
 * 与项目里「{@code *Config} 只存参数、{@code *Configure} 负责装配」的约定一致
 * （对照 {@code GlobalConfigure#registerListenerByFlex()}、
 * {@code ValidationConfigure#adapter()} 这两处同样是「注册型」初始化）。</p>
 *
 * <h2>路径写法</h2>
 * <ul>
 *     <li><b>绝对路径</b>（推荐用于生产）：按<b>部署机自身的平台</b>书写，如 Linux 的
 *         {@code /data/nuocao/ip2region/ip2region_v4.xdb}、Windows 的
 *         {@code D:/nuocao/ip2region/ip2region_v4.xdb}。是不是绝对路径交给 JDK 按该平台的规则判断
 *         （{@link File#isAbsolute()}），<b>不需要也不应该自己按「开头是不是斜杠」去猜</b>；</li>
 *     <li><b>跨平台的坑</b>：两个平台的规则并不一致。Windows 除了 {@code D:/x} 之外，
 *         反斜杠写法、UNC（双反斜杠或双斜杠开头的网络路径）以及<b>单个斜杠开头的 {@code /x}</b>
 *         都算绝对路径（其中 {@code /x} 会被解析成<b>当前盘符根</b>，即 {@code C:\x}）；
 *         而 Linux 只认 {@code /} 开头的。因此把 Windows 盘符路径配到 Linux 上会被误当成相对路径
 *         （这种情况会打 warn 日志提示，避免排查困难）。生产配置请随环境覆盖；</li>
 *     <li><b>相对路径</b>：先按进程工作目录解析；若不存在，再<b>从工作目录逐级向上</b>查找
 *         （最多 {@value #MAX_UPWARD_LEVELS} 层）。这样无论是 IDEA 把工作目录设成仓库根目录还是子模块目录，
 *         配置 {@code ./data/ip2region/ip2region_v4.xdb} 都能命中，提交到 git 的配置无需按机器修改；</li>
 *     <li><b>{@code classpath:} 前缀</b>：当作 jar 内资源读取（仅作兼容/测试用，官方要求该方式必须配合全量内存缓存）。</li>
 * </ul>
 *
 * <h2>容错</h2>
 * 这是旁路能力：初始化失败只记录 error 日志并让归属地查询统一返回「未知」，不中断启动；
 * 只有 {@code nuocao.ip2region.fail-fast=true} 时才抛异常中断。
 */
@Slf4j
@Configuration
public class NuocaoIp2RegionConfigure {

    /** {@code classpath:} 前缀，命中时按 jar 内资源加载 */
    private static final String CLASSPATH_PREFIX = "classpath:";

    /** 相对路径向上查找的最大层数 */
    private static final int MAX_UPWARD_LEVELS = 6;

    /** 默认缓存策略：向量索引缓存 */
    private static final String DEFAULT_CACHE_POLICY = "vector-index";

    /** 默认查询器数量（官方示例取值） */
    private static final int DEFAULT_SEARCHERS = 15;

    /** Windows 盘符写法（{@code D:\x} / {@code D:/x} / {@code D:x}） */
    private static final Pattern DRIVE_LETTER_PATH = Pattern.compile("^[A-Za-z]:");

    /** Windows UNC 写法（双反斜杠或双斜杠开头的网络路径） */
    private static final Pattern UNC_PATH = Pattern.compile("^[\\\\/]{2}");

    /** ip2region 参数（由 Solon 按 {@code nuocao.ip2region} 绑定） */
    @Inject
    private NuocaoIp2RegionConfig config;

    /**
     * 启动时加载 xdb 并注册到 {@link IpUtils}。
     *
     * <p>v4 / v6 允许只配置其中一个：另一个为空，对应协议的查询会直接返回「未知」。
     * 两个都没配则视为未启用（警告并跳过）。</p>
     *
     * <p><b>注意：本方法必须保持同步，禁止改成 {@code @Init(async = true)}。</b>
     * Solon 把 {@code @Init} 注册为 index=1 的生命周期节点（{@code @Init#index()} 默认 0，
     * 注册时取 {@code index() + 1}），而 HTTP 端口监听由 {@code SmHttpPlugin} 注册为 index=99；
     * 生命周期按 index <b>升序</b>执行，所以同步的 {@code @Init} 一定跑在端口对外服务之前，
     * 无需担心「请求已经进来了、IP 库却还没加载好」。一旦标成 {@code async}，它会被挪到
     * {@code executeAsyncInitTasks} 里延后执行（发生在端口监听<b>之后</b>），这个保证就失效了。</p>
     */
    @Init
    public void init() {
        if (!config.isEnable()) {
            log.warn(">>> ip2region 已通过配置(nuocao.ip2region.enable=false)关闭，归属地查询将统一返回「未知」");
            return;
        }
        if (StrUtil.isBlank(config.getV4Path()) && StrUtil.isBlank(config.getV6Path())) {
            log.warn(">>> ip2region 未配置 xdb 路径(nuocao.ip2region.v4-path / v6-path)，归属地查询将统一返回「未知」");
            return;
        }
        try {
            Ip2Region ip2Region = createService();
            IpUtils.init(ip2Region);
            log.info(">>> ip2region 初始化完成：cachePolicy={}, searchers={}, v4={}, v6={}",
                    normalizePolicyName(),
                    resolveCachePolicy() == Config.BufferCache ? "(单实例)" : config.getSearchers(),
                    StrUtil.isBlank(config.getV4Path()) ? "(未配置)" : locatePath(config.getV4Path()),
                    StrUtil.isBlank(config.getV6Path()) ? "(未配置)" : locatePath(config.getV6Path()));
        } catch (Exception e) {
            IpUtils.init(null);
            if (config.isFailFast()) {
                throw new CommonException("ip2region 初始化失败，请检查 nuocao.ip2region 配置：" + e.getMessage());
            }
            log.error(">>> ip2region 初始化失败，归属地查询将统一返回「未知」（如需失败即中断启动，请设置 nuocao.ip2region.fail-fast=true）", e);
        }
    }

    /**
     * 服务关闭时释放查询器资源。
     */
    @Destroy
    public void destroy() {
        IpUtils.close();
    }

    /**
     * 依据 v4 / v6 路径创建双协议查询服务。
     *
     * <p>官方说明：v4 / v6 配置各自独立，可分别为 {@code null}，此时对应协议的查询返回 {@code null}。</p>
     */
    private Ip2Region createService() throws Exception {
        Config v4Config = StrUtil.isBlank(config.getV4Path()) ? null : buildConfig(config.getV4Path(), true);
        Config v6Config = StrUtil.isBlank(config.getV6Path()) ? null : buildConfig(config.getV6Path(), false);
        return Ip2Region.create(v4Config, v6Config);
    }

    /**
     * 构建单个协议的 {@link Config}。
     *
     * @param rawPath 配置中的原始路径
     * @param v4      true 构建 v4 配置，false 构建 v6 配置
     */
    private Config buildConfig(String rawPath, boolean v4) throws Exception {
        String located = locatePath(rawPath);
        ConfigBuilder builder = Config.custom();

        if (isClasspath(rawPath)) {
            // 官方约束：使用 InputStream 时缓存策略只能是全量内存缓存
            builder.setCachePolicy(Config.BufferCache);
            String resource = rawPath.substring(CLASSPATH_PREFIX.length());
            if (!resource.startsWith("/")) {
                resource = "/" + resource;
            }
            InputStream in = NuocaoIp2RegionConfigure.class.getResourceAsStream(resource);
            if (in == null) {
                throw new IOException("classpath 资源不存在: " + rawPath);
            }
            builder.setXdbInputStream(in);
        } else {
            File file = new File(located);
            if (!file.isFile()) {
                throw new IOException("xdb 文件不存在或不可读: " + located);
            }
            builder.setCachePolicy(resolveCachePolicy());
            if (resolveCachePolicy() != Config.BufferCache) {
                builder.setSearchers(config.getSearchers() <= 0 ? DEFAULT_SEARCHERS : config.getSearchers());
            }
            builder.setXdbPath(file.getAbsolutePath());
        }

        return v4 ? builder.asV4() : builder.asV6();
    }

    /**
     * 把配置里的路径定位为一个可用于日志/文件的绝对路径。
     *
     * <p>{@code classpath:} 前缀原样返回；绝对路径原样返回；相对路径先按工作目录解析，
     * 找不到再逐级向上查找。</p>
     */
    private String locatePath(String rawPath) {
        if (isClasspath(rawPath)) {
            return rawPath;
        }
        File direct = new File(rawPath);
        if (direct.isAbsolute()) {
            return direct.getAbsolutePath();
        }
        // 【跨平台】本机不认、但形似另一平台绝对路径的写法会被静默当成相对路径，这里显式提示
        warnIfForeignAbsolute(rawPath);
        Path base = Paths.get(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        for (int i = 0; i < MAX_UPWARD_LEVELS; i++) {
            Path candidate = base.resolve(rawPath).normalize();
            if (Files.isRegularFile(candidate) && Files.isReadable(candidate)) {
                return candidate.toFile().getAbsolutePath();
            }
            Path parent = base.getParent();
            if (parent == null) {
                break;
            }
            base = parent;
        }
        // 未命中时返回按工作目录解析的结果，便于在报错信息里看出期望位置
        return Paths.get(System.getProperty("user.dir", ".")).resolve(rawPath).normalize().toFile().getAbsolutePath();
    }

    private boolean isClasspath(String path) {
        return StrUtil.isNotBlank(path) && path.regionMatches(true, 0, CLASSPATH_PREFIX, 0, CLASSPATH_PREFIX.length());
    }

    /**
     * 提示「形似其他平台绝对路径、但当前平台不认」的写法。
     *
     * <p>典型场景：在 Linux 上配了 Windows 盘符路径（如 {@code D:/data/ip2region/x.xdb}），
     * {@link File#isAbsolute()} 会返回 false，于是被静默当成相对路径去拼工作目录，
     * 最终报「xdb 文件不存在或不可读」时看到的是一串被拼接过的奇怪路径，很难一眼看出原因。
     * 这里提前给一条明确的日志。</p>
     *
     * <p>反向同理：Linux 的 {@code /data/x.xdb} 在 Windows 上虽然会被判为绝对路径，
     * 但会被解析成<b>当前盘符根</b>（{@code C:\data\x.xdb}），同样不是预期位置。</p>
     */
    private void warnIfForeignAbsolute(String rawPath) {
        if (DRIVE_LETTER_PATH.matcher(rawPath).find() || UNC_PATH.matcher(rawPath).find()) {
            log.warn(">>> ip2region 路径「{}」在当前平台({})不被识别为绝对路径，将按相对路径处理；"
                            + "请确认是否写成了其他平台的风格（Linux 用 /data/... ，Windows 用 D:/data/... ）",
                    rawPath, System.getProperty("os.name"));
        }
    }

    /**
     * 把配置中的策略名称映射为 ip2region 的常量；无法识别时回退为向量索引缓存。
     */
    private int resolveCachePolicy() {
        String name = normalizePolicyName();
        switch (name) {
            case "buffer":
            case "buffer-cache":
            case "full":
            case "content":
                return Config.BufferCache;
            case "file":
            case "no-cache":
            case "nocache":
                return Config.NoCache;
            case "vector-index":
            case "vectorindex":
            case "vindex":
            case "vindexcache":
                return Config.VIndexCache;
            default:
                log.warn(">>> 无法识别的 ip2region 缓存策略「{}」，已回退为 {}", config.getCachePolicy(), DEFAULT_CACHE_POLICY);
                return Config.VIndexCache;
        }
    }

    private String normalizePolicyName() {
        return StrUtil.blankToDefault(config.getCachePolicy(), DEFAULT_CACHE_POLICY).trim().toLowerCase(Locale.ROOT);
    }
}
