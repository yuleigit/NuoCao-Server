package com.nuocao.sys.modular.user.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.nuocao.common.mapper.LambdaQueryWrapperX;
import com.nuocao.common.pojo.KeyValue;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.sys.api.enums.SysUserIdentityTypeEnum;
import com.nuocao.sys.modular.role.entity.SysRole;
import com.nuocao.sys.modular.user.entity.SysUserIdentity;
import com.nuocao.sys.modular.user.mapper.SysUserIdentityMapper;
import com.nuocao.sys.modular.user.pojo.result.SysUserIdentityRoleResult;
import com.nuocao.sys.modular.user.service.SysUserIdentityService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户身份管理Service 实现
 */
@Component
public class SysUserIdentityServiceImpl implements SysUserIdentityService {


    @Inject
    private SysUserIdentityMapper mapper;


    @Override
    public int selectUserCountByRoleId(Long roleId) {
        return selectUserCount(SysUserIdentityTypeEnum.ROLE,roleId);
   }

    @Override
    public List<Long> selectUserIdListByRoleId(Long roleId) {
        return mapper.selectObjectListByQueryAs(new LambdaQueryWrapperX<SysUserIdentity>().eqX(SysUserIdentity::getObjectId,roleId).selectX(SysUserIdentity::getUserId),Long.class);
    }

    @Override
    public int selectUserCountByPositionId(Long roleId) {
        return selectUserCount(SysUserIdentityTypeEnum.POSITION,roleId);
    }

    @Override
    public int selectUserCount(SysUserIdentityTypeEnum type, Long objectId) {
        return mapper.selectCount(SysUserIdentity::getObjectId,objectId,SysUserIdentity::getType, type.getValue()).intValue();
    }

    @Override
    public List<Long> selectObjectIdListByUserId(SysUserIdentityTypeEnum type, Long userId) {
        LambdaQueryWrapperX<SysUserIdentity> queryWrapper = buildQueryByUserId(type,userId)
                .selectX(SysUserIdentity::getObjectId);
        return mapper.selectObjectListByQueryAs(queryWrapper,Long.class);
    }

    @Override
    public List<SysUserIdentity> selectListByUserIds(SysUserIdentityTypeEnum type, List<Long> userIds) {
        if(CollectionUtils.isEmpty(userIds) || type==null) return new ArrayList<>();
        LambdaQueryWrapperX<SysUserIdentity> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eqX(SysUserIdentity::getType,type.getValue())
                .inX(SysUserIdentity::getUserId,userIds)
                ;
        return mapper.selectListByQuery(queryWrapper);
    }

    @Override
    public List<Long> selectRoleIdListByUserId(Long userId) {
        return selectObjectIdListByUserId(SysUserIdentityTypeEnum.ROLE,userId);
    }

    @Override
    public List<String> selectRoleCodeListByUserId(Long userId) {
        QueryWrapper queryWrapper = buildQueryByUserId(SysUserIdentityTypeEnum.ROLE,userId);
        queryWrapper.select(SysRole::getCode)
                .leftJoin(SysRole.class).on(SysRole::getId,SysUserIdentity::getObjectId);
        return mapper.selectObjectListByQueryAs(queryWrapper,String.class);
    }


    @Override
    public List<String> selectRoleNameListByUserId(Long userId) {
        QueryWrapper queryWrapper = buildQueryByUserId(SysUserIdentityTypeEnum.ROLE,userId);
        queryWrapper.select(SysRole::getName)
                .leftJoin(SysRole.class).on(SysRole::getId,SysUserIdentity::getObjectId);
        return mapper.selectObjectListByQueryAs(queryWrapper,String.class);
    }

    @Override
    public List<SysUserIdentityRoleResult> selectRoleInfoByUserIds(List<Long> userIds) {
        LambdaQueryWrapperX<SysUserIdentity>  queryWrapper=  new LambdaQueryWrapperX<>();
        queryWrapper.inX(SysUserIdentity::getUserId,userIds)
                .eqX(SysUserIdentity::getType,SysUserIdentityTypeEnum.ROLE.getValue())
        ;
        queryWrapper.select(SysRole::getId,SysRole::getName,SysRole::getCode)
                .leftJoin(SysRole.class).on(SysRole::getId,SysUserIdentity::getObjectId);
        return mapper.selectListByQueryAs(queryWrapper,SysUserIdentityRoleResult.class);
    }

    @Override
    public List<Long> selectPositionIdListByUserId(Long userId) {
        return selectObjectIdListByUserId(SysUserIdentityTypeEnum.POSITION,userId);
    }

    @Override
    public int deleteByUserId(SysUserIdentityTypeEnum type, Long userId) {
        LambdaQueryWrapperX<SysUserIdentity> queryWrapper = buildQueryByUserId(type,userId);
        return mapper.deleteByQuery(queryWrapper);
    }

    @Override
    public int deleteUserByObjectId(SysUserIdentityTypeEnum type, Long objectId) {
        LambdaQueryWrapperX<SysUserIdentity> queryWrapper = new LambdaQueryWrapperX<SysUserIdentity>()
                .eqX(SysUserIdentity::getObjectId,objectId).eqX(SysUserIdentity::getType,type.getValue());
        return mapper.deleteByQuery(queryWrapper);
    }

    @Override
    public int deleteRoleByUserId(Long userId) {
        return deleteByUserId(SysUserIdentityTypeEnum.ROLE,userId);
    }

    @Override
    public int deletePositionByUserId(Long userId) {
        return deleteByUserId(SysUserIdentityTypeEnum.POSITION,userId);
    }

    @Override
    public void insertList(SysUserIdentityTypeEnum type, Long userId, List<Long> objectIds) {
        Db.executeBatch(objectIds, 500, SysUserIdentityMapper.class
                , (mapper, roleId) -> {
                    SysUserIdentity ur = new SysUserIdentity();
                    ur.setType(type.getValue());
                    ur.setObjectId(roleId);
                    ur.setUserId(userId);
                    mapper.insert(ur);
                });
    }

    @Override
    public void insertRoleList(Long userId, List<Long> roleIds) {
        insertList(SysUserIdentityTypeEnum.ROLE,userId,roleIds);
    }

    @Override
    public void insertPositionList(Long userId, List<Long> positionIds) {
        insertList(SysUserIdentityTypeEnum.POSITION,userId,positionIds);
    }

    @Override
    @Transaction
    public void recreateByList(SysUserIdentityTypeEnum type, Long userId, List<Long> objectIds) {
        deleteByUserId(type,userId);
        insertList(type,userId,objectIds);
    }

    @Override
    public void recreateRoleByList(Long userId, List<Long> roleIds) {
        recreateByList(SysUserIdentityTypeEnum.ROLE,userId,roleIds);
    }

    @Override
    public void recreatePositionByList(Long userId, List<Long> positionIds) {
        recreateByList(SysUserIdentityTypeEnum.ROLE,userId,positionIds);
    }


    private LambdaQueryWrapperX<SysUserIdentity> buildQueryByUserId(SysUserIdentityTypeEnum type,Long userId){
        LambdaQueryWrapperX<SysUserIdentity>  queryWrapper=  new LambdaQueryWrapperX<>();
        queryWrapper.eqX(SysUserIdentity::getUserId,userId)
                .eqX(SysUserIdentity::getType,type.getValue())
        ;
        return queryWrapper;
    }
}
