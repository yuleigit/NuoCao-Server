package com.nuocao.sys.modular.tenant.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.nuocao.common.cache.CacheUtils;
import com.nuocao.common.enums.CommonEnableStatusEnum;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.language.LanguageUtils;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.utils.ConvertUtils;
import com.nuocao.common.utils.LocalDateTimeUtils;
import com.nuocao.common.utils.NumberUtils;
import com.nuocao.common.utils.ValidationUtils;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.data.access.datascope.annotation.DataScopeIgnore;
import com.nuocao.data.access.tenant.annotation.TenantIgnore;
import com.nuocao.data.access.tenant.constants.TenantConstants;
import com.nuocao.data.access.tenant.util.TenantUtils;
import com.nuocao.sys.api.SysTenantPackageApi;
import com.nuocao.sys.api.dto.SysAccountDTO;
import com.nuocao.sys.api.dto.SysTenantDTO;
import com.nuocao.sys.api.dto.SysUserDTO;
import com.nuocao.sys.core.enums.SysRoleCodeEnum;
import com.nuocao.sys.modular.account.service.SysAccountService;
import com.nuocao.sys.modular.menu.service.SysMenuService;
import com.nuocao.sys.modular.org.pojo.param.SysOrgAddParam;
import com.nuocao.sys.modular.org.service.SysOrgService;
import com.nuocao.sys.modular.role.pojo.param.SysRoleAddParam;
import com.nuocao.sys.modular.role.service.SysRoleService;
import com.nuocao.sys.modular.tenant.convert.SysTenantConvert;
import com.nuocao.sys.modular.tenant.entity.SysTenant;
import com.nuocao.sys.modular.tenant.mapper.SysTenantMapper;
import com.nuocao.sys.modular.tenant.param.SysTenantAddParam;
import com.nuocao.sys.modular.tenant.param.SysTenantEditParam;
import com.nuocao.sys.modular.tenant.param.SysTenantIdParam;
import com.nuocao.sys.modular.tenant.param.SysTenantPageParam;
import com.nuocao.sys.modular.tenant.reslut.SysTenantPageResult;
import com.nuocao.sys.modular.tenant.reslut.SysTenantViewResult;
import com.nuocao.sys.modular.tenant.service.SysTenantService;
import com.nuocao.sys.modular.user.entity.SysUser;
import com.nuocao.sys.modular.user.service.SysUserService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.annotation.CacheRemove;
import org.noear.solon.data.annotation.Transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户机构Service接口实现类
 *
 * @author Lei
 * @date  2025/06/20 14:26
 **/
@Component
public class SysTenantServiceImpl implements SysTenantService {

    @Inject
    private SysTenantService thisService;

    @Inject
    private SysTenantMapper sysTenantMapper;
    @Inject
    private SysTenantPackageApi sysTenantPackageApi;
    @Inject
    private SysUserService sysUserService;

    @Inject
    private SysOrgService sysOrgService;

    @Inject
    private SysAccountService accountService;


    @Inject
    private SysRoleService roleService;

    @Inject
    private SysMenuService sysMenuService;

    @Inject
    private CacheUtils cacheUtils;


    @Override
    public PageResult<SysTenantPageResult> page(SysTenantPageParam sysTenantPageParam) {
        QueryWrapper query = getQueryWrapper(sysTenantPageParam);
        PageResult<SysTenantPageResult> page= SysTenantConvert.INSTANCE.convertPage(sysTenantMapper.selectPage(sysTenantPageParam, query));
        return page;
    }

