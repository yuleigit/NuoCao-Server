package com.nuocao.dev.modular.message.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;

import com.alibaba.excel.util.StringUtils;
import com.mybatisflex.core.query.QueryWrapper;
import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.common.enums.CommonYesNoEnum;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.mapper.LambdaQueryWrapperX;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.sse.CommonSseParam;
import com.nuocao.common.utils.ConvertUtils;
import com.nuocao.common.utils.NumberUtils;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.dev.modular.message.convert.DevMessageConvert;
import com.nuocao.dev.modular.message.entity.DevMessage;
import com.nuocao.dev.modular.message.entity.DevMessageReceiver;
import com.nuocao.dev.modular.message.mapper.DevMessageMapper;
import com.nuocao.dev.modular.message.pojo.param.*;
import com.nuocao.dev.modular.message.pojo.result.DevMessageDetailResult;
import com.nuocao.dev.modular.message.pojo.result.DevMessageReceiverResult;
import com.nuocao.dev.modular.message.pojo.result.DevMessageSenderResult;
import com.nuocao.dev.modular.message.service.DevMessageReceiverService;
import com.nuocao.dev.modular.message.service.DevMessageService;
import com.nuocao.sys.api.SysUserApi;
import com.nuocao.sys.api.dto.SysUserSampleResultDTO;
import lombok.extern.slf4j.Slf4j;
import nuocao.plugin.dev.api.DevSseApi;
import nuocao.plugin.dev.api.dto.message.DevMessageReceiverInfoDTO;
import nuocao.plugin.dev.api.enums.message.DevMessageCategoryEnum;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Component;
import org.noear.solon.data.annotation.Transaction;
import org.noear.solon.web.sse.SseEmitter;


import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 站内信Service接口实现类
 **/
@Component
@Slf4j
public class DevMessageServiceImpl  implements DevMessageService {

    @Inject
    private DevMessageMapper mapper;
    /*
    * 注意：message使用DEV_RELATION存储状态，后续内容越来越多可能造成性能瓶颈，而且他每次要读取所有数据然后用read=false来查询，所以这个后期一定要优化掉
    * */
    @Inject
    private SysUserApi sysUserApi;

    @Inject
    private DevSseApi devSseApi;
    @Inject
    private DevMessageReceiverService devMessageReceiverService;

    @Transaction
    @Override
    public void send(DevMessageSendParam devMessageSendParam) {
        devMessageSendParam.setContent(ObjectUtil.isEmpty(devMessageSendParam.getContent()) ?
                devMessageSendParam.getTitle() : devMessageSendParam.getContent());
        devMessageSendParam.setCategory(ObjectUtil.isEmpty(devMessageSendParam.getCategory()) ?
                DevMessageCategoryEnum.SYS.getValue() : devMessageSendParam.getCategory());
        devMessageSendParam.setSenderUserId(devMessageSendParam.getSenderUserId()==null? StpLoginUserUtils.getLoginUserId():devMessageSendParam.getSenderUserId());
        DevMessage devMessage = DevMessageConvert.INSTANCE.convertSend(devMessageSendParam);
        mapper.insert(devMessage);
        List<Long> receiverIdList = devMessageSendParam.getReceiverIdList();
        if (ObjectUtil.isNotEmpty(receiverIdList)) {
            devMessageReceiverService.addNewMessageUser(devMessage.getId(),receiverIdList);
        }
    }


