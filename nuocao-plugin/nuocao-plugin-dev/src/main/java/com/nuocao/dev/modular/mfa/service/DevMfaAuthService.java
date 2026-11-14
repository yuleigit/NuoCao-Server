package com.nuocao.dev.modular.mfa.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.nuocao.common.cache.CacheUtils;
import com.nuocao.common.config.NuoCaoMfaConfig;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.totp.TotpSecretCipher;
import com.nuocao.common.totp.TotpUtils;
import com.nuocao.dev.core.enums.NuoCaoMfaEnforcementEnum;
import com.nuocao.dev.core.utils.QrCodeUtils;
import com.nuocao.dev.modular.mfa.dto.DevMfaChallenge;
import com.nuocao.dev.modular.mfa.entity.DevMfa;
import com.nuocao.dev.modular.mfa.mapper.DevMfaMapper;
import com.nuocao.plugin.dev.api.DevConfigApi;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaBindInitDTO;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaLoginCheckDTO;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaLoginCheckParamDTO;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaStatusDTO;
import com.nuocao.plugin.dev.api.dto.mfa.DevMfaVerifyResultDTO;
import com.nuocao.plugin.dev.api.enums.mfa.DevMfaEventEnum;
import com.nuocao.plugin.dev.api.enums.mfa.DevMfaStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Transaction;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

/**
 * 2FA 认证核心服务：登录策略、动态码校验、绑定流程。
 **/
@Component
@Slf4j
public class DevMfaAuthService {

    /** 登录 2FA 开关配置项 */
    private static final String CONFIG_KEY = "account_login_2fa_verify";

    /** 当前只支持用户级绑定 */
    private static final String OWNER_TYPE_USER = "user";

    private static final String REQ_CACHE_PREFIX = "auth-mfa:req:";

    private static final String BIND_ATTEMPT_PREFIX = "auth-mfa:bind-attempt:";

    private static final String ALGORITHM = "SHA1";

    @Inject
    private DevMfaMapper devMfaMapper;

    @Inject
    private DevMfaEventRecorder eventRecorder;

    @Inject
    private DevConfigApi devConfigApi;

    @Inject
    private CacheUtils cacheUtils;

    @Inject
    private NuoCaoMfaConfig mfaConfig;

    @Inject
    private TotpSecretCipher totpSecretCipher;

    // ======================= 登录校验 =======================

    /**
     * 登录鉴权通过后，判断是否需要 2FA。
     */
    public DevMfaLoginCheckDTO checkLogin(DevMfaLoginCheckParamDTO param) {
        DevMfaLoginCheckDTO result = new DevMfaLoginCheckDTO();

        NuoCaoMfaEnforcementEnum enforcement = currentEnforcement();
        if (enforcement == NuoCaoMfaEnforcementEnum.OFF) {
            return result;
        }

        DevMfa mfa = selectMfa(param.getTenantId(), param.getUserId());
        boolean active = mfa != null && Objects.equals(mfa.getStatus(), DevMfaStatusEnum.ACTIVE.getValue());

        if (active) {
            assertNotLocked(mfa);
            String reqId = createChallenge(param, DevMfaChallenge.PURPOSE_VERIFY, null);
            result.setNeedMfa(true);
            result.setMfaReqId(reqId);
            result.setExpiresIn(requestTtlSeconds());
            return result;
        }

        // 未绑定或已失效：只有强制开启时才要求先绑定
        if (enforcement == NuoCaoMfaEnforcementEnum.REQUIRED) {
            DevMfaBindInitDTO bind = doBeginBind(param.getTenantId(), param.getUserId(),
                    resolveAccount(param.getUsername(), param.getUserId()), param.getIp(), param.getUserAgent());
            String reqId = createChallenge(param, DevMfaChallenge.PURPOSE_BIND, bind.getBindId());
            result.setNeedBindMfa(true);
            result.setMfaReqId(reqId);
            result.setExpiresIn(requestTtlSeconds());
            result.setBindId(bind.getBindId());
            result.setOtpauthUri(bind.getOtpauthUri());
            result.setQrCodeDataUrl(bind.getQrCodeDataUrl());
            result.setBindExpiresIn(bind.getExpiresIn());
        }
        return result;
    }

