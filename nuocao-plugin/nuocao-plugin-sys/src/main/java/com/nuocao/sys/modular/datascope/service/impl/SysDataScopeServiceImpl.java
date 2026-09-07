package com.nuocao.sys.modular.datascope.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.utils.ConvertUtils;
import com.nuocao.common.utils.LockUtils;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.data.access.datascope.annotation.DataScopeIgnore;
import com.nuocao.data.access.tenant.annotation.TenantIgnore;
import com.nuocao.sys.api.dto.DataScopeEditDTO;
import com.nuocao.sys.api.dto.OrgDataScopeDTO;
import com.nuocao.sys.api.enums.DataScopeCategoryEnum;
import com.nuocao.sys.api.enums.DataScopeObjectTypeEnum;
import com.nuocao.sys.api.enums.DataScopeTypeEnum;
import com.nuocao.sys.api.utils.SysTenantApiUtils;
import com.nuocao.sys.modular.datascope.convert.SysDataScopeConvert;
import com.nuocao.sys.modular.datascope.entity.SysDataScope;
import com.nuocao.sys.modular.datascope.mapper.SysDataScopeMapper;
import com.nuocao.sys.modular.datascope.result.SysDataScopeViewResult;
import com.nuocao.sys.modular.datascope.service.SysDataScopeService;
import com.nuocao.sys.modular.org.service.SysOrgService;
import com.nuocao.sys.modular.user.entity.SysUser;
import com.nuocao.sys.modular.user.service.SysUserService;
import org.noear.solon.annotation.Component;
import org.noear.solon.data.annotation.Transaction;
import org.noear.solon.annotation.Inject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据权限Service接口实现类
 **/
@Component
public class SysDataScopeServiceImpl implements SysDataScopeService {


    //更新数据权限锁key，避免重复增加相同权限
    private static final String UPDATE_LOCK_KEY="sys:datascope:lock:{}";


    @Inject
    private SysDataScopeMapper sysDataScopeMapper;

    @Inject
    private SysUserService sysUserService;

    @Inject
    private SysOrgService sysOrgService;

    @Inject
    private LockUtils lockUtils;


    @Transaction
    @Override
    public void updateDataScope(DataScopeEditDTO data) {
        lockUtils.lock(UPDATE_LOCK_KEY+data.getObjectId(),()->{
           //查询数据，
           SysDataScope dataScope = get(data.getCategory(),data.getObjectType(),data.getObjectId());
           //转换信息
            SysDataScope upData = SysDataScopeConvert.INSTANCE.convert(data);
           if(dataScope!=null){
                upData.setId(dataScope.getId());
           }
           if(data.getIds()==null){
               data.setIds(new HashSet<>());
           }
           if(!data.getIds().isEmpty() && !DataScopeTypeEnum.CUSTOM.getValue().equals(data.getScopeType())){
               data.setIds(new HashSet<>());
           }
           //更新
            if(upData.getId()!=null){
                sysDataScopeMapper.updateById(upData);
            }else{
                sysDataScopeMapper.save(upData);
            }
        });
    }



    @Override
    public SysDataScopeViewResult getViewByObjectId(String category, String objectType, Long objectId) {
        return SysDataScopeConvert.INSTANCE.convertView(get(category,objectType,objectId));
    }


    @Override
    public SysDataScope get(String category, String objectType, Long objectId) {
        QueryWrapper query = new QueryWrapper();
        query.eq(SysDataScope::getObjectType,objectType)
                .eq(SysDataScope::getCategory,category)
                .eq(SysDataScope::getObjectId,objectId);
        return sysDataScopeMapper.selectOneByQuery(query);
    }

    @Override
    public DataScopeEditDTO getEditDto(String category, String objectType, Long objectId) {
        SysDataScope data  = get(category,objectType,objectId);
        if(data==null){
            return null;
        }
        return SysDataScopeConvert.INSTANCE.convert(data);
    }


