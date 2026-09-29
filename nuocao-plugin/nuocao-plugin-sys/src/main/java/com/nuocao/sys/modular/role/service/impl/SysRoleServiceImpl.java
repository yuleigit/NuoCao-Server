package com.nuocao.sys.modular.role.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.mybatisflex.core.query.QueryWrapper;
import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.auth.api.dto.SaUserLoginInfoDTO;
import com.nuocao.common.ObjectUtils;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.mapper.LambdaQueryWrapperX;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.utils.NumberUtils;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.data.access.tenant.util.TenantUtils;
import com.nuocao.sys.api.SysDataScopeApi;
import com.nuocao.sys.api.SysUserPermissionApi;
import com.nuocao.sys.api.dto.DataScopeEditDTO;
import com.nuocao.sys.api.dto.SysRoleDTO;
import com.nuocao.sys.api.dto.UserPermissionEditDTO;
import com.nuocao.sys.api.enums.DataScopeCategoryEnum;
import com.nuocao.sys.api.enums.DataScopeObjectTypeEnum;
import com.nuocao.sys.api.utils.SysTenantApiUtils;
import com.nuocao.sys.core.enums.SysRoleCodeEnum;
import com.nuocao.sys.modular.org.service.SysOrgService;
import com.nuocao.sys.modular.role.convert.SysRoleConvert;
import com.nuocao.sys.modular.role.entity.SysRole;
import com.nuocao.sys.modular.role.mapper.SysRoleMapper;
import com.nuocao.sys.modular.role.pojo.param.*;
import com.nuocao.sys.modular.role.pojo.result.SysRolePermissionResult;
import com.nuocao.sys.modular.role.service.SysRoleService;
import com.nuocao.sys.modular.tenant.service.SysTenantService;
import com.nuocao.sys.modular.user.entity.SysUserIdentity;
import com.nuocao.sys.modular.user.service.SysUserService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色Service接口实现类
 **/
@Component
public class SysRoleServiceImpl implements SysRoleService {

    @Inject
    private SysRoleMapper mapper;

    @Inject
    private SysUserPermissionApi sysUserPermissionApi;


    @Inject
    private SysOrgService sysOrgService;


    @Inject
    private SysUserService sysUserService;

    @Inject
    private SysTenantService tenantService;

    @Inject
    private SysDataScopeApi sysDataScopeApi;

    // * 是否启用多租户
    @Inject(value = "${tenant.enable}",required = false)
    private Boolean tenantEnable=false;

    @Override
    public PageResult<SysRole> page(SysRolePageParam sysRolePageParam) {
        LambdaQueryWrapperX<SysRole> queryWrapper = new LambdaQueryWrapperX<>();
        // 查询部分字段
        queryWrapper.select(SysRole::getId, SysRole::getOrgId, SysRole::getName,
                SysRole::getCategory, SysRole::getSortCode,SysRole::getCode,SysRole::getWeight);

        queryWrapper.eqIfPresent(SysRole::getOrgId,sysRolePageParam.getId());
        queryWrapper.eqIfPresent(SysRole::getCategory,sysRolePageParam.getCategory());
        queryWrapper.likeIfPresent(SysRole::getName,sysRolePageParam.getSearchKey());
        queryWrapper.eqIfPresent(SysRole::getOrgId,sysRolePageParam.getId());

        if(CollectionUtils.isEmpty(sysRolePageParam.getSortingFields()))
        {
            queryWrapper.orderBy(SysRole::getSortCode,true).orderBy(SysRole::getId,false);
        }

        return mapper.selectPage(sysRolePageParam,queryWrapper);
    }

    @Override
    public String add(SysRoleAddParam sysRoleAddParam) {
        SysRole role  = SysRoleConvert.INSTANCE.convert(sysRoleAddParam);
        validInfo(role);
        mapper.insert(role);
        return null;
    }

    @Override
    public void edit(SysRoleEditParam sysRoleEditParam) {
        SysRole role  = SysRoleConvert.INSTANCE.convert(sysRoleEditParam);
        validInfo(role);
        mapper.updateById(role);
    }

