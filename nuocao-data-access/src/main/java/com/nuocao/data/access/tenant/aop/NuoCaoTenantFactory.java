package com.nuocao.data.access.tenant.aop;

import com.mybatisflex.core.tenant.TenantFactory;
import com.nuocao.data.access.tenant.context.TenantContext;
import org.noear.solon.annotation.Inject;

import java.util.List;

/**
 * mybatis-flex 租户方法实现
 */
public class NuoCaoTenantFactory implements TenantFactory {
    @Override
    public Object[] getTenantIds() {

        if(TenantContext.isIgnore() || TenantContext.getCurrentTenant()==null){
            return new Object[0];
        }

        return new Object[]{TenantContext.getCurrentTenant()};
    }
}
