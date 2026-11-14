package com.nuocao.dev.modular.mfa.service;

import com.nuocao.common.page.PageResult;
import com.nuocao.dev.modular.mfa.param.DevMfaEventPageParam;
import com.nuocao.dev.modular.mfa.result.DevMfaEventPageResult;

/**
 * 2FA 操作事件查询服务
 **/
public interface DevMfaEventService {

    /**
     * 分页查询操作事件
     */
    PageResult<DevMfaEventPageResult> page(DevMfaEventPageParam param);
}
