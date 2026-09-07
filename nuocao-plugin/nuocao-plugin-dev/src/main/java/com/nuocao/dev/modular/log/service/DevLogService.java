package com.nuocao.dev.modular.log.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.nuocao.common.page.PageResult;
import com.nuocao.dev.modular.log.entity.DevLog;
import com.nuocao.dev.modular.log.param.DevLogDeleteParam;
import com.nuocao.dev.modular.log.param.DevLogPageParam;
import com.nuocao.dev.modular.log.result.DevLogOpBarChartDataResult;
import com.nuocao.dev.modular.log.result.DevLogOpPieChartDataResult;
import com.nuocao.dev.modular.log.result.DevLogVisLineChartDataResult;
import com.nuocao.dev.modular.log.result.DevLogVisPieChartDataResult;

import java.util.List;

/**
 * 日志Service接口
 */
public interface DevLogService {

    /**
     * 获取日志分页
     */
    PageResult<DevLog> page(DevLogPageParam devLogPageParam);


    Long add(DevLog log);

    /**
     * 清空日志
     */
    void delete(DevLogDeleteParam devLogDeleteParam);

    /**
     * 获取访问日志折线图数据
     */
    List<DevLogVisLineChartDataResult> visLogLineChartData();

    /**
     * 获取访问日志饼状图数据
     */
    List<DevLogVisPieChartDataResult> visLogPieChartData();

    /**
     * 获取操作日志柱状图数据
     */
    List<DevLogOpBarChartDataResult> opLogBarChartData();

    /**
     * 获取操作日志饼状图数据
     */
    List<DevLogOpPieChartDataResult> opLogPieChartData();


    DevLog detail(Long logId);
}
