# nuocao-data-access 租户 / 数据权限拦截审查与整改方案

> 范围：`tenant/aop/TenantAspect`、`tenant/filter/TenantFilter`、`datascope/aop/DataScopeAspect`，连带 `tenant/aop/NuoCaoTenantFactory`、`tenant/context/TenantContext`、`tenant/config/TenantConfig`、`tenant/annotation/TenantIgnore`、`datascope/context/DataScopeContext`
> 结论：`@TenantIgnore` 标在 Controller 上失效**不是写错了一个判断**，而是**放错了层**——Solon 的 `Filter` 跑在路由匹配之前，`ctx.action()` 恒为 `null`。除此之外还有 4 个必须先修的问题（线程上下文不清理、租户缺失默认全量放行、`expireTime` 为 null 时 NPE、enable 判断拆箱 NPE）。

---

## 一、根因：`Filter` 里拿不到 `Action`（Solon 4.1.1 反编译证据）

| 步骤 | 代码事实 |
| --- | --- |
| ① 请求进入 | `SolonApp.tryHandle()` → `chains.doFilter(ctx, this._handler)`。**所有 `Filter` 包在 `_handler` 之外**，`_handler`（`HandlerPipeline`）的末端才是 `RouterHandler` |
| ② 路由匹配 | `RouterHandler.handle()` 里才执行 `router.matchMainAndStatus(ctx)`，匹配成功后 `ctx.attrSet(ATTR_MAIN_HANDLER, mainHandler)` |
| ③ `ctx.action()` | 实现是 `Handler h = mainHandler(); return h instanceof Action ? (Action) h : null;` |

→ **在任何 `Filter` 里 `ctx.action()` 永远是 `null`**，所以 `hasTenantIgnoreAnnotation()` 恒返回 `false`，`TenantFilter` 第 68~73 行整块是**死代码**（注释里"得不到 action"的判断是对的，但结论不该是"注释掉"）。

### 为什么注解在 Service 上"好像能生效"

`MethodWrap` 构造函数会遍历 **方法上的注解** 和 **类上的注解**，对每个注解依次尝试：
`context.beanInterceptorGet(annotationType)` → `annotationType.getAnnotation(Around.class)`。

`@TenantIgnore` 上标了 `@Around(TenantAspect.class)`，于是**注解自带拦截器**：只要方法（或类）带了 `@TenantIgnore`，调用它时就会走 `TenantAspect`，把 `TenantContext.ignore` 置为 `true`。它只能影响"方法调用这一层"，管不到 HTTP 过滤器。

### 于是现状是"两套开关各管一半"

| 关卡 | 实现位置 | `@TenantIgnore` 能放过吗 |
| --- | --- | --- |
| 必须携带 `Tenant-Id`、登录用户租户必须一致、租户必须存在且未过期 | `TenantFilter`（Filter 层） | **不能** |
| SQL 自动追加 `tenant_id = ?` | `TenantAspect` + `NuoCaoTenantFactory`（方法层） | **能**（Service / Controller 方法级都行） |

`/dev/config/sysConfigList` 的现象就是这么来的：`dev_config` 在 `tenant.table-ignore` 里，**SQL 本来就不带租户条件**，所以数据看着没问题；但过滤器那一侧的校验照样执行 → 仍然要求 `Tenant-Id` 头、租户无效就直接返回 600。

> 补充：`@TenantIgnore` 打在**类**上（如 `SysTenantController`）同样会触发切面 —— `MethodWrap` 也会扫描 `ownerClz.getAnnotations()`。所以类上的注解在"方法层"是生效的，在"过滤器层"同样不生效。

---

## 二、严重缺陷（建议优先修）

### 1. `TenantFilter#hasTenantIgnoreAnnotation` — 死代码，注解在 Controller 上永不生效

现象与证据见上文。**改法**：把租户关卡整体迁到 `RouterInterceptor`（见第四节方案）。

### 2. `NuoCaoTenantFactory` — "没有租户上下文"被当成"忽略租户"→ 跨租户全量放行（安全红线）

```java
if (TenantContext.isIgnore() || TenantContext.getCurrentTenant() == null) {
    return new Object[0];          // ← 不加 tenant_id 条件 = 查全量
}
```

"没有租户上下文"和"显式忽略租户"是两件完全不同的事，却被合并处理。任何**绕过请求链路**的执行路径（定时任务、`@Async`/线程池、MQ 消费、服务间内部调用），只要 `ignore=false` 且 `currentTenant=null`，就会**静默地跨租户查全量数据**，而且不报错。

**改法**：默认收紧。

### 3. `TenantContext` 只写不清 —— `clear()` 全项目零调用点（grep 确认）

* `TenantFilter` 第 68~73、82~87 行：`setIgnore(true)` → `chain.doFilter()` → `setIgnore(false)`，**中间没有 `try/finally`**。下游（含控制器）抛异常时复位语句不会执行；而 `GlobalErrorFilter` 恰好在外层把异常吞掉，问题更隐蔽。
* 第 96 行 `TenantContext.setCurrentTenant(tenantId)` 之后，第 100 行（未登录）、第 105 行（租户不匹配）、第 120/125 行（租户不存在/已过期）都是**直接 `return`，不清理 `CURRENT_TENANT`**。
* 与缺陷 2 叠加：残留的 `tenantId` + 本次请求 `ignore=false` → 后续查询会被拼上**上一个请求的** `tenant_id`（线程池复用线程时必然发生）。
* `SysOrgServiceImpl:222` 用 `TenantContext.getCurrentTenant()` 拼缓存 key，残留值会命中错误的缓存。

**改法**：谁设置谁清理 —— 入口 `setIgnore(false)` 归零，`try { ... } finally { TenantContext.clear(); }` 兜住所有出口（含异常与提前 return）。

### 4. `TenantFilter` 第 125 行 —— 租户到期时间为 `null` 时 NPE

```java
if (LocalDateTimeUtils.toTimestamp(tenant.getExpireTime()) <= LocalDateTimeUtils.currentTimestamp())
```

`toTimestamp(LocalDateTime)` 内部是 `dateTime.atZone(...)`，**入参为 null 直接 NPE**，被 `GlobalErrorFilter` 包装成"服务器异常"。结果是：**没设到期时间的"永久租户"永远登录不了**。

**改法**：`tenant.getExpireTime() != null && ... <= now` 才算过期。

### 5. `TenantAspect` 第 19 行 —— `Boolean` 拆箱 NPE

`TenantConfig#tenantEnable` 是包装类型 `Boolean`，`!tenantConfig.getTenantEnable()` 在配置缺失时会 NPE；而 `TenantFilter` 第 59 行专门做了 null 判断 —— **两边不一致**（一个放行、一个崩）。

**改法**：统一用 `Boolean.TRUE.equals(...)`，或把字段改基本类型 + 注入默认值。

---

## 三、设计与健壮性（P1）

### 6. 忽略状态不是"栈式"的，过滤器用常量覆盖

`TenantFilter` 出口写死 `TenantContext.setIgnore(false)`。如果外层是 `TenantUtils.executeIgnore(...)`（定时任务里调 service），过滤器一返回就把外层状态**抹掉**了。规范做法：入口归零、出口 `clear()`（或恢复进入前的值），不要中途用常量覆盖。

### 7. `Filter` 之间的顺序靠隐式约定，异常兜底可能不在最外层

| Filter | index |
| --- | --- |
| `GlobalErrorFilter` | 无（= 0） |
| `CorsFilter` | 0 |
| `NuoCaoLanguageFilter` | 0 |
| `GlobaSFilter` | 1 |
| `TenantFilter` | 2 |