    /**
     * 校验登录动态码。成功返回签发 token 所需的用户与设备信息。
     */
    public DevMfaVerifyResultDTO verifyLogin(String mfaReqId, Long userId, String code,
                                             String ip, String userAgent) {
        DevMfaChallenge challenge = requireChallenge(mfaReqId, userId);
        if (!DevMfaChallenge.PURPOSE_VERIFY.equals(challenge.getPurpose())) {
            throw new CommonExceptionI18n("dev.mfa.req.invalid");
        }

        DevMfa mfa = selectMfa(challenge.getTenantId(), userId);
        if (mfa == null || !Objects.equals(mfa.getStatus(), DevMfaStatusEnum.ACTIVE.getValue())) {
            throw new CommonExceptionI18n("dev.mfa.notBound");
        }
        assertNotLocked(mfa);

        long matchedStep = verifyCode(mfa, code);
        if (matchedStep < 0) {
            handleVerifyFailure(challenge, mfa, ip, userAgent);
            throw new CommonExceptionI18n("dev.mfa.code.error");
        }

        int offset = (int) (matchedStep - TotpUtils.currentTimeStep(mfa.getPeriodSeconds()));
        int updated = markUsed(challenge.getTenantId(), userId, matchedStep, offset);
        if (updated <= 0) {
            // 同一时间步已被使用：防重放
            throw new CommonExceptionI18n("dev.mfa.replay");
        }

        cacheUtils.remove(reqCacheKey(mfaReqId));
        eventRecorder.record(challenge.getTenantId(), OWNER_TYPE_USER, userId, DevMfaEventEnum.VERIFY_OK,
                mfaReqId, "动态码验证通过", ip, userAgent);

        DevMfaVerifyResultDTO result = new DevMfaVerifyResultDTO();
        result.setTenantId(challenge.getTenantId());
        result.setUserId(userId);
        result.setUsername(challenge.getUsername());
        result.setDevice(challenge.getDevice());
        result.setDeviceId(challenge.getDeviceId());
        result.setMatchedStep(matchedStep);
        return result;
    }

    /**
     * 查询用户 2FA 状态。
     */
    public DevMfaStatusDTO getStatus(Long tenantId, Long userId) {
        DevMfa mfa = selectMfa(tenantId, userId);
        DevMfaStatusDTO result = new DevMfaStatusDTO();
        if (mfa == null) {
            result.setBound(false);
            return result;
        }
        result.setStatus(mfa.getStatus());
        result.setStatusName(statusName(mfa.getStatus()));
        result.setBound(Objects.equals(mfa.getStatus(), DevMfaStatusEnum.ACTIVE.getValue()));
        result.setActivatedAt(mfa.getActivatedAt());
        result.setLastUsedAt(mfa.getLastUsedAt());
        result.setLockedUntil(mfa.getLockedUntil());
        result.setFailedAttempts(mfa.getFailedAttempts());
        return result;
    }

    // ======================= 绑定 =======================

    /**
     * 已登录用户发起绑定。
     */
    @Transaction
    public DevMfaBindInitDTO beginBind(Long tenantId, Long userId, String username,
                                       String ip, String userAgent) {
        return doBeginBind(tenantId, userId, resolveAccount(username, userId), ip, userAgent);
    }

    /**
     * 已登录用户确认绑定。
     */
    @Transaction
    public void confirmBind(Long tenantId, Long userId, String bindId, String code,
                            String ip, String userAgent) {
        doConfirmBind(tenantId, userId, resolveAccount(null, userId), bindId, code, ip, userAgent);
    }

    /**
     * 登录流程中未绑定用户发起绑定，凭 mfaReqId 免登录。
     */
    public DevMfaBindInitDTO beginBindByLogin(String mfaReqId, Long userId, String ip, String userAgent) {
        DevMfaChallenge challenge = requireChallenge(mfaReqId, userId);
        if (!DevMfaChallenge.PURPOSE_BIND.equals(challenge.getPurpose())) {
            throw new CommonExceptionI18n("dev.mfa.req.invalid");
        }
        DevMfaBindInitDTO result = doBeginBind(challenge.getTenantId(), userId,
                resolveAccount(challenge.getUsername(), userId), ip, userAgent);
        challenge.setBindId(result.getBindId());
        cacheUtils.setObject(reqCacheKey(mfaReqId), challenge, remainSeconds(challenge));
        return result;
    }

