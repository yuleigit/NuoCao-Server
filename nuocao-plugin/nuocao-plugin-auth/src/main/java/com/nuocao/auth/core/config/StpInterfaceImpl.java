package com.nuocao.auth.core.config;

import cn.dev33.satoken.stp.StpInterface;
import com.nuocao.auth.api.AuthUserLoginApi;
import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.auth.core.enums.SaLoginTypeEnum;
import com.nuocao.common.cache.CacheUtils;
import com.nuocao.common.utils.ConvertUtils;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限认证接口实现类，集成权限认证功能
 **/
@Component
public class StpInterfaceImpl implements StpInterface {

    @Inject
    private AuthUserLoginApi userLoginApi;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if (SaLoginTypeEnum.ADMIN.getValue().equals(loginType)) {
            return userLoginApi.getPermissionListByUserId(ConvertUtils.toLong(loginId));
        }else{
            //  permissionListObject = commonCacheOperator.get(CacheConstant.AUTH_C_PERMISSION_LIST_CACHE_KEY + loginId);
        }
        return new ArrayList<>();
    }

    /**
     * 返回一个账号所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return StpLoginUserUtils.getLoginUser().getRoles();
    }
}