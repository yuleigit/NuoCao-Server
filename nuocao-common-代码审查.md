# com.nuocao.common 代码审查与整改建议

> 范围：`nuocao-common/src/main/java/com/nuocao/common/**`（约 90 个类）  
> 结论：整体功能可用，但存在 **若干会直接导致错误结果/崩溃的缺陷**、**全局静态可变状态**、**大量重复造轮子（hutool / commons-lang3 已提供）** 与 **风格不统一** 四类问题。下面按优先级给出「位置 → 问题 → 建议改法」。

---

## 一、严重缺陷（会导致错误结果或运行时崩溃，建议优先修）

### 1. `utils/MobileValidator.java` — 加解密方向写反 + 结果未被使用

```java
if (anno.encrypted()) {
    realPhone = CryptUtils.sm2Encrypt(value);   // ① 应解密，却调了加密
}
return PhoneUtil.isMobile(value) ...             // ② 校验的是原 value，realPhone 从未使用
```

**改法**：改为 `realPhone = CryptUtils.sm2Decrypt(value);`，并 `return PhoneUtil.isMobile(realPhone) ? ... : ...`。（若前端传明文则 `encrypted` 语义要重新确认）

### 2. `utils/ConvertUtils.java#str(Object, Charset)` — 基本类型数组强转崩溃

```java
} else if (obj instanceof byte[] || obj instanceof Byte[]) {
    return str((Byte[]) obj, charset);          // byte[] 无法转 Byte[] → ClassCastException
}
```

**改法**：拆开判断 —— `byte[]` 走 `str((byte[])obj, charset)`，`Byte[]` 再单独转。

### 3. `utils/TreeBuildUtils.java#getOrInitChildren` — 多子节点会丢失

每次调用都 `new ArrayList<>()` 并覆盖父节点的 children，父节点第 2 个子节点加入时会把第 1 个冲掉。  
**改法**：先取已有 children，为 null/空才新建并 setter；或改为遍历一次用 `Map<id,List>` 累积后再 setter。

### 4. `utils/StringUtils.java#isNumber` — 正则误判

`[-+]?\d*(\.\d+)?` 会匹配 `""`、`"+"`、`"-"`、`"."`。  
**改法**：`return NumberUtil.isNumber(cs.toString());`（hutool）或严格正则 `[-+]?(\d+(\.\d+)?|\.\d+)`。

### 5. `utils/DateUtils.java` — 两个时间/格式缺陷

- `getTimestamp()` 返回 `Integer`（秒），2038 年溢出 → 返回 `long`。
- `timestampMillisToEndTime` 用 `"yyyy-MM-dd HH:mm:ss.9999"`，`9` 不是模式字母，会按**字面量**输出 `.9999` → 改为 `".SSS"`（并与 `timestampMillisToBeginTime` 的 `.000` 统一）。

### 6. `utils/NumberUtils.java#isZero(BigDecimal)` — scale 敏感比较

`value.equals(BigDecimal.ZERO)`：`new BigDecimal("0.00")` 判定为非 0。  
**改法**：`value == null || value.signum() == 0`（`compareTo(ZERO)==0`）。

### 7. `cache/CacheUtils.java` — 两处逻辑错误

- `getList(key, classz)`：类型检查写在了 `if (classz == null)` 分支里（反了），且该分支内调用 `classz.isInstance(...)` 必然 NPE。**改法**：条件改为 `classz != null` 再校验，`null` 时跳过。
- `getOrSetList(String key, Supplier<List<T>> supplier)`：`return getOrSetList(key, supplier);` **自调用 → 无限递归**。**改法**：委托到带 `classz/expireSeconds` 的实现（补默认 classz 或加真实参数）。

### 8. `mapper/BaseMapperX.java#selectSum` — SQL 注入面 + 类型不安全

`query.select("SUM(" + column + ") as sumResult")` 拼接列名；`(R)` 强转返回。  
**改法**：用 mybatis-flex 聚合 API（`QueryMethods.sum(...)`/`select(...)`）；`column` 若可能来自外部必须走白名单。

### 9. `mybatis/MyBatisUtils.java#buildQueryOrder` — 潜在 NPE

`allowedColumns.contains(field)`，而当 `entityClass == null` 时 `allowedColumns` 为 `null` → NPE。  
**改法**：`if (allowedColumns != null && allowedColumns.contains(field))`，否则按非法字段处理。

### 10. `language/LanguageContext.java` — 默认语言不确定

`Map.of(...)` 无序，用 `LOCALE_MAP.keySet().iterator().next()` 取"第一个"当默认，结果不稳定。  
**改法**：定义常量 `DEFAULT_LOCALE_CODE = "cn"` 显式使用；`LOCALE_MAP` 加 `final`。

### 11. `exception/CommonExceptionI18n.java` — 构造语义不一致

`CommonExceptionI18n(Integer code, String i18nKey, ...)` 用 `StrFormatUtils.doFormat` 而非 `LanguageUtils.getMessage`，另两个构造走 i18n，这个不走。  
**改法**：统一 `LanguageUtils.getMessage(i18nKey, args)`。

---

## 二、设计与健壮性（P1）