    @Transaction
    @Override
    public Long add(SysTenantAddParam sysTenantAddParam) {

        String packageName = sysTenantPackageApi.getNameByCache(sysTenantAddParam.getPackageId());
        CommonExceptionI18n.notBlank(packageName,"sys.tenant.package.notExists",sysTenantAddParam.getPackageId());
        CommonExceptionI18n.isTrue(ValidationUtils.isLowerNumber(sysTenantAddParam.getTenantCode()),"sys.tenant.code.formatError");
        SysTenant sysTenant = BeanUtil.toBean(sysTenantAddParam, SysTenant.class);

        SysTenant temp = sysTenantMapper.selectOneByQuery(QueryWrapper.create().eq(SysTenant::getTenantName,sysTenant.getTenantName()));
        if(temp!=null){
            throw new CommonExceptionI18n("sys.tenant.name.exists",sysTenant.getTenantName());
        }
         temp = sysTenantMapper.selectOneByQuery(QueryWrapper.create().eq(SysTenant::getTenantCode,sysTenant.getTenantCode()));
        if(temp!=null){
            throw new CommonExceptionI18n("sys.tenant.code.exists",sysTenant.getTenantName());
        }
        SysAccountDTO accountDto = accountService.getAccountDtoByCache(sysTenantAddParam.getAdminAccount());
        CommonExceptionI18n.notNull(accountDto,"sys.tenant.admin.accountNotExists",sysTenantAddParam.getAdminAccount());
        sysTenant.setTenantAdminId(0L); //默认为0
        sysTenantMapper.insert(sysTenant,true);
        TenantUtils.execute(sysTenant.getId(),()->{
            //创建租户默认菜单和授权（默认创建一个管理员角色和授权资源）
            Long roleId = createTenantMenuRole(sysTenant);

            //创建租户管理员
            sysTenant.setTenantAdminId(syncAdminUser(sysTenant, accountDto,roleId));

            //完成保存创建基础信息

            this.createOk(sysTenant,sysTenantAddParam.getAdminAccount());
        });


        thisService.clearCache(sysTenant.getId(),sysTenant.getTenantCode());
        return sysTenant.getId();
    }

    public void createOk(SysTenant tenant,String adminAccount){

        //以租户的名义创建顶级组织
        TenantUtils.execute(tenant.getId(),()->{
            SysOrgAddParam orgAddParam = new SysOrgAddParam();
            orgAddParam.setParentId(0L);
            orgAddParam.setOrgCode(tenant.getTenantCode());
            orgAddParam.setOrgName(tenant.getTenantName());
            orgAddParam.setSortCode(99);
            orgAddParam.setStatus(CommonEnableStatusEnum.ENABLE.getValue());
            orgAddParam.setDirectorUserId(tenant.getTenantAdminId());
            sysOrgService.add(orgAddParam);
        });
    }

    /**
     * 创建租户默认用户
     * @param tenant
     * @param accountDTO

     * @return
     */
    private Long syncAdminUser(SysTenant tenant,SysAccountDTO accountDTO,Long roleId){
        //没有用户则创建
        SysUser userNew =sysUserService.createUserWithAccount(accountDTO.getUsername(),accountDTO.getNickname(),accountDTO.getPhone(),accountDTO.getPassword(), roleId,true);
        sysUserService.updateTenantId(userNew.getId(), tenant.getId());
        UpdateChain.of(SysTenant.class).set(SysTenant::getTenantAdminId,userNew.getId())
                .eq(SysTenant::getId,tenant.getId()).update();
        return ConvertUtils.toLong(userNew.getId());
    }

    /**
     * 创建用户默认资源并返回新角色信息
     * @param tenant
     * @return
     */
    private Long  createTenantMenuRole(SysTenant tenant)
    {
        //1.得到套餐所有资源

        //2.得到套餐所有解耦权限

        //3.创建一个角色
        return TenantUtils.execute(tenant.getId(),()->{
            SysRoleAddParam defaultRole = new SysRoleAddParam();
            defaultRole.setCategory("");
            defaultRole.setName(LanguageUtils.getMessage("sys.tenant.default.roleName"));
            defaultRole.setCode(SysRoleCodeEnum.SUPER_ADMIN.getValue());
            defaultRole.setSortCode(0);
            defaultRole.setWeight(99);
            return ConvertUtils.toLong(roleService.add(defaultRole));
        });
    }

    @Transaction
    @Override
    public void edit(SysTenantEditParam sysTenantEditParam) {
        SysTenant sysTenant = this.queryEntity(sysTenantEditParam.getId());

        sysTenant = BeanUtil.toBean(sysTenantEditParam, SysTenant.class);

        sysTenant.setTenantCode(null);
        sysTenantMapper.update(sysTenant);
        //创建租户对应数据表
        thisService.clearCache(sysTenant.getId(),sysTenant.getTenantCode());
    }

