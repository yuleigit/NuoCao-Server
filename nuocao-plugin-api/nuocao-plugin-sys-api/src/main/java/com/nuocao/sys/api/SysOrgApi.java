package com.nuocao.sys.api;


import cn.hutool.core.lang.tree.Tree;
import cn.hutool.json.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.nuocao.common.page.PageResult;
import com.nuocao.sys.api.dto.SysOrgTreeResult;

import java.util.List;

/**
 * 组织API
 **/
public interface SysOrgApi {

    /**
     * 根据id获取名称
     **/
    String getNameById(Long orgId);

    String getNameCacheById(Long orgId);

    /**
     * 根据组织id获取部门主管id
     **/
    Long getDirectorUserIdByOrgId(Long orgId);

    /**
     * 获取组织树选择器
     **/
    List<SysOrgTreeResult> orgTreeSelector();

    /**
     * 获取组织列表选择器
     **/
    List<SysOrgTreeResult> orgListSelector(Long parentId);

}
