package com.nuocao.dev.core.client.file;

import com.nuocao.common.exception.CommonException;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileStorageFactory {
    private final Map<String, FileStorageClient> clientMap = new ConcurrentHashMap<>();
    @Inject
    private List<FileStorageClient> clients;
    @Init

    public void registerEngine(FileStorageClient client) {
        clientMap.putIfAbsent(client.getType(),client);
    }

    /**
     * 重载配置信息，在修改后配置后和系统运行启动后执行
     */
    public void reloadAllConfigs() {
        clientMap.values().forEach(client -> {
            client.reloadConfig();
        });
    }

    /**
     * 动态获取引擎：支持运行时切换
     */
    public FileStorageClient getClient(String engineType) {
        FileStorageClient client = clientMap.get(engineType);
        if (client == null) {
            throw new CommonException("Unsupported file engine：" + engineType);
        }
        if(!client.isReady()){
            client.reloadConfig();
        }
        if(!client.isReady()){
            throw new CommonException("File engine not configured yet, loading failed：" + engineType);
        }
        return client;
    }
}
