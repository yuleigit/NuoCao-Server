package com.nuocao.dev.modular.message.service;


import com.nuocao.common.page.PageResult;
import com.nuocao.dev.modular.message.entity.DevMessage;
import com.nuocao.dev.modular.message.pojo.param.*;
import com.nuocao.dev.modular.message.pojo.result.DevMessageReceiverResult;
import com.nuocao.dev.modular.message.pojo.result.DevMessageDetailResult;
import com.nuocao.dev.modular.message.pojo.result.DevMessageSenderResult;
import org.noear.solon.web.sse.SseEmitter;

import java.util.List;

/**
 * 站内信Service接口
 **/
public interface DevMessageService  {

    /**
     * 发送站内信
     **/
    void send(DevMessageSendParam devMessageSendParam);

    /**
     * 获取站内信分页
     */
    PageResult<DevMessage> page(DevMessagePageParam devMessagePageParam);

    /**
     * 获取站内信分页
     */
    PageResult<DevMessageReceiverResult> pageResult(DevMessagePageParam devMessagePageParam);


    /**
     * 获取用户发送的数据
     */
    PageResult<DevMessageSenderResult> userSendPage(DevMessagePageParam devMessageUserSendPageParam);

    /**
     * 获取站内信列表
     */
    List<DevMessageReceiverResult> list(DevMessageListParam devMessageListParam);

    /**
     * 获取未读站内信数量
     */
    Long unreadCount(Long userId);

    /**
     * 获取总消息树
     */
    Long countByUserId(Long userId);



    /**
     * 删除站内信
     **/
    void delete(List<DevMessageIdParam> devMessageIdParamList);

    /**
     * 获取站内信详情（管理员后台读取用的，不会改变读取状态）
     */
    DevMessageDetailResult detail(Long id);

    /**
     * 获取站内信息详情（用户号自己的，读取信息后会改变已读状态）
     * @param id
     * @return
     */
    DevMessageReceiverResult detailReader(Long id);


    /**
     * 获取站内信息详情（用户号自己的，读取信息后会改变已读状态）
     * @param id
     * @return
     */
    DevMessageSenderResult detailSender(Long id);

    /**
     * 获取站内信详情
     */
    DevMessage queryEntity(Long id);

    Long count();



    SseEmitter createSseConnect(String clientId);
}
