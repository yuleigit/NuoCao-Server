package com.nuocao.common.event;

import org.noear.solon.core.event.EventBus;

/**
 * event工具类
 */
public class NuoCaoEventHelper {

    public static void send(NuoCaoEvent event){
        EventBus.publish(event);
    }
}