    @Transaction
    @Override
    public void delete(List<SysTenantIdParam> sysTenantIdParamList) {
        throw new CommonException("Do not support deletion operation");
        // 执行删除
        //thisService.clearCache
        //sysTenantMapper.deleteBatchByIds(CollStreamUtil.toList(sysTenantIdParamList, SysTenantIdParam::getTenantId));
    }

    @Override
    public SysTenant detail(SysTenantIdParam sysTenantIdParam) {
        return this.queryEntity(sysTenantIdParam.getId());
    }

    @Override
    public SysTenantViewResult viewStat(SysTenantIdParam sysTenantIdParam) {
        SysTenant tenant = this.queryEntity(sysTenantIdParam.getId());
        SysTenantViewResult result = BeanUtil.toBean(tenant,SysTenantViewResult.class);
        result.setAccountCount(sysUserService.getCountByTenant(tenant.getId()));
        return result;
    }

    @Override
    public SysTenant queryEntity(Long id) {
        SysTenant sysTenant = sysTenantMapper.selectOneById(id);
        if(ObjectUtil.isEmpty(sysTenant)) {
            throw new CommonException("客户机构不存在，id值为：{}", id);
        }
        return sysTenant;
    }

    @Override
    @Cache(key = "cache:tenant:${tenantId}:pojo",tags = "cache:tenant:${tenantId}")
    public SysTenantDTO getDTO(Long tenantId){
        if(!NumberUtils.isGtZero(tenantId)) return null;
        return SysTenantConvert.INSTANCE.convertDTO(sysTenantMapper.selectOneById(tenantId));
    }

    @Override
    @Cache(key = "cache:tenant:${tenantCode}:pojo",tags = "cache:tenant:${tenantCode}")
    public SysTenantDTO getDTObyCode(String tenantCode) {
        if(StrUtil.isBlank(tenantCode)) return null;
        return SysTenantConvert.INSTANCE.convertDTO(sysTenantMapper.selectOne(SysTenant::getTenantCode,tenantCode));
    }

    @Override
    @Cache(key = "cache:tenant:${tenantId}:name",tags = "cache:tenant:${tenantId}")
    public String getNameByCache(Long tenantId) {
        SysTenant sysTenant = sysTenantMapper.selectOneById(tenantId);
        return sysTenant==null?"":sysTenant.getTenantName();
    }

    @CacheRemove(tags = "cache:tenant:${tenantId},cache:tenant:${tenantCode}")
    public void clearCache(Long tenantId,String tenantCode){
    }


    @Override
    public List<SysTenantPageResult> getSelector(SysTenantPageParam pageParam) {
        QueryWrapper query = getQueryWrapper(pageParam);
        return SysTenantConvert.INSTANCE.convertList(sysTenantMapper.selectListByQuery(query));
    }

    @Override
    public List<SysTenantPageResult> getSelectorAll(SysTenantPageParam pageParam) {
        QueryWrapper query = getQueryWrapper(pageParam,true);
        return SysTenantConvert.INSTANCE.convertList(sysTenantMapper.selectListByQuery(query));
    }


    @Override
    public boolean isTenantAdmin(Long tenantId,Long userId) {
        //可以考虑，没传入id的话，则以系统ID作为验证，由于租户管理员的唯一性，除了管理员，其他都不是
        if(NumberUtils.isLeZero(tenantId)){
            return false;
        }
        //获取住户信息
        SysTenantDTO tenant = getDTO(tenantId);
        if(tenant==null) return false;
        return tenant.getTenantAdminId().compareTo(userId)==0;

    }

    @Override
    public List<String> getTenantPermissionCodeList(Long tenantId) {
        return  sysMenuService.getMenuCodeList(getTenantPermissionMenuIdList(tenantId));
    }

    @Override
    public List<Long> getTenantPermissionMenuIdList(Long tenantId) {
        SysTenantDTO tenant = queryTenantById(tenantId);
        return  sysTenantPackageApi.getMenuIds(tenant.getPackageId());
    }

    private SysTenantDTO queryTenantById(Long tenantId){
        if(tenantId==null){
            throw new CommonExceptionI18n("sys.tenant.notExists");
        }
        SysTenantDTO tenant = getDTO(tenantId);
        CommonExceptionI18n.notNull(tenant,"sys.tenant.notExistsOrDisable");
        //不是系统租户，并且租户无套餐，则返回错误
        if(!tenantId.equals(TenantConstants.TENANT_SYSTEM_ID) && NumberUtils.isLeZero(tenant.getPackageId())){
            throw new CommonExceptionI18n("sys.tenant.notPackage");
        }
        return tenant;
    }

