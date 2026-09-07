package com.nuocao.common.validation;

import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.Validator;

public class MobileValidator implements Validator<Mobile> {


    @Override
    public Result validateOfContext(Context ctx, Mobile anno, String value, StringBuilder tmp) {
        if (StrUtil.isEmpty(value)) {
            return Result.failure();
        }
        // 校验手机
        return PhoneUtil.isMobile(value)?Result.succeed():Result.failure();
    }
}