`GlobalErrorFilter` 与另外两个同为 0，谁先执行取决于 bean 扫描顺序 —— **而它必须是第一个**（否则捕获不到后面 filter 抛出的异常）。**改法**：`GlobalErrorFilter` 用负数（如 -100），其余 0 / 10 / 20 / 30 拉开。

### 8. `path-ignore` 用 `ctx.path()` 匹配，而路由匹配用 `ctx.pathNew()`

`RouterDefault.matchMainAndStatus` 用的是 `ctx.pathNew()`（已去掉 `server.contextPath`）。当前没配 `contextPath` 所以看不出问题，**一配则白名单全部失配**。**改法**：统一用 `ctx.pathNew()`。

### 9. 每个请求都查一次库校验租户

`sysTenantApi.getTenant(tenantId)` 无缓存。建议接入 `CacheUtils`（注意 `expireTime` 变更时要失效）。

### 10. 冗余与不可达代码

* `TenantFilter` 第 82 行 `if(!tenantConfig.getTenantEnable() || shouldIgnore)` —— `tenantEnable` 第 59 行已判过。
* `TenantAspect`：`if (!shouldIgnore) return inv.invoke();` **永远不会成立**（切面本身就是被注解触发的），`if (TenantContext.isIgnore()) return` 也冗余。真正语义只有"置位 + finally 还原"，保留注解判断只为可读性/防误用。
* `TenantAop` 标了 `@Deprecated`，`doIntercept` 是空壳（注释掉全部逻辑），`handleQueryWrapper` 是死方法 —— **建议直接删除**，避免后来人误用。
* `userInfo.getTenantId().equals(tenantId)` 若 `getTenantId()` 为 null 会 NPE，建议反写成 `tenantId.equals(userInfo.getTenantId())`。

### 11. `@Around` 的固有限制：同类内部 this 调用不生效

`SysTenantServiceImpl:313`、`SysUserServiceImpl:203/865`、`SysOrgServiceImpl:70/178/220` 上的 `@TenantIgnore` / `@DataScopeIgnore`，**如果调用方是同一个类的另一个方法（`this.xxx()`），切面不会触发**，注解静默失效。需要逐个确认调用来源；这类场景应改用 `TenantUtils.executeIgnore(...)` 显式包裹。

### 12. `DataScopeAspect` 与 `TenantAspect` 高度重复

两个切面结构完全一样，区别只在"注解 + Context 类型"。且：

* `DataScopeAspect` **没有 enable 开关**（`TenantAspect` 有），数据权限无法整体关闭（`@DataScope` 已 `@Deprecated`，`OrgDataScopeRule` 是"按表名白名单"常开）。
* `DataScopeContext` 同样没有 `clear()`（泄漏风险低于 `TenantContext`，因为只在切面内 set/finally 还原）。
* 建议抽一个 `AbstractIgnoreAspect<T>` 泛型基类，或至少补上开关与 `clear()`。

---

## 四、整改方案（推荐：租户关卡整体迁到 `RouterInterceptor`）

### 为什么这层是对的

反编译确认：`AppContext` 在 bean 注册时对 `raw() instanceof Filter` 和 `raw() instanceof RouterInterceptor` **分别做自动装配**，且都用 `BeanWrap.index()` 作为排序权重：

```java
if (bw.raw() instanceof Filter)            app.router().filter(bw.index(), (Filter) bw.raw());
if (bw.raw() instanceof RouterInterceptor) app.router().routerInterceptor(bw.index(), (RouterInterceptor) bw.raw());
```

而 `RouterInterceptor` 由 `RouterHandler.handle()` 在**路由匹配之后**执行（`chainManager.doRouterIntercept(...)`），此时 `ATTR_MAIN_HANDLER` 已写入 → **`ctx.action()` 有值**。项目里 `GlobalConfigure#addInterceptors` 已经用了同一套 API（手工 `router.routerInterceptor(...)`），只是没走 bean 自动装配。

### 改动清单

| # | 文件 | 动作 |
| --- | --- | --- |
| 1 | `tenant/interceptor/TenantInterceptor.java` | **新增**（由 `TenantFilter` 改造而来，逻辑等价 + 修复） |
| 2 | `tenant/filter/TenantFilter.java` | **删除** |
| 3 | `tenant/config/TenantConfig.java` | 字段类型/默认值加固 |
| 4 | `tenant/aop/NuoCaoTenantFactory.java` | 无租户上下文时默认拒绝 |
| 5 | `tenant/aop/TenantAspect.java` | 精简 + 拆箱 NPE 修复 |
| 6 | `datascope/aop/DataScopeAspect.java` | 精简（可选：抽公共基类、补开关） |
| 7 | `tenant/aop/TenantAop.java` | 删除（`@Deprecated` 空实现） |
| 8 | `core/config/handler/GlobalErrorFilter.java` | index 改 -100，确保最外层 |

### 1) 新增 `com/nuocao/data/access/tenant/interceptor/TenantInterceptor.java`