    @Override
    public void delete(List<SysRoleIdParam> sysRoleIdParamList) {
        if(CollectionUtils.isEmpty(sysRoleIdParamList)) return;
        deleteByIds(sysRoleIdParamList.stream().map(SysRoleIdParam::getId).collect(Collectors.toList()));
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return;
        }
        ids=ids.stream().filter(NumberUtils::isGtZero).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(ids)){
            return;
        }
        //得到所有角色
        List<SysRole> roleList = mapper.selectListByIds(ids);

        SysRole systemRole = roleList.stream().filter(o->o.getCode().equals(SysRoleCodeEnum.SUPER_ADMIN.getValue())).findFirst().orElse(null);


        if (ObjectUtils.isNotNull(systemRole)) {
            throw new CommonExceptionI18n("sys.role.builtIn.disableEditCode",systemRole.getName());
        }

        //验证是否有改角色的用户，如果有则禁止删除，需要先移除掉用户
        for (SysRole role:
             roleList) {
            int count = sysUserService.getCountByRoleId(role.getId());
            if(count>0){
                throw new CommonExceptionI18n("sys.role.existsUser.disableDelete",systemRole.getName());
            }
        }

        // 执行删除
        mapper.deleteBatchByIds(roleList.stream().map(SysRole::getId).collect(Collectors.toList()));
    }

    @Override
    public SysRole detail(SysRoleIdParam sysRoleIdParam) {
        return queryEntity(sysRoleIdParam.getId());
    }

    @Override
    public SysRole queryEntity(Long roleId) {
        SysRole role = mapper.selectOneById(roleId);
        CommonExceptionI18n.notNull(role,"sys.role.notExists",roleId);
        return role;
    }

    @Override
    public SysRolePermissionResult getPermission(SysRoleIdParam sysRoleIdParam){
        SysRole role = queryEntity(sysRoleIdParam.getId());
        return SysRoleConvert.INSTANCE.convertPermissionResult(role,sysUserPermissionApi.getPermissionMenuIdList(DataScopeObjectTypeEnum.ROLE,role.getId()));
    }

    @Override
    public void setPermission(SysRoleSetPermissionParam permissionEdit) {
        SysRole role = queryEntity(permissionEdit.getId());
        UserPermissionEditDTO editDTO = new UserPermissionEditDTO();
        editDTO.setMenuIds(permissionEdit.getMenuIds());
        editDTO.setObjectId(permissionEdit.getId());
        editDTO.setObjectType(DataScopeObjectTypeEnum.ROLE.getType());
        sysUserPermissionApi.updatePermission(editDTO);
    }

    @Override
    public DataScopeEditDTO getDataSpace(Long roleId) {
        return sysDataScopeApi.getDataScope(DataScopeCategoryEnum.ORG, DataScopeObjectTypeEnum.ROLE,roleId);
    }

    @Override
    public void setDataScope(SysRoleSetDataScopeParam param) {
        SysRole role = queryEntity(param.getId());
        DataScopeEditDTO data = new DataScopeEditDTO();
        data.setObjectId(param.getId());
        data.setCategory(DataScopeCategoryEnum.ORG.getType());
        data.setObjectType(DataScopeObjectTypeEnum.ROLE.getType());
        data.setIds(param.getIds());
        data.setScopeType(param.getScopeType());
        sysDataScopeApi.updateDataScope(data);
    }

    @Override
    public List<String> getNames(List<Long> roleIds) {
        if(roleIds==null || roleIds.isEmpty()) return  new ArrayList<>();
        QueryWrapper query = new QueryWrapper();
        query.in(SysRole::getId,roleIds)
                .select(SysRole::getName);
        return mapper.selectObjectListByQueryAs(query,String.class);
    }

    @Override
    public List<SysRoleDTO> getRoleListByUserId(Long userId) {
        QueryWrapper query = new QueryWrapper();
        query.select(SysRole::getId,SysRole::getName,SysRole::getCode,SysRole::getWeight)
                .rightJoin(SysUserIdentity.class).on(SysUserIdentity::getObjectId,SysRole::getId)
                .eq(SysUserIdentity::getUserId,userId);
        List<SysRoleDTO> roleList= mapper.selectListByQueryAs(query,SysRoleDTO.class);
        return roleList==null?new ArrayList<>():roleList;
    }

    @Override
    public List<SysRoleDTO> getLoginUserChildrenRoleList() {
        SaUserLoginInfoDTO loginUser = StpLoginUserUtils.getLoginUser();
        if(loginUser==null || NumberUtils.isLeZero(loginUser.getId())){
            throw new CommonExceptionI18n("common.not.login");
        }
        List<SysRoleDTO> roleList;
        if(StpLoginUserUtils.hasPermission("sys:role:query") || SysTenantApiUtils.isTenantAdmin(loginUser.getTenantId(),loginUser.getId()))
        {
            roleList= mapper.selectListByQueryAs(new LambdaQueryWrapperX<SysRole>(),SysRoleDTO.class);
        }else {
            List<SysRoleDTO> userRoleList=getRoleListByUserId(loginUser.getId());
            if(CollectionUtils.isEmpty((userRoleList))) return new ArrayList<>();
            int roleWeight = userRoleList.stream().mapToInt(SysRoleDTO::getWeight).max().orElse(0);
            LambdaQueryWrapperX<SysRole> query = new LambdaQueryWrapperX<SysRole>().ltX(SysRole::getWeight,roleWeight);
            roleList= mapper.selectListByQueryAs(query,SysRoleDTO.class);
        }

        return roleList==null?new ArrayList<>():roleList;
    }

    private void validInfo(SysRole info){
        //验证是否重复
        SysRole temp = mapper.selectOne(SysRole::getName,info.getName());
        if(temp!=null && !temp.getId().equals(info.getId())){
            throw new CommonExceptionI18n("sys.role.name.exists",info.getName());
        }
        temp = mapper.selectOne(SysRole::getCode,info.getCode());
        if(temp!=null && !temp.getId().equals(info.getId())){
            throw new CommonExceptionI18n("sys.role.code.exists",info.getCode());
        }
        if(NumberUtils.isGtZero(info.getOrgId())){
            //验证组织是否存在
            if(StringUtils.isBlank(sysOrgService.getNameByCache(info.getOrgId()))){
                throw new CommonExceptionI18n("sys.org.notExists",info.getOrgId());
            }
        }else {
            info.setId(info.getId());
        }



        //如果是修改，则不允许修改内置角色的code
        if(NumberUtils.isGtZero(info.getId())){
            SysRole oldRole = mapper.selectOneById(info.getId());
            CommonExceptionI18n.notNull(oldRole,"sys.role.notExists");
            if(info.getCode().equals(SysRoleCodeEnum.SUPER_ADMIN.getValue()) && !info.getCode().equals(oldRole.getCode())){
                throw new CommonExceptionI18n("sys.role.builtIn.disableEditCode",info.getCode());
            }
        }else{
            if(info.getOrgId()==null){
                info.setOrgId(0L);
            }
        }


        if(StringUtils.isBlank(info.getCategory())){
            info.setCategory("default");
        }

        if(info.getSortCode()==null){
            info.setSortCode(0);
        }

    }



}
