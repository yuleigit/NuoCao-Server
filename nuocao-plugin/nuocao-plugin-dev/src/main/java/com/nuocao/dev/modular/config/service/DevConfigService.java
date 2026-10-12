package com.nuocao.dev.modular.config.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.nuocao.common.page.PageResult;
import com.nuocao.dev.modular.config.entity.DevConfig;
import com.nuocao.dev.modular.config.param.*;

import java.util.List;
import java.util.Map;

/**
 * 配置Service接口
 * TODO:关于多语言扩展的想法
 * 目前所有值都以字符串存储，后续可以根据Context中的语言类型对应存储表中的值
 * 方案1：将value改为键值对如{zh:"中文",en:"英文“}的形式存储
 * 方案2：增加config_lang列，每种语言单独配置，缓存中增加
 **/
public interface DevConfigService {

    /**
     * 根据键获取值
     **/
    String getValueByKey(String key);


    /**
     * 获取布尔型值
     * @param key
     * @param defaultValue
     * @return
     */
    Boolean getBooleanValueByKey(String key,boolean defaultValue);

    /**
     * 获取整数型数据
     * @param key
     * @param defaultValue
     * @return
     */
    Integer getIntegerValueByKey(String key,Integer defaultValue);

    /**
     * 获取长整数型数据
     * @param key
     * @param defaultValue
     * @return
     */
    Long getLongValueByKey(String key,Long defaultValue);


    /**
     * 获取配置并转为map独享
     * @param key
     * @return 没有合适的或转换错误返回null
     */
    Map<String,Object> getValueToMap(String key);


    /**
     * 获取配置分页
     * @return 仅返回OTHRE配置
     */
    PageResult<DevConfig> page(DevConfigPageParam devConfigPageParam);


    /**
     * 获取基础配置列表
     */
    List<DevConfig> sysConfigList();

    /**
     * 获取配置列表
     */
    List<DevConfig> list(DevConfigListParam devConfigListParam);


    /**
     * 添加配置
     */
    void add(DevConfigAddParam devConfigAddParam);

    /**
     * 编辑配置
     */
    void edit(DevConfigEditParam devConfigEditParam);

    /**
     * 删除配置
     */
    void deleteByIds(List<Long> configIds);

    /**
     * 获取配置详情
     */
    DevConfig detail(DevConfigIdParam devConfigIdParam);

    /**
     * 获取配置详情
     */
    DevConfig queryEntity(Long configId);

    /**
     * 配置批量更新
     **/
    void editBatch(List<DevConfigBatchParam> devConfigBatchParamList);
}