    /**
     * 登录流程中未绑定用户确认绑定，成功后前端必须回到登录页重新登录。
     */
    @Transaction
    public void confirmBindByLogin(String mfaReqId, Long userId, String bindId, String code,
                                   String ip, String userAgent) {
        DevMfaChallenge challenge = requireChallenge(mfaReqId, userId);
        if (!DevMfaChallenge.PURPOSE_BIND.equals(challenge.getPurpose())) {
            throw new CommonExceptionI18n("dev.mfa.req.invalid");
        }
        doConfirmBind(challenge.getTenantId(), userId, resolveAccount(challenge.getUsername(), userId),
                bindId, code, ip, userAgent);
        cacheUtils.remove(reqCacheKey(mfaReqId));
    }

    // ======================= 内部实现 =======================

    private DevMfaBindInitDTO doBeginBind(Long tenantId, Long userId, String account,
                                          String ip, String userAgent) {
        DevMfa existing = selectMfa(tenantId, userId);
        if (existing != null && Objects.equals(existing.getStatus(), DevMfaStatusEnum.ACTIVE.getValue())) {
            throw new CommonExceptionI18n("dev.mfa.alreadyBound");
        }

        NuoCaoMfaConfig.Totp totp = mfaConfig.getTotp();
        String secret = TotpUtils.generateSecret(totp.getSecretBytes());
        String bindId = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime bindExpiresAt = LocalDateTime.now().plus(mfaConfig.getBindTtl());

        DevMfa mfa = new DevMfa();
        mfa.setTenantId(tenantId);
        mfa.setOwnerType(OWNER_TYPE_USER);
        mfa.setOwnerId(userId);
        mfa.setOwnerName(account);
        mfa.setSecretCipher(totpSecretCipher.encrypt(secret));
        mfa.setCipherVersion(totpSecretCipher.getActiveVersion());
        mfa.setStatus(DevMfaStatusEnum.PENDING.getValue());
        mfa.setAlgorithm(ALGORITHM);
        mfa.setDigits(totp.getDigits());
        mfa.setPeriodSeconds(totp.getPeriodSeconds());
        mfa.setBindId(bindId);
        mfa.setBindExpiresAt(bindExpiresAt);
        saveBinding(mfa);

        eventRecorder.record(tenantId, OWNER_TYPE_USER, userId, DevMfaEventEnum.BIND_INIT,
                bindId, "发起绑定", ip, userAgent);

        String otpauthUri = TotpUtils.buildOtpAuthUri(totp.getIssuer(), account, secret,
                totp.getDigits(), totp.getPeriodSeconds());
        DevMfaBindInitDTO result = new DevMfaBindInitDTO();
        result.setBindId(bindId);
        result.setOtpauthUri(otpauthUri);
        result.setQrCodeDataUrl(QrCodeUtils.toPngDataUrl(otpauthUri));
        result.setExpiresIn(Math.max(1, Duration.between(LocalDateTime.now(), bindExpiresAt).getSeconds()));
        return result;
    }

    private void doConfirmBind(Long tenantId, Long userId, String account, String bindId, String code,
                               String ip, String userAgent) {
        DevMfa mfa = selectMfa(tenantId, userId);
        if (mfa == null || !Objects.equals(mfa.getStatus(), DevMfaStatusEnum.PENDING.getValue())
                || !Objects.equals(mfa.getBindId(), bindId)) {
            throw new CommonExceptionI18n("dev.mfa.bind.notPending");
        }
        if (mfa.getBindExpiresAt() == null || mfa.getBindExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CommonExceptionI18n("dev.mfa.bind.expired");
        }

        String attemptKey = BIND_ATTEMPT_PREFIX + bindId;
        int usedAttempts = valueOrZero(cacheUtils.get(attemptKey, Integer.class));
        int maxAttempts = mfaConfig.getTotp().getMaxFailedAttempts();
        if (usedAttempts >= maxAttempts) {
            throw new CommonExceptionI18n("dev.mfa.attempt.limit");
        }

        long matchedStep = verifyCode(mfa, code);
        if (matchedStep < 0) {
            cacheUtils.setObject(attemptKey, usedAttempts + 1, Math.max(1, remainSeconds(mfa.getBindExpiresAt())));
            eventRecorder.record(tenantId, OWNER_TYPE_USER, userId, DevMfaEventEnum.BIND_FAIL,
                    bindId, "绑定验证码校验失败", ip, userAgent);
            throw new CommonExceptionI18n("dev.mfa.code.error");
        }

        int updated = activateBinding(tenantId, userId, bindId, matchedStep, account);
        if (updated <= 0) {
            throw new CommonExceptionI18n("dev.mfa.bind.expired");
        }
        cacheUtils.remove(attemptKey);
        eventRecorder.record(tenantId, OWNER_TYPE_USER, userId, DevMfaEventEnum.BIND_CONFIRM,
                bindId, "绑定成功", ip, userAgent);
    }

