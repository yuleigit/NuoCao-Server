package com.nuocao.dev.modular.message.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.update.UpdateChain;
import com.nuocao.common.cache.CacheUtils;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.utils.LocalDateTimeUtils;
import com.nuocao.common.utils.NumberUtils;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.dev.modular.message.entity.DevMessageReceiver;
import com.nuocao.dev.modular.message.mapper.DevMessageReceiverMapper;
import com.nuocao.dev.modular.message.service.DevMessageReceiverService;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;


import java.util.List;

/**
 * 站内信Service接口实现类
 **/
@Component
@Slf4j
public class DevMessageReceiverServiceImpl implements DevMessageReceiverService {

    private static final String UNREAD_COUNT_CACHE_PREFIX="cache:dev:message:unread:";
    private static final String COUNT_CACHE_PREFIX="cache:dev:message:count:";
    private static final Integer COUNT_CACHE_EXPIRE =60*60; //缓存1小时，3600秒
    @Inject
    private DevMessageReceiverMapper mapper;
    @Inject
    private CacheUtils cacheUtils;
    @Override
    public void addNewMessageUser(Long msgId, List<Long> receiverIdList) {
        CommonException.notEmpty(receiverIdList,"接收用户ID必能为空");

        Db.executeBatch(receiverIdList, 500, DevMessageReceiverMapper.class
                , (mapper, userId) -> {
                    DevMessageReceiver newUser = new DevMessageReceiver();
                    newUser.setMessageId(msgId);
                    newUser.setRead(0);
                    newUser.setReceiverUserId(userId);
                    mapper.insert(newUser);
                });
        receiverIdList.forEach(this::removeUnreadCache);
    }

    @Override
    public Long unreadCount(Long userId) {
        if(NumberUtils.isLeZero(userId)){
            return 0L;
        }
        //读取短信数量缓存
        String key =UNREAD_COUNT_CACHE_PREFIX+userId;
        return cacheUtils.getOrSetObject(key,Long.class,()->{
            Long count = mapper.selectCountByQuery(new QueryWrapper().eq(DevMessageReceiver::getReceiverUserId,userId)
                    .eq(DevMessageReceiver::getRead,0));
            return count;
        });

    }

    @Override
    public Long countByUserId(Long userId) {
        if(NumberUtils.isLeZero(userId)){
            return 0L;
        }
        //读取短信数量缓存
        String key =COUNT_CACHE_PREFIX+userId;
        return cacheUtils.getOrSetObject(key,Long.class,()->{
            return mapper.selectCountByQuery(new QueryWrapper().eq(DevMessageReceiver::getReceiverUserId,userId));
        });
    }


    private void removeUnreadCache(Long userId){
        if(userId==null) return;
        cacheUtils.remove(UNREAD_COUNT_CACHE_PREFIX+userId);
    }
    private void removeUnreadCache(List<Long> userIds){
        if(CollectionUtils.isEmpty(userIds)) return;
        for (Long userId:
                userIds) {
            removeUnreadCache(userId);
        }
    }

    /**
     * 根据短信ID清理统计缓存，一般为设为已读后调用
     * @param messageUserIds 消息用户的id,DevMessageUser.id
     */
    private void removeUnreadCacheByIds(List<Long> messageUserIds){
        if(CollectionUtils.isEmpty(messageUserIds)) return;
        QueryWrapper query = new QueryWrapper();
        query.select(DevMessageReceiver::getReceiverUserId)
                .in(DevMessageReceiver::getId,messageUserIds);
        List<Long> userIds = mapper.selectObjectListByQueryAs(query,Long.class);
        removeUnreadCache(userIds);
    }
    @Override
    public void deleteByMessageId(List<Long> messageIds) {
        deleteByMessageId(messageIds,null);
    }

    @Override
    public void deleteByMessageId(List<Long> messageIds, Long userId) {
        if(CollectionUtils.isEmpty(messageIds)) return;
        QueryWrapper query = new QueryWrapper();
        query.in(DevMessageReceiver::getMessageId,messageIds);
        if(userId!=null){
            query.eq(DevMessageReceiver::getReceiverUserId,userId);
        }
        if(NumberUtils.isGtZero(userId)){
            removeUnreadCache(userId);
        }else{ //得到所有接收人id信息
            QueryWrapper query2 = query.clone();
            query2.select(DevMessageReceiver::getReceiverUserId);
            List<Long> userIds = mapper.selectObjectListByQueryAs(query2,Long.class);
            removeUnreadCache(userIds);
        }
        mapper.deleteByQuery(query);

    }

    @Override
    public List<DevMessageReceiver> getMessageUserList(Long messageId) {
        QueryWrapper query = QueryWrapper.create().eq(DevMessageReceiver::getMessageId,messageId);
        return mapper.selectListByQuery(query);
    }

    @Override
    public void setReads(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)) return;
        UpdateChain.of(DevMessageReceiver.class).in(DevMessageReceiver::getId,ids)
                .set(DevMessageReceiver::getReadTime, LocalDateTimeUtils.now())
                .set(DevMessageReceiver::getRead,1).update();
        removeUnreadCacheByIds(ids);

    }

    @Override
    public void setReadByUserAll(Long userId) {
        UpdateChain.of(DevMessageReceiver.class).eq(DevMessageReceiver::getReceiverUserId,userId)
                .eq(DevMessageReceiver::getRead,0)
                .set(DevMessageReceiver::getReadTime, LocalDateTimeUtils.now())
                .set(DevMessageReceiver::getRead,1).update();
        removeUnreadCache(userId);
    }
}
