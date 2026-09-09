package com.nuocao.sys.modular.permission.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.nuocao.common.cache.CacheUtils;
import com.nuocao.common.event.NuoCaoEventHelper;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.utils.LockUtils;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.data.access.tenant.constants.TenantConstants;
import com.nuocao.sys.api.SysTenantApi;
import com.nuocao.sys.api.dto.UserPermissionEditDTO;
import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;
import com.nuocao.sys.api.enums.DataScopeObjectTypeEnum;
import com.nuocao.sys.api.utils.SysTenantApiUtils;
import com.nuocao.sys.core.event.RolePermissionUpdateEvent;
import com.nuocao.sys.core.event.UserPermissionUpdateEvent;
import com.nuocao.sys.modular.menu.service.SysMenuService;
import com.nuocao.sys.modular.permission.convert.SysUserPermissionConvert;
import com.nuocao.sys.modular.permission.entity.SysUserPermission;
import com.nuocao.sys.modular.permission.mapper.SysUserPermissionMapper;
import com.nuocao.sys.modular.permission.result.SysUserPermissionViewResult;
import com.nuocao.sys.modular.permission.service.SysUserPermissionService;
import com.nuocao.sys.modular.user.entity.SysUser;
import com.nuocao.sys.modular.user.service.SysUserService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.*;

/**
 * 数据权限Service接口实现类
 **/
@Component
public class SysUserPermissionServiceImpl implements SysUserPermissionService {


    //更新功能权限锁key，避免重复增加相同权限
    private static final String UPDATE_LOCK_KEY="sys:user-permission:lock:{}";

    //更新功能权限锁key，避免重复增加相同权限
    private static final String USER_PERMISSION_CACHE_KEY="sys:user-permission:cache:{}";


    @Inject
    private SysUserPermissionMapper sysUserPermissionMapper;

    @Inject
    private SysUserService sysUserService;

    @Inject
    private SysMenuService sysMenuService;
    @Inject
    private SysTenantApi tenantApi;
    @Inject
    private LockUtils lockUtils;
    @Inject
    private CacheUtils cacheUtils;

    @Override
    public void updatePermission(UserPermissionEditDTO data) {
        lockUtils.lock(UPDATE_LOCK_KEY+data.getObjectId(),()->{
            //查询数据，
            SysUserPermission dataScope = get(data.getObjectType(),data.getObjectId());
            //转换信息
            SysUserPermission upData = SysUserPermissionConvert.INSTANCE.convert(data);
            if(dataScope!=null){
                upData.setId(dataScope.getId());
            }
            if(data.getMenuIds()==null){
                data.setMenuIds(new HashSet<>());
            }

            //更新
            if(upData.getId()!=null){
                sysUserPermissionMapper.updateById(upData);
            }else{
                sysUserPermissionMapper.insert(upData);
            }

        });
        //更新的是用户权限
        if(data.getObjectType().equals(DataScopeObjectTypeEnum.USER.getType())){
            NuoCaoEventHelper.send(new UserPermissionUpdateEvent(data.getObjectId()));
        }else if(data.getObjectType().equals(DataScopeObjectTypeEnum.ROLE.getType())){
            NuoCaoEventHelper.send(new RolePermissionUpdateEvent(data.getObjectId()));
        }
    }

    @Override
    public SysUserPermissionViewResult getViewByObjectId(String objectType, Long objectId) {
        return SysUserPermissionConvert.INSTANCE.convertView(get(objectType,objectId));
    }

    @Override
    public SysUserPermission get(String objectType, Long objectId) {
        QueryWrapper query = new QueryWrapper();
        query.eq(SysUserPermission::getObjectType,objectType)
                .eq(SysUserPermission::getObjectId,objectId);
        return sysUserPermissionMapper.selectOneByQuery(query);
    }

    @Override
    public UserPermissionEditDTO getEditDto(String objectType, Long objectId) {
        SysUserPermission data  = get(objectType,objectId);
        if(data==null){
            return null;
        }
        return SysUserPermissionConvert.INSTANCE.convert(data);
    }

    @Override
    public List<Long> getUserHaveMenuIdList(String objectType, Long objectId) {
        QueryWrapper query = new QueryWrapper();
        query.eq(SysUserPermission::getObjectType,objectType)
                .eq(SysUserPermission::getObjectId,objectId);
        SysUserPermission userPermission = sysUserPermissionMapper.selectOneByQuery(query);
        return userPermission==null?new ArrayList<>(): new ArrayList<>(userPermission.getMenuIds());
    }