```java
package com.nuocao.data.access.tenant.interceptor;

import cn.hutool.core.util.StrUtil;
import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.auth.api.dto.SaUserLoginInfoDTO;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.utils.ConvertUtils;
import com.nuocao.common.utils.LocalDateTimeUtils;
import com.nuocao.common.utils.SolonFilterUtils;
import com.nuocao.data.access.tenant.annotation.TenantIgnore;
import com.nuocao.data.access.tenant.config.TenantConfig;
import com.nuocao.data.access.tenant.constants.TenantConstants;
import com.nuocao.data.access.tenant.context.TenantContext;
import com.nuocao.sys.api.SysTenantApi;
import com.nuocao.sys.api.dto.SysTenantDTO;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChain;
import org.smartboot.http.common.utils.AntPathMatcher;

import java.util.List;

import static com.nuocao.data.access.tenant.constants.TenantConstants.TENANT_ID_HEADER;

/**
 * 租户请求拦截。
 *
 * 与旧 TenantFilter 的关键差别：
 * 1) 由 Filter 改为 RouterInterceptor —— Filter 跑在路由匹配之前，ctx.action() 恒为 null，
 *    导致 @TenantIgnore 永远检测不到；RouterInterceptor 在路由之后，可以拿到 Action。
 * 2) 入口归零 + try/finally 清理 ThreadLocal，杜绝线程复用时的租户上下文残留。
 */
@Slf4j
@Component(index = 100)   // RouterInterceptor 链内按 index 升序；此处的 index 与 Filter 链互不相干
public class TenantInterceptor implements RouterInterceptor {

    @Inject
    private TenantConfig tenantConfig;

    @Inject
    private SysTenantApi sysTenantApi;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        // 1. 未启用多租户 / OPTIONS 预检 → 直接放行
        if (!Boolean.TRUE.equals(tenantConfig.getTenantEnable()) || SolonFilterUtils.isOptionsRequest(ctx)) {
            chain.doIntercept(ctx, mainHandler);
            return;
        }

        // 2. 没匹配到任何 handler（404/405/静态资源兜底）→ 交给 Solon，不报"请携带租户ID"
        if (mainHandler == null) {
            chain.doIntercept(ctx, mainHandler);
            return;
        }

        // 3. 请求级「忽略租户」判定：@TenantIgnore（方法/类） 或 路径白名单
        boolean ignore = hasTenantIgnoreAnnotation(ctx) || matchIgnorePath(ctx);

        // 4. 入口归零，防止线程复用残留上一次请求的状态
        TenantContext.setIgnore(false);
        try {
            if (ignore) {
                TenantContext.setIgnore(true);
                chain.doIntercept(ctx, mainHandler);
                return;
            }

            // ---- 以下为原有校验逻辑，语义保持不变 ----
            Long tenantId = ConvertUtils.toLong(ctx.header(TENANT_ID_HEADER), 0L);
            if (tenantId.compareTo(0L) <= 0) {
                log.info("[TenantInterceptor] -> 此次请求未携带租户id ip:{},method:{},uri:{}", ctx.realIp(), ctx.method(), ctx.uri());
                SolonFilterUtils.writeResult(ctx, CommonResult.error("请携带租户ID:Tenant-Id"));
                return;
            }
            // 先落上下文：后续所有 DB 访问（含 sys_tenant 查询）都在租户上下文内执行
            TenantContext.setCurrentTenant(tenantId);

            SaUserLoginInfoDTO userInfo = StpLoginUserUtils.getLoginUser();
            if (userInfo == null) {
                SolonFilterUtils.writeResult(ctx, CommonResult.error("未登录"));
                return;
            }
            if (!tenantId.equals(userInfo.getTenantId())) {
                log.info("[TenantInterceptor] -> 无该租户访问权限user_id:{},tenant_id:{} ip:{},method:{},uri:{}", userInfo.getId(), tenantId, ctx.realIp(), ctx.method(), ctx.uri());
                SolonFilterUtils.writeResult(ctx, CommonResult.error("无权访问此租户信息"));
                return;
            }
            // 系统租户直接放行
            if (TenantConstants.TENANT_SYSTEM_ID.equals(tenantId)) {
                chain.doIntercept(ctx, mainHandler);
                return;
            }
            // 租户状态校验
            SysTenantDTO tenant = sysTenantApi.getTenant(tenantId);
            if (tenant == null) {
                SolonFilterUtils.writeResult(ctx, CommonResult.error("不存在的租户"));
                return;
            }
            // expireTime 为空 = 永不过期（原来的写法会在这里 NPE）
            if (tenant.getExpireTime() != null
                    && LocalDateTimeUtils.toTimestamp(tenant.getExpireTime()) <= LocalDateTimeUtils.currentTimestamp()) {
                SolonFilterUtils.writeResult(ctx, CommonResult.error("您的账户所属单位已过期"));
                return;
            }

            chain.doIntercept(ctx, mainHandler);
        } finally {
            // 5. 无论正常、异常还是提前 return，一律归还线程上下文
            TenantContext.clear();
        }
    }

    /**
     * 方法或类上的 @TenantIgnore（只有在路由之后才能拿到 action）
     */
    private boolean hasTenantIgnoreAnnotation(Context ctx) {
        Action action = ctx.action();
        if (action == null) {
            return false;
        }
        return action.method().isAnnotationPresent(TenantIgnore.class)
                || action.controller().clz().isAnnotationPresent(TenantIgnore.class);
    }

    /**
     * 路径白名单。用 pathNew()（与 Solon 路由匹配保持同一口径，已剔除 server.contextPath）
     */
    private boolean matchIgnorePath(Context ctx) {
        List<String> patterns = tenantConfig.getIgnorePathList();
        if (patterns == null || patterns.isEmpty()) {
            return false;
        }
        String path = ctx.pathNew();
        return patterns.stream()
                .filter(StrUtil::isNotBlank)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
```

> `writeResult` 内部已 `ctx.setHandled(true)`，而 `RouterHandler.handle1` 会先判断 `ctx.getHandled()` 再调用 mainHandler，因此这里 `return` 后控制器不会被再执行。

### 2) 删除 `tenant/filter/TenantFilter.java`
全项目仅自引用，删除即可（`GlobalHandle`/`GlobaSFilter` 等无关）。

### 3) `tenant/config/TenantConfig.java`

```java
@Component
@Setter
@Getter
public class TenantConfig {
    /** 忽略租户的接口路径（Ant 风格），用 pathNew() 匹配 */
    @Inject("${tenant.path-ignore}")
    private List<String> ignorePathList;

    /** 是否启用多租户。用基本类型 + 默认值，避免包装类型拆箱 NPE */
    @Inject("${tenant.enable:false}")
    private boolean tenantEnable;
}
```

（若对 `${key:default}` 默认值语法不放心，可保留 `Boolean`，但**所有使用点统一写 `Boolean.TRUE.equals(...)`**。）

### 4) `tenant/aop/NuoCaoTenantFactory.java` —— 默认收紧

```java
@Override
public Object[] getTenantIds() {
    // 显式忽略（@TenantIgnore / 路径白名单 / TenantUtils.executeIgnore）→ 不加租户条件
    if (TenantContext.isIgnore()) {
        return new Object[0];
    }

    Long tenantId = TenantContext.getCurrentTenant();
    if (tenantId == null) {
        // 「没有租户上下文」≠「忽略租户」。定时任务/异步线程请显式使用 TenantUtils.executeIgnore(...)
        log.error("[NuoCaoTenantFactory] 无租户上下文且未显式忽略租户，已按 tenant_id={} 过滤，请检查调用链", NO_TENANT_ID);
        return new Object[]{NO_TENANT_ID};   // 常量 -1L，宁可不返回数据，也不跨租户
    }

    return new Object[]{tenantId};
}
```

两种强度可选：
* **`-1L`（推荐先上）** —— 查不到数据 + error 日志，不改变调用方行为，风险最低；
* **直接抛异常（fail-fast）** —— 安全语义最干净，但需要先扫一遍所有"请求链路之外"的查询点，确认都已显式 `executeIgnore`。

### 5) `tenant/aop/TenantAspect.java` —— 精简 + 修 NPE

```java
@Component
public class TenantAspect implements Interceptor {
    @Inject
    private TenantConfig tenantConfig;

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        // 未开启，或已被更外层忽略（请求级 @TenantIgnore / TenantUtils.executeIgnore）
        if (!Boolean.TRUE.equals(tenantConfig.getTenantEnable()) || TenantContext.isIgnore()) {
            return inv.invoke();
        }

        // 本切面是由 @TenantIgnore 自身（@Around 元注解）触发的，这里再校验一次只为防误用
        boolean annotated = inv.getMethodAnnotation(TenantIgnore.class) != null
                || inv.getTargetAnnotation(TenantIgnore.class) != null;
        if (!annotated) {
            return inv.invoke();
        }

        // save / restore：嵌套调用与异常都能正确还原
        boolean previous = TenantContext.isIgnore();
        try {
            TenantContext.setIgnore(true);
            return inv.invoke();
        } finally {
            TenantContext.setIgnore(previous);
        }
    }
}
```

### 6) `datascope/aop/DataScopeAspect.java`
按同样风格精简（注解判断 → `isIgnore` 短路 → save/restore），并建议补 `DataScopeContext.clear()` 与 `datascope.enable` 开关；有精力再把两个切面抽成公共基类。

### 7) `GlobalErrorFilter` 顺序加固

```java
@Component(index = -100)   // 必须最外层，否则捕获不到其它 Filter 抛出的异常
public class GlobalErrorFilter implements Filter { ... }
```

---

## 五、注解语义（建议写进团队规范）

| 注解位置 | 效果 |
| --- | --- |
| Controller 方法 / 类 | 跳过请求级租户校验（不必带 `Tenant-Id`）+ 本次请求 SQL 不加 `tenant_id` |
| Service 方法 / 类 | **仅**本方法调用栈内的 SQL 不加 `tenant_id`；请求级校验照旧 |
| 同类内部 this 调用 | **不生效**（Solon `@Around` 限制），改用 `TenantUtils.executeIgnore(...)` |
| 定时任务 / MQ / 异步线程等非请求链路 | 统一用 `TenantUtils.executeIgnore(...)`，不要依赖注解 |

