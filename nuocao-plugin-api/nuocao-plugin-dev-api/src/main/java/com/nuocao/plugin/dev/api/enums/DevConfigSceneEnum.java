package com.nuocao.plugin.dev.api.enums;

import com.nuocao.common.enums.StrArrayValuable;
import com.nuocao.common.exception.CommonException;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 消息应用场景。
 *
 * <p>
 * 场景是稳定的业务标识，与消息媒介正交。
 * 场景决定验证码业务类型和必填变量。
 * 具体使用哪个模板、哪个短信通道，由配置决定。
 * #################
 * 说说想法，这个设计主要是为了以后针对场景发送提醒式可以灵活配置而不需要去修改代码，
 * 这里通过config_key与场景的关联，在关联到sms、email等更多扩展需求，可通过配置灵活修改模板而不需要修改系统中的变量
 * 特别是如果多语言配置区分时可以将config_key对应到local就可以了
 * 但是也有过度设计的问题，因为大多数一个场景常量+smsTemplateCode就足够了
 * ##############################
 * </p>
 */
@Getter
public enum DevConfigSceneEnum implements StrArrayValuable {

    /**
     * 注册
     */
    ACCOUNT_REG(
            "account_reg",
            Map.of(
                    MessageMediumEnum.SMS,
                    MediumMeta.required(
                            "sms_scene_account_reg_code",
                            "code",
                            "time"
                    ),
                    MessageMediumEnum.EMAIL,
                    MediumMeta.required(
                            "email_scene_account_reg_code",
                            "code",
                            "link",
                            "time"
                    )
            )
    ),

    /**
     * 账号登录
     */
    ACCOUNT_LOGIN(
            "account_login",
            Map.of(
                    MessageMediumEnum.SMS,
                    MediumMeta.required(
                            "sms_scene_account_login_code",
                            "code",
                            "time"
                    ),
                    MessageMediumEnum.EMAIL,
                    MediumMeta.required(
                            "email_scene_account_login_code",
                            "code",
                            "time"
                    )
            )
    ),

    /**
     * 找回密码
     */
    PASSWORD_GET(
            "password_get",
            Map.of(
                    MessageMediumEnum.SMS,
                    MediumMeta.required(
                            "sms_scene_password_get_code",
                            "code",
                            "time"
                    ),
                    MessageMediumEnum.EMAIL,
                    MediumMeta.required(
                            "email_scene_password_get_code",
                            "code",
                            "link",
                            "time"
                    )
            )
    ),

    /**
     * 修改密码
     */
    PASSWORD_UPDATE(
            "password_update",
            Map.of(
                    MessageMediumEnum.SMS,
                    MediumMeta.required(
                            "sms_scene_password_update_code",
                            "code",
                            "time"
                    ),
                    MessageMediumEnum.EMAIL,
                    MediumMeta.required(
                            "email_scene_password_update_code",
                            "code",
                            "time"
                    )
            )
    ),

    /**
     * 更改绑定手机
     */
    BIND_UPDATE(
            "bind_update",
            Map.of(
                    MessageMediumEnum.SMS,
                    MediumMeta.required(
                            "sms_scene_bind_update_code",
                            "code"
                    ),
                    MessageMediumEnum.EMAIL,
                    MediumMeta.required(
                            "email_scene_bind_update_code",
                            "code"
                    )
            )
    ),

    /**
     * 消息通知
     */
    MESSAGE_NOTIFY(
            "message_notify",
            Map.of(
                    MessageMediumEnum.EMAIL,
                    MediumMeta.optional(
                            "email_scene_notify",
                            "sender",
                            "category",
                            "title",
                            "link"
                    ),
                    MessageMediumEnum.SMS,
                    MediumMeta.optional(
                            "sms_scene_notify",
                            "sender",
                            "category",
                            "title"
                    )
            )
    );

    private static final Map<String, DevConfigSceneEnum> SCENE_CACHE;

    private static final Map<String, ConfigKeyRef> CONFIG_KEY_CACHE;

    private static final Set<String> VALUES;

    static {
        Map<String, DevConfigSceneEnum> sceneCache = new HashMap<>();
        Map<String, ConfigKeyRef> configKeyCache = new HashMap<>();

        for (DevConfigSceneEnum scene : values()) {
            if (scene.scene == null || scene.scene.isBlank()) {
                throw new IllegalStateException(
                        "场景值不能为空：" + scene.name()
                );
            }

            if (scene.metaByMedium.isEmpty()) {
                throw new IllegalStateException(
                        "场景未声明任何媒介：" + scene.scene
                );
            }

            if (sceneCache.put(scene.scene, scene) != null) {
                throw new IllegalStateException(
                        "场景值重复：" + scene.scene
                );
            }

            for (Map.Entry<MessageMediumEnum, MediumMeta> entry
                    : scene.metaByMedium.entrySet()) {

                MessageMediumEnum medium = entry.getKey();
                MediumMeta meta = entry.getValue();

                ConfigKeyRef old = configKeyCache.put(
                        meta.configKey,
                        new ConfigKeyRef(scene, medium, meta)
                );

                if (old != null) {
                    throw new IllegalStateException(
                            "配置键重复：" + meta.configKey
                                    + "，当前场景：" + scene.scene
                                    + "，重复场景：" + old.getScene().getScene()
                    );
                }
            }
        }

        SCENE_CACHE = Collections.unmodifiableMap(sceneCache);
        CONFIG_KEY_CACHE = Collections.unmodifiableMap(configKeyCache);
        VALUES = Collections.unmodifiableSet(sceneCache.keySet());
    }

    /**
     * 场景值
     */
    private final String scene;

    /**
     * 当前场景在各媒介下的配置契约
     */
    private final Map<MessageMediumEnum, MediumMeta> metaByMedium;