    /**
     * 校验动态码，返回命中的时间步；失败返回 -1。
     */
    private long verifyCode(DevMfa mfa, String code) {
        String secret;
        try {
            secret = totpSecretCipher.decrypt(mfa.getSecretCipher());
        } catch (Exception e) {
            log.error("解密 2FA secret 失败，tenantId={}, userId={}", mfa.getTenantId(), mfa.getOwnerId(), e);
            throw new CommonExceptionI18n("dev.mfa.data.error");
        }
        return TotpUtils.verify(secret, code, 0L,
                mfa.getDigits(), mfa.getPeriodSeconds(), mfaConfig.getTotp().getWindow());
    }

    private void handleVerifyFailure(DevMfaChallenge challenge, DevMfa mfa,
                                     String ip, String userAgent) {
        int maxAttempts = mfaConfig.getTotp().getMaxFailedAttempts();
        int usedAttempts = challenge.getAttempts() + 1;
        challenge.setAttempts(usedAttempts);

        int existingFailures = valueOrZero(mfa.getFailedAttempts());
        boolean lockExpired = mfa.getLockedUntil() != null && mfa.getLockedUntil().isBefore(LocalDateTime.now());
        int totalFailures = (lockExpired ? 0 : existingFailures) + 1;
        boolean shouldLock = totalFailures >= maxAttempts;
        LocalDateTime lockedUntil = shouldLock
                ? LocalDateTime.now().plus(mfaConfig.getTotp().getLockDuration())
                : null;

        incrementFailure(challenge.getTenantId(), challenge.getOwnerId(), maxAttempts, lockedUntil);
        eventRecorder.record(challenge.getTenantId(), OWNER_TYPE_USER, challenge.getOwnerId(),
                shouldLock ? DevMfaEventEnum.LOCKED : DevMfaEventEnum.VERIFY_FAIL,
                challenge.getReqId(), shouldLock ? "连续失败次数过多，已锁定" : "动态码校验失败", ip, userAgent);

        if (usedAttempts >= maxAttempts || shouldLock) {
            cacheUtils.remove(reqCacheKey(challenge.getReqId()));
        } else {
            cacheUtils.setObject(reqCacheKey(challenge.getReqId()), challenge, remainSeconds(challenge));
        }
    }

    private DevMfa selectMfa(Long tenantId, Long userId) {
        return devMfaMapper.selectOneByQuery(new QueryWrapper()
                .eq(DevMfa::getTenantId, tenantId)
                .eq(DevMfa::getOwnerType, OWNER_TYPE_USER)
                .eq(DevMfa::getOwnerId, userId));
    }

