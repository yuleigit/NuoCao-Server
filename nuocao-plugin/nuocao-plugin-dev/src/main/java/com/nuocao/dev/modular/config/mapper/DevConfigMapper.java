package com.nuocao.dev.modular.config.mapper;

import com.mybatisflex.core.BaseMapper;
import com.nuocao.common.mapper.BaseMapperX;
import com.nuocao.dev.modular.config.entity.DevConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置Mapper接口
 **/
@Mapper
public interface DevConfigMapper extends BaseMapperX<DevConfig> {
}