---

## 六、验收 / 自测清单

1. **注解生效**：`GET /dev/config/sysConfigList` **不带** `Tenant-Id`、不带 token → 应正常返回数据（改造前返回 600 "请携带租户ID"）。
2. **校验未被削弱**：`GET /sys/tenant/page` 带伪造 `Tenant-Id: 999999` → 仍应返回"不存在的租户"或"无权访问此租户信息"；不带 `Tenant-Id` → 仍应返回 600。
3. **上下文不残留**：同一线程连续两次请求 —— A 带 `Tenant-Id` 正常请求，B 走白名单路径；打开 mybatis-flex SQL 日志，确认 B 的 SQL 里**没有** A 的 `tenant_id`。
4. **永久租户**：造一个 `expire_time` 为 null 的租户，登录/访问 → 不再 500，能正常使用。
5. **配置缺失**：临时注释掉 `tenant.enable` 后启动 → 不再因拆箱 NPE 启动失败/请求 500（按"未启用"放行）。
6. **404 行为**：访问一个不存在的路径 → 返回 404，而不是"请携带租户ID"。
7. 在拦截器内临时 `log.debug("action={}, ignore={}", action, ignore)`，确认 `action` 非空（这是本次根因的直接证据）。

---

## 七、顺手建议

* `app.yml` 的 `path-ignore` 里已有 `/dev/config/sysBaseList`，可临时把 `/dev/config/sysConfigList` 也加进去止血；但根治仍是修注解（按路径配制是"数据"层面的开关，注解才是"代码"层面的意图）。
* `/dev/config/sysConfigList` 目前**没有** `@SaCheckPermission`，即设计上是匿名接口。改造后它会真正变成匿名可访问，请确认这是预期（登录页取配置）——如果不是，需要补权限注解。
* `TenantAop`、`DataScope`（`@Deprecated`）这两个历史遗留建议一并清理。

---

## 八、修复实施记录（2026-10-13）与对前文的两处纠正

### 8.1 已落地的改动

| # | 文件 | 动作 |
| --- | --- | --- |
| 1 | `tenant/interceptor/TenantInterceptor.java` | **新增**（`@Component(index = -200)`，见 8.2 纠正） |
| 2 | `tenant/filter/TenantFilter.java` | **删除** |
| 3 | `tenant/aop/TenantAop.java` | **删除**（`@Deprecated` 空实现） |
| 4 | `tenant/config/TenantConfig.java` | 两个配置项改 `required = false` + 字段默认值，新增空安全 `isEnabled()` |
| 5 | `tenant/aop/NuoCaoTenantFactory.java` | 无租户上下文 → `tenant_id = -1` 兜底 + error 日志（不再静默全量） |
| 6 | `tenant/aop/TenantAspect.java` | 精简 + `isEnabled()` 修拆箱 NPE |
| 7 | `datascope/aop/DataScopeAspect.java` | 精简 + 防误用判断 |
| 8 | `datascope/context/DataScopeContext.java` | 补 `clear()` |
| 9 | `core/config/handler/GlobalErrorFilter.java` | `index = -100` 保证最外层；删除 `String str="aaa"` 调试残留 |
| 10 | `SysUserServiceImpl` / `SysUserController` / `SysAccountServiceImpl` | 5 处 `getTenantEnable()` → `isEnabled()` |
| 11 | `sys/core/event/RolePermissionUpdateEvent.java` | 构造时捕获 tenantId，异步执行时用 `TenantUtils` 重建上下文（见 8.3） |

### 8.2 纠正一：`TenantInterceptor` 的 index 必须是 **-200**，不是 100

前文（第四节）写的是 `@Component(index = 100)`，**这是错的**，会让整轮改造失效：

* `SaTokenConfigure#saTokenInit()` 用 `@Bean(index = -100)` 注册 `SaTokenInterceptor`，而它**也实现 `RouterInterceptor`**，所以它就在同一条路由拦截器链上，位置是 -100。
* 反编译 `SaTokenInterceptor.doIntercept` 确认：它在 `chain.doIntercept(...)` **之前**就完成鉴权（含注解鉴权 `authAnno(action)`）。
* 而注解鉴权会走 `StpInterfaceImpl#getPermissionList` → `SysUserPermissionServiceImpl#getUserHaveMenuCodeList` → `sysUserService.getById(...)`，读的是 `sys_user`（**带 `@Column(tenantId = true)`**）。
* 于是：若本拦截器排在 Sa-Token 之后，这次查询就落在"无租户上下文"上，配合新的默认收紧策略会**查不到用户 → 报"用户不存在" → 所有 `@SaCheckPermission` 接口全挂**。

**结论：`TenantInterceptor` 必须排在 `SaTokenInterceptor` 之前（index < -100），取 -200。** 这与旧 `TenantFilter`（Filter 链，天然早于所有 `RouterInterceptor`）的执行次序一致。

### 8.3 纠正二：`tenant.table-ignore` 是**无效配置**；`-1L` 还会影响 insert

* `tenant.table-ignore` 全项目**只有已删除的 `TenantAop` 引用过**，而它本身是空实现 —— 该配置一直没生效。
* flex 在多租户上的真实判据是**实体字段上的 `@Column(tenantId = true)`**（`TableInfo#tenantIdColumn`）；`DevConfig` 没有这个标记，所以 `dev_config` 的 SQL 本来就不带 `tenant_id`。前文把它归因为 `table-ignore` 属于表述错误，现象结论不变。
* `TableInfo#initTenantIdIfNecessary` 会在 **insert** 时，如果实体的租户字段为空就用 `TenantFactory` 的返回值回填 —— 即 `-1L` 不仅影响查询条件，也可能写进数据。据此逐个核查了"非请求链路"的 DB 写入：
  * `DevLogUtils.submit(...)`（`ThreadUtil.execute` 异步落库）：`setCommonData` 已显式 `setTenantId(...)`，`initTenantIdIfNecessary` 会提前返回，**不受影响**；
  * `RolePermissionUpdateEvent#execute`（异步 **查询** `sys_user_identity`）：**受影响**，已按 8.1 第 11 项修复；
  * `LockUtils` 的线程池代码已注释、`TencentFileStorageClientImpl` 线程池只做网络传输、SSE 心跳不碰库 —— 均不受影响；
  * 项目目前**没有定时任务**（`@Scheduled` / Quartz / XxlJob 全无）。

### 8.4 遗留：data-scope 整套规则**当前没有被装配**（因此 `@DataScopeIgnore` 无实际作用）

`OrgDataScopeRule` / `OrgDataScopeDialectImpl` 全项目**没有任何地方 new 或注册**（没有 `OrgDataScopeRuleCustomizer` 实现，也没有 `FlexGlobalConfig#setDialect`），`DataScopeRuleFactory` 也无实现类。所以：

* `DataScopeContext.isIgnore()` 的两个读取点（`OrgDataScopeRule#getQueryCondition`、`OrgDataScopeDialectImpl#prepareAuth`）都不可达；
* `@DataScopeIgnore` 目前**对 SQL 没有任何影响**；`DataScopeAspect` / `DataScopeUtils` 只是把状态写进 ThreadLocal；
* 前文第三节第 11 条里那些 `@DataScopeIgnore` 的同类内部 `this.` 调用（`validInfo` 223/256、`selectUserListByIds` 811）**当前无实际影响**，暂未改动。

