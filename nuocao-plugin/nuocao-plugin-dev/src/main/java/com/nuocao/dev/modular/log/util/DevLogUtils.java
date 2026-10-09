package com.nuocao.dev.modular.log.util;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.useragent.UserAgent;
import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.auth.api.dto.SaUserLoginInfoDTO;
import com.nuocao.common.ObjectUtils;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.enums.CommonUserTypeEnum;
import com.nuocao.common.utils.*;
import com.nuocao.common.utils.http.IpUtils;
import com.nuocao.common.utils.http.RequestUtils;
import com.nuocao.common.utils.json.JsonUtils;
import com.nuocao.data.access.tenant.util.TenantUtils;
import com.nuocao.dev.modular.log.entity.DevLog;
import com.nuocao.dev.modular.log.enums.DevLogCategoryEnum;
import com.nuocao.dev.modular.log.enums.DevLogExeStatusEnum;
import com.nuocao.dev.modular.log.service.DevLogService;
import com.nuocao.plugin.dev.api.dto.log.DevLogAddDTO;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


/**
 * 操作日志工具类。
 *
 * <h2>线程约定（重要）</h2>
 * 这里的方法分两类，调用时不要在错误的线程上使用：
 * <ul>
 *     <li>{@link #buildLogDto()} —— <b>必须在业务调用线程（主线程）执行</b>。它依赖 Sa-Token 与 Solon 的
 *         ThreadLocal（当前请求、登录会话），在异步线程里调用拿不到数据。</li>
 *     <li>{@link #executeOperationLog} / {@link #executeExceptionLog} —— 同样在业务线程内被调用，
 *         负责组装 {@link DevLog}，随后把<b>只有落库这一步</b>丢给异步线程。</li>
 * </ul>
 *
 * <h2>容错约定</h2>
 * 日志属于旁路能力：本类所有对外方法<b>都不会向外抛异常</b>——包括同步执行的 {@link #buildLogDto()} 采集、
 * 组装，以及异步的落库；内部任何失败只记录 warn 日志。这样「记日志失败」永远不会把一次成功的业务操作
 * 变成接口报错。
 *
 * <h2>敏感信息</h2>
 * 请求参数落库前会做三层过滤：跳过技术性参数（{@link Context}、上传文件、流等）、
 * 跳过注解声明的 {@link CommonLog#excludeParamNames()}、对密码/令牌类字段打码，
 * 并对最终文本做长度截断，避免大字段（如 base64 头像）撑爆日志表。
 */
@Slf4j
public class DevLogUtils {

    private static final DevLogService devLogService = Solon.context().getBean(DevLogService.class);

    /** 落库文本的最大长度，超出截断，防止大字段（base64 图片等）撑爆日志表 */
    private static final int MAX_TEXT_LEN = 4000;

    /** 参数名命中这些关键字时，值不落库，用掩码代替 */
    private static final String[] SENSITIVE_KEYWORDS =
            {"password", "passwd", "pwd", "secret", "token", "credential", "salt", "privatekey"};

    /** 敏感值的掩码 */
    private static final String MASK = "******";

    /**
     * 在当前线程采集日志上下文（请求URL、登录用户、租户、开始时间）。
     *
     * <p>必须在业务方法的调用线程中执行：Sa-Token 的登录会话与 Solon 的 {@code Context.current()}
     * 都依赖 ThreadLocal，异步线程里取不到。</p>
     *
     * @return 已填充基础信息的日志 DTO
     */
    public static DevLogAddDTO buildLogDto() {
        DevLogAddDTO addDto = DevLogAddDTO.builder()
                .userType(CommonUserTypeEnum.ADMIN.getValue())
                .beginTime(LocalDateTimeUtils.currentTimestamp())
                .build();

        // 采集请求地址：非 Web 线程（定时任务、消息消费等）拿不到，忽略即可
        try {
            addDto.setReqUrl(RequestUtils.getReqUrl());
        } catch (Throwable e) {
            log.warn("操作日志：获取请求地址失败，已忽略", e);
        }

        // 采集登录用户：未登录、令牌过期等场景取不到，按匿名（0/空）记录
        try {
            SaUserLoginInfoDTO loginUser = StpLoginUserUtils.getLoginUser();
            if (ObjectUtils.isNotEmpty(loginUser)) {
                // 补齐用户信息：不填的话日志里的「操作人」永远是空/0
                addDto.setUserId(loginUser.getId());
                addDto.setUserName(loginUser.getName());
                addDto.setTenantId(loginUser.getTenantId());
            }
        } catch (Throwable ignored) {
            // 未登录、令牌过期等场景取不到用户，按匿名（0/空）记录
        }
        return addDto;
    }

