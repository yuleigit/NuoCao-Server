package com.nuocao.dev.modular.config.provider;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.nuocao.dev.modular.config.service.DevConfigService;
import nuocao.plugin.dev.api.DevConfigApi;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Component;

import java.util.Map;

/**
 * 配置API接口实现类
 **/
@Component
public class DevConfigApiProvider implements DevConfigApi {

    @Inject
    private DevConfigService devConfigService;

    @Override
    public String getValueByKey(String key) {
        return devConfigService.getValueByKey(key);
    }

    @Override
    public Integer getIntegerValue(String key) {
        return devConfigService.getIntegerValueByKey(key,null);
    }

    @Override
    public Integer getIntegerValue(String key,Integer defaultValue) {
        return devConfigService.getIntegerValueByKey(key,defaultValue);
    }

    @Override
    public Boolean getBooleanValue(String key,Boolean defaultValue) {
        return devConfigService.getBooleanValueByKey(key,defaultValue);
    }

    @Override
    public Map<String, Object> getValueToMap(String key) {
        return devConfigService.getValueToMap(key);
    }

    @Override
    public boolean isTrueByKey(String key){
       return this.getBooleanValue(key,false);
    }
}