    /**
     * 发起绑定时写入或覆盖绑定记录，保证同一用户只有一条记录。
     */
    private void saveBinding(DevMfa mfa) {
        if (selectMfa(mfa.getTenantId(), mfa.getOwnerId()) == null) {
            mfa.setLastAcceptedStep(-1L);
            mfa.setClockOffsetSteps(0);
            mfa.setFailedAttempts(0);
            mfa.setDeleteFlag(0);
            devMfaMapper.insert(mfa);
            return;
        }
        UpdateChain.of(DevMfa.class)
                .set(DevMfa::getOwnerName, mfa.getOwnerName())
                .set(DevMfa::getSecretCipher, mfa.getSecretCipher())
                .set(DevMfa::getCipherVersion, mfa.getCipherVersion())
                .set(DevMfa::getStatus, mfa.getStatus())
                .set(DevMfa::getAlgorithm, mfa.getAlgorithm())
                .set(DevMfa::getDigits, mfa.getDigits())
                .set(DevMfa::getPeriodSeconds, mfa.getPeriodSeconds())
                .set(DevMfa::getLastAcceptedStep, -1L)
                .set(DevMfa::getClockOffsetSteps, 0)
                .set(DevMfa::getRecoveryHashes, null)
                .set(DevMfa::getFailedAttempts, 0)
                .set(DevMfa::getLockedUntil, null)
                .set(DevMfa::getBindId, mfa.getBindId())
                .set(DevMfa::getBindExpiresAt, mfa.getBindExpiresAt())
                .set(DevMfa::getActivatedAt, null)
                .set(DevMfa::getLastUsedAt, null)
                .set(DevMfa::getUpdateUser, mfa.getOwnerName())
                .set(DevMfa::getUpdateTime, LocalDateTime.now())
                .where(DevMfa::getTenantId).eq(mfa.getTenantId())
                .and(DevMfa::getOwnerType).eq(mfa.getOwnerType())
                .and(DevMfa::getOwnerId).eq(mfa.getOwnerId())
                .update();
    }

    /**
     * 绑定确认：只有 PENDING 且 bindId 匹配时才允许激活。
     */
    private int activateBinding(Long tenantId, Long userId, String bindId, long step, String updateUser) {
        LocalDateTime now = LocalDateTime.now();
        boolean updated = UpdateChain.of(DevMfa.class)
                .set(DevMfa::getStatus, DevMfaStatusEnum.ACTIVE.getValue())
                .set(DevMfa::getLastAcceptedStep, step)
                .set(DevMfa::getClockOffsetSteps, 0)
                .set(DevMfa::getActivatedAt, now)
                .set(DevMfa::getBindId, null)
                .set(DevMfa::getBindExpiresAt, null)
                .set(DevMfa::getFailedAttempts, 0)
                .set(DevMfa::getLockedUntil, null)
                .set(DevMfa::getUpdateUser, updateUser)
                .set(DevMfa::getUpdateTime, now)
                .where(DevMfa::getTenantId).eq(tenantId)
                .and(DevMfa::getOwnerType).eq(OWNER_TYPE_USER)
                .and(DevMfa::getOwnerId).eq(userId)
                .and(DevMfa::getStatus).eq(DevMfaStatusEnum.PENDING.getValue())
                .and(DevMfa::getBindId).eq(bindId)
                .update();
        return updated ? 1 : 0;
    }

    /**
     * 验证通过：last_accepted_step 单调递增，天然防重放。
     */
    private int markUsed(Long tenantId, Long userId, long step, int offset) {
        LocalDateTime now = LocalDateTime.now();
        boolean updated = UpdateChain.of(DevMfa.class)
                .set(DevMfa::getLastAcceptedStep, step)
                .set(DevMfa::getLastUsedAt, now)
                .set(DevMfa::getFailedAttempts, 0)
                .set(DevMfa::getLockedUntil, null)
                .set(DevMfa::getClockOffsetSteps, offset)
                .set(DevMfa::getUpdateTime, now)
                .where(DevMfa::getTenantId).eq(tenantId)
                .and(DevMfa::getOwnerType).eq(OWNER_TYPE_USER)
                .and(DevMfa::getOwnerId).eq(userId)
                .and(DevMfa::getStatus).eq(DevMfaStatusEnum.ACTIVE.getValue())
                .and(DevMfa::getLastAcceptedStep).lt(step)
                .update();
        return updated ? 1 : 0;
    }

    /**
     * 验证失败计数，用失败次数做乐观锁避免并发覆盖，达到上限时锁定。
     */
    private void incrementFailure(Long tenantId, Long userId, int maxAttempts, LocalDateTime lockedUntil) {
        for (int i = 0; i < 3; i++) {
            DevMfa current = selectMfa(tenantId, userId);
            if (current == null) {
                return;
            }
            int previous = valueOrZero(current.getFailedAttempts());
            boolean updated = UpdateChain.of(DevMfa.class)
                    .set(DevMfa::getFailedAttempts, previous + 1)
                    .set(DevMfa::getLockedUntil, previous + 1 >= maxAttempts ? lockedUntil : current.getLockedUntil())
                    .set(DevMfa::getUpdateTime, LocalDateTime.now())
                    .where(DevMfa::getTenantId).eq(tenantId)
                    .and(DevMfa::getOwnerType).eq(OWNER_TYPE_USER)
                    .and(DevMfa::getOwnerId).eq(userId)
                    .and(DevMfa::getFailedAttempts).eq(previous)
                    .update();
            if (updated) {
                return;
            }
        }
    }

