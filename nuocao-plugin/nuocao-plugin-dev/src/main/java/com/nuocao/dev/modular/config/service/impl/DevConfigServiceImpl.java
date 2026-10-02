package com.nuocao.dev.modular.config.service.impl;


import com.alibaba.excel.util.StringUtils;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.nuocao.common.cache.CacheUtils;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.mapper.LambdaQueryWrapperX;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.utils.ConvertUtils;
import com.nuocao.common.utils.NumberUtils;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.common.utils.json.JsonUtils;
import com.nuocao.dev.modular.config.convert.DevConfigConvert;
import com.nuocao.dev.modular.config.entity.DevConfig;
import com.nuocao.dev.modular.config.enums.DevConfigCategoryEnum;
import com.nuocao.dev.modular.config.mapper.DevConfigMapper;
import com.nuocao.dev.modular.config.param.*;
import com.nuocao.dev.modular.config.service.DevConfigService;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Transaction;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 系统配置Service接口实现类
 **/
@Component
@Slf4j
public class DevConfigServiceImpl  implements DevConfigService {




    private static final String CONFIG_CACHE_KEY = "dev-config:";
    private static final String CONFIG_CACHE_MAP_KEY = "dev-config-map:";

    private static final Set<String> ALLOW_BLANK_KEY_SET=Set.of(new String[]{"dev_file_aliyun_bind_domain_url","dev_file_tencent_bind_domain_url"});

    @Inject
    private DevConfigMapper mapper;

    @Inject
    private CacheUtils cacheUtils;

    @Override
    public String getValueByKey(String key) {
        return cacheUtils.getOrSetObject(CONFIG_CACHE_KEY + key,String.class,()->{
            String value = mapper.selectObjectByQueryAs(new QueryWrapper().eq(DevConfig::getConfigKey,key).select(DevConfig::getConfigValue),String.class);
            return value;
        });
    }

    @Override
    public Boolean getBooleanValueByKey(String key, boolean defaultValue) {
        return  ConvertUtils.toBool(this.getValueByKey(key),defaultValue);
    }

    @Override
    public Integer getIntegerValueByKey(String key, Integer defaultValue) {
        return ConvertUtils.toInt(this.getValueByKey(key),defaultValue);
    }

    @Override
    public Map<String, Object> getValueToMap(String key) {
        String cacheKey = CONFIG_CACHE_MAP_KEY + key;
        Map<String,Object> mapCache = cacheUtils.get(cacheKey,Map.class);
        if(mapCache!=null){
            return mapCache;
        }
        String value = getValueByKey(key);
        try
        {
            mapCache = JsonUtils.parseObject(value,Map.class);
            //写入缓存
        }catch (Exception ex){
            log.error("配置信息转MAP失败,{}",key);
        }
        if(mapCache==null){
            return null;
        }
        cacheUtils.setObject(cacheKey,mapCache);
        return mapCache;
    }

    @Override
    public PageResult<DevConfig> page(DevConfigPageParam devConfigPageParam) {

        // 查询部分字段
        LambdaQueryWrapperX<DevConfig> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.likeIfPresent(DevConfig::getConfigKey, devConfigPageParam.getConfigKey());
        queryWrapper.select(DevConfig::getId,DevConfig::getConfigGroup,  DevConfig::getConfigKey, DevConfig::getConfigValue,
                DevConfig::getConfigCategory, DevConfig::getRemark, DevConfig::getSortCode);
       if(!devConfigPageParam.hasSortingFields()) {
            queryWrapper.orderBy(DevConfig::getSortCode, true);
        }
        queryWrapper.eq(DevConfig::getConfigCategory, DevConfigCategoryEnum.OTHER.getValue());
        return mapper.selectPage(devConfigPageParam, queryWrapper);
    }

    @Override
    public List<DevConfig> sysConfigList() {
        DevConfigListParam devConfigListParam = new DevConfigListParam();
        devConfigListParam.setConfigCategory(DevConfigCategoryEnum.SYS_CONFIG.getValue());
        return this.list(devConfigListParam);
    }