    @Override
    public List<String> getUserHaveMenuCodeList(Long userId) {
        //1.获取用户实体
        SysUser user =sysUserService.getById(userId);
        CommonExceptionI18n.notNull(user,"sys.user.notExists",userId);
        //2.1如果该用户为租户管理员，得到该租户所有权限
        if(SysTenantApiUtils.isTenantAdmin(user.getTenantId(),user.getId())){
            return TenantConstants.TENANT_SYSTEM_ID.equals(user.getTenantId())?
                    sysMenuService.getAllMenuCodeList() //是系统租户则直接拿到全部权限
                    :
                    tenantApi.getTenantPremissionCodeList(user.getTenantId());
        }
        List<Long> menuIds =  getUserHaveMenuIdList(userId);

        return sysMenuService.getMenuCodeList(menuIds);
    }

    public List<String> getUserHaveMenuCodeListByCache(Long userId){
        return cacheUtils.getOrSetList(StringUtils.format(USER_PERMISSION_CACHE_KEY,userId),
                String.class,
                ()->{
                    return getUserHaveMenuCodeList(userId);
                },
                60*5 //缓存五分钟
        );
    }

    @Override
    public void clearUserHaveMenuCodeCache(Long userId) {
        cacheUtils.remove(StringUtils.format(USER_PERMISSION_CACHE_KEY,userId));
    }

    @Override
    public List<Long> getUserHaveMenuIdList(Long userId) {
        //1.获取用户实体
        SysUser user =sysUserService.getById(userId);
        CommonExceptionI18n.notNull(user,"sys.user.notExists",userId);
        //2.1如果该用户为租户管理员，得到该租户所有权限
        if(SysTenantApiUtils.isTenantAdmin(user.getTenantId(),user.getId())){
            return TenantConstants.TENANT_SYSTEM_ID.equals(user.getTenantId())?
                    sysMenuService.getAllMenuIdList() //是系统租户则直接拿到全部权限
                    :
                    tenantApi.getTenantPremissionMenuIdList(user.getTenantId());
        }
        List<SysUserPermission> list = getUserPermissionListByUser(user);
        List<Long> result = new ArrayList<>();
        //4.循环处理并填充orgIds
        for (SysUserPermission scope:
                list) {
            if(CollectionUtils.isNotEmpty(scope.getMenuIds())){
                result.addAll(scope.getMenuIds());
            }
        }


        return result;
    }

    @Override
    public List<SysMenuTreeResultDTO> getUserHaveMenuTree(Long userId) {
        //得到所有用户可用的菜单ID
        List<Long> menuIds = getUserHaveMenuIdList(userId);
        //不存在菜单则返回空，可以考虑给用户菜单权限增加一个缓存包括。
        if(CollectionUtils.isEmpty(menuIds)){
            return new ArrayList<>();
        }
        //得到菜单树
        return sysMenuService.treeByIds(menuIds);
    }

    /**
     * 获取用户权限查询query
     * @param user
     * @return
     */
    private List<SysUserPermission> getUserPermissionListByUser(SysUser user){
        if(user==null) return new ArrayList<>();
        //1.得到用户所有角色
        List<Long> roleIdList = sysUserService.getUserRoleIdListByCache(user.getId());
        //2.创建数据权限返回实体
        List<String> result=new ArrayList<>();


        //3.得到所有角色的权限(角色和个人权限都）
        QueryWrapper query= new QueryWrapper();
        if(roleIdList.isEmpty()){
            query.and(
                    o -> {
                        o.eq(SysUserPermission::getObjectType, DataScopeObjectTypeEnum.USER.getType())
                                .eq(SysUserPermission::getObjectId, user.getId());
                    }
            );
        }else {
            query.and(
                    o -> {
                        o.and(b -> {
                                            b.eq(SysUserPermission::getObjectType, DataScopeObjectTypeEnum.ROLE.getType())
                                                    .in(SysUserPermission::getObjectId, roleIdList);
                                        }
                                )
                                .or(a -> {
                                    a.eq(SysUserPermission::getObjectType, DataScopeObjectTypeEnum.USER.getType())
                                            .eq(SysUserPermission::getObjectId, user.getId());
                                })


                        ;
                    }
            );
        }
        return sysUserPermissionMapper.selectListByQuery(query);
    }


}
