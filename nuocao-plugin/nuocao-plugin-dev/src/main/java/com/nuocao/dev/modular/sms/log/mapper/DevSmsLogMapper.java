package com.nuocao.dev.modular.sms.log.mapper;

import com.nuocao.common.mapper.BaseMapperX;
import com.nuocao.dev.modular.sms.log.entity.DevSmsLog;
import org.noear.solon.annotation.Component;

/**
 * 短信Mapper接口
 **/
@Component //可能是因为在子目录，app.yml中配置扫描不到，加个这个，一般不用加
public interface DevSmsLogMapper extends BaseMapperX<DevSmsLog> {
}
