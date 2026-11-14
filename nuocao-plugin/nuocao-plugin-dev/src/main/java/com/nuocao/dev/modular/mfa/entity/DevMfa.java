package com.nuocao.dev.modular.mfa.entity;

import com.mybatisflex.annotation.Table;
import com.nuocao.common.pojo.CommonEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户 2FA 绑定实体。
 * <p>主键为 (tenant_id, owner_type, owner_id)，表结构由 SQL 保证唯一，实体不再重复声明 @Id，
 * 读写统一通过 QueryWrapper / UpdateChain 按复合主键条件操作，避免复合主键 API 的歧义。
 **/
@Getter
@Setter
@Table(value = "dev_mfa")
public class DevMfa extends CommonEntity {

    @ApiModelProperty(value = "租户ID", position = 1)
    private Long tenantId;

    @ApiModelProperty(value = "绑定主体类型，当前固定为 user", position = 2)
    private String ownerType;

    @ApiModelProperty(value = "绑定主体ID，用户级即用户ID", position = 3)
    private Long ownerId;

    @ApiModelProperty(value = "绑定主体名称", position = 4)
    private String ownerName;

    @ApiModelProperty(value = "secret 密文", position = 5)
    private String secretCipher;

    @ApiModelProperty(value = "secret 密钥版本", position = 6)
    private Integer cipherVersion;

    @ApiModelProperty(value = "状态：1=绑定中，9=已启用，4=已失效", position = 7)
    private Integer status;

    @ApiModelProperty(value = "算法", position = 8)
    private String algorithm;

    @ApiModelProperty(value = "验证码位数", position = 9)
    private Integer digits;

    @ApiModelProperty(value = "时间步长（秒）", position = 10)
    private Integer periodSeconds;

    @ApiModelProperty(value = "最近一次验证通过的时间步，用于防重放", position = 11)
    private Long lastAcceptedStep;

    @ApiModelProperty(value = "时钟偏移时间步", position = 12)
    private Integer clockOffsetSteps;

    @ApiModelProperty(value = "备用恢复码哈希", position = 13)
    private String recoveryHashes;

    @ApiModelProperty(value = "连续失败次数", position = 14)
    private Integer failedAttempts;

    @ApiModelProperty(value = "锁定截止时间", position = 15)
    private LocalDateTime lockedUntil;

    @ApiModelProperty(value = "绑定流程ID", position = 16)
    private String bindId;

    @ApiModelProperty(value = "绑定过期时间", position = 17)
    private LocalDateTime bindExpiresAt;

    @ApiModelProperty(value = "启用时间", position = 18)
    private LocalDateTime activatedAt;

    @ApiModelProperty(value = "最近验证时间", position = 19)
    private LocalDateTime lastUsedAt;
}
