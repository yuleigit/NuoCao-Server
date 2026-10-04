package com.nuocao.dev.modular.see.pojo;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.noear.solon.web.sse.SseEmitter;

import java.util.concurrent.ScheduledFuture;

/**
 * SSE 连接对象
 */
@Getter
@Builder
public class DevSseConnection {
    private final String clientId;
    private final String loginId;
    private final SseEmitter emitter;
    private final ScheduledFuture<?> heartbeatFuture;
}