    private String createChallenge(DevMfaLoginCheckParamDTO param, String purpose, String bindId) {
        DevMfaChallenge challenge = new DevMfaChallenge();
        challenge.setReqId(UUID.randomUUID().toString().replace("-", ""));
        challenge.setTenantId(param.getTenantId());
        challenge.setOwnerType(OWNER_TYPE_USER);
        challenge.setOwnerId(param.getUserId());
        challenge.setUsername(param.getUsername());
        challenge.setDevice(param.getDevice());
        challenge.setDeviceId(param.getDeviceId());
        challenge.setPurpose(purpose);
        challenge.setBindId(bindId);
        challenge.setIp(param.getIp());
        challenge.setUserAgent(param.getUserAgent());
        challenge.setExpireAt(System.currentTimeMillis() + requestTtlSeconds() * 1000L);
        challenge.setAttempts(0);
        cacheUtils.setObject(reqCacheKey(challenge.getReqId()), challenge, requestTtlSeconds());
        return challenge.getReqId();
    }

    private DevMfaChallenge requireChallenge(String mfaReqId, Long userId) {
        if (mfaReqId == null || mfaReqId.isBlank() || userId == null) {
            throw new CommonExceptionI18n("dev.mfa.req.invalid");
        }
        DevMfaChallenge challenge = cacheUtils.get(reqCacheKey(mfaReqId), DevMfaChallenge.class);
        if (challenge == null) {
            throw new CommonExceptionI18n("dev.mfa.req.expired");
        }
        if (!Objects.equals(challenge.getOwnerId(), userId)) {
            throw new CommonExceptionI18n("dev.mfa.req.invalid");
        }
        if (challenge.getExpireAt() <= System.currentTimeMillis()) {
            cacheUtils.remove(reqCacheKey(mfaReqId));
            throw new CommonExceptionI18n("dev.mfa.req.expired");
        }
        return challenge;
    }

    private void assertNotLocked(DevMfa mfa) {
        if (mfa.getLockedUntil() == null || mfa.getLockedUntil().isBefore(LocalDateTime.now())) {
            return;
        }
        long minutes = Math.max(1, ChronoUnit.MINUTES.between(LocalDateTime.now(), mfa.getLockedUntil()) + 1);
        throw new CommonExceptionI18n("dev.mfa.locked", minutes);
    }

    private NuoCaoMfaEnforcementEnum currentEnforcement() {
        Integer value = devConfigApi.getIntegerValue(CONFIG_KEY, NuoCaoMfaEnforcementEnum.OFF.getValue());
        return NuoCaoMfaEnforcementEnum.ofValue(value);
    }

    private int requestTtlSeconds() {
        return (int) Math.max(1, mfaConfig.getRequestTtl().getSeconds());
    }

    private int remainSeconds(DevMfaChallenge challenge) {
        return Math.max(1, (int) ((challenge.getExpireAt() - System.currentTimeMillis()) / 1000));
    }

    private int remainSeconds(LocalDateTime expiresAt) {
        return Math.max(1, (int) Duration.between(LocalDateTime.now(), expiresAt).getSeconds());
    }

    private String reqCacheKey(String reqId) {
        return REQ_CACHE_PREFIX + reqId;
    }

    private String resolveAccount(String username, Long userId) {
        return username == null || username.isBlank() ? String.valueOf(userId) : username;
    }

    private int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    private String statusName(Integer status) {
        DevMfaStatusEnum statusEnum = DevMfaStatusEnum.ofValue(status);
        if (statusEnum == null) {
            return "";
        }
        switch (statusEnum) {
            case PENDING:
                return "绑定中";
            case ACTIVE:
                return "已启用";
            case DISABLED:
                return "已失效";
            default:
                return "";
        }
    }
}