要真正启用数据权限，需要补一个 `OrgDataScopeRuleCustomizer` 实现（配置哪些表按 `org_id` / `create_user` 过滤）并把 `OrgDataScopeDialectImpl` 装到 flex 的 dialect 上。

### 8.5 遗留：租户注解的同类内部调用（已逐个确认）

| 调用点 | 注解方法 | 是否跨 bean |
| --- | --- | --- |
| `SysUserServiceImpl:341`（`updatePassword`）、`:480`（`updatePasswordByOld`） | `syncPasswordByUsername` `@TenantIgnore` | **否，`this.` 直调 → 切面不触发** |
| `SysUserApi.syncPassword` → `SysUserApiProvider` | 同上 | 是（走代理，切面触发） |
| `SysTenantServiceImpl:320` → `sysUserService.selectListByUsernameIgnoreTenant` | `@TenantIgnore` | 是 |
| `SysUserAuthLoginProvider:76` → 同上 | `@TenantIgnore` | 是 |

即：`syncPasswordByUsername` 的"忽略租户"只在**外部调用**时生效，内部两处直调仍是**带租户条件更新**。本次**未改动**——改成 `TenantUtils.executeIgnore(...)` 会让这两处变成"按用户名跨租户更新密码"，属于**扩大跨租户写入**的语义变更，需业务确认后再动。

### 8.6 建议的回归验证

除第六节清单外，重点补两条：

1. **鉴权链路**（对应 8.2）：登录后访问任意带 `@SaCheckPermission` 的接口 → 必须正常（若 index 取错，这里会报"用户不存在"）。
2. **异步事件**（对应 8.3）：改一次角色权限 → 观察日志无 `[NuoCaoTenantFactory] 无租户上下文...`，且被授权用户的菜单缓存确实被清掉（重新拉一次权限即可看到变化）。


## 九、登录鉴权核对（2026-10-14 对运行实例实测）

### 9.1 结论：`/dev/config/sysConfigList` **并没有**被放开

对正在运行的实例（IDEA 启动的 `com.nuocao.Application`，`server.port: 8098`）做**无任何凭证**探测：

| 请求（无凭证） | 实际响应 |
| --- | --- |
| `GET /dev/config/sysConfigList` | `{"code":500,"msg":"该账号无权限：未能读取到有效 token"}` |
| `GET /dev/config/page` | `{"code":500,"msg":"请携带租户ID:Tenant-Id"}` |
| `GET /sys/user/page` | `{"code":500,"msg":"请携带租户ID:Tenant-Id"}` |
| `GET /dev/config/sysBaseList` | `{"code":500,"msg":"该账号无权限：未能读取到有效 token"}` |
| `GET /` | `200 Welcome to NuoCao,version:1.0.0` |

`sysConfigList` 之所以从"请携带租户ID"变成"未能读取到有效 token"，正是 `@TenantIgnore` 生效后**越过租户关卡、落到 sa-token 关卡**的结果 —— 反过来说明第八节的 index 调整正确：`TenantInterceptor(-200)` 确实先于 `SaTokenInterceptor(-100)` 执行。

另用一个最小 Solon 4.1.1 容器（只放 `SaTokenConfigure#saTokenInit` 的等价代码 + 三个测试 Controller）复现同一套配置：无注解接口 → 401，`@SaCheckPermission` 接口 → 401，`@SaIgnore` 方法 → 200。**说明该 `setAuth` 全局规则本身有效，不需要改。**

### 9.2 为什么会产生"不登录也能访问"的错觉

1. **token 名是 `token`，不是 `satoken`。** `AuthConfigure#getStpLogic()` 注册的是 `new StpLogic("admin")`，运行时实际读写的凭证名是 `token`。用假值实测：

   | 传参方式 | 响应 |
   | --- | --- |
   | `-H "token: fake123"` | `token 无效：fake123`（进入校验） |
   | `-b "token=fake123"` | `token 无效：fake123`（Cookie 通道生效） |
   | `?token=fake123` | `token 无效：fake123`（参数通道生效） |
   | `-H "satoken: fake123"` / `-H "Authorization: fake123"` | 完全不识别，仍报 `未能读取到有效 token` |

2. **sa-token 默认 `isReadCookie = true`**（`SaTokenConfig` 默认值，本项目未覆盖），登录时会把 token 写进名为 `token` 的 Cookie；浏览器对同域请求会自动携带。于是"直接在地址栏敲 URL 也能拿到数据"，本质是**带着 Cookie 的已登录状态**。要复现"未登录"，请用无痕窗口 / 清掉该站点 Cookie / 用 `curl` 不带任何凭证。
3. 前端把 `AuthLoginResult.token` 存在本地并作为 `token` 头回传。退出登录走 `AuthController#logout → StpUtil.logout()`，服务端会作废 token；若前端未清本地缓存，刷新后继续带旧 token，表现为"退了还能用"（此时服务端会返回 `token 无效`）。

### 9.3 顺带发现：`/dev/config/sysBaseList` 的定位前后不一致（待你决策）

`app.yml` 的 `tenant.path-ignore` 里放了 `/dev/config/sysBaseList`，说明作者把它当作"登录前可访问的接口"；但它**不在** `SaTokenConfigure.NO_LOGIN_PATH_ARR` 中，实测无凭证访问同样是 `未能读取到有效 token`。若登录页/欢迎页确实需要匿名读取，两点需要显式声明：

```java
// 方式一：注解（推荐，意图直接写在代码上）
@Get
@Mapping("/dev/config/sysConfigList")
@TenantIgnore      // 跳过租户校验
@SaIgnore          // 跳过 sa-token 校验（整段全局 setAuth 一并跳过 = 完全公开）
public CommonResult<List<DevConfigKeyValueResult>> sysConfigList() {
    return CommonResult.data(devConfigService.sysConfigList());
}

// 方式二：路径白名单（SaTokenConfigure.NO_LOGIN_PATH_ARR 追加）
"/dev/config/sysConfigList",
```

⚠️ `@SaIgnore` 会让 `SaTokenInterceptor#authAnno()` 返回 `false`，进而**整段全局 `setAuth` 都不执行**；既然要公开，就不要再叠 `@SaCheckPermission`，避免"看着有权限校验其实没有"。

### 9.4 本次实测用到的脚手架（离线可复用）

```bash
# 1) 修 13 个构件目录的 _remote.repositories（>central= → >alimaven=）后可离线解析
mvn -o org.apache.maven.plugins:maven-dependency-plugin:3.1.2:build-classpath \
    -pl nuocao-server -Dmdep.includeScope=runtime -Dmdep.outputFile=target/cp.txt

# 2) 启动真实实例（工作目录必须是仓库根，nuocao.ip2region.* 是相对路径）
java -Dfile.encoding=UTF-8 -cp "<各模块 target/classes>;nuocao-server/target/cp.txt" com.nuocao.Application

# 3) 探测（必须绕过本机 60838 代理，否则 127.0.0.1 会被代理转成 502）
curl -s --noproxy '*' "http://127.0.0.1:8098/dev/config/sysConfigList"
```

## 十、会话凭证到底存在哪（2026-10-14 对 Redis + 最小容器实测）

### 10.1 结论先行
1. **Redis 里确实有活跃会话** —— 它不是"缓存"，而是鉴权的真身：服务端只认 token 字符串，不认浏览器。
2. **"换个浏览器就被拦"是正常现象**，恰好证明鉴权在工作；原浏览器"不用登录也能访问"，是因为它一直在自动带 Cookie。

