package com.nuocao.sys.modular.datascope.service;


import com.nuocao.sys.api.dto.DataScopeEditDTO;
import com.nuocao.sys.api.dto.OrgDataScopeDTO;
import com.nuocao.sys.modular.datascope.entity.SysDataScope;
import com.nuocao.sys.modular.datascope.result.SysDataScopeViewResult;

import java.util.List;

/**
 * 数据权限Service接口
 *
 **/
public interface SysDataScopeService  {


    /**
     * 更新数据权限信息，没有添加，有责更新
     */
    void updateDataScope(DataScopeEditDTO data);


    /**
     * 获取数据权限详情
     *
     * @author LEI
     * @date  2025/09/25 18:23
     **/
    SysDataScopeViewResult getViewByObjectId(String category, String objectType, Long objectId);

    /**
     * 获取权限信息，不存在返回null
     * @param category
     * @param objectType
     * @param objectId
     * @return
     */
    SysDataScope get(String category, String objectType, Long objectId);

    /**
     * 获取更新对象
     * @param category
     * @param objectType
     * @param objectId
     * @return
     */
    DataScopeEditDTO getEditDto(String category,String objectType,Long objectId);


    /**
     * 得到对象可用数据权限
     * @param userId
     * @return
     */
    OrgDataScopeDTO getOrgDataScopeByUserId(Long userId);






}
