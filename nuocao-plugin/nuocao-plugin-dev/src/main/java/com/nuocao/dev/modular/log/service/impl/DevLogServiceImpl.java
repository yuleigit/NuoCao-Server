package com.nuocao.dev.modular.log.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.nuocao.common.mapper.LambdaQueryWrapperX;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.utils.LocalDateTimeUtils;
import com.nuocao.dev.modular.log.entity.DevLog;
import com.nuocao.dev.modular.log.enums.DevLogCategoryEnum;
import com.nuocao.dev.modular.log.mapper.DevLogMapper;
import com.nuocao.dev.modular.log.param.DevLogDeleteParam;
import com.nuocao.dev.modular.log.param.DevLogPageParam;
import com.nuocao.dev.modular.log.result.DevLogOpBarChartDataResult;
import com.nuocao.dev.modular.log.result.DevLogOpPieChartDataResult;
import com.nuocao.dev.modular.log.result.DevLogVisLineChartDataResult;
import com.nuocao.dev.modular.log.result.DevLogVisPieChartDataResult;
import com.nuocao.dev.modular.log.service.DevLogService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 日志Service接口实现类
 */
@Component
public class DevLogServiceImpl implements DevLogService {

    @Inject
    private DevLogMapper mapper;

    @Override
    public PageResult<DevLog> page(DevLogPageParam devLogPageParam) {
        QueryWrapper queryWrapper = buildQueryWrapper(devLogPageParam);
        return mapper.selectPage(devLogPageParam, queryWrapper);
    }

    @Override
    public Long add(DevLog log) {
        mapper.insert(log);
        return log.getId();
    }

    @Override
    public void delete(DevLogDeleteParam devLogDeleteParam) {
        mapper.deleteByQuery(new QueryWrapper().eq(DevLog::getCategory, devLogDeleteParam.getCategory()));
    }

    @Override
    public List<DevLogVisLineChartDataResult> visLogLineChartData() {
        DateTime lastWeek = DateUtil.lastWeek();
        DateTime now = DateTime.now();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.in(DevLog::getCategory, DevLogCategoryEnum.LOGIN.getValue(), DevLogCategoryEnum.LOGOUT.getValue())
                .between(DevLog::getBeginTime, lastWeek, now).orderBy(DevLog::getBeginTime);
        Map<String, List<JSONObject>> listMap = mapper.selectListByQuery(queryWrapper)
                .stream().map(devLog -> JSONUtil.parseObj(devLog).set("date", LocalDateTimeUtils.formatDate(devLog.getBeginTime())))
                .collect(Collectors.groupingBy(jsonObject -> jsonObject.getStr("date")));
        long between = DateUtil.between(lastWeek, now, DateUnit.DAY);
        List<DevLogVisLineChartDataResult> resultList = CollectionUtil.newArrayList();
        for (int i = 1; i <= between; i++) {
            DevLogVisLineChartDataResult devLogVisLineChartDataResult = new DevLogVisLineChartDataResult();
            String date = DateUtil.formatDate(DateUtil.offsetDay(lastWeek, i));
            devLogVisLineChartDataResult.setDate(date);
            List<JSONObject> jsonObjectList = listMap.get(date);
            if (ObjectUtil.isNotEmpty(jsonObjectList)) {
                devLogVisLineChartDataResult.setLoginCount(jsonObjectList.stream().filter(jsonObject -> jsonObject.getStr("category")
                        .equals(DevLogCategoryEnum.LOGIN.getValue())).count());
                devLogVisLineChartDataResult.setLogoutCount(jsonObjectList.stream().filter(jsonObject -> jsonObject.getStr("category")
                        .equals(DevLogCategoryEnum.LOGOUT.getValue())).count());
            } else {
                devLogVisLineChartDataResult.setLoginCount(0L);
                devLogVisLineChartDataResult.setLogoutCount(0L);
            }
            resultList.add(devLogVisLineChartDataResult);
        }
        return resultList;
    }

    @Override
    public List<DevLogVisPieChartDataResult> visLogPieChartData() {
        List<DevLogVisPieChartDataResult> resultList = CollectionUtil.newArrayList();
        DevLogVisPieChartDataResult devLogLoginPieChartDataResult = new DevLogVisPieChartDataResult();
        devLogLoginPieChartDataResult.setType("登录");
        devLogLoginPieChartDataResult.setValue(mapper.selectCountByQuery(new QueryWrapper()
                .eq(DevLog::getCategory, DevLogCategoryEnum.LOGIN.getValue())));
        resultList.add(devLogLoginPieChartDataResult);

        DevLogVisPieChartDataResult devLogLogoutPieChartDataResult = new DevLogVisPieChartDataResult();
        devLogLogoutPieChartDataResult.setType("登出");
        devLogLogoutPieChartDataResult.setValue(mapper.selectCountByQuery(new QueryWrapper()
                .eq(DevLog::getCategory, DevLogCategoryEnum.LOGOUT.getValue())));
        resultList.add(devLogLogoutPieChartDataResult);
        return resultList;
    }

