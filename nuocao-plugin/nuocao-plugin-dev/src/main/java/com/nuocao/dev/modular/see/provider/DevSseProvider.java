package com.nuocao.dev.modular.see.provider;

import com.nuocao.common.sse.CommonSseParam;
import com.nuocao.dev.modular.see.service.DevSseEmitterService;
import com.nuocao.plugin.dev.api.DevSseApi;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.web.sse.SseEmitter;

import java.util.function.Consumer;

/**
 * SSE 接口提供者
 **/
@Component
public class DevSseProvider implements DevSseApi {

    @Inject
    private DevSseEmitterService devSseEmitterService;

    /**
     * 创建SSE连接
     **/
    @Override
    public SseEmitter createSseConnect(String clientId, Boolean setHeartBeat, Boolean defaultHeartbeat, Consumer<CommonSseParam> consumer) {
        return devSseEmitterService.createSseConnect(clientId,setHeartBeat,defaultHeartbeat,consumer);
    }

    /**
     * 关闭连接
     **/
    @Override
    public void closeSseConnect(String clientId) {
        devSseEmitterService.closeSseConnect(clientId);
    }

    /**
     * 推送消息到所有客户端
     **/
    @Override
    public void sendMessageToAllClient(String msg) {
        devSseEmitterService.sendMessageToAllClient(msg);
    }

    /**
     * 根据clientId发送消息给某一客户端
     **/
    @Override
    public void sendMessageToOneClient(String clientId, String msg) {
        devSseEmitterService.sendMessageToOneClient(clientId,msg);
    }
}