    /**
     * 记录「操作成功」日志。内部已兜底，不会抛异常。
     *
     * @param commonLog 方法上的日志注解，可能为 null
     * @param addDto    {@link #buildLogDto()} 采集到的上下文
     * @param inv       Solon 调用上下文，用于取类名/方法名/参数
     * @param result    业务方法返回值；是否记录由 {@link CommonLog#isSaveResponseData()} 决定
     */
    public static void executeOperationLog(CommonLog commonLog, DevLogAddDTO addDto, Invocation inv, Object result) {
        try {
            addDto.setEndTime(LocalDateTimeUtils.currentTimestamp());

            DevLog devLog = buildBaseLog();
            setCommonData(devLog, commonLog, addDto, inv);
            devLog.setExeStatus(DevLogExeStatusEnum.SUCCESS.getValue());
            devLog.setExeMessage(DevLogExeStatusEnum.SUCCESS.getValue());
            if (commonLog == null || commonLog.isSaveResponseData()) {
                devLog.setResultData(toLogText(result));
            }
            creatLogSignValue(devLog);

            submit(devLog, false);
        } catch (Throwable e) {
            log.warn("记录操作日志失败，已忽略。class={}, method={}",
                    inv.target().getClass().getName(), inv.method().getMethod().getName(), e);
        }
    }

    /**
     * 记录「操作失败」日志。内部已兜底，不会抛异常。
     *
     * @param commonLog 方法上的日志注解，可能为 null
     * @param addDto    {@link #buildLogDto()} 采集到的上下文
     * @param inv       Solon 调用上下文
     * @param throwable 业务方法抛出的原始异常（无需自行拆包，内部会还原）
     */
    public static void executeExceptionLog(CommonLog commonLog, DevLogAddDTO addDto, Invocation inv, Throwable throwable) {
        try {
            addDto.setEndTime(LocalDateTimeUtils.currentTimestamp());

            // 还原反射包装，取真实异常的信息
            Throwable real = unwrap(throwable);

            DevLog devLog = buildBaseLog();
            setCommonData(devLog, commonLog, addDto, inv);
            devLog.setExeStatus(DevLogExeStatusEnum.FAIL.getValue());
            devLog.setExeMessage(real.getMessage() == null ? real.getClass().getName() : real.getMessage());
            creatLogSignValue(devLog);

            // 异常场景可能已经丢了租户上下文，忽略租户拦截器直接落库
            submit(devLog, true);
        } catch (Throwable e) {
            log.warn("记录异常日志失败，已忽略。class={}, method={}",
                    inv.target().getClass().getName(), inv.method().getMethod().getName(), e);
        }
    }

    /**
     * 异步落库。只有这一步需要放到异步线程，其余采集/组装都在业务线程内完成。
     *
     * @param ignoreTenant 是否忽略租户拦截器
     */
    private static void submit(DevLog devLog, boolean ignoreTenant) {
        ThreadUtil.execute(() -> {
            try {
                if (ignoreTenant) {
                    TenantUtils.executeIgnore(() -> devLogService.add(devLog));
                } else {
                    devLogService.add(devLog);
                }
            } catch (Throwable e) {
                log.warn("操作日志落库失败，已忽略。title={}", devLog.getTitle(), e);
            }
        });
    }