    /**
     * 初始化查询语句，如果之传入一个userId或则返回leftjson的
     * @param isReceiver 是否接收人模式 ，否则以发送人角度查询
     * @return
     */
    private QueryWrapper initQueryWrapper(DevMessagePageParam devMessagePageParam,boolean isReceiver){
        LambdaQueryWrapperX<DevMessage> queryWrapper = new LambdaQueryWrapperX<>();

        queryWrapper.selectX(DevMessage::getId,DevMessage::getCategory,DevMessage::getSenderUserId,
                DevMessage::getContent,DevMessage::getTitle,DevMessage::getCreateTime,
                DevMessage::getCreateUser
        );
        if (StringUtils.isNotBlank(devMessagePageParam.getSearchKey())) {
            queryWrapper.likeX(DevMessage::getTitle, devMessagePageParam.getSearchKey());
        }
        if(devMessagePageParam.getReceiverUserId()!=null && CollectionUtils.isNotEmpty(devMessagePageParam.getReceiverUserIds())){
            CommonException.throwException("接收用户userId和userIdList只能选择其一");
        }

        if (StringUtils.isNotBlank(devMessagePageParam.getCategory())) {
            queryWrapper.eqX(DevMessage::getCategory, devMessagePageParam.getCategory());
        }
        Long userId=devMessagePageParam.getReceiverUserId();
        if(CollectionUtils.isNotEmpty(devMessagePageParam.getReceiverUserIds()) && isReceiver){
            //如果只有一个，就按userId处理
            if(devMessagePageParam.getReceiverUserIds().size()==1 && userId==null){
                userId=devMessagePageParam.getReceiverUserIds().get(0);
            }
        }
        if(NumberUtils.isGtZero(devMessagePageParam.getSenderUserId())){
            queryWrapper.eq(DevMessage::getSenderUserId,devMessagePageParam.getSenderUserId());
        }
        //如果指定单user，则只查询这个user的
        if (NumberUtils.isGtZero(userId)) {
            //单个使用联合查询
            queryWrapper.leftJoin(DevMessageReceiver.class).on(DevMessage::getId,DevMessageReceiver::getMessageId);
            queryWrapper.select(DevMessageReceiver::getRead,DevMessageReceiver::getReadTime);
            queryWrapper.eq(DevMessageReceiver::getReceiverUserId,userId);

        }else{
            if(CollectionUtils.isNotEmpty(devMessagePageParam.getReceiverUserIds())) {
                queryWrapper.in(DevMessage::getId,QueryWrapper.create().select(DevMessageReceiver::getMessageId).in(DevMessageReceiver::getReceiverUserId,devMessagePageParam.getReceiverUserIds()));
            }
        }

        if(ObjectUtil.isEmpty(devMessagePageParam.getSortingFields())) {
            queryWrapper.orderBy(DevMessage::getId,false);
        }
        return queryWrapper;
    }


    @Override
    public PageResult<DevMessage> page(DevMessagePageParam devMessagePageParam) {
        QueryWrapper queryWrapper = initQueryWrapper(devMessagePageParam,false);



        return mapper.selectPage(devMessagePageParam, queryWrapper);
    }

    @Override
    public PageResult<DevMessageReceiverResult> pageResult(DevMessagePageParam devMessagePageParam) {
        if(NumberUtils.isLeZero(devMessagePageParam.getReceiverUserId())){
            return PageResult.empty();
        }
        QueryWrapper queryWrapper = initQueryWrapper(devMessagePageParam,true);
        queryWrapper.orderBy(DevMessage::getId,false);
        PageResult<DevMessageReceiverResult> page= mapper.selectJoinPage(devMessagePageParam, DevMessageReceiverResult.class, queryWrapper);
        page.setRecords(builderSendUser(page.getRecords()));
        return page;
    }

    @Override
    public PageResult<DevMessageSenderResult> userSendPage(DevMessagePageParam devMessagePageParam) {
        if(NumberUtils.isLeZero(devMessagePageParam.getSenderUserId())){
            throw new CommonException("senderUserId not null");
        }
        QueryWrapper queryWrapper = initQueryWrapper(devMessagePageParam,false);
        queryWrapper.orderBy(DevMessage::getId,false);
        PageResult<DevMessageSenderResult> page= mapper.selectJoinPage(devMessagePageParam, DevMessageSenderResult.class, queryWrapper);
        return page;
    }

    @Override
    public List<DevMessageReceiverResult> list(DevMessageListParam devMessageListParam) {
        if(NumberUtils.isLeZero(devMessageListParam.getReceiverId())){
            return new ArrayList<>();
        }

        DevMessagePageParam pageParam = new DevMessagePageParam();
        pageParam.setReceiverUserId(devMessageListParam.getReceiverId());

        QueryWrapper query =initQueryWrapper(pageParam,true);
        if(devMessageListParam.getRead()!=null){
            query.eq(DevMessageReceiver::getRead,devMessageListParam.getRead());
        }
        query.orderBy(DevMessage::getId,false);
        query.limit(ConvertUtils.toInt(devMessageListParam.getLimit(),10));

        return builderSendUser(mapper.selectListByQueryAs(query, DevMessageReceiverResult.class));
    }

    private List<DevMessageReceiverResult> builderSendUser(List<DevMessageReceiverResult> list){
        List<Long> userIds = list.stream().map(o->o.getSenderUserId()).filter(o->o.compareTo(0L)>0).distinct().collect(Collectors.toList());;
        if(CollectionUtils.isEmpty(userIds)){
            return list;
        }
        List<SysUserSampleResultDTO> userList = sysUserApi.getUserSampleListByIds(userIds);
        Map<Long,SysUserSampleResultDTO> userMap = CollectionUtils.convertMap(userList,SysUserSampleResultDTO::getId);
        for (DevMessageReceiverResult p:
             list) {
            if(p.getSenderUserId()!=null) {
                p.setSender(DevMessageConvert.INSTANCE.convertUser(userMap.get(p.getSenderUserId())));
            }else{
            }
        }
        return list;
    }

    @Override
    public Long unreadCount(Long userId) {

        return devMessageReceiverService.unreadCount(userId);
    }
    @Override
    public Long countByUserId(Long userId) {

        return devMessageReceiverService.countByUserId(userId);
    }

