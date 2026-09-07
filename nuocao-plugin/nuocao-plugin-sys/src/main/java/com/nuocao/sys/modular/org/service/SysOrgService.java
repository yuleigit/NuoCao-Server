package com.nuocao.sys.modular.org.service;

import com.nuocao.common.page.PageResult;
import com.nuocao.sys.api.dto.SysOrgTreeResult;
import com.nuocao.sys.modular.org.entity.SysOrg;
import com.nuocao.sys.modular.org.pojo.param.SysOrgAddParam;
import com.nuocao.sys.modular.org.pojo.param.SysOrgEditParam;
import com.nuocao.sys.modular.org.pojo.param.SysOrgIdParam;
import com.nuocao.sys.modular.org.pojo.param.SysOrgPageParam;
import com.nuocao.sys.modular.org.pojo.result.SysOrgResult;

import java.util.List;

/**
 * 组织Service接口
 **/
public interface SysOrgService {

    /**
     * 获取组织分页
     */
    PageResult<SysOrg> page(SysOrgPageParam sysOrgPageParam);

    /**
     * 获取组织树
     */
    List<SysOrgTreeResult> tree();

    /**
     * 获取所有部门的tree，仅在管理页面使用
     */
    List<SysOrgTreeResult> treeAll();

    /**
     * 创建tree
     * @param orgList
     * @return
     */
    List<SysOrgTreeResult> buildTreeByList(List<SysOrg> orgList);
    /**
     * 添加组织
     */
    Long add(SysOrgAddParam sysOrgAddParam);

    /**
     * 编辑组织
     */
    Long edit(SysOrgEditParam sysOrgEditParam);

    /**
     * 组织ID集合
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 获取组织详情
     */
    SysOrgResult detail(SysOrgIdParam sysOrgIdParam);

    /**
     * 获取组织详情
     **/
    SysOrg queryEntity(Long id);

    /**
     * 获取信息
     * @param orgId
     * @return
     */
    SysOrg getById(Long orgId);

    /**
     * 获取所有组织
     **/
    List<SysOrg> getAllOrgList();

    /**
     * 获取组织树选择器
     */
    List<SysOrgTreeResult> orgTreeSelector();

    /**
     * 获取组织列表选择器
     **/
    List<SysOrgTreeResult> orgListSelector(Long parentId);

    /**
     * 从缓存中读取名称
     * @param orgId
     * @return
     */
    String getNameByCache(Long orgId);

    /**
     * 获取下级ID集合
     * @param orgId
     * @return
     */
    List<Long> getOrgChildIds(Long orgId);


    List<SysOrg> getAllListByCache();

    /**
     * 清理用户作为主管的组织主管信息
     * @param userIds
     */
    void clearDirectorByUserIds(List<Long> userIds);

    /**
     * 查询id对应的所有组织
     * @param orgIds
     * @return
     */
    List<SysOrg> selectListByIds(List<Long> orgIds);


}
