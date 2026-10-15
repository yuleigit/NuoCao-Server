package com.nuocao.sys.modular.tenant.service;


import com.nuocao.common.page.PageResult;
import com.nuocao.sys.api.dto.SysTenantDTO;
import com.nuocao.sys.modular.tenant.entity.SysTenant;
import com.nuocao.sys.modular.tenant.param.SysTenantAddParam;
import com.nuocao.sys.modular.tenant.param.SysTenantEditParam;
import com.nuocao.sys.modular.tenant.param.SysTenantIdParam;
import com.nuocao.sys.modular.tenant.param.SysTenantPageParam;
import com.nuocao.sys.modular.tenant.result.SysTenantPageResult;
import com.nuocao.sys.modular.tenant.result.SysTenantViewResult;

import java.util.List;

/**
 * 客户机构Service接口
 *
 * @author Lei
 **/
public interface SysTenantService  {

    /**
     * 获取客户机构分页
     *
     * @author Lei
     * @date  2025/06/20 14:26
     */
    PageResult<SysTenantPageResult> page(SysTenantPageParam sysTenantPageParam);

    /**
     * 添加客户机构
     *
     * @author Lei
     * @date  2025/06/20 14:26
     */
    Long add(SysTenantAddParam sysTenantAddParam);

    /**
     * 编辑客户机构
     *
     * @author Lei
     * @date  2025/06/20 14:26
     */
    void edit(SysTenantEditParam sysTenantEditParam);

    /**
     * 删除客户机构
     *
     * @author Lei
     * @date  2025/06/20 14:26
     */
    void delete(List<SysTenantIdParam> sysTenantIdParamList);

    /**
     * 获取客户机构详情
     *
     * @author Lei
     * @date  2025/06/20 14:26
     */
    SysTenant detail(SysTenantIdParam sysTenantIdParam);

    /**
     * 得到客户详情，含项目统计信息
     * @param sysTenantIdParam
     * @return
     */
    SysTenantViewResult viewStat(SysTenantIdParam sysTenantIdParam);

    /**
     * 获取客户机构详情
     *
     * @author Lei
     * @date  2025/06/20 14:26
     **/
    SysTenant queryEntity(Long id);

    SysTenantDTO getDTO(Long id);


    /**
     * 根据租户编号获取表明
     * @param tenantCode
     * @return
     */
    SysTenantDTO getDTObyCode(String tenantCode);

    /**
     * 重缓存中获取租户名称
     * @param tenantId
     * @return
     */
    String getNameByCache(Long tenantId);

    /**
     * 清理缓存
     * @param tenantId
     * @param tenantCode
     */
    void clearCache(Long tenantId,String tenantCode);

    /**
     * 获取selelt窜想列表
     * @param pageParam
     * @return
     */
    List<SysTenantPageResult> getSelector(SysTenantPageParam pageParam);

    List<SysTenantPageResult> getSelectorAll(SysTenantPageParam pageParam);


    /**
     * 获取租户的所有权限编码
     * @param tenantId
     * @return
     */
    List<String> getTenantPermissionCodeList(Long tenantId);

    /**
     * 获取租户的所有权限菜单ID
     * @param tenantId
     * @return
     */
    List<Long> getTenantPermissionMenuIdList(Long tenantId);


    /**
     * 验证用户是否是管理员
     * @param userId
     * @return
     */
    boolean isTenantAdmin(Long tenantId,Long userId);

    List<SysTenantDTO> getTenantListByUsername(String account);


    /**
     * 根据套餐统计租户数
     * @param packageId
     * @return
     */
    int countByPackageId(Long packageId);
}
