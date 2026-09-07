package com.nuocao.common.utils;

import org.noear.solon.core.handle.Context;

public class SolonFilterUtils {

    public static boolean isOptionsRequest(Context ctx)
    {
        if ("OPTIONS".equalsIgnoreCase(ctx.method())) {
            return true;
        }else{
            return false;
        }
    }

    /**
     * 输出结果
     * @param ctx
     * @param result
     * @throws Throwable
     */
    public static void writeResult(Context ctx, Object result) throws Throwable {
        if (result != null) {
            ctx.render(result);
        }

        ctx.setHandled(true);
    }
}