    @Override
    public List<DevLogOpBarChartDataResult> opLogBarChartData() {
        DateTime lastWeek = DateUtil.lastWeek();
        DateTime now = DateTime.now();
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper
                .select(DevLog::getId,DevLog::getTitle,DevLog::getOperatorIp,DevLog::getOperatorAddress,DevLog::getCategory,DevLog::getClassName,
                        DevLog::getFunName,DevLog::getBeginTime,DevLog::getOperatorUserName)
                .in(DevLog::getCategory, DevLogCategoryEnum.OPERATE.getValue(), DevLogCategoryEnum.EXCEPTION.getValue()).between(DevLog::getBeginTime, lastWeek, now)
                .orderBy(DevLog::getBeginTime);
        Map<String, List<JSONObject>> listMap = mapper.selectListByQuery(queryWrapper)
                .stream().map(devLog -> JSONUtil.parseObj(devLog).set("date", LocalDateTimeUtils.formatDate(devLog.getBeginTime())))
                .collect(Collectors.groupingBy(jsonObject -> jsonObject.getStr("date")));
        long between = DateUtil.between(lastWeek, now, DateUnit.DAY);
        List<DevLogOpBarChartDataResult> resultList = CollectionUtil.newArrayList();
        for (int i = 1; i <= between; i++) {
            String date = DateUtil.formatDate(DateUtil.offsetDay(lastWeek, i));
            DevLogOpBarChartDataResult devLogOperateBarChartDataResult = new DevLogOpBarChartDataResult();
            devLogOperateBarChartDataResult.setDate(date);
            devLogOperateBarChartDataResult.setTitle("操作日志");
            DevLogOpBarChartDataResult devLogExceptionBarChartDataResult = new DevLogOpBarChartDataResult();
            devLogExceptionBarChartDataResult.setDate(date);
            devLogExceptionBarChartDataResult.setTitle("异常日志");
            List<JSONObject> jsonObjectList = listMap.get(date);
            if (ObjectUtil.isNotEmpty(jsonObjectList)) {
                devLogOperateBarChartDataResult.setCount(jsonObjectList.stream().filter(jsonObject -> jsonObject.getStr("category")
                        .equals(DevLogCategoryEnum.OPERATE.getValue())).count());
                devLogExceptionBarChartDataResult.setCount(jsonObjectList.stream().filter(jsonObject -> jsonObject.getStr("category")
                        .equals(DevLogCategoryEnum.EXCEPTION.getValue())).count());
            } else {
                devLogOperateBarChartDataResult.setCount(0L);
                devLogExceptionBarChartDataResult.setCount(0L);
            }
            resultList.add(devLogOperateBarChartDataResult);
            resultList.add(devLogExceptionBarChartDataResult);
        }
        return resultList;
    }

    @Override
    public List<DevLogOpPieChartDataResult> opLogPieChartData() {
        List<DevLogOpPieChartDataResult> resultList = CollectionUtil.newArrayList();
        DevLogOpPieChartDataResult devLogOperatePieChartDataResult = new DevLogOpPieChartDataResult();
        devLogOperatePieChartDataResult.setType("操作日志");
        devLogOperatePieChartDataResult.setValue(mapper.selectCountByQuery(new QueryWrapper()
                .eq(DevLog::getCategory, DevLogCategoryEnum.OPERATE.getValue())));
        resultList.add(devLogOperatePieChartDataResult);

        DevLogOpPieChartDataResult devLogExceptionPieChartDataResult = new DevLogOpPieChartDataResult();
        devLogExceptionPieChartDataResult.setType("异常日志");
        devLogExceptionPieChartDataResult.setValue(mapper.selectCountByQuery(new QueryWrapper()
                .eq(DevLog::getCategory, DevLogCategoryEnum.EXCEPTION.getValue())));
        resultList.add(devLogExceptionPieChartDataResult);
        return resultList;
    }


    @Override
    public DevLog detail(Long logId) {
        return mapper.selectOneById(logId);
    }

    private QueryWrapper buildQueryWrapper(DevLogPageParam param){
        LambdaQueryWrapperX<DevLog> queryWrapper =  new LambdaQueryWrapperX<>();
        queryWrapper.eqIfPresent(DevLog::getCategory,param.getCategory())
                .likeIfPresent(DevLog::getTitle,param.getTitle());

        if (!param.hasSortingFields()) {
            queryWrapper.orderBy(DevLog::getBeginTime, false);
        }
        // page查询中排除较大的字段（提升查询速度）
        queryWrapper.select(DevLog::getId, DevLog::getCategory,DevLog::getModular, DevLog::getTitle, DevLog::getExeStatus, DevLog::getOperatorIp,
                DevLog::getOperatorAddress, DevLog::getOperatorType, DevLog::getOperatorBrowser,
                DevLog::getFunName, DevLog::getOperatorOs, DevLog::getClassName, DevLog::getReqMethod,
                DevLog::getReqUrl, DevLog::getBeginTime, DevLog::getOperatorUserName, DevLog::getCreateTime, DevLog::getCreateTime,
                DevLog::getCreateUser,DevLog::getBeginTime,DevLog::getEndTime,DevLog::getDuration);

        return queryWrapper;
    }
}
