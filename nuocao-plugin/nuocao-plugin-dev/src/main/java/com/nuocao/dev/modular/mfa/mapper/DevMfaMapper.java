package com.nuocao.dev.modular.mfa.mapper;

import com.nuocao.common.mapper.BaseMapperX;
import com.nuocao.dev.modular.mfa.entity.DevMfa;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

/**
 * 2FA 绑定 Mapper。
 * <p>表的唯一性依赖数据库主键 (tenant_id, owner_type, owner_id)，所有写操作都使用显式 SQL，
 * 并且只改需要改的列，避免并发下互相覆盖。
 **/
@Mapper
public interface DevMfaMapper extends BaseMapperX<DevMfa> {

    @Select("SELECT * FROM dev_mfa WHERE tenant_id = #{tenantId} AND owner_type = #{ownerType} " +
            "AND owner_id = #{ownerId} AND delete_flag = 0")
    DevMfa selectByOwner(@Param("tenantId") Long tenantId,
                         @Param("ownerType") String ownerType,
                         @Param("ownerId") Long ownerId);

    @Select("SELECT * FROM dev_mfa WHERE tenant_id = #{tenantId} AND owner_type = #{ownerType} " +
            "AND owner_id = #{ownerId} AND delete_flag = 0 FOR UPDATE")
    DevMfa selectByOwnerForUpdate(@Param("tenantId") Long tenantId,
                                  @Param("ownerType") String ownerType,
                                  @Param("ownerId") Long ownerId);

    /**
     * 发起绑定：不存在则插入，已存在（尤其是逻辑删除过的历史行）则原地覆盖并复活。
     */
    @Insert("INSERT INTO dev_mfa (" +
            "tenant_id, owner_type, owner_id, owner_name, secret_cipher, cipher_version, status, algorithm, " +
            "digits, period_seconds, last_accepted_step, clock_offset_steps, recovery_hashes, failed_attempts, " +
            "locked_until, bind_id, bind_expires_at, activated_at, last_used_at, delete_flag, " +
            "create_user, create_time, update_user, update_time" +
            ") VALUES (" +
            "#{tenantId}, #{ownerType}, #{ownerId}, #{ownerName}, #{secretCipher}, #{cipherVersion}, #{status}, #{algorithm}, " +
            "#{digits}, #{periodSeconds}, -1, 0, NULL, 0, NULL, #{bindId}, #{bindExpiresAt}, NULL, NULL, 0, " +
            "#{ownerName}, NOW(), #{ownerName}, NOW()" +
            ") ON DUPLICATE KEY UPDATE " +
            "owner_name = VALUES(owner_name), secret_cipher = VALUES(secret_cipher), " +
            "cipher_version = VALUES(cipher_version), status = VALUES(status), algorithm = VALUES(algorithm), " +
            "digits = VALUES(digits), period_seconds = VALUES(period_seconds), last_accepted_step = -1, " +
            "clock_offset_steps = 0, recovery_hashes = NULL, failed_attempts = 0, locked_until = NULL, " +
            "bind_id = VALUES(bind_id), bind_expires_at = VALUES(bind_expires_at), " +
            "activated_at = NULL, last_used_at = NULL, delete_flag = 0, " +
            "update_user = VALUES(update_user), update_time = NOW()")
    int savePending(DevMfa mfa);

    /**
     * 绑定确认：只有 PENDING 且 bindId 匹配时才允许激活。
     */
    @Update("UPDATE dev_mfa SET status = 9, last_accepted_step = #{step}, clock_offset_steps = 0, " +
            "activated_at = NOW(), bind_id = NULL, bind_expires_at = NULL, " +
            "failed_attempts = 0, locked_until = NULL, update_time = NOW(), update_user = #{updateUser} " +
            "WHERE tenant_id = #{tenantId} AND owner_type = #{ownerType} AND owner_id = #{ownerId} " +
            "AND status = 1 AND bind_id = #{bindId} AND delete_flag = 0")
    int activate(@Param("tenantId") Long tenantId,
                 @Param("ownerType") String ownerType,
                 @Param("ownerId") Long ownerId,
                 @Param("bindId") String bindId,
                 @Param("step") long step,
                 @Param("updateUser") String updateUser);

    /**
     * 验证通过：last_accepted_step 单调递增，天然防重放。
     */
    @Update("UPDATE dev_mfa SET last_accepted_step = #{step}, last_used_at = NOW(), " +
            "failed_attempts = 0, locked_until = NULL, clock_offset_steps = #{offset}, update_time = NOW() " +
            "WHERE tenant_id = #{tenantId} AND owner_type = #{ownerType} AND owner_id = #{ownerId} " +
            "AND status = 9 AND delete_flag = 0 AND last_accepted_step < #{step}")
    int markUsed(@Param("tenantId") Long tenantId,
                 @Param("ownerType") String ownerType,
                 @Param("ownerId") Long ownerId,
                 @Param("step") long step,
                 @Param("offset") int offset);

    /**
     * 验证失败计数。MySQL 赋值从左到右生效，locked_until 判断的是自增后的失败次数。
     */
    @Update("UPDATE dev_mfa SET failed_attempts = failed_attempts + 1, " +
            "locked_until = IF(failed_attempts >= #{maxAttempts}, #{lockedUntil}, locked_until), " +
            "update_time = NOW() " +
            "WHERE tenant_id = #{tenantId} AND owner_type = #{ownerType} AND owner_id = #{ownerId} " +
            "AND delete_flag = 0")
    int incrementFailure(@Param("tenantId") Long tenantId,
                         @Param("ownerType") String ownerType,
                         @Param("ownerId") Long ownerId,
                         @Param("maxAttempts") int maxAttempts,
                         @Param("lockedUntil") LocalDateTime lockedUntil);

    /**
     * 管理员重置失败次数并解锁。
     */
    @Update("UPDATE dev_mfa SET failed_attempts = 0, locked_until = NULL, update_time = NOW(), " +
            "update_user = #{updateUser} " +
            "WHERE tenant_id = #{tenantId} AND owner_type = #{ownerType} AND owner_id = #{ownerId} " +
            "AND delete_flag = 0")
    int resetFailure(@Param("tenantId") Long tenantId,
                     @Param("ownerType") String ownerType,
                     @Param("ownerId") Long ownerId,
                     @Param("updateUser") String updateUser);

    /**
     * 管理员失效绑定：状态改为 DISABLED，清空绑定流程数据，保留密文供审计。
     */
    @Update("UPDATE dev_mfa SET status = 4, bind_id = NULL, bind_expires_at = NULL, " +
            "failed_attempts = 0, locked_until = NULL, update_time = NOW(), update_user = #{updateUser} " +
            "WHERE tenant_id = #{tenantId} AND owner_type = #{ownerType} AND owner_id = #{ownerId} " +
            "AND delete_flag = 0")
    int disable(@Param("tenantId") Long tenantId,
                @Param("ownerType") String ownerType,
                @Param("ownerId") Long ownerId,
                @Param("updateUser") String updateUser);

    /**
     * 管理员删除绑定：逻辑删除；用户下次绑定时由 savePending 原地复活，不会产生重复记录。
     */
    @Update("UPDATE dev_mfa SET delete_flag = 1, update_time = NOW(), update_user = #{updateUser} " +
            "WHERE tenant_id = #{tenantId} AND owner_type = #{ownerType} AND owner_id = #{ownerId} " +
            "AND delete_flag = 0")
    int logicDelete(@Param("tenantId") Long tenantId,
                    @Param("ownerType") String ownerType,
                    @Param("ownerId") Long ownerId,
                    @Param("updateUser") String updateUser);
}