    @Override
    @DataScopeIgnore
    @TenantIgnore
    public OrgDataScopeDTO getOrgDataScopeByUserId(Long userId) {
        SysUser user =sysUserService.getById(userId);
        CommonExceptionI18n.notNull(user,"sys.user.notExists",userId);
        //1.得到用户所有角色
        List<Long> roleIdList = sysUserService.getUserRoleIdListByCache(userId);
        //2.创建数据权限返回实体
        OrgDataScopeDTO result = new OrgDataScopeDTO();
        result.setSelf(true); //默认都有自己的数据
        //2.1如果该用户为租户管理员，默认有全部权限
        if(SysTenantApiUtils.isTenantAdmin(user.getTenantId(),user.getId())){
            result.setAll(true);
            return result;
        }
        List<Long> orgIds = new ArrayList<>();
        //3.得到所有角色的权限(角色和个人权限都）
        QueryWrapper query= QueryWrapper.create()
                .eq(SysDataScope::getCategory, DataScopeCategoryEnum.ORG.getType());
        if(roleIdList.isEmpty()){
             query.and(
                    o -> {
                        o.eq(SysDataScope::getObjectType, DataScopeObjectTypeEnum.USER.getType())
                                .eq(SysDataScope::getObjectId, userId);
                    }
                );
        }else {
             query.and(
                    o -> {
                        o.and(b -> {
                                            b.eq(SysDataScope::getObjectType, DataScopeObjectTypeEnum.ROLE.getType())
                                                    .in(SysDataScope::getObjectId, roleIdList);
                                        }
                                )
                                .or(a -> {
                                    a.eq(SysDataScope::getObjectType, DataScopeObjectTypeEnum.USER.getType())
                                            .eq(SysDataScope::getObjectId, userId);
                                })


                        ;
                    }
                );
        }
        List<SysDataScope> list = sysDataScopeMapper.selectListByQuery(query);
        boolean isAddSelfOrgId = false; //是否添加了自己所在部门去啊年
        boolean isAddChildOrgId = false; //是否添加了自己所在部门机器下属部门权限
        //4.循环处理并填充orgIds
        for (SysDataScope scope:
             list) {
            //4.1 拥有全部权限，则不需要处理了
            if(scope.getScopeType().equals(DataScopeTypeEnum.ALL.getValue())){
                result.setAll(true);
                break;
            }
            //4.2 是自定义权限，则返回所有被选中的权限
            if(scope.getScopeType().equals(DataScopeTypeEnum.CUSTOM.getValue()))
            {
                orgIds.addAll(scope.getIds());
            }

            if(user.getOrgId()!=null){
                //4.3 只拥有所在部门的权限
                if(!isAddSelfOrgId && scope.getScopeType().equals(DataScopeTypeEnum.ONLY.getValue())){
                    isAddSelfOrgId=true;
                    orgIds.add(ConvertUtils.toLong(user.getOrgId(),0L));
                }
                //4.4 自己部门和下级部门权限
                if(!isAddChildOrgId && scope.getScopeType().equals(DataScopeTypeEnum.CHILD.getValue())){
                    isAddChildOrgId=true;
                    isAddSelfOrgId=true;
                    orgIds.add(ConvertUtils.toLong(user.getOrgId(),0L));
                    //获取所有下级角色部门ID结合
                    List<Long> orgIdList = sysOrgService.getOrgChildIds(user.getOrgId());
                    if(CollectionUtils.isNotEmpty(orgIdList)){
                        orgIds.addAll(orgIdList.stream().map(o-> ConvertUtils.toLong(o,0L)).collect(Collectors.toList()));
                    }
                }
            }
        }
        //5. 如果没有全部权限，且所能管理的部门不为空，则填入
        if(!result.getAll() && !orgIds.isEmpty()){
            Set<Long> orgIds2=orgIds.stream().distinct()
                    .collect(Collectors.toSet());
            result.setOrgIds(orgIds2);
        }

        return result;
    }



    //################ 以下为私有个工具方法


    /**
    * 验证信息
    */
    private void validInfo(SysDataScope sysDataScope){
        //填写验证方法，有问题抛出异常

    }




}
