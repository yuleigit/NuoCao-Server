# nuocao 项目长期笔记

> 本文件有大小上限，只放**最高频结论**；详细证据与实测数据见仓库根：
> `nuocao-data-access-租户与数据权限代码审查.md`
> （§1 请求管道 · §2~8 租户与数据权限 · §9 离线验证脚手架 · §9~12 鉴权/白名单/凭证 · §13 通用约定速查）

## 硬事实（改代码前必读）
- `nuocao-server` source level=**11**（禁用 `instanceof` 模式匹配等 16+ 语法）；mvn 不在 PATH：`/e/dev_run_env/apache-maven-3.9.2/bin/mvn`。
- `*Config`=参数（`@Inject`+getter/setter）；`*Configure`=装配（`@Bean`/`@Init`）。分层判据见 §13.2。
- `@Init` rank=**1** < server listen=**99** ⇒ 同步 `@Init` 必早于端口；`@Init` **必须无参**、**禁止 async**（会延后到监听之后）。
- **Filter 拿不到 `ctx.action()`**（注解检测只能放 `RouterInterceptor`/`@Around`）；`@Component(index=N)` = 链上权重升序；`@Around` **同类 `this.xxx()` 直调不生效**。
- 裸 `@Bean` 的 bean 名为空（不回退方法名）⇒ 同类型多个裸 `@Bean` 互相覆盖；knife4j `DocDocket` 必须写名 `@Bean("sysDocApi")`。
- 租户关卡 `TenantInterceptor` = **index -200**；`SaTokenInterceptor` = **-100**，其鉴权会查带租户列的 `sys_user` ⇒ **需租户上下文才能查库的拦截器 index 必须 < -100**。
- **租户 ID 只认请求头 `Tenant-Id`**（`?Tenant-Id=1` 无效）；flex 多租户判据 = 实体 `@Column(tenantId = true)`（`tenant.table-ignore` 是死配置）。
- `TenantFactory#getTenantIds()` 返回值**兼用于查询条件与 insert 回填** ⇒ 无租户上下文的兜底值用 `-1L`。
- 鉴权：token 名 = **`token`**（Header/Cookie/query 三通道都读）；`setAuth` 对 `/**` 全局生效 ⇒ **无注解接口同样被拦**；`@SaIgnore` = 跳过整段全局 `setAuth`（完全公开）；Solon `PathMatcher` 对普通字符串是**精确匹配**。
- `@PublicMapping` 白名单：数据源必须是 Solon **路由表**，且要在 **`@Init` 里算好**存字段 —— `setAuth` 的 lambda **每请求执行**，`notMatch(...)` 的参数每请求求值；**不可改用 `addExclude`**（会连 `setBeforeAuth` 一起跳过 → CORS 头不设）。
- 会话真身在 Redis db0，key 前缀 **`token:admin:`**（别用 `admin:login:*` 扫）；**token 与设备无绑定** ⇒「某接口不登录能访问」≈「该客户端带着旧凭证」；登录下发 30 天持久 Cookie（`httpOnly=false`）。
- 已知未决：datascope 整套规则**从未装配**（`@DataScopeIgnore` 对 SQL 无影响）；`SysUserServiceImpl:341/480` 同类直调 `@TenantIgnore` 方法（改动前需用户确认）。

## 跑实例验证
各模块 `target/classes` + `nuocao-server/target/cp-nolocal.txt`（**必须剔掉 m2 里的旧 nuocao 构件**，否则旧 `TenantFilter.class` 抢先拦截）→ `com.nuocao.Application --server.port=8099`，**cwd 必须仓库根**；curl 必须 `--noproxy '*'`（环境有代理）；后台跑用 `run_in_background`。详见 §9.4。
