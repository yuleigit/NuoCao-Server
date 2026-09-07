package com.nuocao.dev.modular.dict.convert;

import com.nuocao.dev.modular.config.entity.DevConfig;
import com.nuocao.dev.modular.config.param.DevConfigAddParam;
import com.nuocao.dev.modular.config.param.DevConfigEditParam;
import com.nuocao.dev.modular.dict.entity.DevDict;
import com.nuocao.dev.modular.dict.param.DevDictAddParam;
import com.nuocao.dev.modular.dict.param.DevDictEditParam;
import com.nuocao.dev.modular.dict.param.DevDictListParam;
import com.nuocao.dev.modular.dict.param.DevDictPageParam;
import com.nuocao.dev.modular.dict.result.DevDictTreeResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 字典 转换器
 */
@Mapper
public interface DevDictConvert {

    DevDictConvert INSTANCE = Mappers.getMapper(DevDictConvert.class);


    DevDict convert(DevDictAddParam bean);

    DevDict convert(DevDictEditParam bean);

    DevDictPageParam convertPageParam(DevDictListParam bean);

    List<DevDictTreeResult> convertTreeResultList(List<DevDict> devDictList);

}
