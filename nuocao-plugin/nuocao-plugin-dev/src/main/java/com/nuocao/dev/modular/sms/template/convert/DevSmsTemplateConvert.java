package com.nuocao.dev.modular.sms.template.convert;

import com.nuocao.common.page.PageResult;
import com.nuocao.dev.modular.sms.template.param.DevSmsTemplateEditParam;
import com.nuocao.dev.modular.sms.template.entity.DevSmsTemplate;
import com.nuocao.dev.modular.sms.template.param.DevSmsTemplateAddParam;
import com.nuocao.dev.modular.sms.template.result.DevSmsTemplateResult;
import com.nuocao.plugin.dev.api.dto.sms.DevSmsTemplateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 短信模板 转换器
 */
@Mapper
public interface DevSmsTemplateConvert {

    DevSmsTemplateConvert INSTANCE = Mappers.getMapper(DevSmsTemplateConvert.class);


    DevSmsTemplate convert(DevSmsTemplateAddParam bean);

    DevSmsTemplate convert(DevSmsTemplateEditParam bean);

    PageResult<DevSmsTemplateResult> convertPage(PageResult<DevSmsTemplate> page);

    DevSmsTemplateDTO convertDto(DevSmsTemplate bean);


}
