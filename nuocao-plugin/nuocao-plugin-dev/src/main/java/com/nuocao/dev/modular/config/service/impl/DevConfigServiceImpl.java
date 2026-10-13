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
import com.nuocao.dev.modular.config.result.DevConfigKeyValueResult;
import com.nuocao.plugin.dev.api.enums.DevConfigSceneEnum;
import com.nuocao.dev.modular.config.mapper.DevConfigMapper;
import com.nuocao.dev.modular.config.param.*;
import com.nuocao.dev.modular.config.service.DevConfigService;
import com.nuocao.plugin.dev.api.DevSmsApi;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Transaction;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统配置Service接口实现类
 **/
@Component
@Slf4j
public class DevConfigServiceImpl  implements DevConfigService {


    private static final String OTHER_CONFIG_GROUP="other";

    private static final String CONFIG_CACHE_KEY = "dev-config:";
    private static final String CONFIG_CACHE_MAP_KEY = "dev-config-map:";

    private static final Set<String> ALLOW_BLANK_KEY_SET=Set.of(new String[]{"dev_file_aliyun_bind_domain_url","dev_file_tencent_bind_domain_url"});

    @Inject
    private DevConfigMapper mapper;

    @Inject
    private CacheUtils cacheUtils;

    @Inject
    private DevSmsApi devSmsApi;

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
    public Long getLongValueByKey(String key, Long defaultValue) {
        return ConvertUtils.toLong(this.getValueByKey(key),defaultValue);
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
    public List<DevConfigKeyValueResult> sysConfigList() {
        LambdaQueryWrapperX<DevConfig> query = new LambdaQueryWrapperX<>();
        query.selectX(DevConfig::getConfigGroup,DevConfig::getConfigValue)
                .eqX(DevConfig::getConfigCategory,DevConfigCategoryEnum.SYS_CONFIG.getValue());
        return mapper.selectListByQueryAs(query,DevConfigKeyValueResult.class);
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
        if(StringUtils.isBlank(devConfig.getConfigGroup())){
            devConfig.setConfigKey(OTHER_CONFIG_GROUP);
        }
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

        //1. 得到所有key，一次性从数据库中拿到所有配置
        List<String> configKeys = devConfigBatchParamList.stream().map(DevConfigBatchParam::getConfigKey).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(configKeys)){
            throw new CommonExceptionI18n("dev.config.notExists");
        }
        List<DevConfig> configList = mapper.selectList(DevConfig::getConfigKey,configKeys);
        if(CollectionUtils.isEmpty(configKeys)){
            throw new CommonExceptionI18n("dev.config.notExists",configKeys.get(0));
        }

        Map<String,DevConfig> oldConfigMap = CollectionUtils.convertMap(configList,DevConfig::getConfigKey);

        //2.验证填写准确性

        devConfigBatchParamList.forEach(item->{
            //2.1 是否存在
            if(!oldConfigMap.containsKey(item.getConfigKey())){
                throw new CommonExceptionI18n("dev.config.notExists",item.getConfigKey());
            }
            DevConfig config = oldConfigMap.get(item.getConfigKey());
            //2.2 得到是不是信息场景配置中的内容
            DevConfigSceneEnum.ConfigKeyRef sceneConfigRef =  DevConfigSceneEnum.ofConfigKey(item.getConfigKey());
            //2.2 是否为空，在允许为空白名单中、短信验证码类可以为空
            if(! ALLOW_BLANK_KEY_SET.contains(item.getConfigKey()) || (sceneConfigRef==null || !sceneConfigRef.getMeta().isRequired())){
                if(StringUtils.isBlank(item.getConfigValue())){
                    throw new CommonExceptionI18n("dev.config.value.notEmpty",config.getRemark()==null?"":config.getRemark());
                }
            }
            //2.3 看是不是
            if(sceneConfigRef!=null){
                //验证选中模板的可用性
                devSmsApi.availabilitySmsTemplate(ConvertUtils.toLong(item.getConfigValue()),sceneConfigRef.getMeta().getVariables());
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