    @Transaction
    @Override
    public void delete(List<DevMessageIdParam> devMessageIdParamList) {
        List<Long> devMessageIdList = CollStreamUtil.toList(devMessageIdParamList, DevMessageIdParam::getId);
        if (ObjectUtil.isNotEmpty(devMessageIdList)) {
            // 清除站内信与用户关联关系
            devMessageReceiverService.deleteByMessageId(devMessageIdList);
            // 执行删除
            mapper.deleteBatchByIds(devMessageIdList);
        }
    }

    @Override
    public DevMessageDetailResult detail(Long id) {
        DevMessageDetailResult devMessageResult = new DevMessageDetailResult();
        DevMessage devMessage = this.queryEntity(id);
        BeanUtil.copyProperties(devMessage, devMessageResult);

        //得到所有接受者信息
        List<DevMessageReceiver> messageUserList = devMessageReceiverService.getMessageUserList(devMessage.getId());
        if(CollectionUtils.isEmpty(messageUserList)){
            return devMessageResult;
        }

        List<SysUserSampleResultDTO> userList = sysUserApi.getUserSampleListByIds(messageUserList.stream().map(DevMessageReceiver::getReceiverUserId).collect(Collectors.toList()));
        Map<Long,SysUserSampleResultDTO> userMap = CollectionUtils.convertMap(userList,SysUserSampleResultDTO::getId);

        //生成阅读状态集合
        List<DevMessageReceiverInfoDTO> receiveInfoList = messageUserList.stream().map(msgUser->{
            DevMessageReceiverInfoDTO devReceiveInfo = new DevMessageReceiverInfoDTO();
            String userName = null;
            try {
                //这里需要优化，加入缓存机制或什么的，因为如果这样查询，需要每次遍历所有用户数据
                devReceiveInfo.setReceiver( DevMessageConvert.INSTANCE.convertUser(userMap.get(msgUser.getReceiverUserId())));
            }
            catch (Exception e) {
                // 收件人中包含删除用户 在此处做处理
            }
            if (StringUtils.isBlank(userName)) {
                return null;
            }
            devReceiveInfo.setReceiverUserId(msgUser.getReceiverUserId());
            devReceiveInfo.setRead(ConvertUtils.toBool(msgUser.getRead(),false));
            devReceiveInfo.setReadTime(msgUser.getReadTime());
            return devReceiveInfo;
        }).collect(Collectors.toList());

        devMessageResult.setReceiverInfoList(receiveInfoList);
        return devMessageResult;
    }

    @Transaction
    @Override
    public DevMessageReceiverResult detailReader(Long id) {

        DevMessage message = queryEntity(id);
        List<DevMessageReceiver> messageUserList = devMessageReceiverService.getMessageUserList(message.getId());
        DevMessageReceiver user = messageUserList.stream().filter(o->o.getReceiverUserId().equals(StpLoginUserUtils.getLoginUserId())).findFirst().orElse(null);
        if(user==null){
            throw new CommonExceptionI18n("dev.message.notExists",message.getId());
        }
        DevMessageReceiverResult result = DevMessageConvert.INSTANCE.convert2(message,user);
        result.setSender(DevMessageConvert.INSTANCE.convertUser(sysUserApi.getUserSampleListById(message.getSenderUserId())));
        //未读消息设为已读
        if(!CommonYesNoEnum.isYes(result.getRead())){
            result.setRead(CommonYesNoEnum.YES.getValue());
            result.setReadTime(LocalDateTime.now());
            devMessageReceiverService.setReads(CollectionUtils.toList(user.getId()));
        }
        return result;
    }


    @Override
    public DevMessageSenderResult detailSender(Long id) {
        DevMessageDetailResult detail = detail(id);
        if(!StpLoginUserUtils.getLoginUserId().equals(detail.getSenderUserId())){
            throw new CommonExceptionI18n("dev.message.notSender");
        }
        return DevMessageConvert.INSTANCE.convertSenderResult(detail);
    }

    @Override
    public DevMessage queryEntity(Long id) {
        DevMessage devMessage = mapper.selectOneById(id);
        if (ObjectUtil.isEmpty(devMessage)) {
            throw new CommonExceptionI18n("dev.message.notExists", id);
        }
        return devMessage;
    }

    @Override
    public Long count() {
        return mapper.selectCount();
    }

    @Override
    public SseEmitter createSseConnect(String clientId) {
        Consumer<CommonSseParam> consumer = m -> {
            //获取用户未读消息
            long unreadMessageNum = unreadCount(m.getUserIdToLong());
            //发送消息
            devSseApi.sendMessageToOneClient(m.getClientId(), String.valueOf(unreadMessageNum));
        };
        return devSseApi.createSseConnect(clientId,true,false,consumer);
    }
}
