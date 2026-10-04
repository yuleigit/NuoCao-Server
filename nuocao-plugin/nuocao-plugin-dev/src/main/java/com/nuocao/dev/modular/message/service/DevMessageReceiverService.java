
package com.nuocao.dev.modular.message.service;

import com.nuocao.dev.modular.message.entity.DevMessageReceiver;

import java.util.List;

/**
 * 站内信用户Service接口
 **/
public interface DevMessageReceiverService {


    void addNewMessageUser(Long msgId,List<Long> receiverIdList);

    /**
     * 获取未读信息统计数量,有缓存，1小时内不更新则读取缓存
     * @param userId
     * @return
     */
    Long unreadCount(Long userId);

    /**
     * 获取用户消息统计
     * @param userId
     * @return
     */
    Long countByUserId(Long userId);


    void deleteByMessageId(List<Long> messageIds);


    void deleteByMessageId(List<Long> messageIds,Long userId);


    List<DevMessageReceiver> getMessageUserList(Long messageId);


    void setReads(List<Long> ids);

    /**
     * 将该用户所有未读消息标为已读
     * @param userId
     */
    void setReadByUserAll(Long userId);
}
