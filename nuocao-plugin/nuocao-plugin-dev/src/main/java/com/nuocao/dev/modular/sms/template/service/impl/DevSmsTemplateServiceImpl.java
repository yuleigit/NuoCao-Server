package com.nuocao.dev.modular.sms.template.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.mapper.LambdaQueryWrapperX;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.utils.ConvertUtils;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.dev.modular.sms.template.convert.DevSmsTemplateConvert;
import com.nuocao.dev.modular.sms.template.entity.DevSmsTemplate;
import com.nuocao.dev.modular.sms.template.mapper.DevSmsTemplateMapper;
import com.nuocao.dev.modular.sms.template.param.DevSmsTemplateAddParam;
import com.nuocao.dev.modular.sms.template.param.DevSmsTemplateEditParam;
import com.nuocao.dev.modular.sms.template.param.DevSmsTemplatePageParam;
import com.nuocao.dev.modular.sms.template.service.DevSmsTemplateService;
import com.nuocao.dev.modular.sms.template.param.DevSmsTemplateIdParam;
import lombok.extern.slf4j.Slf4j;
import com.nuocao.plugin.dev.api.dto.sms.DevSmsTemplateDTO;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 短信模板 Service接口实现类
 **/
@Component
@Slf4j
public class DevSmsTemplateServiceImpl implements DevSmsTemplateService {

    @Inject
    private DevSmsTemplateMapper devSmsTemplateMapper;



    @Override
    public PageResult<DevSmsTemplate> page(DevSmsTemplatePageParam pageParams) {

        return devSmsTemplateMapper.selectPage(pageParams,buildQueryWrapper(pageParams));
    }

    @Override
    public Long add(DevSmsTemplateAddParam addParam) {
        DevSmsTemplate template = DevSmsTemplateConvert.INSTANCE.convert(addParam);
        validInfo(template);
        devSmsTemplateMapper.insert(template);
        return template.getId();
    }

    @Override
    public void edit(DevSmsTemplateEditParam devDictEditParam) {
        DevSmsTemplate template = DevSmsTemplateConvert.INSTANCE.convert(devDictEditParam);
        validInfo(template);
        devSmsTemplateMapper.updateById(template);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)) return;
        devSmsTemplateMapper.deleteBatchByIds(ids);
    }

    @Override
    public DevSmsTemplate detail(DevSmsTemplateIdParam devSmsTemplateIdParam) {
        return queryEntity(devSmsTemplateIdParam.getTemplateId());
    }

    @Override
    public DevSmsTemplate queryEntity(Long templateId) {
        DevSmsTemplate template = devSmsTemplateMapper.selectOneById(templateId);
        CommonExceptionI18n.notNull(template,"dev.sms.template.notExists",templateId);
        return template;
    }

    @Override
    public DevSmsTemplateDTO getDtoByChannelAndCode(String channelCode, String templateCode) {
        DevSmsTemplate template = devSmsTemplateMapper.selectOne(DevSmsTemplate::getChannelCode,channelCode,DevSmsTemplate::getCode,templateCode);
        return DevSmsTemplateConvert.INSTANCE.convertDto(template);
    }

    @Override
    public DevSmsTemplateDTO getAndValidDtoByChannelAndCode(String channelCode, String templateCode) {
        DevSmsTemplateDTO template = getDtoByChannelAndCode(channelCode,templateCode);
        CommonExceptionI18n.notNull(template,"dev.sms.template.notExists",channelCode+"/"+templateCode);
        return template;
    }

    @Override
    public void validParams(DevSmsTemplateDTO dto, LinkedHashMap<String, String> inputTemplateParams) {
        if(CollectionUtils.isEmpty(dto.getParams())){
            return;
        }
        if(inputTemplateParams==null){
            throw new CommonExceptionI18n("dev.sms.template.missParams",dto.getId(),"*");
        }
        for (String key:
                dto.getParams()) {
            if(!inputTemplateParams.containsKey(key)){
                throw new CommonExceptionI18n("dev.sms.template.missParams",dto.getId(),key);
            }
        }
    }

    @Override
    public String buildTemplateContent(DevSmsTemplateDTO dto, LinkedHashMap<String, String> templateParams) {
        if(dto==null || CollectionUtils.isEmpty(dto.getParams()) || templateParams==null){
            return ConvertUtils.toStr(dto.getContent(),"");
        }
        // 建议增加：如果 content 本身为空的处理
        if (StringUtils.isEmpty(dto.getContent())) {
            return "";
        }
        validParams(dto,templateParams);
        //生成parms
        // 3. 渲染逻辑（这里可以做得更通用，比如支持 ${code} 这种格式）
        String content = dto.getContent();
        for (Map.Entry<String, String> entry : templateParams.entrySet()) {
            // 建议：统一处理占位符，防止误删非参数内容
            String placeholder = "${" + entry.getKey() + "}";
            content = content.replace(placeholder, entry.getValue());
        }
        return content;
    }


    private List<String> extractParams(String content) {
        if (content == null || content.isEmpty()) {
            return new ArrayList<>();
        }

        // 正则表达式解释:
        // \$\{ : 匹配 "${"
        // ([^}]+) : 捕获组，匹配非 "}" 的一个或多个字符
        // \} : 匹配 "}"
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(content);

        // 使用 LinkedHashSet 是为了：1. 去重（同一个变量出现多次只计一个）；2. 保持出现顺序
        Set<String> params = new LinkedHashSet<>();

        while (matcher.find()) {
            // matcher.group(1) 得到的是括号 () 内捕获的内容，即不含 ${} 的变量名
            params.add(matcher.group(1).trim());
        }

        return new ArrayList<>(params);
    }

    /**
     * 数据添加、修改验证数据
     * @param info
     */
    private void validInfo(DevSmsTemplate info){
        //验证的是否支持该类型

        DevSmsTemplate temp = devSmsTemplateMapper.selectOne(DevSmsTemplate::getChannelCode,info.getChannelCode(),DevSmsTemplate::getCode,info.getCode());
        if(temp!=null && !temp.getId().equals(info.getId()))
        {
            throw new CommonExceptionI18n("dev.sms.template.channleExistsCode", info.getChannelCode(),temp.getCode());
        }


    }


    /**
     * 组装查询条件
     * @param param
     * @return
     */
    private QueryWrapper buildQueryWrapper(DevSmsTemplatePageParam param){
        LambdaQueryWrapperX<DevSmsTemplate> queryWrapper = new LambdaQueryWrapperX<>();

        queryWrapper.likeIfPresent(DevSmsTemplate::getName,param.getName());
        queryWrapper.eqIfPresent(DevSmsTemplate::getCode,param.getCode());
        queryWrapper.eqIfPresent(DevSmsTemplate::getChannelCode,param.getChannelCode());
        queryWrapper.select(DevSmsTemplate::getId,
                DevSmsTemplate::getName,
                DevSmsTemplate::getCode,
                DevSmsTemplate::getApiTemplateId,
                DevSmsTemplate::getChannelCode,
                DevSmsTemplate::getContent,
                DevSmsTemplate::getRemark,
                DevSmsTemplate::getIsSys,
                DevSmsTemplate::getStatus,
                DevSmsTemplate::getType
        );



        if (!param.hasSortingFields()) {
            queryWrapper.orderBy(DevSmsTemplate::getId,false);
        }
        return  queryWrapper;
    }

}
