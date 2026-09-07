package com.nuocao.dev.modular.sms.log.convert;

import com.nuocao.dev.modular.sms.log.entity.DevSmsLog;
import com.nuocao.dev.modular.sms.log.param.DevSmsLogAddParam;
import org.mapstruct.factory.Mappers;

/**
 * 短信日志 转换器
 */
public interface DevSmsLogConvert {
    DevSmsLogConvert INSTANCE = Mappers.getMapper(DevSmsLogConvert.class);

    DevSmsLog convert(DevSmsLogAddParam bean);



}
