package com.nuocao.dev.modular.sms.template.service;

import com.nuocao.common.page.PageResult;
import com.nuocao.dev.modular.sms.template.entity.DevSmsTemplate;
import com.nuocao.dev.modular.sms.template.param.DevSmsTemplateEditParam;
import com.nuocao.dev.modular.sms.template.param.DevSmsTemplatePageParam;
import com.nuocao.dev.modular.sms.template.param.DevSmsTemplateAddParam;
import com.nuocao.dev.modular.sms.template.param.DevSmsTemplateIdParam;
import com.nuocao.plugin.dev.api.dto.sms.DevSmsTemplateDTO;
import com.nuocao.plugin.dev.api.enums.DevConfigSceneEnum;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * 短信模板Service接口
 **/
public interface DevSmsTemplateService {

    /**
     * 获取短信模板分页
     */
    PageResult<DevSmsTemplate> page(DevSmsTemplatePageParam pageParams);


    /**
     * 添加短信模板
     */
    Long add(DevSmsTemplateAddParam addParam);

    /**
     * 编辑短信模板
     */
    void edit(DevSmsTemplateEditParam editParam);

    /**
     * 删除短信模板
     */
    void deleteByIds(List<Long> ids);

    /**
     * 获取短信模板详情
     */
    DevSmsTemplate detail(DevSmsTemplateIdParam devSmsTemplateIdParam);

    /**
     * 获取短信模板详情
     */
    DevSmsTemplate queryEntity(Long templateId);

    /**
     * 根据通道编码和模板编码获取模板
     * @param channelCode
     * @param templateCode
     * @return
     */
    DevSmsTemplateDTO getDtoByChannelAndCode(String channelCode, String templateCode);

    /**
     * 根据通道编码和模板编码获取模板
     * @param channelCode
     * @param templateCode
     * @return
     */
    DevSmsTemplateDTO getAndValidateDtoByChannelAndCode(String channelCode, String templateCode);

    /**
     * 根据场景获取模板实体
     * @param sceneEnum
     * @return
     */
    DevSmsTemplateDTO getDtoByScene(DevConfigSceneEnum sceneEnum);

    /**
     * 根据场景获取模板实体
     * @param sceneEnum
     * @return
     */
    DevSmsTemplateDTO getAndValidateDtoByScene(DevConfigSceneEnum sceneEnum);


    void validParams(DevSmsTemplateDTO dto,LinkedHashMap<String,String> inputTemplateParams);

    /**
     * 使用参数整合内容
     * @param dto
     * @param inputTemplateParams 用户输入的模板内容
     * @return
     */
    String buildTemplateContent(DevSmsTemplateDTO dto, LinkedHashMap<String,String> inputTemplateParams);

    /**
     * 验证模板可用性，不可用抛出异常,配合config配置时使用
     * @param templateId 模板编码
     * @param variables 模板需要携带的变量
     *
     */
    void availabilitySmsTemplate(Long templateId, Set<String> variables);





}
