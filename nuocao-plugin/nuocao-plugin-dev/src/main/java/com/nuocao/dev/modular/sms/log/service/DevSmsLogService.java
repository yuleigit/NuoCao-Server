
package com.nuocao.dev.modular.sms.log.service;


import com.nuocao.common.page.PageResult;
import com.nuocao.dev.modular.sms.log.entity.DevSmsLog;
import com.nuocao.dev.modular.sms.log.param.DevSmsLogAddParam;
import com.nuocao.dev.modular.sms.log.param.DevSmsLogIdParam;
import com.nuocao.dev.modular.sms.log.param.DevSmsLogPageParam;

import java.util.List;

/**
 * 短信Service接口
 **/
public interface DevSmsLogService {

    /**
     * 获取短信分页
     */
    PageResult<DevSmsLog> page(DevSmsLogPageParam pageParam);

    /**
     * 删除短信
     **/
    void deleteByIds(List<Long> logIds);

    /**
     * 获取短信详情
     */
    DevSmsLog detail(DevSmsLogIdParam idParam);

    /**
     * 获取短信详情
     */
    DevSmsLog queryEntity(Long id);

    /**
     * 添加短信
     * @param smsLog
     * @return
     */
    Long add(DevSmsLogAddParam addParam);
}
