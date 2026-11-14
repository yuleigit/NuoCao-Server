package com.nuocao.dev.modular.mfa.mapper;

import com.nuocao.common.mapper.BaseMapperX;
import com.nuocao.dev.modular.mfa.entity.DevMfa;

/**
 * 2FA 绑定 Mapper。
 * <p>表主键为 (tenant_id, owner_type, owner_id)，统一使用 MyBatis-Flex 的
 * QueryWrapper / UpdateChain 完成读写，不再手写 SQL。
 **/
public interface DevMfaMapper extends BaseMapperX<DevMfa> {
}
