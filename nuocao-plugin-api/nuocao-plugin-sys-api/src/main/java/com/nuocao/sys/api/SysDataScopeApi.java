package com.nuocao.sys.api;


import com.nuocao.sys.api.dto.DataScopeEditDTO;
import com.nuocao.sys.api.dto.OrgDataScopeDTO;
import com.nuocao.sys.api.enums.DataScopeCategoryEnum;
import com.nuocao.sys.api.enums.DataScopeObjectTypeEnum;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

@Valid
public interface SysDataScopeApi {


    /**
     * 得到用户数据权限信息
     * @param id
     * @return
     */
    OrgDataScopeDTO getOrgDataScopeByUserId(Long id);


    /**
     * 更新数据权限
     * @param data
     */
    void updateDataScope(@Validated DataScopeEditDTO data);

    /**
     * 获取数据权限
     * @param category 类型
     * @param objectType 对象类型
     * @param objectId 对象ID
     */
    DataScopeEditDTO getDataScope(DataScopeCategoryEnum category, DataScopeObjectTypeEnum objectType, Long objectId);


}
