package com.nuocao.dev.modular.see.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.sse.CommonSseParam;
import com.nuocao.dev.modular.see.service.DevSseEmitterService;
import com.nuocao.dev.modular.see.util.DevSseUtils;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.web.sse.SseEmitter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * SSE通信Service接口实现类
 **/
@Slf4j
@Component
public class DevSseEmitterServiceImpl implements DevSseEmitterService {
    private static final long DEFAULT_TIMEOUT = 10 * 60 * 1000L; // 10秒钟后断开

    /**
     * 心跳线程池
     */
    private static final ScheduledExecutorService heartbeatExecutors = Executors.newScheduledThreadPool(10);


    /**
     * 创建连接
     **/
    @Override
    public SseEmitter createSseConnect(String clientId, Boolean setHeartBeat, Boolean defaultHeartbeat, Consumer<CommonSseParam> consumer) {
        // 设置超时时间，0表示不过期。默认30秒，超过时间未完成会抛出异常：AsyncRequestTimeoutException
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);//设置10分钟，0好像不支持，设置为0是30秒
        String loginId = StpUtil.getLoginIdAsString();
        // 判断连接是否有效
        if (DevSseUtils.connectionValidity(clientId,loginId)) {
            return DevSseUtils.getSseEmitterByClientId(clientId);
        }else{
            DevSseUtils.removeConnection(clientId);
        }
        clientId = IdUtil.simpleUUID();
        String finalClientId = clientId;
        // 增加心跳
        final ScheduledFuture<?> future;
        // 是否自定义心跳任务
        if (setHeartBeat!=null&&setHeartBeat) {
            //是否使用默认心跳任务
            if(defaultHeartbeat!=null&&defaultHeartbeat){
                //默认心跳任务
                future = heartbeatExecutors.scheduleAtFixedRate(() ->{
                            try{
                                DevSseUtils.sendMessageToOneClient(finalClientId,finalClientId+"-"+loginId);
                            }catch (Exception ex){
                            }
                        }
                                ,
                        2, 15, TimeUnit.SECONDS);
            }else{
                //自定义心跳任务
                CommonSseParam commonSseParam = new CommonSseParam();
                commonSseParam.setClientId(clientId);
                commonSseParam.setUserId(loginId);
                future = heartbeatExecutors.scheduleAtFixedRate(() ->{
                            try{
                                consumer.accept(commonSseParam);
                            }catch (Exception ex){
                            }
                        },
                        2, 15, TimeUnit.SECONDS);
            }
            // 增加连接
            DevSseUtils.addConnection(clientId, loginId, sseEmitter, future);
        } else {
            // 增加连接
            DevSseUtils.addConnection(clientId, loginId, sseEmitter, null);
        }
        // 长链接完成后回调(即关闭连接时调用)
        sseEmitter.onCompletion(DevSseUtils.completionCallBack(clientId));
        // 连接超时回调
        sseEmitter.onTimeout(DevSseUtils.timeoutCallBack(clientId));
        // 推送消息异常回调
        sseEmitter.onError(DevSseUtils.errorCallBack(clientId));
        // 初次建立连接,推送客户端id
        CommonResult<String> message = new CommonResult<>(0,"",clientId);
        DevSseUtils.sendMessageToClientByClientId(clientId,message);
        return sseEmitter;
    }

    /**
     * 关闭连接
     **/
    @Override
    public void closeSseConnect(String clientId){
        DevSseUtils.removeConnection(clientId);
    }

    /**
     * 推送消息到所有客户端
     **/
    @Override
    public void sendMessageToAllClient(String msg) {
        DevSseUtils.sendMessageToAllClient(msg);
    }

    /**
     * 根据clientId发送消息给某一客户端
     **/
    @Override
    public void sendMessageToOneClient(String clientId, String msg) {
        DevSseUtils.sendMessageToOneClient(clientId,msg);
    }

}
