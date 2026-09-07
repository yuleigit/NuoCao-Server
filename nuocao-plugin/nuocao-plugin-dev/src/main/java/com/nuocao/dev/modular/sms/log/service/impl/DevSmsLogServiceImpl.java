package com.nuocao.dev.modular.sms.log.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.mapper.LambdaQueryWrapperX;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.utils.ValidationUtils;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.dev.modular.sms.log.convert.DevSmsLogConvert;
import com.nuocao.dev.modular.sms.log.entity.DevSmsLog;
import com.nuocao.dev.modular.sms.log.mapper.DevSmsLogMapper;
import com.nuocao.dev.modular.sms.log.param.DevSmsLogAddParam;
import com.nuocao.dev.modular.sms.log.param.DevSmsLogIdParam;
import com.nuocao.dev.modular.sms.log.param.DevSmsLogPageParam;
import com.nuocao.dev.modular.sms.log.service.DevSmsLogService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 短信Service接口实现类
 **/
@Component
public class DevSmsLogServiceImpl implements DevSmsLogService {

    //可排序字段名称
    private final static Set<String> ORDER_COLUMN_NAME= Set.of("sendTime");

    @Inject
    private DevSmsLogMapper mapper;

    @Override
    public PageResult<DevSmsLog> page(DevSmsLogPageParam pageParam) {

        return mapper.selectPage(pageParam,buildQueryWrapper(pageParam));
    }

    @Override
    public void deleteByIds(List<Long> logIds) {
        if(CollectionUtils.isEmpty(logIds)) return;
        mapper.deleteBatchByIds(logIds);

    }

    @Override
    public DevSmsLog detail(DevSmsLogIdParam idParam) {
        return queryEntity(idParam.getId());
    }

    @Override
    public DevSmsLog queryEntity(Long id) {
        DevSmsLog log = mapper.selectOneById(id);
        CommonExceptionI18n.notNull(log,"dev.sms.log.notExists",id);
        return log;
    }

    @Override
    public Long add(DevSmsLogAddParam addParam) {
        ValidationUtils.validate(addParam);
        DevSmsLog smsLog = DevSmsLogConvert.INSTANCE.convert(addParam);
        mapper.insert(smsLog);
        return smsLog.getId();
    }


    private QueryWrapper buildQueryWrapper(DevSmsLogPageParam pageParam){
        LambdaQueryWrapperX<DevSmsLog> queryWrapper =new LambdaQueryWrapperX<>();
        queryWrapper.eqIfPresent(DevSmsLog::getChannelCode,pageParam.getChannelCode())
                .eqIfPresent(DevSmsLog::getTemplateCode,pageParam.getTemplateCode())
                .eqIfPresent(DevSmsLog::getPhone, pageParam.getPhone())
                .likeIfPresent(DevSmsLog::getContent,pageParam.getContent());
        if(pageParam.hasSortingFields()){
            pageParam.setSortingFields(pageParam.getSortingFields().stream().filter(o->ORDER_COLUMN_NAME.contains(o.getField())).collect(Collectors.toList()));
        }else{
            queryWrapper.orderBy(DevSmsLog::getSendTime,false);
        }
        return queryWrapper;
    }

}