### 12. 全局静态可变状态：`config/NuocaoConfig`、`config/NuocaoCryptConfig`

字段为 `private static`，setter 是实例方法、getter 是静态方法，全局可写、难以测试。  
**改法**：改为普通单例 Bean，`private` 实例字段 + 实例 getter/setter（或 `@Configuration` + `@Inject` 注入使用），不再用 static。  
附带：`NuocaoConfig` 类注释写成了「缓存基础配置」（复制粘贴错误）。

### 13. `config/NuocaoCryptConfig` 注释错乱

`sm4Key` 注释「sm2公钥」、`sm4Iv` 注释「sm2私钥」；类注释「请与前端一直」→「一致」。

### 14. `utils/LockUtils.java`

- `fairLock(...)` 的 `finally` 直接 `lock.unlock()`，未判断 `isHeldByCurrentThread()`，与非公平锁版本不一致 → 可能抛 `IllegalMonitorStateException`。
- 类末尾大段注释掉的 RLock 接口说明属死代码；`lock(...Callable)` 里 `throwException` 后又 `return resp` 为不可达。
- `lock(lockKey, timeoutMillis, ...)` 的 `timeoutMillis` 实际是 **leaseTime（持锁时长）**，注释「最多锁定时长」易误解，建议区分 waitTime/leaseTime。

### 15. 工具类一致性

`ObjectUtils / StringUtils / NumberUtils / ConvertUtils / DateUtils / Md5Utils / DesensitizationUtils / StrFormatUtils / ValidationUtils / PermissionCodeBuildUtils / MyBatisUtils / TreeBuildUtils / SolonFilterUtils / BeanUtils / JsonUtils` 均未做「不可实例化」（缺 `final class` + 私有构造），而 `LocalDateTimeUtils`、`IpUtils` 做了 → 建议统一补上。

### 16. `utils/ValidationUtils.java`

- `validate(Object,...)` 每次调用 `Validation.buildDefaultValidatorFactory()`，开销大 → 静态缓存 `Validator` 单例。
- `validUsername` 的 javadoc 写成「校验邮箱格式」（复制粘贴）。
- 同时存在 `javax.validation` 与 `solon.validation` 两套校验体系，建议明确边界（统一入口）。

### 17. 调试代码混入正式逻辑

`MapperLambdaUtils`、`MyBatisUtils.buildQueryOrderSafe`、`BaseMapperX.getEntityType` 中都出现 `Long startTime = LocalDateTimeUtils.currentTimestamp();` 仅用于一行 debug 日志；另外 `BaseMapperX.getEntityType()` 每次调用都反射遍历接口，应缓存到 `Class` 上。

### 18. `utils/Md5Utils.java`

- `hash()` 中 `new String(toHex(md5(s)).getBytes(UTF_8), UTF_8)` 是无意义的编码往返。
- `private static final String toHex(...)` 的 `final` 对方法无意义；`StringBuffer` → `StringBuilder`；`s.getBytes("UTF-8")` → `StandardCharsets.UTF_8`。
- 建议直接用 commons-codec `DigestUtils.md5Hex`；且 MD5 不宜用于口令，口令请用 BCrypt。

### 19. `mapper/BaseMapperX.java`

- `updateById(T)` 内部调用的是 `this.update(...)`，方法名与行为不符，容易误用。
- `exists(...)` 用 `selectOne` 加载整行，建议 `selectCountByQuery(...) > 0`。

### 20. `utils/IpUtils.java`

静态字段直接 `initSearcher()` 在建类时读盘，异常直接抛 `CommonException` 导致类初始化失败（NoClassDefFoundError 连带风险）。建议延迟初始化或失败降级。

### 21. `utils/collection/ArrayUtils.java#toArray(Collection)`

`return (T[]) (new Object[0]);` 空集合时返回 `Object[]`，赋给 `String[]` 会在调用处 `ClassCastException`。建议改用 `CollectionUtils.convertList` + 目标类型数组。

### 22. `utils/ConvertUtils.java`

整个类是 ruoyi/hutool 的拷贝，与已引入的 `cn.hutool.core.convert.Convert` 重复；且缩进整体多一层 8 空格。`toStrArray/toSBC/toDBC` 对入参为 null 会 NPE。

### 23. `utils/json/JsonUtils.java`

`private static ObjectMapper objectMapper` 可变且被 `init()` 覆盖，非线程安全；同时混用 jackson / fastjson2 / hutool 三套 JSON（`parseObject2` 走 hutool、其余走 jackson）。建议固定 Jackson 单例（`ObjectMapper` 本身线程安全，配置后不要替换），并统一门面。

### 24. `pojo/CommonValidList.java`

手工实现 `List` 全部约 40 个方法做委托 → 应 `extends AbstractList<E>`，只实现 `get(int)` 和 `size()`。同时 `KeyValue` 与 `KeyValueData` 功能重复，建议合并。

### 25. `pojo/CommonResult.java`

- `toString()` 手工拼 JSON：未转义、`data` 一律加引号 → 交给 JSON 序列化。
- `setCode/setMsg/setData` 返回 `this` 违反 JavaBean setter 约定（会干扰部分框架/IDE），建议改用 `withXxx(...)` 或 `builder()`。

