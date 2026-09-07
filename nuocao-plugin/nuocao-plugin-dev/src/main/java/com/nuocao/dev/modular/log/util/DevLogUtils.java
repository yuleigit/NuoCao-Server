package com.nuocao.dev.modular.log.util;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.utils.*;
import com.nuocao.common.utils.http.IpUtils;
import com.nuocao.common.utils.http.RequestUtils;
import com.nuocao.common.utils.json.JsonUtils;
import com.nuocao.data.access.tenant.util.TenantUtils;
import com.nuocao.dev.modular.log.entity.DevLog;
import com.nuocao.dev.modular.log.enums.DevLogCategoryEnum;
import com.nuocao.dev.modular.log.enums.DevLogExeStatusEnum;
import com.nuocao.dev.modular.log.service.DevLogService;
import nuocao.plugin.dev.api.dto.log.DevLogAddDTO;
import org.noear.solon.Solon;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.handle.Context;


/**
 * 日志工具类
 */
public class DevLogUtils {

    private static final DevLogService devLogService = Solon.context().getBean(DevLogService.class);

    /**
     * 记录操作日志
     */
    public static void executeOperationLog(CommonLog commonLog, DevLogAddDTO addDto, Invocation inv, String resultJson) {
        DevLog devLog = buildBaseLog();
        ThreadUtil.execute(() -> {
            setCommonData(devLog,commonLog,addDto,inv);
            devLog.setExeMessage(DevLogExeStatusEnum.SUCCESS.getValue());
            devLog.setResultData(resultJson);
            creatLogSignValue(devLog);
            devLogService.add(devLog);
        });
    }

    /**
     * 记录异常日志
     */
    public static void executeExceptionLog(CommonLog commonLog, DevLogAddDTO addDto, Invocation inv, Exception exception) {

        DevLog devLog = buildBaseLog();
        ThreadUtil.execute(() -> {
            creatLogSignValue(devLog);
            devLog.setExeMessage(DevLogExeStatusEnum.FAIL.getValue());
            devLog.setExeMessage(exception.getMessage());
            devLogService.add(devLog);
        });
    }

    private static void setCommonData(DevLog devLog,CommonLog commonLog,DevLogAddDTO addDto,Invocation inv){
        Context request = RequestUtils.getCurrentRequest();
        Long loginId = StpUtil.isLogin()? StpUtil.getLoginIdAsLong():0L;
        if (NumberUtils.isLtZero(loginId)) {
            loginId = 0L;
        }
        Long finalLoginId = loginId;
        devLog.setCategory(DevLogCategoryEnum.OPERATE.getValue());
        devLog.setTitle(commonLog.value());
        devLog.setOperationType(commonLog.operationType().name());
        devLog.setExeStatus(DevLogExeStatusEnum.SUCCESS.getValue());
        devLog.setClassName(inv.target().getClass().getName());
        devLog.setFunName(inv.method().getMethod().getName());
        devLog.setReqMethod(inv.method().getMethod().getName());
        devLog.setReqUrl(request.url());
        devLog.setReqParams(JsonUtils.toJsonString(inv.args()));
        devLog.setCreateUser(finalLoginId);
        devLog.setOperatorType(addDto.getUserType());
        if(StringUtils.isBlank(devLog.getOperatorType())){
            devLog.setOperatorType(commonLog.operatorType().name());
        }
        devLog.setModular(commonLog.modular());
        devLog.setOperatorUserName(addDto.getUserName());
        devLog.setOperatorUserId(addDto.getUserId()==null?finalLoginId: addDto.getUserId());
        devLog.setBeginTime(addDto.getBeginTime());
        devLog.setEndTime(addDto.getEndTime());
        devLog.setDuration(addDto.getDuration());
    }

    /**
     * 记录登录日志
     */
    public static void executeLoginLog(String userName,Long tenantId) {
        addUserLoginOrLogOutLog(userName,tenantId,DevLogCategoryEnum.LOGIN);
    }

    /**
     * 记录登出日志
     */
    public static void executeLogoutLog(String userName,Long tenantId) {
        addUserLoginOrLogOutLog(userName,tenantId,DevLogCategoryEnum.LOGOUT);
    }

    private static void addUserLoginOrLogOutLog(String username,Long tenantId,DevLogCategoryEnum category){
        DevLog devLog = buildBaseLog();
        ThreadUtil.execute(() -> {
            TenantUtils.executeIgnore(()->{
                devLog.setCategory(category.getValue());
                devLog.setTitle(category.equals(DevLogCategoryEnum.LOGOUT)?"用户登出":"用户登录");
                devLog.setExeStatus(DevLogExeStatusEnum.SUCCESS.getValue());
                devLog.setBeginTime(LocalDateTimeUtils.now());
                devLog.setOperatorUserName(username);
                devLog.setTenantId(tenantId==null?0L:tenantId);
                creatLogSignValue(devLog);
                devLogService.add(devLog);
            });
        });

    }

    /**
     * 构建基础日志属性

     */
    private static DevLog buildBaseLog() {
        Context request = RequestUtils.getCurrentRequest();
        String ip = IpUtils.getIp(request);
        Long loginId;
        try {
            loginId = StpUtil.isLogin()? StpUtil.getLoginIdAsLong():0L;
            if (NumberUtils.isLtZero(loginId)) {
                loginId = 0L;
            }
        } catch (Exception e) {
            loginId = 0L;
        }
        DevLog devLog = new DevLog();
        devLog.setOperatorIp(ip);
        devLog.setOperatorAddress(IpUtils.getCityInfo(ip));
        devLog.setOperatorBrowser(RequestUtils.getBrowser(request));
        devLog.setOperatorOs(RequestUtils.getOs(request));
        devLog.setCreateUser(loginId);
        return devLog;
    }

    /**
     * 构建日志完整性保护签名数据
     */
    private static void creatLogSignValue (DevLog devLog) {
        String logStr = devLog.toString().replaceAll(" +","");
        devLog.setSignData(CommonCryptogramUtils.doSignature(logStr));
    }
}
