package com.nuocao.sys.modular.user.mapper;

import com.nuocao.common.mapper.BaseMapperX;
import com.nuocao.sys.modular.user.entity.SysUser;
import org.apache.ibatis.annotations.Update;

/**
 * 用户Mapper接口
 **/
public interface SysUserMapper extends BaseMapperX<SysUser> {

    @Update("UPDATE sys_user SET tenant_id=#{tenantId} WHERE id=#{userId}")
    void updateUserTenantId(Long userId,Long tenantId);

    @Update("UPDATE sys_user SET password=#{password} WHERE delete_flag=0 AND `username`=#{username}")
    void syncSysUserPasswordIgnoreTenantByAccount(String username,String password);

    @Update("UPDATE sys_user SET phone=#{phone} WHERE delete_flag=0 AND `username`=#{username}")
    void syncSysUserPhoneIgnoreTenantByAccount(String account,String phone);



}