### 10.2 服务端：Redis db0 实测

`SaTokenConfigure#saTokenDaoInit` 用 `new SaTokenDaoForRedisson(redissonClient)` 接管了 `SaTokenDao`，所以会话全部落 Redis。实测 db0：

```
token:admin:token:z0V0ICRz…（32位随机）                              TTL 2458852s ≈ 28.5 天
token:admin:token-session:z0V0ICRz…                                  TTL 同上
token:admin:session:1                                                TTL 同上  ← loginId=1，createTime 2026-10-05 16:13:56
```

- key 前缀是 `{tokenName}:{loginType}:` = `token:admin:`，**不是** `admin:login:` —— 排查时别用错 pattern（我一开始就扫错了）。
- `SaSession` 内 `historyTerminalCount: 9`，即该账号历史上有 9 次终端登录记录。
- **实测**：只把这个 token 值放进请求头（完全不带 Cookie）→ `/dev/config/sysConfigList` 直接返回真实数据；不带则 `未能读取到有效 token`。⇒ **token 与浏览器/设备无绑定，谁持有这个字符串谁就是"已登录"。**

### 10.3 客户端：浏览器侧的凭证从哪来（最小容器实测 Set-Cookie）

`app.yml` 的 sa-token 段只显式设了 `token-name: token`、`timeout: 2592000`、`active-timeout: -1`、`is-concurrent: true`、`is-share: true`、`max-login-count: -1`、`token-style: random-32`，**没有覆盖任何 cookie 配置**，因此走 sa-token 默认值：

| 配置项 | 默认值 | 后果 |
|---|---|---|
| `isReadCookie` | `true` | 登录时会下发 Cookie |
| `isWriteHeader` | `false` | **不写响应头**，前端必须从 body 取 token |
| `cookie.httpOnly` | `false` | `document.cookie` 能直接读到 token |
| `cookie.secure` | `false` | HTTP 也照发 |
| `cookie.sameSite` | `null`（未声明） | 浏览器按 `Lax` 处理 → 顶级 GET 导航也携带 |

实测登录响应头（最小容器，等价配置）：

```
Set-Cookie: <tokenName>=<32位随机>; Max-Age=2592000; Expires=<30天后>; Path=/
```

（`SaCookieConfig.path` 本身为 `null`，是 Solon 的 `addCookie` 兜底补成 `/`）

⇒ 它是一个 **Path=/、30 天有效的持久 Cookie**。所以关闭浏览器再打开、甚至在地址栏直接敲 URL，请求都会自动带上它。

### 10.4 为什么会被误判成"接口放开了"

- Cookie 由浏览器底层自动发送，**不需要前端 JS 参与**，所以地址栏直接访问也带。
- 若只靠前端本地存的 token + axios 手动塞 header，地址栏直接访问**不会**带 → 会被拦。所以"敲 URL 也能看到数据"恰好证明走的是 Cookie 通道。
- Vue 端登录成功后 axios 还会另外在 header 里传 token，两条通道并存，进一步加深"没登录也能访问"的错觉。

### 10.5 自查三步

1. F12 → Application → Cookies → `http://localhost:8098` → 应能看到 `token`：`Expires` 在 30 天后、`Path=/`、`HttpOnly` 未勾选。
2. Console 执行 `document.cookie` → 能直接看到 `token=...`（因为 httpOnly=false）。
3. 无痕窗口打开同一地址 → 被拦；或在原浏览器 logout 之后再敲 URL → 也被拦。

### 10.6 安全建议（本次未改代码）

| 项 | 现状 | 建议 |
|---|---|---|
| `sa-token.cookie.http-only` | 未设（false） | 设 `true`；否则任何 XSS 都能直接窃取一个 30 天有效的 token |
| `sa-token.cookie.secure` | 未设（false） | 生产 HTTPS 下设 `true` |
| `sa-token.cookie.same-site` | 未声明（浏览器按 Lax） | 显式声明；若前端是独立域名部署，Strict 会导致 Cookie 不发送，需评估 |
| `sa-token.is-read-cookie` | 未设（true） | 前端若已统一用 header 传 token，可关掉以消除 CSRF 面（会让现有浏览器用户立即掉线） |
| `sa-token.timeout` | 2592000（30 天） | 偏长，且 `active-timeout=-1` 不做闲置过期，建议按业务缩短 |

⚠️ 改 cookie 名或属性会让现有登录态失效（浏览器持旧 Cookie、服务端读新名），需在低峰期或与前端方案切换一起做。

### 10.7 主动清理

`DEL token:admin:*`（按登录类型精确删）可强制所有端下线。当前实例上那个 2026-10-05 创建的会话，若确认不是己方人员，建议清掉并让相关账号改密。


---

## 十一、`@PublicMapping` 注解白名单为什么失效（2026-10-14 修复 + 实机验证）

### 11.1 症状

`DevConfigController#sysConfigList` 上标了 `@PublicMapping`，但未登录访问仍被拦：

```java
@ApiOperation("获取系统基础配置")
@Get
@Mapping("/dev/config/sysConfigList")
@PublicMapping
@TenantIgnore
public CommonResult<List<DevConfigKeyValueResult>> sysConfigList() { ... }
```

### 11.2 根因（三层，第一层是致命的）

**① 只检查了「类」级别的注解，而注解打在「方法」上。**

```java
context.beanForeach(beanWrap -> {
    Class<?> clazz = beanWrap.clz();
    if (clazz.isAnnotationPresent(PublicMapping.class)) {   // ← 只看类
        ...
    }
});
```

`DevConfigController` 的类上没有 `@PublicMapping`（只有方法上有），所以 `publicEndpoints` **恒为空集**，
`.notMatch(空集合)` 什么也放不开 → 请求照旧走到 `StpUtil.checkLogin()`。
这一条本身就足以解释全部现象。

**② 即使注解真打在类上，收集到的「类前缀」也匹配不上最终 URI。**

`getControllerPath(clazz)` 取的是类上 `@Mapping` 的 value（如 `/dev/config`），但实际请求 URI 是
`/dev/config/sysConfigList`。实测 Solon `PathMatcher` 是**精确匹配**：

| pattern | path | 结果 |
|---|---|---|
| `/dev/config` | `/dev/config/sysConfigList` | ❌ false |
| `/dev/config/**` | `/dev/config/sysConfigList` | ✅ true |
| `/dev/config/sysConfigList` | `/dev/config/sysConfigList` | ✅ true |
| `/sys/user/{id}` | `/sys/user/123` | ✅ true（支持路径参数） |

而 `SaRouter.isMatch()` 走的正是 `SaStrategy.instance.routeMatcher` —— 项目里被 sa-token 的 solon 插件
（`SaBeanRegister`）换成了 Solon `PathMatcher`，所以上表结论直接适用。**不存在"前缀自动匹配子路径"这回事。**

**③ 每次请求都全量扫描 bean。** `getMappingPublicUrlsFromAnnotations()` 是在 `setAuth(r -> {...})` 的
lambda **里面**调用的，等于每个请求都 `context.beanForeach` 遍历一次全部 bean。

### 11.3 修复方案

把数据来源从「Controller 类注解 + 类前缀」换成 **Solon 路由表**：

```java
Collection<Routing<Handler>> routings = Solon.app().router().findAll();
for (Routing<Handler> routing : routings) {
    for (VersionedTarget<Handler> vt : routing.targets()) {
        Handler handler = vt.getTarget();
        if (handler instanceof Action && isPublicAction((Action) handler)) {
            urls.add(routing.path());   // path() 已是「类前缀 + 方法路径」拼好的最终 URI
            break;
        }
    }
}
```

