package com.nuocao.dev.modular.log.provider;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.common.page.PageResult;
import com.nuocao.data.access.tenant.util.TenantUtils;
import com.nuocao.dev.modular.log.entity.DevLog;
import com.nuocao.dev.modular.log.enums.DevLogCategoryEnum;
import com.nuocao.dev.modular.log.param.DevLogPageParam;
import com.nuocao.dev.modular.log.service.DevLogService;
import com.nuocao.dev.modular.log.util.DevLogUtils;
import nuocao.plugin.dev.api.DevLogApi;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 日志API接口实现类
 */
@Component
public class DevLogApiProvider implements DevLogApi {

    @Inject
    private DevLogService devLogService;

    @Override
    public void executeLoginLog(String userName) {
        DevLogUtils.executeLoginLog(userName, TenantUtils.currentTenantId());
    }

    @Override
    public void executeLogoutLog(String userName) {
        DevLogUtils.executeLogoutLog(userName, TenantUtils.currentTenantId());
    }

    @Override
    public List<JSONObject> currentUserVisLogList() {
        DevLogPageParam pageParam = new DevLogPageParam();
        pageParam.setCategorys(Arrays.asList(DevLogCategoryEnum.LOGIN.getValue(), DevLogCategoryEnum.LOGOUT.getValue()));
        pageParam.setUserId(StpLoginUserUtils.getLoginUserId());
        PageResult<DevLog> page = devLogService.page(pageParam);
        return page.getRecords().stream().map(JSONUtil::parseObj).collect(Collectors.toList());
    }

    @Override
    public List<JSONObject> currentUserOpLogList() {
        DevLogPageParam pageParam = new DevLogPageParam();
        pageParam.setCategorys(Arrays.asList(DevLogCategoryEnum.LOGIN.getValue(), DevLogCategoryEnum.LOGOUT.getValue()));
        pageParam.setUserId(StpLoginUserUtils.getLoginUserId());
        PageResult<DevLog> page = devLogService.page(pageParam);
        return page.getRecords().stream().map(JSONUtil::parseObj).collect(Collectors.toList());
    }
}
