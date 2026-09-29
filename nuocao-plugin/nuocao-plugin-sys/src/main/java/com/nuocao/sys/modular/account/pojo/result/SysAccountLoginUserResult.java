package com.nuocao.sys.modular.account.pojo.result;


import com.nuocao.auth.api.dto.AccountLoginInfoDTO;
import com.nuocao.common.enums.CommonEnableStatusEnum;

/**
 * 系统账户登录 用户对象
 * 仅在登录过程中起作用，登录成功后以sys_user为准
 **/
public class SysAccountLoginUserResult extends AccountLoginInfoDTO {

    /**
     * 实现是否可以登录
     *
     **/
    public Boolean getEnabled() {
        // 仅判断状态是否正常，可自行扩展
        return this.getStatus().equals(CommonEnableStatusEnum.ENABLE.getValue());
    }
}
