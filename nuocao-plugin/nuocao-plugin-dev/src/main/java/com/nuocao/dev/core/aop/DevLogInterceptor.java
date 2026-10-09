package com.nuocao.dev.core.aop;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.auth.api.dto.SaUserLoginInfoDTO;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.enums.CommonUserTypeEnum;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.utils.LocalDateTimeUtils;
import com.nuocao.common.utils.http.RequestUtils;
import com.nuocao.dev.modular.log.util.DevLogUtils;
import com.nuocao.plugin.dev.api.dto.log.DevLogAddDTO;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

import java.lang.reflect.InvocationTargetException;


public class DevLogInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        CommonLog commonLog = inv.method().getAnnotation(CommonLog.class);
        Object result = null;
        //构建基础信息
        DevLogAddDTO logDto = DevLogAddDTO.builder()
                .userType(CommonUserTypeEnum.ADMIN.getValue() )
                .beginTime(LocalDateTimeUtils.currentTimestamp())
                .reqUrl(RequestUtils.getReqUrl())
                .build();
        try {
            result = inv.invoke();
            SaUserLoginInfoDTO loginUser=null;
            String userName = "未知";
            try {
                loginUser = StpLoginUserUtils.getLoginUser();
                if (ObjectUtil.isNotNull(loginUser)) {
                    userName = loginUser.getName();
                }
            } catch (Exception ignored) {
            }
            // 异步记录日志
            logDto.setEndTime(LocalDateTimeUtils.currentTimestamp());
            logDto.setTenantId(loginUser!=null?loginUser.getTenantId():0L);
            DevLogUtils.executeOperationLog(commonLog, logDto, inv, JSONUtil.toJsonStr(result));

        } catch (Throwable e) {
            // 1. 还原反射包装的真实异常
            Throwable realThrowable = e;
            if (e instanceof InvocationTargetException && e.getCause() != null) {
                realThrowable = e.getCause();
            }

            // 2. 将 Throwable 转为 Exception 传入 executeExceptionLog
            Exception targetException;
            if (realThrowable instanceof Exception) {
                targetException = (Exception) realThrowable;
            } else {
                // 处理极少见的 JVM 级别的 Error (如 OutOfMemoryError 等)
                targetException = new Exception(realThrowable.getMessage(), realThrowable);
            }
            String userName = "未知";
            SaUserLoginInfoDTO loginUser=null;
            try {
                loginUser =StpLoginUserUtils.getLoginUser();
                if (ObjectUtil.isNotNull(loginUser)) {
                    userName = loginUser.getName();
                }
            } catch (Exception ignored) {
            }
            //异步记录日志
            logDto.setEndTime(LocalDateTimeUtils.currentTimestamp());
            logDto.setTenantId(loginUser!=null?loginUser.getTenantId():0L);
            DevLogUtils.executeExceptionLog(commonLog, logDto, inv, targetException);
            throw new CommonException(e.getMessage());
        }

        return result;
    }
}
