# 糯草管理系统（NuoCao）

基于 Java + Solon 的轻量级多租户后台管理框架，专注「快速落地」：内置租户隔离、国密加密、权限扩展、站内信与多语言，模块按插件拆分，可按需裁剪。

- 前端仓库：[element-plus-admin-mini（nuocao 分支）](https://github.com/admin-mini/element-plus-admin-mini/tree/nuocao)

---

## 项目特点

### 1. 真正的多租户

账号（account）与租户用户（user）分离，登录身份与租户内身份解耦，支持一个账号在多个租户间切换。

> 为方便「多租户 / 非多租户」两种模式的快速二开，`account` 与 `user` 两张表都冗余存储了 `password` 字段。若只采用单一模式，可自行梳理删除冗余字段及相关逻辑。

租户开关与忽略规则集中在 `app.yml`：

```yaml
tenant:
  enable: true          # 多租户总开关
  table-ignore:         # 不参与租户隔离的表
    - dev_dict
    - dev_config
    - sys_tenant
  path-ignore:          # 不需要租户校验的请求路径
    - /
    - /auth/account/**
```

### 2. 国密（SM）加密支持

- **SM2 非对称加密**：登录密码等敏感数据由前端使用公钥加密后传输，后端私钥解密，避免明文过网。
- **SM4 对称加密**：用于大数据量加密与敏感字段加密存储。

配置位于 `app.yml` 的 `nuocao.crypt`，需与前端 `nuocao/utils/smCrypto.js` 保持一致；生产环境请通过 `app-pro.yml` 覆盖密钥，前端保持同一 `sm2PublicKey`。

### 3. 可快速扩展的数据权限与功能权限

基于用户身份管理服务 `SysUserIdentityService`，配合以下两个服务可快速扩展授权方式：

| 服务 | 作用 |
| --- | --- |
| `SysUserPermissionService` | 功能权限（菜单、按钮、接口）授权 |
| `SysDataScopeService` | 数据权限（数据范围）授权 |

可在此基础上扩展按**角色、部门、岗位、个人**等多种维度的数据权限与业务功能授权，新增授权方式无需侵入核心逻辑。

### 4. 技术栈

- 后端：Java 17、[Solon](https://solon.noear.org/) 4.1.1、[Sa-Token](https://sa-token.cc/) 1.46.0、[MyBatis-Flex](https://mybatis-flex.com/) 1.11.8、MySQL（可扩展其他数据库）
- 前端：Vue 3 + Element Plus（[element-plus-admin-mini](https://github.com/admin-mini/element-plus-admin-mini/tree/nuocao)）

### 5. 内置站内信

提供消息发送、接收人管理、已读未读、消息详情等能力（`dev` 插件 `message` 模块），可直接作为系统通知、业务提醒的统一出口。

### 6. 多语言支持

依托 Solon 的 `I18nUtil`，结合自建多语言上下文 `LanguageContext`（通过请求头 `Language` 切换，默认 `cn`），并配合 `ValidatorFailureHandler` 实现**校验注解多语言提示**：

校验注解无需编写 `message`，即可自动返回当前语言的属性名提示，例如 `@Null` 会自动提示「xxx 不能为空」。

### 7. 分层插件化架构

模块之间通过 API 层解耦，业务模块可插拔，便于按需裁剪与独立二开。

---

## 后端运行环境

| 组件 | 版本要求 | 说明 |
| --- | --- | --- |
| JDK | **17 及以上**（推荐 17 LTS） | 开发环境使用 JDK 17；源码编译目标为 14，可运行于 JDK 14+ |
| Maven | 3.6.9+ | 依赖管理与构建 |
| MySQL | **8.x**（推荐 8.0.30+） | 项目使用 `mysql-connector-j 8.3.0`，字符集 `utf8mb4` |
| Redis | 5.0+（推荐 6.x / 7.x） | 缓存、Sa-Token 会话存储（Redisson / Redis 序列化） |
| 操作系统 | Windows / Linux / macOS | 均可运行，生产推荐 Linux |
| IDE | IntelliJ IDEA | 已提供 `.idea` 工程配置 |

可选 / 可扩展：

- 文件存储：本地、MinIO、阿里云 OSS、腾讯云 COS
- 数据库：MySQL 为主，另预留 PostgreSQL、达梦、人大金仓、Oracle、MSSQL 依赖（默认注释，按需开启）
- 短信 / 邮件：阿里云、腾讯云

前端环境（独立仓库）：

| 组件 | 版本要求 |
| --- | --- |
| Node.js | 18 及以上（建议使用 LTS 版本） |

---

## 模块说明

```
nuocao
├── nuocao-common            基础配置及工具
├── nuocao-data-access       数据权限、多租户控制
├── nuocao-plugin            业务模块插件
│   ├── nuocao-plugin-auth   认证门面：登录、账号、验证码、场景控制
│   ├── nuocao-plugin-sys    系统管理：用户、角色、菜单、部门、租户、数据权限、账号
│   ├── nuocao-plugin-dev    开发支撑：字典、配置、文件、日志、站内信、短信、邮件
│   └── nuocao-plugin-gen    代码生成：表管理与代码生成
├── nuocao-plugin-api        各模块 API 层（模块解耦调用）
│   ├── nuocao-plugin-auth-api
│   ├── nuocao-plugin-sys-api
│   ├── nuocao-plugin-dev-api
│   └── nuocao-plugin-gen-api
├── nuocao-server            服务启动模块
└── data
    ├── ip2region            IP 归属地离线库（v4 / v6）
    └── sql                  数据库初始化脚本目录
```

---

## 快速开始

### 1. 准备环境

安装 JDK 17、Maven、MySQL 8.x、Redis，并启动 MySQL 与 Redis。

### 2. 初始化数据库

1. 创建数据库（字符集 `utf8mb4`，排序规则 `utf8mb4_general_ci`）：

```sql
CREATE DATABASE `nuocao` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

2. 导入项目提供的 SQL 脚本（建表 + 基础数据）。

### 3. 修改配置

- `nuocao-server/src/main/resources/app-local.yml`：数据源与 Redis 连接信息

```yaml
nuocao.redis:
  db: 0
  server: "127.0.0.1:6379"
  password: ""

nuocao.datasource:
  dynamic:
    master:
      jdbcUrl: "jdbc:mysql://localhost:3306/nuocao?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true"
      username: "root"
      password: "******"
```

- `nuocao-server/src/main/resources/app.yml`：服务端口（默认 `8098`）、环境（`solon.env: local`）、多租户开关、国密密钥、IP 归属地库路径等

> 生产环境请新增 `app-pro.yml` 覆盖数据库密码、`nuocao.crypt` 密钥等敏感配置，`app.yml` 中的示例密钥仅用于本地开发。

### 4. 构建与启动

```bash
# 根目录执行
mvn clean package -DskipTests

# 启动（或直接运行 nuocao-server 模块的 com.nuocao.Application）
java -jar nuocao-server/target/nuocao-server-1.0.0.jar
```

启动成功后访问：

- 服务首页：<http://localhost:8098/>
- 接口文档（Knife4j）：<http://localhost:8098/doc.html>（本地环境已关闭 basic 认证，生产环境请开启并修改默认账号密码）

### 5. 启动前端

按前端仓库说明安装依赖并启动（默认前端地址 `http://localhost:9988`，与 `app.yml` 中的 `nuocao.config.front-url` 保持一致）。

### 6. IP 归属地离线库

`data/ip2region/` 下的 `ip2region_v4.xdb`、`ip2region_v6.xdb` 未打包进 jar，部署时需放在配置路径上，或通过 `app-pro.yml` 覆盖为部署机的绝对路径：

```yaml
nuocao.ip2region:
  enable: true
  v4-path: /data/nuocao/ip2region/ip2region_v4.xdb
  v6-path: /data/nuocao/ip2region/ip2region_v6.xdb
  fail-fast: false   # 初始化失败降级为「未知」，不影响主业务
```

---

## 下一步开发计划

1. 代码生成器
2. 字段颗粒度权限授权机制
3. WebSocket 支持
4. MQ 服务抽象（消息队列，支持 Redis / RocketMQ）

---

## 参考

- Snowy-Lite：<https://gitee.com/xiaonuoadmin/snowy-lite>
- 前端 element-plus-admin-mini：<https://github.com/admin-mini/element-plus-admin-mini/tree/nuocao>
