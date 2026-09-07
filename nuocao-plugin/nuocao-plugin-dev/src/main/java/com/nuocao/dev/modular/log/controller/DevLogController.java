package com.nuocao.dev.modular.log.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.paginate.Page;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.dev.modular.log.entity.DevLog;
import com.nuocao.dev.modular.log.param.DevLogDeleteParam;
import com.nuocao.dev.modular.log.param.DevLogPageParam;
import com.nuocao.dev.modular.log.result.DevLogOpBarChartDataResult;
import com.nuocao.dev.modular.log.result.DevLogOpPieChartDataResult;
import com.nuocao.dev.modular.log.result.DevLogVisLineChartDataResult;
import com.nuocao.dev.modular.log.result.DevLogVisPieChartDataResult;
import com.nuocao.dev.modular.log.service.DevLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.Valid;


import java.util.List;

/**
 * 日志控制器
 */
@Api(tags = "日志控制器")
@Controller
@Valid
public class DevLogController {

    @Inject
    private DevLogService devLogService;

    /**
     * 获取日志分页
     */
    @ApiOperation("获取日志分页")
    @Get
    @SaCheckPermission("dev:log:query")
    @Mapping("/dev/log/page")
    public CommonResult<PageResult<DevLog>> page(DevLogPageParam devLogPageParam) {
        return CommonResult.data(devLogService.page(devLogPageParam));
    }

    /**
     * 获取日志分页
     */
    @ApiOperation("获取日志详情")
    @Get
    @SaCheckPermission("dev:log:query")
    @Mapping("/dev/log/detail")
    public CommonResult<DevLog> detail(Long id) {
        return CommonResult.data(devLogService.detail(id));
    }


    /**
     * 清空日志
     */
    @ApiOperation("清空日志")
    @CommonLog("清空日志")
    @Post
    @SaCheckPermission("dev:log:delete")
    @Mapping("/dev/log/delete")
    public CommonResult<String> delete(DevLogDeleteParam devLogDeleteParam) {
        devLogService.delete(devLogDeleteParam);
        return CommonResult.ok();
    }

    /**
     * 获取访问日志折线图数据
     */
    @ApiOperation("获取访问日志折线图数据")
    @Get
    @Mapping("/dev/log/vis/lineChartData")
    public CommonResult<List<DevLogVisLineChartDataResult>> visLogLineChartData() {
        return CommonResult.data(devLogService.visLogLineChartData());
    }

    /**
     * 获取访问日志饼状图数据
     */
    @ApiOperation("获取访问日志饼状图数据")
    @Get
    @Mapping("/dev/log/vis/pieChartData")
    public CommonResult<List<DevLogVisPieChartDataResult>> visLogPieChartData() {
        return CommonResult.data(devLogService.visLogPieChartData());
    }

    /**
     * 获取操作日志柱状图数据
     */
    @ApiOperation("获取操作日志柱状图数据")
    @Get
    @Mapping("/dev/log/op/barChartData")
    public CommonResult<List<DevLogOpBarChartDataResult>> opLogBarChartData() {
        return CommonResult.data(devLogService.opLogBarChartData());
    }

    /**
     * 获取操作日志饼状图数据
     */
    @ApiOperation("获取操作日志饼状图数据")
    @Get
    @Mapping("/dev/log/op/pieChartData")
    public CommonResult<List<DevLogOpPieChartDataResult>> opLogPieChartData() {
        return CommonResult.data(devLogService.opLogPieChartData());
    }
}
