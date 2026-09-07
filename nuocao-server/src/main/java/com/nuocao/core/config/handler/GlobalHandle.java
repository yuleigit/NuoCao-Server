package com.nuocao.core.config.handler;

import com.nuocao.common.utils.license.LicenseVerifyUtils;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 * @author noear 2022/10/4 created
 */
@Slf4j
@Component
public class GlobalHandle implements Handler {

    @Override
    public void handle(Context ctx) throws Throwable {
        LicenseVerifyUtils.verify();
    }
}