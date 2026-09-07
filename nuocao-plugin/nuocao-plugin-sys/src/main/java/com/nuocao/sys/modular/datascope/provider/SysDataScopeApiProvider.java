package com.nuocao.sys.modular.datascope.provider;

import com.nuocao.sys.api.SysDataScopeApi;
import com.nuocao.sys.api.dto.DataScopeEditDTO;
import com.nuocao.sys.api.dto.OrgDataScopeDTO;
import com.nuocao.sys.api.enums.DataScopeCategoryEnum;
import com.nuocao.sys.api.enums.DataScopeObjectTypeEnum;
import com.nuocao.sys.modular.datascope.service.SysDataScopeService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;


/**
 * 实现数据权限接口
 */
@Component
public class SysDataScopeApiProvider implements SysDataScopeApi {


    @Inject
    private SysDataScopeService sysDataScopeService;

    @Override
    public OrgDataScopeDTO getOrgDataScopeByUserId(Long userId) {
        return sysDataScopeService.getOrgDataScopeByUserId(userId);
    }

    @Override
    public void updateDataScope(DataScopeEditDTO data) {
        sysDataScopeService.updateDataScope(data);
    }

    @Override
    public DataScopeEditDTO getDataScope(DataScopeCategoryEnum category, DataScopeObjectTypeEnum objectType, Long objectId) {
        return sysDataScopeService.getEditDto(category.getType(),objectType.getType(),objectId);
    }
}
