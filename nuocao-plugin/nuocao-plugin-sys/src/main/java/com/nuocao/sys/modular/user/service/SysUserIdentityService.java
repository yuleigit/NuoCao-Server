package com.nuocao.sys.modular.user.service;

import com.nuocao.common.pojo.KeyValue;
import com.nuocao.sys.api.enums.SysUserIdentityTypeEnum;
import com.nuocao.sys.modular.user.entity.SysUserIdentity;
import com.nuocao.sys.modular.user.pojo.result.SysUserIdentityRoleResult;

import java.util.List;
import java.util.Map;

public interface SysUserIdentityService {

    /**
     * 根据角色ID统计用户人数
     * @param roleId
     * @return
     */
    int selectUserCountByRoleId(Long roleId);

    /**
     * 根据角色id获取用户ID集合
     * @param roleId
     * @return
     */
    List<Long> selectUserIdListByRoleId(Long roleId);

    /**
     * 根据岗位ID统计用户人数
     * @param roleId
     * @return
     */
    int selectUserCountByPositionId(Long roleId);
    /**
     * 统计用户人数
     * @param  type 身份类型
     * @param objectId
     * @return
     */
    int selectUserCount(SysUserIdentityTypeEnum type, Long objectId);


    /**
     * 获取角色ID集合
     * @param type
     * @param userId
     * @return
     */
    List<Long> selectObjectIdListByUserId(SysUserIdentityTypeEnum type,Long userId);

    /**
     * 获取对象ID和用户ID合计
     * @param type 授权对象类型
     * @param userIds 授权用户ID集合
     * @return
     */
    List<SysUserIdentity> selectListByUserIds(SysUserIdentityTypeEnum type, List<Long> userIds);


    /**
     * 获取用户角色Id集合
     * @param userId
     * @return
     */
    List<Long> selectRoleIdListByUserId(Long userId);

    /**
     * 获取用户角色编码集合
     * @param userId
     * @return
     */
    List<String> selectRoleCodeListByUserId(Long userId);

    /**
     * 获取用户角色名称集合
     * @param userId
     * @return
     */
    List<String> selectRoleNameListByUserId(Long userId);

    /**
     * 根据用户ID获取这些用户所包含的角色id与name对应列表，一般用与列表中填充角色信息
     * @param userIds
     * @return
     */
    List<SysUserIdentityRoleResult> selectRoleInfoByUserIds(List<Long> userIds);

    /**
     * 获取用户岗位ID集合
     * @param userId
     * @return
     */
    List<Long> selectPositionIdListByUserId(Long userId);


    /**
     * 根据用户ID删除身份
     * @param type
     * @param userId
     */
    int deleteByUserId(SysUserIdentityTypeEnum type,Long userId);

    /**
     * 根据身份ObjectId删除用户
     * @param type
     * @param objectId
     */
    int deleteUserByObjectId(SysUserIdentityTypeEnum type,Long objectId);

    /**
     * 根据用户ID删除角色
     * @param userId
     */
    int deleteRoleByUserId(Long userId);

    /**
     * 根据用户ID删除岗位
     * @param userId
     */
    int deletePositionByUserId(Long userId);

    /**
     * 写入全新的权限
     * @param type
     * @param userId
     * @param objectIds
     */
    void insertList(SysUserIdentityTypeEnum type,Long userId,List<Long> objectIds);

    /**
     * 写入用户角色集合
     * @param userId
     * @param roleIds
     */
    void insertRoleList(Long userId,List<Long> roleIds);

    /**
     * 写入用户岗位集合
     * @param userId
     * @param positionIds
     */
    void insertPositionList(Long userId,List<Long> positionIds);


    /**
     * 重建用户身份信息,实际是先delete然后再delete
     * @param type
     * @param userId
     * @param objectIds
     */
    void recreateByList(SysUserIdentityTypeEnum type,Long userId,List<Long> objectIds);

    /**
     * 重建用户角色信息
     * @param userId
     * @param roleIds
     */
    void recreateRoleByList(Long userId,List<Long> roleIds);


    /**
     * 重建用户岗位信息
     * @param userId
     * @param positionIds
     */
    void recreatePositionByList(Long userId,List<Long> positionIds);


}
