package com.nuocao.dev.modular.message.provider;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nuocao.common.enums.CommonYesNoEnum;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.utils.NumberUtils;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.dev.modular.message.convert.DevMessageConvert;
import com.nuocao.dev.modular.message.pojo.param.DevMessageIdParam;
import com.nuocao.dev.modular.message.pojo.param.DevMessageListParam;
import com.nuocao.dev.modular.message.pojo.param.DevMessagePageParam;
import com.nuocao.dev.modular.message.pojo.param.DevMessageSendParam;
import com.nuocao.dev.modular.message.service.DevMessageService;
import com.nuocao.dev.modular.message.service.DevMessageReceiverService;
import nuocao.plugin.dev.api.DevConfigApi;
import nuocao.plugin.dev.api.DevMessageApi;
import nuocao.plugin.dev.api.dto.message.DevMessagePageParamDTO;
import nuocao.plugin.dev.api.dto.message.DevMessageResultDTO;
import nuocao.plugin.dev.api.dto.message.DevMessageSendParamDTO;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 站内信API接口提供者

 **/
@Component
public class DevMessageProvider implements DevMessageApi {

    @Inject
    private DevMessageService devMessageService;
    @Inject
    private DevMessageReceiverService devMessageUserService;

    @Inject
    private DevConfigApi devConfigApi;





    @Override
    public void sendMessage(DevMessageSendParamDTO paramDTO) {
        DevMessageSendParam devMessageSendParam = new DevMessageSendParam();
        devMessageSendParam.setReceiverIdList(paramDTO.getReceiverIdList());
        devMessageSendParam.setTitle(paramDTO.getTitle());
        devMessageSendParam.setCategory(paramDTO.getCategory().getValue());
        devMessageSendParam.setContent(StringUtils.isEmpty(devMessageSendParam.getContent())?paramDTO.getTitle():paramDTO.getContent());
        devMessageService.send(devMessageSendParam);
    }

    @Override
    public List<DevMessageResultDTO> list(Long receiverIdList, Integer limit) {
        DevMessageListParam devMessageListParam = new DevMessageListParam();
        devMessageListParam.setReceiverId(receiverIdList);
        devMessageListParam.setLimit(limit);
        return DevMessageConvert.INSTANCE.convertDtoResultList(devMessageService.list(devMessageListParam));
    }
    @Override
    public List<DevMessageResultDTO> unreadList(Long receiverIdList, Integer limit) {
        DevMessageListParam devMessageListParam = new DevMessageListParam();
        devMessageListParam.setReceiverId(receiverIdList);
        devMessageListParam.setRead(CommonYesNoEnum.NO.getValue());
        devMessageListParam.setLimit(limit);
        return DevMessageConvert.INSTANCE.convertDtoResultList(devMessageService.list(devMessageListParam));
    }
    @Override
    public Long unreadCount(Long userId) {
        return devMessageService.unreadCount(userId);
    }

    @Override
    public Long countByUserId(Long userId){
        return devMessageService.countByUserId(userId);
    }



    @Override
    public PageResult<DevMessageResultDTO> page(DevMessagePageParamDTO pageParam) {
        if(NumberUtils.isLeZero(pageParam.getReceiverUserId())){
            return new PageResult<>();
        }
        DevMessagePageParam param = DevMessageConvert.INSTANCE.convertPageParam(pageParam);
        param.setReceiveUserId(pageParam.getReceiverUserId());
        return DevMessageConvert.INSTANCE.convertDtoResultPage(devMessageService.pageResult(param));
    }

    @Override
    public DevMessageResultDTO detail(Long id) {
        DevMessageIdParam devMessageIdParam = new DevMessageIdParam();
        devMessageIdParam.setId(id);
        return DevMessageConvert.INSTANCE.convertResultDto(devMessageService.detailReader(devMessageIdParam));
    }

    @Override
    public void allMessageMarkRead(Long userId) {
        devMessageUserService.setReadByUserAll(userId);
    }
}
