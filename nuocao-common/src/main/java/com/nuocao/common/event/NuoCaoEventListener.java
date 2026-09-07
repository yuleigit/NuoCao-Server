package com.nuocao.common.event;

import org.noear.solon.core.event.EventListener;

/**
 * 时间监听统一入口
 */
public class NuoCaoEventListener implements EventListener<NuoCaoEvent> {
    @Override
    public void onEvent(NuoCaoEvent event) throws Throwable {
        event.execute();
    }
}