`isPublicAction` 同时看方法级与类级：

```java
action.method().isAnnotationPresent(PublicMapping.class)
        || action.controller().clz().isAnnotationPresent(PublicMapping.class);
```

实机确认路由表里的内容是符合预期的（自建最小工程 dump）：

```
path=/dev/config/sysConfigList  method=GET   target=ActionDefault  methodHasPub=true
path=/clz/one                   method=GET   target=ActionDefault  methodHasPub=true   ← 类前缀 /clz + 方法 one
path=/sys/user/{id}             method=GET   target=ActionDefault  methodHasPub=true   ← 路径参数原样保留
```

三点收益：**（a）** 天然拿到最终 URI，不再受「前缀 vs 全路径」困扰；
**（b）** 支持路径参数、父类 Controller、`@Mapping(path=...)` 等所有 Solon 路由形态；
**（c）** 扫描结果**加锁缓存**（`volatile` + 双检），只在首次请求扫一次，消除每请求全量遍历。

> 注：`nuocao-server` 模块 `source level = 11`，**不能用 `instanceof` 模式匹配**（那是 Java 16+ 语法），
> 必须写 `handler instanceof Action && isPublicAction((Action) handler)`。

### 11.4 实机验证（独立实例 8099 端口，避开正在运行的 8098）

| 请求 | 结果 |
|---|---|
| 无凭证 `GET /dev/config/sysConfigList` | ✅ **`code:200` + 真实数据**（`sys_title=糯草管理系统` …） |
| 无凭证 `GET /dev/config/page` | ❌ `请携带租户ID:Tenant-Id` |
| `Tenant-Id: 1` 无 token `GET /dev/config/page` | ❌ `Token-Session 获取失败：token 为空` |
| `Tenant-Id: 1` 无 token `GET /dev/config/list` | ❌ 同上（未被误放开） |
| 无凭证 `GET /sys/user/page` | ❌ `请携带租户ID:Tenant-Id` |
| `GET /doc.html` | ✅ 200（静态白名单不受影响） |

启动日志确认扫描范围精确，**没有误放开**：

```
[SaToken] PublicMapping 扫描到 1 个公开接口: [/dev/config/sysConfigList]
```

**踩坑提醒：** 用「各模块 `target/classes` + `nuocao-server/target/cp.txt`」这种手工 classpath 跑实例时，
`cp.txt` 里含**本地 m2 里 10-11 的旧 `nuocao-data-access-1.0.0.jar`**，而那个 jar 里还带着**已删除的
`TenantFilter.class` / `TenantAop.class`**。它会作为 `Filter` 被自动装配，抢在路由拦截器之前把
`sysConfigList` 拦成 `请携带租户ID`，导致误判"`@TenantIgnore` 没生效"。清理方式：

```bash
tr ';' '\n' < nuocao-server/target/cp.txt | grep -v 'com\\nuocao' | tr '\n' ';' > nuocao-server/target/cp-nolocal.txt
# 用 target/classes + cp-nolocal.txt 启动
```

### 11.5 顺带发现（未改动）

* `TenantInterceptor:101` 的 `SaUserLoginInfoDTO userInfo = StpLoginUserUtils.getLoginUser();`
  后面那句 `if (userInfo == null) → "未登录"` 是**死分支**：`getLoginUser()` 实现是
  `StpUtil.getTokenSession().get("loginUser")`，**无 token 时直接抛** `SaTokenException`
  （实测响应 `{"code":11073,"msg":"Token-Session 获取失败：token 为空"}`），不会返回 null。
  安全上无影响（照样拦住），但对外错误语义应该是「未登录」。属旧 `TenantFilter` 就有的问题，本次未动。
* 如果要更省事，sa-token 自带的 `@SaIgnore` 效果完全等价（`SaTokenInterceptor#authAnno` 命中它时
  会整段跳过全局 `setAuth`），零代码、零扫描。`@PublicMapping` 的价值在于语义自持，二选一即可，别叠加。

### 11.6 改动文件

* `nuocao-server/src/main/java/com/nuocao/core/config/SaTokenConfigure.java`（重写注解白名单扫描 + 缓存）

---

## 十二、白名单的计算时机：从「首次请求懒加载」改为「启动期一次性算好」（2026-10-14）

### 12.1 问题：`notMatch(...)` 的参数是**每个请求**都要求值的

直觉上 `SaRouter.match("/**").notMatch(whiteList).check(...)` 是「配置一次、之后复用」，但 sa-token 的 API 不是这个语义：

* `setAuth(SaParamFunction auth)` 注册的 lambda 是**每个请求执行一次**的回调（`SaTokenInterceptor` 每请求 `auth.run(handler)`）。
* lambda 体内的 `notMatch(getMappingPublicUrlsFromAnnotations())`，**参数表达式在 lambda 执行时才求值** ⇒ 每请求都会调用一次该方法。
* `SaRouter.match(...)` 返回的 `SaRouterStaff` 是**基于 ThreadLocal 的每请求临时对象**，match / notMatch / check 这条链**无法**在启动期预构建后复用 —— 只能在请求线程里现搭。

所以十一节的 `volatile + 双检` 只解决了「不要每请求全量扫路由表」，**每请求仍会**：一次 `volatile` 读 + 一次方法调用，外加 `CollectionUtil.newArrayList(NO_LOGIN_PATH_ARR)` 每请求 new 一个 ArrayList。

### 12.2 改法：把「求值」挪到启动期，lambda 只引用字段

```java
/** 静态白名单 + @PublicMapping 扫描结果，启动期合并算好 */
private volatile List<String> anonUrls = Collections.emptyList();

@Init
public void initAnonUrls() {
    Set<String> urls = new LinkedHashSet<>(Arrays.asList(NO_LOGIN_PATH_ARR));
    try {
        urls.addAll(scanPublicUrlsFromAnnotations());
    } catch (Exception e) {
        log.error("[SaToken] 扫描 {} 失败，本次仅使用静态白名单", PublicMapping.class.getSimpleName(), e);
    }
    this.anonUrls = Collections.unmodifiableList(new ArrayList<>(urls));
    log.info("[SaToken] 匿名放行 URI（{} 个）: {}", anonUrls.size(), anonUrls);
}

.setAuth(r -> SaRouter.match("/**").notMatch(anonUrls).check(r1 -> StpUtil.checkLogin()))
```

五个要点：

1. **必须是同步 `@Init`**：`@Init` 的 rank = 1 < server listen 的 99，保证首个请求进来时字段已就绪；标 `async = true` 会被挪到监听之后，保证失效。
2. **不能放进 `saTokenInit()` 这个 `@Bean` 方法里**：`@Bean` 执行于 `beanScan` 期间，与 Controller 路由注册的先后顺序**不确定**，可能扫不全。`@Init` 执行于 `AppContext.start()`，晚于 `beanScan`，路由表必然完整。
3. **`anonUrls` 用 `volatile`**：`@Init` 写在启动线程、请求线程读，保证跨线程可见性。
4. **两个列表合并成一份**：原来 `notMatch(静态白名单)` + `notMatch(注解 URI)` 是两次列表遍历 + 一次 ArrayList 分配，现在一次搞定。
5. **扫描异常降级**：吞掉异常退回静态白名单；失败方向是「多要一次登录」，不会误放行 —— 符合项目「旁路能力不影响主链路」的约定。