    DevConfigSceneEnum(
            String scene,
            Map<MessageMediumEnum, MediumMeta> metaByMedium
    ) {
        this.scene = scene;
        this.metaByMedium = Collections.unmodifiableMap(metaByMedium);
    }

    /**
     * 根据媒介取得配置契约。
     *
     * @return 不支持时返回 null
     */
    public MediumMeta metaOf(MessageMediumEnum medium) {
        return metaByMedium.get(medium);
    }

    public MediumMeta getSmsMeta(){return metaOf(MessageMediumEnum.SMS);}
    public MediumMeta getEmailMeta(){return metaOf(MessageMediumEnum.SMS);}

    /**
     * 判断当前场景是否支持指定媒介。
     */
    public boolean supports(MessageMediumEnum medium) {
        return metaByMedium.containsKey(medium);
    }

    /**
     * 取得指定媒介下使用的配置键。
     *
     * @throws CommonException 当前场景不支持指定媒介
     */
    public String configKeyOf(MessageMediumEnum medium) {
        MediumMeta meta = metaOf(medium);

        if (meta == null) {
            throw new CommonException(
                    "场景 {} 不支持媒介 {}",
                    scene,
                    medium
            );
        }

        return meta.getConfigKey();
    }

    /**
     * 取得指定媒介下要求的模板变量。
     *
     * @return 不支持时返回 null
     */
    public Set<String> variablesOf(MessageMediumEnum medium) {
        MediumMeta meta = metaOf(medium);
        return meta == null ? null : meta.getVariables();
    }

    /**
     * 判断指定媒介是否必须配置。
     */
    public boolean requiredOf(MessageMediumEnum medium) {
        MediumMeta meta = metaOf(medium);
        return meta != null && meta.isRequired();
    }

    /**
     * 根据场景值获取枚举。
     */
    public static DevConfigSceneEnum of(String scene) {
        DevConfigSceneEnum result = SCENE_CACHE.get(scene);

        if (result == null) {
            throw new CommonException(
                    "不支持的应用场景：{}",
                    scene
            );
        }

        return result;
    }

    /**
     * 校验场景值是否合法。
     */
    public static void validate(String scene) {
        of(scene);
    }

    /**
     * 根据配置键获取配置引用信息。
     *
     * <p>
     * 配置键全局唯一，一个配置键可以反查：
     * 场景、媒介、模板变量要求。
     * </p>
     */
    public static ConfigKeyRef ofConfigKey(String configKey) {
        ConfigKeyRef result = CONFIG_KEY_CACHE.get(configKey);

//        if (result == null) {
//            throw new CommonException(
//                    "不存在的消息配置键：{}",
//                    configKey
//            );
//        }

        return result;
    }

    /**
     * 根据配置键获取场景。
     */
    public static DevConfigSceneEnum sceneOfConfigKey(String configKey) {
        return ofConfigKey(configKey).getScene();
    }

    /**
     * 根据配置键获取媒介。
     */
    public static MessageMediumEnum mediumOfConfigKey(String configKey) {
        return ofConfigKey(configKey).getMedium();
    }

    /**
     * 判断配置键是否存在。
     */
    public static boolean containsConfigKey(String configKey) {
        return CONFIG_KEY_CACHE.containsKey(configKey);
    }

    /**
     * 获取所有场景值。
     */
    @Override
    public String[] array() {
        return VALUES.toArray(new String[0]);
    }

    /**
     * 配置键反查结果。
     */
    @Getter
    public static final class ConfigKeyRef {

        private final DevConfigSceneEnum scene;

        private final MessageMediumEnum medium;

        private final MediumMeta meta;

        public ConfigKeyRef(
                DevConfigSceneEnum scene,
                MessageMediumEnum medium,
                MediumMeta meta
        ) {
            this.scene = scene;
            this.medium = medium;
            this.meta = meta;
        }
    }

    /**
     * 场景在指定媒介下的配置契约。
     */
    @Getter
    public static final class MediumMeta {

        /**
         * 配置键。
         *
         * <p>
         * 该值会写入配置表、缓存以及发送链路，
         * 上线后不建议随意修改。
         * </p>
         */
        private final String configKey;

        /**
         * 模板必须包含的变量（保持声明顺序）。
         */
        private final Set<String> variables;

        /**
         * 是否必须配置。
         */
        private final boolean required;

        private MediumMeta(
                String configKey,
                String[] variables,
                boolean required
        ) {
            if (configKey == null || configKey.isBlank()) {
                throw new IllegalStateException(
                        "配置键不能为空"
                );
            }

            // 保序：模板变量的声明顺序对使用方有意义（如 code → link → expire），
            // 因此用 LinkedHashSet 而非 HashSet；
            // 重复声明在此直接拦下，避免被 Set 静默去重后"声明两次只生效一次"。
            String[] safeVariables = variables == null ? new String[0] : variables;
            Set<String> ordered = new LinkedHashSet<>(
                    Arrays.asList(safeVariables)
            );

            if (ordered.size() != safeVariables.length) {
                throw new IllegalStateException(
                        "配置键 " + configKey + " 的模板变量存在重复："
                                + Arrays.toString(safeVariables)
                );
            }

            this.configKey = configKey;
            this.variables = Collections.unmodifiableSet(ordered);
            this.required = required;
        }

        /**
         * 创建必填配置。
         */
        public static MediumMeta required(
                String configKey,
                String... variables
        ) {
            return new MediumMeta(configKey, variables, true);
        }

        /**
         * 创建可选配置。
         */
        public static MediumMeta optional(
                String configKey,
                String... variables
        ) {
            return new MediumMeta(configKey, variables, false);
        }
    }
}