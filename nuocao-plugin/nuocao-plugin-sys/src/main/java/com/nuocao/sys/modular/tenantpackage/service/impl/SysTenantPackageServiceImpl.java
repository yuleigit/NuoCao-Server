package com.nuocao.sys.modular.tenantpackage.service.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.nuocao.common.cache.CacheUtils;
import com.nuocao.common.enums.CommonYesNoEnum;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.utils.StrFormatUtils;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.data.access.tenant.annotation.TenantIgnore;
import com.nuocao.sys.api.SysMenuApi;
import com.nuocao.sys.api.SysTenantApi;
import com.nuocao.sys.api.dto.menu.SysMenuDTO;
import com.nuocao.sys.modular.org.service.SysOrgService;
import com.nuocao.sys.modular.tenantpackage.convert.SysTenantPackageConvert;
import com.nuocao.sys.modular.tenantpackage.entity.SysTenantPackage;
import com.nuocao.sys.modular.tenantpackage.mapper.SysTenantPackageMapper;
import com.nuocao.sys.modular.tenantpackage.param.SysTenantPackageAddParam;
import com.nuocao.sys.modular.tenantpackage.param.SysTenantPackageEditParam;
import com.nuocao.sys.modular.tenantpackage.param.SysTenantPackageIdParam;
import com.nuocao.sys.modular.tenantpackage.param.SysTenantPackagePageParam;
import com.nuocao.sys.modular.tenantpackage.service.SysTenantPackageService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Transaction;

import java.util.*;

/**
 * 租户套餐Service接口实现类
 *
 * @author Lei
 * @date  2025/06/19 16:30
 **/
@Component
@TenantIgnore
public class SysTenantPackageServiceImpl implements SysTenantPackageService {

    private static final int CACHE_TIME=1000*60*60*24; //缓存一天

    private static final String NAME_CACHE_KEY="cache:tenant_package:name:{}";
    private static final String MENU_IDS_CACHE_KEY="cache:tenant_package:menu_ids:{}";


    @Inject
    private SysTenantPackageMapper tenantPackageMapper;

    @Inject
    private SysOrgService sysOrgService;
    @Inject
    private SysMenuApi sysMenuApi;

    @Inject
    private SysTenantApi sysTenantApi;



    @Inject
    private CacheUtils cacheUtils;




    @Override
    public PageResult<SysTenantPackage> page(SysTenantPackagePageParam sysTenantPackagePageParam) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if(ObjectUtil.isNotEmpty(sysTenantPackagePageParam.getName())) {
            queryWrapper.like(SysTenantPackage::getName, sysTenantPackagePageParam.getName());
        }
        if(CollectionUtils.isEmpty(sysTenantPackagePageParam.getSortingFields())){
            queryWrapper.orderBy(SysTenantPackage::getId);
        }
        return tenantPackageMapper.selectPage(sysTenantPackagePageParam,queryWrapper);
    }

    @Transaction
    @Override
    public void add(SysTenantPackageAddParam sysTenantPackageAddParam) {
        SysTenantPackage sysTenantPackage = SysTenantPackageConvert.INSTANCE.convert(sysTenantPackageAddParam);
        validInfo(sysTenantPackage);
        tenantPackageMapper.insert(sysTenantPackage);
        //保存成功，开始创建基础资源
    }

    /**
     * 初始化租户资源
     */
    public void InitTenantRes(){

    }

    @Transaction
    @Override
    public void edit(SysTenantPackageEditParam sysTenantPackageEditParam) {
        SysTenantPackage sysTenantPackage = SysTenantPackageConvert.INSTANCE.convert(sysTenantPackageEditParam);
        validInfo(sysTenantPackage);
        tenantPackageMapper.updateById(sysTenantPackage);
        this.clearCache(sysTenantPackage.getId());
    }

    @Transaction
    @Override
    public void delete(List<SysTenantPackageIdParam> SysTenantPackageIdParamList) {
        List<Long> ids = CollStreamUtil.toList(SysTenantPackageIdParamList, SysTenantPackageIdParam::getId);

    }

    @Override
    public void deleteByIds(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)) return ;
        List<SysTenantPackage> packageList = tenantPackageMapper.selectListByIds(ids);
        if(CollectionUtils.isEmpty(packageList)){
            return;
        }
        for (SysTenantPackage pack:
             packageList) {
            if(sysTenantApi.countByPackageId(pack.getId())>0){
                throw new CommonExceptionI18n("sys.tenant.package.delete.existsTenant",pack.getName());
            }
        }

        // 执行删除
        tenantPackageMapper.deleteBatchByIds(ids);

        for (Long id:
                ids) {
            this.clearCache(id);
        }
    }

    @Override
    public SysTenantPackage detail(SysTenantPackageIdParam SysTenantPackageIdParam) {
        return this.queryEntity(SysTenantPackageIdParam.getId());
    }

    @Override
    public SysTenantPackage queryEntity(Long id) {
        SysTenantPackage SysTenantPackage = tenantPackageMapper.selectOneById(id);
        if(ObjectUtil.isEmpty(SysTenantPackage)) {
             throw new CommonExceptionI18n("sys.tenant.package.notExists", id);
        }
        return SysTenantPackage;
    }


    @Override
    public SysTenantPackage getById(Long packageId) {
        return tenantPackageMapper.selectOneById(packageId);
    }

    @Override
    public List<SysTenantPackage> getSelector() {
        QueryWrapper queryWrapper = new QueryWrapper()
                .select(SysTenantPackage::getId, SysTenantPackage::getName)
                        .eq(SysTenantPackage::getEnableStatus, CommonYesNoEnum.YES.getValue());
        queryWrapper.orderBy(SysTenantPackage::getId);
        return  tenantPackageMapper.selectListByQuery(queryWrapper);
    }

    @Override
    public String getNameByCache(Long packageId) {
        if(packageId==null) return "";
        return cacheUtils.getOrSetObject(StrFormatUtils.doFormat(NAME_CACHE_KEY,packageId),String.class,()->{
            SysTenantPackage plan = tenantPackageMapper.selectOneById(packageId);
            return plan==null ? "":plan.getName();
        });
    }

    public void clearCache(Long packageId){
        if(packageId==null) return ;
        cacheUtils.removes(StrFormatUtils.doFormat(NAME_CACHE_KEY,packageId)
             ,StrFormatUtils.doFormat(MENU_IDS_CACHE_KEY,packageId)
                );
    }


    private void validInfo(SysTenantPackage info){

        SysTenantPackage temp = tenantPackageMapper.selectOne(SysTenantPackage::getName,info.getName());
        if(temp!=null && !temp.getId().equals(info.getId()))
        {
            throw new CommonExceptionI18n("sys.tenant.package.edit.nameExists",temp.getName());
        }
    }

    @Override
    public List<Long> getPackageMenuIdsByCache(Long packageId) {
        return cacheUtils.getOrSetList(StrFormatUtils.doFormat(MENU_IDS_CACHE_KEY,packageId),Long.class,()->{
             return getPackageMenuIds(packageId);
        });
    }

    @Override
    public List<Long> getPackageMenuIds(Long packageId) {
        SysTenantPackage pack = getById(packageId);
        return pack==null || pack.getMenuIds()==null?Collections.emptyList():pack.getMenuIds();
    }

    @Override
    public List<SysMenuDTO> getPackageMenuList(Long packageId) {

        return sysMenuApi.getMenuDtoList(getPackageMenuIdsByCache(packageId));
    }
}
