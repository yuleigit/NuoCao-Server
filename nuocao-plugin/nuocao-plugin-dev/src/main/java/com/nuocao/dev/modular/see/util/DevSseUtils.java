package com.nuocao.dev.modular.see.util;

import cn.hutool.core.util.StrUtil;
import com.nuocao.common.ObjectUtils;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.common.utils.json.JsonUtils;
import com.nuocao.dev.modular.see.pojo.DevSseConnection;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.web.sse.SseEmitter;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

/**
 * SseEmitter工具类
 **/
@Slf4j
public class DevSseUtils {

    /**
     * 创建一个容器来存储所有的 SseEmitter(使用ConcurrentHashMap是因为它是线程安全的)。
     */
    public static Map<String, DevSseConnection> SSE_CACHE = new ConcurrentHashMap<>();


    /**
     * 根据客户端id获取连接对象
     **/
    public static SseEmitter getSseEmitterByClientId(String clientId) {
        DevSseConnection connection = SSE_CACHE.get(clientId);
        return connection == null ? null : connection.getEmitter();
    }

    /**
     * 根据客户端id获取心跳
     **/
    public static ScheduledFuture<?> getSseFutureByClientId(String clientId) {
        DevSseConnection connection = SSE_CACHE.get(clientId);
        return connection == null ? null : connection.getHeartbeatFuture();
    }

    /**
     * 根据客户端id获取用户登录ID（不是用户ID）
     **/
    public static String getLoginIdByClientId(String clientId) {
        DevSseConnection connection = SSE_CACHE.get(clientId);
        return connection == null ? null : connection.getLoginId();
    }

    /**
     * 根据用户id获取客户端id
     **/
    public static String getClientIdByLoginId(String loginId){
        if(existSseCache()){
            for (Map.Entry<String, DevSseConnection> entry : SSE_CACHE.entrySet()) {
                DevSseConnection conn = SSE_CACHE.get(entry.getKey());
                if(loginId.equals(conn.getLoginId())){
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * 判断容器是否存在连接
     **/
    public static boolean existSseCache() {
        return !SSE_CACHE.isEmpty();
    }

    /**
     * 判断连接是否有效
     **/
    public static boolean connectionValidity(String clientId,String loginId){
        if (StringUtils.isBlank(clientId) || StringUtils.isBlank(loginId)) {
            return false;
        }

        DevSseConnection connection = SSE_CACHE.get(clientId);

        return connection != null
                && Objects.equals(connection.getLoginId(), loginId)
                && connection.getEmitter() != null;
    }

    /**
     * 增加连接
     **/
    public static void addConnection(String clientId,String loginId, SseEmitter emitter, ScheduledFuture<?> future) {
        if(StringUtils.isBlank(clientId)){
            throw new CommonException("[SSE] connection error : clientId not blank");
        }
        if(StringUtils.isBlank(loginId)){
            throw new CommonException("[SSE] connection error : loginId not blank");
        }
        if(ObjectUtils.isNull(emitter)){
            throw new CommonException("[SSE] connection error : emitter not null");
        }

        DevSseConnection connection = DevSseConnection.builder()
                .clientId(clientId)
                .loginId(loginId)
                .emitter(emitter)
                .heartbeatFuture(future)
                .build();

        // 原子添加，避免并发覆盖已有连接
        DevSseConnection oldConnection =
                SSE_CACHE.putIfAbsent(clientId, connection);

        if (oldConnection != null) {
            // 如果心跳任务已经创建，添加失败时需要取消，避免任务泄漏
            if (future != null) {
                future.cancel(true);
            }

            throw new CommonExceptionI18n(
                    "dev.see.clientExists",
                    clientId
            );
        }
    }

    /**
     * 移除连接
     **/
    public static void removeConnection(String clientId) {
        SseEmitter emitter = getSseEmitterByClientId(clientId);
        if (emitter != null) {
            cancelScheduledFuture(clientId);
        }
        SSE_CACHE.remove(clientId);
        //log.info("移除连接:{}", clientId);
    }

    /**
     * 中断心跳发送任务
     */
    public static void cancelScheduledFuture(String clientId){
        ScheduledFuture<?> future = getSseFutureByClientId(clientId);
        if (future != null) {
            future.cancel(true);
        }
    }


    /**
     * 长链接完成后回调
     **/
    public static Runnable completionCallBack(String clientId) {
        return () -> {
            log.info("结束连接:{}", clientId);
            removeConnection(clientId);
            cancelScheduledFuture(clientId);
        };
    }

    /**
     * 连接超时回调
     *
     * @author diantu
     * @date 2023/7/3
     **/
    public static Runnable timeoutCallBack(String clientId){
        return ()->{
            log.info("连接超时:{}", clientId);
            removeConnection(clientId);
            cancelScheduledFuture(clientId);
        };
    }

    /**
     * 推送消息异常时回调
     **/
    public static Consumer<Throwable> errorCallBack(String clientId) {
        return throwable -> {
            log.info("推送消息异常:{}", clientId);
            removeConnection(clientId);
            cancelScheduledFuture(clientId);
        };
    }

    /**
     * 推送消息到所有客户端
     **/
    public static void sendMessageToAllClient(String msg) {
        if (!existSseCache()) {
            return;
        }
        // 判断发送的消息是否为空
        if (StrUtil.isEmpty(msg)){
            log.info("群发消息为空");
            return;
        }
        CommonResult<String> message = new CommonResult<>(CommonResult.CODE_SUCCESS,"",msg);

        if(existSseCache()){
            for (Map.Entry<String, DevSseConnection> entry : SSE_CACHE.entrySet()) {
                sendMessageToClientByClientId(entry.getKey(), message);
            }
        }
    }

    /**
     * 根据clientId发送消息给某一客户端
     **/
    public static void sendMessageToOneClient(String clientId, String msg) {
        if (StrUtil.isEmpty(clientId)){
            log.info("客户端ID为空");
            return;
        }
        if (StrUtil.isEmpty(msg)){
            log.info("向客户端{}推送消息为空",clientId);
            return;
        }
        CommonResult<String> message = new CommonResult<>(CommonResult.CODE_SUCCESS,"",msg);
        sendMessageToClientByClientId(clientId,message);
    }

    /**
     * 推送消息到客户端
     *
     **/
    public static void sendMessageToClientByClientId(String clientId, CommonResult<String> message) {
        DevSseConnection conn = SSE_CACHE.get(clientId);
        if (conn==null ) {
            log.error("推送消息失败:客户端{}未创建长链接,失败消息:{}",clientId, message.toString());
            return;
        }
        //新的的solon写法
        try {
            SseEmitter sseEmitter =conn.getEmitter();
            if(sseEmitter==null) {
                return;
            }
            sseEmitter.send(JsonUtils.toJsonString(message));
        }
        catch (Exception ex)
        {

        }
    }

}
