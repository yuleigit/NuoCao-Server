package com.nuocao.sys.api;


import com.nuocao.sys.api.dto.DataScopeEditDTO;
import com.nuocao.sys.api.dto.OrgDataScopeDTO;
import com.nuocao.sys.api.dto.UserPermissionEditDTO;
import com.nuocao.sys.api.enums.DataScopeCategoryEnum;
import com.nuocao.sys.api.enums.DataScopeObjectTypeEnum;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.util.List;

/**
 * 用户权限授权 接口
 */
@Valid
public interface SysUserPermissionApi {


    /**
     * 得到用户得 权限编码列表，用于授权获取，编辑不适用
     * @return
     */
    List<String> getPermissionCodeList(Long userId);



    /**
     * 更新数据权限
     * @param data
     */
    void updatePermission(@Validated UserPermissionEditDTO data);

    /**
     * 获取数据权限
     * @param objectType 对象类型
     * @param objectId 对象ID
     */
    UserPermissionEditDTO getPermission( DataScopeObjectTypeEnum objectType, Long objectId);

    /**
     * 获取对象授权的菜单ID集合
     * @return
     */
    List<Long> getPermissionMenuIdList(DataScopeObjectTypeEnum objectType, Long objectId);


}