### 12.3 为什么**不能**改用 `addExclude(...)`（看似更省）

`SaTokenInterceptor` 的结构是 `if (SaRouter.match(include).notMatch(exclude).isHit()) { runBeforeAuth(); runAuth(); }`
—— 命中 `exclude` 时 **`setBeforeAuth` 也一并被跳过**。而 `setBeforeAuth` 里在设 CORS 响应头（`Access-Control-Allow-Origin` 等）以及 OPTIONS 预检的提前返回。把公开接口塞进 `exclude`，这些接口就**不再返回 CORS 头**，前端跨域直接挂。所以只能留在 `setAuth` 的 `notMatch` 里。

### 12.4 验证（独立实例 8099）

启动日志（线程名 `-main`，证明是启动期而非首次请求时执行）：

```
INFO 2026-10-14 11:59:13.056 [-main][*][c.n.c.c.SaTokenConfigure]:
[SaToken] 匿名放行 URI（16 个）: [/, /swagger-resources/**, ..., /auth/account/**, /dev/config/sysConfigList]
```

| 请求 | 结果 |
|---|---|
| 无凭证 `GET /dev/config/sysConfigList`（`@PublicMapping`） | ✅ `code:200` + 真实数据 |
| 无凭证 `GET /dev/config/page` | ❌ `请携带租户ID:Tenant-Id` |
| 无凭证 `GET /sys/user/page` | ❌ `请携带租户ID:Tenant-Id` |

行为与十一节完全一致，但每请求的开销从「读缓存 + 方法调用 + new ArrayList」降到「读一个已算好的不可变列表」。

---

## 十三、通用约定速查（构建 / 模块分层 / Solon 生命周期与注入）

> 本节归档「跨会话需长期保留、但主题不属于租户/数据权限」的结论，供 `.workbuddy/memory/MEMORY.md` 瘦身后转引。

### 13.1 技术栈与构建

- Solon **4.1.1**（`solon-server-smarthttp`）、JDK17、MyBatis-Flex 1.11.8、Sa-Token 1.46.0、Hutool、ip2region 3.3.7。
- `JAVA_HOME=/e/dev_run_env/java/graalvm-community-openjdk-17.0.9+9.1`；mvn = `/e/dev_run_env/apache-maven-3.9.2/bin/mvn`（**不在 PATH**）。
- 离线报 `present but unavailable` → 把本地仓库该构件目录下 `_remote.repositories` 的 `>central=` 改成 `>alimaven=`。
- ⚠️ **各模块 source level 不一致**：`nuocao-server` = **11**（`instanceof` 模式匹配、`record` 等 Java 16+ 语法会编译失败）；`nuocao-data-access` 宽松些。改动前不确定就写传统 cast。

### 13.2 项目分层约定

- **后缀语义**：`*Config` = 纯参数（`@Inject("${...}")` + 字段 + getter/setter，零逻辑）；`*Configure` = 装配 / 注册 / 初始化（`@Bean` 方法 和/或 `@Init`）。参照实现：`CacheConfig`（参数侧）、`GlobalConfigure` / `ValidationConfigure`（装配侧）。
- **旁路能力（日志、IP 归属地等）绝不影响主链路**：内部异常必须吞掉，未就绪时返回降级值而非抛异常。参见 `IpUtils`（未就绪返回「未知」）、`DevLogUtils`。
- 大体积静态数据（如 ip2region 的 v4 11MB + v6 37MB xdb）**不进 jar**，放仓库根 `data/ip2region/`（已入 git），由 `nuocao.ip2region.*` 配路径，生产用绝对路径覆盖。
- **外部文件路径判断用 JDK 的平台感知 API**（`File#isAbsolute()` / `Path#isAbsolute()`），不要自己按「开头是不是斜杠」猜。Windows 认 `D:\x`、`D:/x`、UNC、以及 `/x`（=当前盘符根），`D:x` 不算；Linux 只认 `/x`。跨平台部署的路径配置必须随环境覆盖。
- **参数 / 装配的分层判据**：看这个能力是否是 **common 自己定义并交付、且只有唯一实现的闭环**。是 → 装配器跟能力一起留 common（`ValidationConfigure`、`NuocaoIp2RegionConfigure` = 同类先例）；否（应用层要在多个方案间选型）→ 装配器放 `nuocao-server/core/config`（`RedisClientConfigure` 在多种 Redis 客户端间选型 = 同类先例）。**不要用「是否依赖外部文件」当判据**。「common 被迫装配」这类顾虑用**配置开关**解决（如 `nuocao.ip2region.enable=false`），不要靠挪模块位置来隔离。

### 13.3 Solon 生命周期与注入补充（反编译 4.1.1 确认）

- `SolonApp.run()`：插件 init → 插件 start → `beanImportTry` → `beanScan` → `AppContext.start()` → `AppLoadEndEvent`。
- `AppContext.start()` 按 `RankEntity.index` **升序**执行所有 `lifecycleBeans` 的 `start()`：`@Init` 的 rank = `@Init.index() + 1`（默认 **1**）；HTTP server 真正 listen（`SmHttpPlugin` → `SmHttpServerComb.start(host,port)`）= **99**。⇒ **同步 `@Init` 一定早于端口对外服务**。
- ⚠️ **唯一破例**：`@Init(async = true)` 会被丢进 `executeAsyncInitTasks` 延后执行（发生在端口监听**之后**）。凡有「启动即必须就绪」语义的 `@Init`，**禁止加 `async = true`**。
- **`@Init` 方法必须无参**：`BeanWrapLifecycle.start()` 调用时传 `new Object[0]`，写参数会在运行时抛 `IllegalArgumentException`。要拿配置只能用 `@Inject` 字段或 `Solon.context().getBean(...)`。
- **`@Bean` 方法支持参数注入**（如 `@Bean public RedissonClient redissonClient(@Inject("${nuocao.redis}") NuoCaoRedisConfig cfg)`）。
- **`@Inject` 字段一定早于 `@Init` 完成**（`beanMake` 里先 `beanInject(obj)` 再 `beanRegister`，而 `@Init` 要等到 `AppContext.start()`）⇒ `@Init` 里直接用 `@Inject` 字段是安全的，不需要 null 兜底。
- ⚠️ **裸 `@Bean`（不写名字）拿不到方法名，bean 名为空**：`tryBuildBeanOfMethod3` 里名字取 `Utils.valueOr(anno.value(), anno.name())`，两者皆空就得到 `""`；`BeanWrap.nameSet("")` 之后 `BeanWrap.name()` 恒为 `""`（**没有**回退成方法名），随后 `BeanContainer.beanRegister` 见名字为空改按类名注册。后果：① **同类型多个裸 `@Bean` 互相覆盖**（都落在同一个类名 key 上）；② **一切「按 bean 名查找」的框架能力失效** —— 典型是 knife4j（`OpenApi2Utils.getApiGroupResourceJson` 用 `beanWrap.name()` 做分组 id 并过滤 `isNotEmpty(name)`），所以 `DocDocket` 的 `@Bean` **必须显式写名字**（`@Bean("sysDocApi")`），否则分组全被过滤、doc.html 空白。返回 `void` 的裸 `@Bean`（纯启动副作用，如 `ValidationConfigure#adapter`）不受影响。
- knife4j 文档分组：分组 id = `DocDocket` 的 bean 名（URL 形如 `/swagger/v2?group=<beanName>`），页面显示名 = `.groupName(...)`。四个模块固定为 `sysDocApi` / `authDocApi` / `devDocApi` / `genDocApi`，改动会变更前端 URL。
