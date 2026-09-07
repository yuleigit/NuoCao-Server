package com.nuocao.common.event;

import org.noear.solon.core.handle.Context;

/**
 * 时间接口，所以内部事件都实现这个
 */
public interface NuoCaoEvent {
    /**
     * 执行具体的业务逻辑
     */
    void execute();
}