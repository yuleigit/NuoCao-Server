package com.nuocao.dev.core.client.sms;

import com.nuocao.common.exception.CommonException;
import com.nuocao.dev.core.client.file.FileStorageClient;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class SmsChannelFactory {
    private final Map<String, SmsChannelClient> clientMap = new ConcurrentHashMap<>();

    public void setChannels(List<SmsChannelClient> clients) {
        clients.forEach(channel -> clientMap.put(channel.getChannelCode(), channel));
    }
    public void registerChannel(SmsChannelClient client) {
        clientMap.putIfAbsent(client.getChannelCode(),client);
    }

    /**
     * 动态获取引擎：支持运行时切换
     */
    public SmsChannelClient getClient(String channelCode) {
        SmsChannelClient client = clientMap.get(channelCode);
        if (client == null) {
            throw new CommonException("Unsupported sms channel：" + channelCode);
        }
        if(!client.isReady()){
            throw new CommonException("Sms channel not configured yet, loading failed：" + channelCode);
        }
        return client;
    }
}