    /**
     * 还原反射调用包装的真实异常。
     *
     * <p>防御性处理：Solon 的 {@code MethodWrap#invoke} 已经调用 {@code Utils.throwableUnwrap}
     * 拆掉了 {@code InvocationTargetException}，正常情况下这里拿到的就是业务异常本身。</p>
     */
    private static Throwable unwrap(Throwable e) {
        if (e instanceof InvocationTargetException && e.getCause() != null) {
            return e.getCause();
        }
        return e;
    }

    private static void setCommonData(DevLog devLog, CommonLog commonLog, DevLogAddDTO addDto, Invocation inv){
        Long loginId = addDto.getUserId();
        if (NumberUtils.isLtZero(loginId)) {
            loginId = 0L;
        }

        devLog.setCategory(DevLogCategoryEnum.OPERATE.getValue());
        devLog.setTitle(commonLog == null ? null : commonLog.value());
        devLog.setOperationType(commonLog == null ? null : commonLog.operationType().name());
        devLog.setExeStatus(DevLogExeStatusEnum.SUCCESS.getValue());
        devLog.setClassName(inv.target().getClass().getName());
        devLog.setFunName(inv.method().getMethod().getName());
        devLog.setReqMethod(inv.method().getMethod().getName());
        devLog.setReqUrl(addDto.getReqUrl());
        devLog.setReqParams(toParamsText(commonLog, inv));
        devLog.setCreateUser(loginId);
        devLog.setOperatorType(addDto.getUserType());
        if(StringUtils.isBlank(devLog.getOperatorType())){
            devLog.setOperatorType(commonLog == null ? null : commonLog.operatorType().name());
        }
        devLog.setModular(getModular(inv));
        devLog.setOperatorUserName(addDto.getUserName());
        devLog.setOperatorUserId(loginId);
        devLog.setBeginTime(addDto.getBeginTime());
        devLog.setEndTime(addDto.getEndTime());
        devLog.setDuration(addDto.getDuration());
        devLog.setTenantId(ConvertUtils.toLong(addDto.getTenantId(),0L));

    }

    /**
     * 组装请求参数文本：过滤技术性参数与排除项，敏感字段打码，最后统一截断。
     *
     * @return 参数 JSON；注解关闭了参数记录时返回 null
     */
    private static String toParamsText(CommonLog commonLog, Invocation inv) {
        if (commonLog != null && !commonLog.isSaveRequestData()) {
            return null;
        }
        try {
            Set<String> excludes = new HashSet<>(Arrays.asList(
                    commonLog == null ? new String[0] : commonLog.excludeParamNames()));

            Map<String, Object> params = new LinkedHashMap<>();
            inv.argsAsMap().forEach((name, value) -> {
                if (excludes.contains(name) || isTechnicalArg(value)) {
                    return;
                }
                params.put(name, isSensitiveName(name) ? MASK : value);
            });
            return toLogText(params);
        } catch (Throwable e) {
            log.warn("操作日志参数序列化失败，已忽略参数记录", e);
            return null;
        }
    }

    /**
     * 判断是否为「技术性参数」——序列化它们没有意义，还可能很大或失败
     */
    private static boolean isTechnicalArg(Object arg) {
        return arg instanceof Context
                || arg instanceof UploadedFile
                || arg instanceof File
                || arg instanceof InputStream
                || arg instanceof OutputStream
                || arg instanceof Reader
                || arg instanceof Writer
                || arg instanceof byte[];
    }

