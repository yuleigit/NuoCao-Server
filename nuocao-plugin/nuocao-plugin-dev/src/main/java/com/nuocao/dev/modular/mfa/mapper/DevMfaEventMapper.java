package com.nuocao.dev.modular.mfa.mapper;

import com.nuocao.common.mapper.BaseMapperX;
import com.nuocao.dev.modular.mfa.entity.DevMfaEvent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 2FA 操作事件 Mapper
 **/
@Mapper
public interface DevMfaEventMapper extends BaseMapperX<DevMfaEvent> {
}