    @Override
    @TenantIgnore
    @DataScopeIgnore
    public List<SysTenantDTO> getTenantListByUserAccount(String account) {
        if(account==null){
            CommonException.throwException("必须传入account");
        }
        //得到所有账号
        List<SysUserDTO> userList =  sysUserService.selectListByUsernameIgnoreTenant(account);
        if(CollectionUtils.isEmpty(userList)){
            return new ArrayList<>();
        }
        //得到所有租户
        List<Long> tenantIds = userList.stream().map(SysUserDTO::getTenantId).collect(Collectors.toList());
        List<SysTenant> tenantList = sysTenantMapper.selectListByIds(tenantIds);
        if(CollectionUtils.isEmpty(tenantList)){
            return new ArrayList<>();
        }
        LocalDateTime currentTime = LocalDateTimeUtils.now();
        //转为按id顺序排序
        tenantList =  tenantList.stream()
                .sorted((o1, o2) -> {
                    // 第一排序条件：是否到期
                    boolean expired1 = o1==null || o1.getExpireTime().isAfter(currentTime);
                    boolean expired2 = o2==null || o2.getExpireTime().isAfter(currentTime);

                    if (expired1 != expired2) {
                        return Boolean.compare(expired1, expired2);
                    }

                    // 第二排序条件：tenantIds中的索引
                    int index1 = tenantIds.indexOf(o1.getId());
                    int index2 = tenantIds.indexOf(o2.getId());
                    index1 = index1 == -1 ? Integer.MAX_VALUE : index1;
                    index2 = index2 == -1 ? Integer.MAX_VALUE : index2;

                    return Integer.compare(index1, index2);
                })
                .collect(Collectors.toList());

        return SysTenantConvert.INSTANCE.convertListDTO(tenantList);
    }

    private QueryWrapper getQueryWrapper(SysTenantPageParam sysTenantPageParam){
        return getQueryWrapper(sysTenantPageParam,false);
    }
    private QueryWrapper getQueryWrapper(SysTenantPageParam sysTenantPageParam,boolean selectSysTenant){
        QueryWrapper queryWrapper = new QueryWrapper();
        if(ObjectUtil.isNotEmpty(sysTenantPageParam.getTenantName())) {
            queryWrapper.like(SysTenant::getTenantName, sysTenantPageParam.getTenantName());
        }
        if(ObjectUtil.isNotEmpty(sysTenantPageParam.getContactName())) {
            queryWrapper.like(SysTenant::getContactName, sysTenantPageParam.getContactName());
        }
        if(ObjectUtil.isNotEmpty(sysTenantPageParam.getSysName())) {
            queryWrapper.like(SysTenant::getSysName, sysTenantPageParam.getSysName());
        }
        if(ObjectUtil.isNotEmpty(sysTenantPageParam.getStartExpireTime()) && ObjectUtil.isNotEmpty(sysTenantPageParam.getEndExpireTime())) {
            queryWrapper.between(SysTenant::getExpireTime, sysTenantPageParam.getStartExpireTime(), sysTenantPageParam.getEndExpireTime());
        }
        if(ObjectUtil.isNotEmpty(sysTenantPageParam.getEnableStatus())) {
            queryWrapper.eq(SysTenant::getEnableStatus, sysTenantPageParam.getEnableStatus());
        }

        if(CollectionUtils.isEmpty(sysTenantPageParam.getSortingFields())){
            queryWrapper.orderBy(SysTenant::getId,false);
        }
        if(ObjectUtil.isNotNull(sysTenantPageParam.getOrgId())){
            queryWrapper.eq(SysTenant::getOrgId,sysTenantPageParam.getOrgId());
        }
        if(!selectSysTenant) {
            //不查询系统租户
            queryWrapper.ne(SysTenant::getId, TenantConstants.TENANT_SYSTEM_ID);
        }


        return queryWrapper;
    }


    @Override
    public int countByPackageId(Long packageId) {
        return sysTenantMapper.selectCount(SysTenant::getPackageId,packageId).intValue();
    }
}
