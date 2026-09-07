package com.nuocao.auth.modular.login.listener;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.convert.Convert;
import com.nuocao.auth.api.AuthAccountLoginApi;
import com.nuocao.auth.api.AuthUserLoginApi;
import com.nuocao.auth.api.dto.SaUserLoginInfoDTO;
import com.nuocao.auth.core.enums.SaLoginTypeEnum;
import com.nuocao.common.ObjectUtils;
import com.nuocao.common.utils.ConvertUtils;
import com.nuocao.common.utils.StringUtils;
import nuocao.plugin.dev.api.DevLogApi;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;


/**
 * 自定义登录监听器
 *
 **/
@Component
public class AuthListener implements SaTokenListener {

    @Inject
    private AuthUserLoginApi loginUserApi;

    @Inject
    private AuthAccountLoginApi accountLoginApi;

    @Inject
    private DevLogApi devLogApi;

    /** 每次登录时触发 */
    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginParameter saLoginParameter) {
        // 更新用户的登录时间和登录ip等信息
        if(SaLoginTypeEnum.ADMIN.getValue().equals(loginType)) {
            loginUserApi.updateUserLoginInfo(Convert.toLong(loginId), saLoginParameter.getDeviceType());
            // 记录用户登录，这个是sysuser登录日志
            Object name = saLoginParameter.getExtra("name");
            if(ObjectUtils.isNotEmpty(name)) {
                devLogApi.executeLoginLog(Convert.toStr(name));
            } else {
                devLogApi.executeLoginLog(null);
            }
        } else {
        }
    }

    /** 每次注销时触发 */
    @Override
    public void doLogout(String loginType, Object loginId, String tokenValue) {
        if(SaLoginTypeEnum.ADMIN.getValue().equals(loginType)) {
            // 记录B端登出日志
            SaUserLoginInfoDTO userLoginInfoDTO = loginUserApi.getUserById(ConvertUtils.toLong(loginId));
            if(ObjectUtils.isNotEmpty(userLoginInfoDTO)) {
                devLogApi.executeLogoutLog(StringUtils.firstNotBlank(userLoginInfoDTO.getName(),userLoginInfoDTO.getNickname()));
            } else {
                devLogApi.executeLogoutLog(null);
            }
        }
    }

    /** 每次被踢下线时触发 */
    @Override
    public void doKickout(String loginType, Object loginId, String tokenValue) {
        // ...
    }

    /** 每次被顶下线时触发 */
    @Override
    public void doReplaced(String loginType, Object loginId, String tokenValue) {
        // ...
    }

    /** 每次被封禁时触发 */
    @Override
    public void doDisable(String s, Object o, String s1, int i, long l) {
        //1.31.0 接口变了
    }

    /** 每次被解封时触发 */
    @Override
    public void doUntieDisable(String s, Object o, String s1) {
        //1.31.0 接口变了
    }

    @Override
    public void doOpenSafe(String s, String s1, String s2, long l) {

    }

    @Override
    public void doCloseSafe(String s, String s1, String s2) {

    }

    /** 每次创建Session时触发 */
    @Override
    public void doCreateSession(String id) {
        // ...
    }

    /** 每次注销Session时触发 */
    @Override
    public void doLogoutSession(String id) {
        // ...
    }

    //solon 3.3.3
    @Override
    public void doRenewTimeout(String s, Object o, String s1, long l) {

    }

}
