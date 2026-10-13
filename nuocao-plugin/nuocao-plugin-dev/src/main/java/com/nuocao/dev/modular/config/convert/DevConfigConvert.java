package com.nuocao.dev.modular.config.convert;

import com.nuocao.dev.modular.config.entity.DevConfig;
import com.nuocao.dev.modular.config.param.*;
import com.nuocao.dev.modular.config.result.DevConfigKeyValueResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 系统配置 转换器
 */
@Mapper
public interface DevConfigConvert {

    DevConfigConvert INSTANCE = Mappers.getMapper(DevConfigConvert.class);


    DevConfig convert(DevConfigAddParam bean);

    DevConfig convert(DevConfigEditParam bean);

    List<DevConfigKeyValueResult> convertKeyValueList(List<DevConfig> list);

}