    /**
     * 判断参数名是否涉及敏感信息（密码、令牌等）
     */
    private static boolean isSensitiveName(String name) {
        if (name == null) {
            return false;
        }
        String lower = name.toLowerCase();
        for (String keyword : SENSITIVE_KEYWORDS) {
            if (lower.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 转成落库文本：JSON 序列化 + 超长截断；序列化失败不抛异常
     */
    private static String toLogText(Object value) {
        if (value == null) {
            return null;
        }
        String text;
        try {
            text = JsonUtils.toJsonString(value);
        } catch (Throwable e) {
            return "[序列化失败: " + e.getClass().getSimpleName() + "]";
        }
        if (text == null) {
            return null;
        }
        return text.length() > MAX_TEXT_LEN ? text.substring(0, MAX_TEXT_LEN) + "...(内容过长已截断)" : text;
    }

    /**
     * 记录登录日志
     */
    public static void executeLoginLog(String userName,Long tenantId) {
        addUserLoginOrLogOutLog(userName,tenantId,DevLogCategoryEnum.LOGIN);
    }

    /**
     * 记录登出日志
     */
    public static void executeLogoutLog(String userName,Long tenantId) {
        addUserLoginOrLogOutLog(userName,tenantId,DevLogCategoryEnum.LOGOUT);
    }

    private static void addUserLoginOrLogOutLog(String username,Long tenantId,DevLogCategoryEnum category){
        // 采集阶段（当前线程）也必须兜底：登录/登出是核心链路，不能因记日志失败而受影响
        try {
            DevLog devLog = buildBaseLog();
            ThreadUtil.execute(() -> {
                try {
                    TenantUtils.executeIgnore(()->{
                        devLog.setCategory(category.getValue());
                        devLog.setTitle(category.equals(DevLogCategoryEnum.LOGOUT)?"用户登出":"用户登录");
                        devLog.setExeStatus(DevLogExeStatusEnum.SUCCESS.getValue());
                        devLog.setBeginTime(LocalDateTimeUtils.currentTimestamp());
                        devLog.setOperatorUserName(username);
                        devLog.setTenantId(tenantId==null?0L:tenantId);
                        creatLogSignValue(devLog);
                        devLogService.add(devLog);
                    });
                } catch (Throwable e) {
                    log.warn("登录/登出日志落库失败，已忽略。category={}", category.getValue(), e);
                }
            });
        } catch (Throwable e) {
            log.warn("登录/登出日志采集失败，已忽略。category={}", category.getValue(), e);
        }
    }

    /**
     * 构建基础日志属性

     */
    private static DevLog buildBaseLog() {
        Context request = RequestUtils.getCurrentRequest();
        String ip = IpUtils.getIp(request);
        // UserAgent 解析是正则匹配，开销不小：只解析一次，浏览器与操作系统共用结果
        UserAgent userAgent = RequestUtils.parseUserAgent(request);
        Long loginId;
        try {
            loginId = StpUtil.isLogin()? StpUtil.getLoginIdAsLong():0L;
            if (NumberUtils.isLtZero(loginId)) {
                loginId = 0L;
            }
        } catch (Exception e) {
            loginId = 0L;
        }
        DevLog devLog = new DevLog();
        devLog.setOperatorIp(ip);
        devLog.setOperatorAddress(IpUtils.getCityInfo(ip));
        devLog.setOperatorBrowser(RequestUtils.getBrowser(userAgent));
        devLog.setOperatorOs(RequestUtils.getOs(userAgent));
        devLog.setCreateUser(loginId);
        return devLog;
    }

    /**
     * 构建日志完整性保护签名数据
     */
    private static void creatLogSignValue (DevLog devLog) {
        String logStr = devLog.toString().replaceAll(" +","");
        devLog.setSignData(CryptUtils.signature(logStr));
    }

    private static final String MODULAR_MARKER = ".modular.";

    /**
     * 解析当前的模块名称
     */
    public static String getModular(Invocation inv) {
        // 1. 获取类名
        String className = inv.target().getClass().getName();

        // 2. 规则一：优先按 ".modular." 定位，截取其后的模块名
        int modularIndex = className.indexOf(MODULAR_MARKER);
        if (modularIndex != -1) {
            int start = modularIndex + MODULAR_MARKER.length();
            int end = className.indexOf('.', start);
            if (end != -1) {
                return className.substring(start, end);
            }
        }

        // 3. 规则二：没有 ".modular." 时，截取第 2 个点和第 3 个点之间的内容
        int p1 = className.indexOf('.');
        if (p1 != -1) {
            int p2 = className.indexOf('.', p1 + 1);
            if (p2 != -1) {
                int p3 = className.indexOf('.', p2 + 1);
                if (p3 != -1) {
                    return className.substring(p2 + 1, p3);
                }
            }
        }

        // 4. 兜底逻辑
        return "";
    }
}
