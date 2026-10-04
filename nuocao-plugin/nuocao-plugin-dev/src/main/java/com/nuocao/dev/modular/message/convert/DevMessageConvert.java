package com.nuocao.dev.modular.message.convert;

import com.nuocao.common.page.PageResult;
import com.nuocao.dev.modular.message.entity.DevMessage;
import com.nuocao.dev.modular.message.entity.DevMessageReceiver;
import com.nuocao.dev.modular.message.pojo.param.DevMessagePageParam;
import com.nuocao.dev.modular.message.pojo.param.DevMessageSendParam;
import com.nuocao.dev.modular.message.pojo.result.DevMessageReceiverResult;
import com.nuocao.sys.api.dto.SysUserSampleResultDTO;
import nuocao.plugin.dev.api.dto.message.DevMessagePageParamDTO;
import nuocao.plugin.dev.api.dto.message.DevMessageResultDTO;
import nuocao.plugin.dev.api.dto.message.DevMessageUserResultDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 站内信 转换器
 */
@Mapper
public interface DevMessageConvert {
    DevMessageConvert INSTANCE = Mappers.getMapper(DevMessageConvert.class);
    @Named("convertReceiverToResultDto")
    DevMessageResultDTO convert(DevMessageReceiverResult bean);

    DevMessageReceiverResult convert(DevMessage bean);

    default DevMessageReceiverResult convert2(DevMessage bean,DevMessageReceiver user){
        DevMessageReceiverResult result = convert(bean);
        if(user!=null){
            result.setRead(user.getRead());
            result.setReadTime(user.getReadTime());
        }
        return result;
    }
    @IterableMapping(qualifiedByName = "convertReceiverToResultDto")
    List<DevMessageResultDTO> convertDtoResultList(List<DevMessageReceiverResult> bean);

    PageResult<DevMessageResultDTO> convertDtoResultPage(PageResult<DevMessageReceiverResult> bean);

    DevMessage convertSend(DevMessageSendParam sendParam);

    DevMessagePageParam convertPageParam(DevMessagePageParamDTO param);


    DevMessageResultDTO convertResultDto(DevMessageReceiverResult bean);

    DevMessageUserResultDTO convertUser(SysUserSampleResultDTO bean);

}