### 26. `page/PageResult.java`

字段 `Long total`，但 `getTotal()` 返回基本类型 `long`（null 时拆箱 NPE）、`setTotal(long)` 又收窄。统一用 `Long`（或统一 `long`）。

### 27. `page/PageParam.java`

`@Max(value = 500, message = "每页条数最大值为 100")` 数值与提示不一致；`PAGE_SIZE_NONE = -1` 与 `@Min(1)` 语义冲突，建议单独字段/方法表达「不分页」。

### 28. `constants/Constants.java`

`SPLIT_STR / EMPTY_STR / UNDERLINE_STR / UTF_8` 是 `public static`（可被改写），应 `static final`，且 `UTF_8` 与 `StandardCharsets.UTF_8` 重复。

### 29. i18n 未覆盖

`pojo/CommonHttpCodes`、`enums/CommonExceptionEnum` 硬编码中文，项目已有 `LanguageUtils` 体系却未使用。

### 30. `config/NuoCaoRedisConfig.java`

`maxIdle`、`maxWaitMillis` 字段定义后未被任何配置类使用（死字段）；类名 `NuoCaoRedisConfig` 与 `NuocaoConfig`/`NuocaoCryptConfig` 大小写风格不一致。

---

## 三、规范与重复代码（P2）

| 位置                                  | 问题                                                                                                                                        | 建议                                 |
| ----------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------- |
| `ObjectUtils`                       | `equals()` 与 `equal()` 完全重复；`import NumberUtil` 未使用                                                                                       | 删冗余方法/导入                           |
| `NumberUtils`                       | `import IOException/InputStream` 未使用                                                                                                      | 删导入                                |
| `StringUtils`                       | 继承 commons-lang3，重复实现 `substring/trim/hasText/format` 等；`getNowDate`/`GetCurrentData` 等命名混乱、拼写（Data/Date）                                 | 只保留业务特有方法，通用能力直接调 commons-lang3    |
| `DateUtils`                         | `getStartOfDay` 用已废弃 `new Date(y,m,d)`；`isAM` 用已废弃 `getHours()`；`parseDateToStr` 每次 new `SimpleDateFormat`；`getTime()`/`dateTime()` 同名不同义 | 迁移到 `java.time`；格式化器缓存/ThreadLocal |
| `Md5Utils`                          | `StringBuffer`、编码往返、无意义 `final`                                                                                                           | 用 `DigestUtils`                    |
| `MapperLambdaUtils`                 | `methodToProperty`/`camelToUnderline`/`firstToLower` 定义后未使用（死代码）；重复实现 hutool `StrUtil` 能力                                                 | 删除或复用                              |
| `NuoCaoValidatorFailureHandler`     | 空 `if` 分支、大段注释死代码、拼写错误的未用常量 `VALIDATION_I18N_LENETN_SIZE_NAME_SET`                                                                        | 清理；变量名修正为 `...LENGTH_SIZE...`      |
| `SolonFilterUtils`                  | `isOptionsRequest` 可 `return "OPTIONS".equalsIgnoreCase(ctx.method());`                                                                   | 简化                                 |
| `NuoCaoEvent`/`NuoCaoEventListener` | 注释「时间接口/时间监听」应为「事件」                                                                                                                       | 改注释                                |
| `CommonSseParam`                    | 注释错别字「关联通用忽的ID」「请使用是」                                                                                                                     | 改注释                                |
| `IdUtils`                           | `workId/datacenterId` 硬编码 1/1，多实例部署会撞 ID                                                                                                  | 落到配置项                              |
| `BeanUtils.copyBeanProp`            | `e.printStackTrace()`                                                                                                                     | 改日志                                |
| `CollectionUtils`                   | `getMaxValue` 对 null 元素 NPE 且 mapper 调用两次；`diffList` 返回裸 `List<List<T>>`                                                                  | 用 `Comparator.nullsLast`；返回具名结果对象  |
| `ConvertUtils`                      | 与 hutool `Convert` 重复                                                                                                                     | 评估后收敛到一处                           |

---

## 四、建议的整改顺序

1. **立即修（正确性）**：第一条清单第 1–11 项（加解密方向、强转崩溃、树丢失、正则、时间戳、CacheUtils 两处、SQL 拼接、NPE、默认语言、i18n 构造）。
2. **短期（设计）**：静态可变配置改造（12–13）、锁一致性（14）、工具类统一不可实例化（15）、Validator 缓存（16）、清理调试/死代码（17、18、27、28）。
3. **中期（收敛）**：JSON/Convert/String 三处重复能力收口到 hutool/commons-lang3；i18n 覆盖返回码；`CommonValidList` 改 `AbstractList`；`CommonResult`/`PageResult` 类型与 setter 规范化。
4. **配套**：补单元测试（尤其 `isNumber`、`ConvertUtils.str`、`TreeBuildUtils`、`CacheUtils`），避免重构回归。

> 说明：以上为静态审查结论，未执行编译/单测验证。如需要，我可以按第一条清单直接提交修复补丁。

