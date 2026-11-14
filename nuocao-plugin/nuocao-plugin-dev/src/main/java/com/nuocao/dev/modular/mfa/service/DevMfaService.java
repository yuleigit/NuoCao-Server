package com.nuocao.dev.modular.mfa.service;

import com.nuocao.common.page.PageResult;
import com.nuocao.dev.modular.mfa.param.DevMfaOwnerParam;
import com.nuocao.dev.modular.mfa.param.DevMfaPageParam;
import com.nuocao.dev.modular.mfa.result.DevMfaPageResult;

/**
 * 2FA 绑定管理服务（管理端）
 **/
public interface DevMfaService {

    /**
     * 分页查询绑定
     */
    PageResult<DevMfaPageResult> page(DevMfaPageParam param);

    /**
     * 查询绑定详情（不返回 secret 密文）
     */
    DevMfaPageResult detail(DevMfaOwnerParam param);

    /**
     * 重置失败次数并解锁
     */
    void reset(DevMfaOwnerParam param);

    /**
     * 失效绑定，用户下次登录需要重新绑定
     */
    void disable(DevMfaOwnerParam param);

    /**
     * 逻辑删除绑定
     */
    void delete(DevMfaOwnerParam param);
}