    @Override
    public List<DevConfig> list(DevConfigListParam devConfigListParam) {
        LambdaQueryWrapperX<DevConfig> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.likeIfPresent(DevConfig::getConfigCategory, devConfigListParam.getConfigCategory());
        // 查询部分字段
        queryWrapper.select(DevConfig::getConfigGroup, DevConfig::getConfigKey, DevConfig::getConfigValue,
                DevConfig::getConfigCategory, DevConfig::getSortCode).orderBy(DevConfig::getSortCode);

        return mapper.selectListByQuery(queryWrapper);
    }

    @Override
    public void add(DevConfigAddParam devConfigAddParam) {
        DevConfig devConfig = DevConfigConvert.INSTANCE.convert(devConfigAddParam);
        validInfo(devConfig);
        mapper.insert(devConfig);
    }

    @Override
    public void edit(DevConfigEditParam devConfigEditParam) {
        DevConfig oldConfig = queryEntity(devConfigEditParam.getId());
        DevConfig devConfig = DevConfigConvert.INSTANCE.convert(devConfigEditParam);
        validInfo(devConfig);
        mapper.updateById(devConfig);
        removeCache(oldConfig.getConfigKey());
    }

    @Transaction
    @Override
    public void deleteByIds(List<Long> configIds) {
        if(CollectionUtils.isEmpty(configIds)) return;
        //1.先得到所有选中配置
        List<DevConfig> configList = mapper.selectListByIds(configIds);
        if(CollectionUtils.isEmpty(configIds)) return;
        for (DevConfig config:
            configList ) {
            if(!DevConfigCategoryEnum.OTHER.getValue().equals(config.getConfigCategory()))
            {
                throw new CommonExceptionI18n("dev.config.delete.disable",config.getConfigKey());
            }
        }
        mapper.deleteBatchByIds(configIds);
    }

    @Override
    public DevConfig detail(DevConfigIdParam devConfigIdParam) {
        return this.queryEntity(devConfigIdParam.getId());
    }

    @Override
    public DevConfig queryEntity(Long configId) {
        DevConfig devConfig = mapper.selectOneById(configId);
        CommonExceptionI18n.notNull(devConfig,"dev.config.notExists",configId);

        return devConfig;
    }

    @Override
    public void editBatch(List<DevConfigBatchParam> devConfigBatchParamList) {

        devConfigBatchParamList.forEach(item->{
            if(! ALLOW_BLANK_KEY_SET.contains(item.getConfigKey())){
                if(StringUtils.isBlank(item.getConfigValue())){
                    QueryWrapper query = new QueryWrapper();
                    query.select(DevConfig::getRemark);
                    query.eq(DevConfig::getConfigKey,item.getConfigKey());
                    String name = mapper.selectObjectByQueryAs(query,String.class);
                    throw new CommonExceptionI18n("dev.config.value.notEmpty",name==null?"":name);
                }
            }
        });

        devConfigBatchParamList.forEach(devConfigBatchParam -> {
            UpdateChain.of(DevConfig.class)
                    .set(DevConfig::getConfigValue, devConfigBatchParam.getConfigValue())
                    .where(DevConfig::getConfigKey).eq(devConfigBatchParam.getConfigKey())
                    .update();
            // 移除对应的缓存
            removeCache(devConfigBatchParam.getConfigKey());
        });
    }


    private void removeCache(String configKey){
        cacheUtils.remove(CONFIG_CACHE_KEY + configKey);
        cacheUtils.remove(CONFIG_CACHE_MAP_KEY + configKey);

    }

    private void validInfo(DevConfig info) {
        //如果不是修改，则验证key是否存在，是修改则不允许修改key
        if(NumberUtils.isLeZero(info.getId())) {
            DevConfig temp = mapper.selectOne(DevConfig::getConfigKey, info.getConfigKey());
            if (temp != null && !temp.getId().equals(info.getId())) {
                throw new CommonExceptionI18n("dev.config.existsKey", info.getConfigKey());
            }
        }else{
            info.setConfigKey(null);
        }

        //如果新增且没有设置目录，则写入配置信息
        if (info.getId() == null && StringUtils.isBlank(info.getConfigCategory())) {
            info.setConfigCategory(DevConfigCategoryEnum.OTHER.getValue());
        }
    }

}
