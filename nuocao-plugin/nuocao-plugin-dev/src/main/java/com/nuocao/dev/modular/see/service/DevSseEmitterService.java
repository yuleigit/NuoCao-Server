package com.nuocao.dev.modular.see.service;

import com.nuocao.common.sse.CommonSseParam;
import org.noear.solon.web.sse.SseEmitter;

import java.util.function.Consumer;

/**
 * SSE通信Service接口
 **/
public interface DevSseEmitterService {

    /**
     * 创建连接
     **/
     SseEmitter createSseConnect(String clientId, Boolean setHeartBeat, Boolean defaultHeartbeat, Consumer<CommonSseParam> consumer);

    /**
     * 关闭连接
     **/
    void closeSseConnect(String clientId);

    /**
     * 推送消息到所有客户端
     **/
     void sendMessageToAllClient(String msg);

    /**
     * 根据clientId发送消息给某一客户端
     **/
     void sendMessageToOneClient(String clientId, String msg);
}
