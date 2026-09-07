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
