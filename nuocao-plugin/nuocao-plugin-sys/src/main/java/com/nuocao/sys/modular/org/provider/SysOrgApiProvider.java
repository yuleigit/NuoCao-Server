package com.nuocao.sys.modular.org.provider;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.nuocao.common.page.PageResult;
import com.nuocao.data.access.tenant.util.TenantUtils;
import com.nuocao.sys.api.SysOrgApi;
import com.nuocao.sys.api.dto.SysOrgTreeResult;
import com.nuocao.sys.modular.org.entity.SysOrg;
import com.nuocao.sys.modular.org.service.SysOrgService;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Component;
import java.util.List;

/**
 * 组织API接口提供者
 **/
@Component
public class SysOrgApiProvider implements SysOrgApi {

    @Inject
    private SysOrgService sysOrgService;



    @Override
    public String getNameById(Long orgId) {
        return sysOrgService.queryEntity(orgId).getOrgName();
    }

    @Override
    public String getNameCacheById(Long orgId){
      return sysOrgService.getNameByCache(orgId);
    }

    @Override
    public Long getDirectorUserIdByOrgId(Long orgId) {
        SysOrg sysOrg = sysOrgService.getById(orgId);
        if(ObjectUtil.isNotEmpty(sysOrg)) {
            return sysOrg.getDirectorUserId();
        }
        return null;
    }

    @Override
    public List<SysOrgTreeResult> orgTreeSelector() {
        return sysOrgService.orgTreeSelector();
    }

    @Override
    public List<SysOrgTreeResult> orgListSelector(Long parentId) {

        return sysOrgService.orgListSelector(parentId);
    }


}
