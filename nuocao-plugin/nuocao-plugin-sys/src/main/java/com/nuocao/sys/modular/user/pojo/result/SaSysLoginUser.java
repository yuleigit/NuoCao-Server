package com.nuocao.sys.modular.user.pojo.result;


import com.nuocao.auth.api.dto.SaUserLoginInfoDTO;
import com.nuocao.common.enums.CommonEnableStatusEnum;

/**
 * 登录用户对象
 **/
public class SaSysLoginUser extends SaUserLoginInfoDTO {

    /**
     * 实现是否可以登录
     **/
    @Override
    public Boolean getEnabled() {
        // 仅判断状态是否正常，可自行扩展
        return getStatus().equals(CommonEnableStatusEnum.ENABLE.getValue());
    }
}
