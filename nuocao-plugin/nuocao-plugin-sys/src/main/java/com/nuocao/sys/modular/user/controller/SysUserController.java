package com.nuocao.sys.modular.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.pojo.CommonValidList;
import com.nuocao.data.access.tenant.config.TenantConfig;
import com.nuocao.sys.api.dto.SysRoleDTO;
import com.nuocao.sys.modular.user.entity.SysUser;
import com.nuocao.sys.modular.user.enums.SysUserSourceFromTypeEnum;
import com.nuocao.sys.modular.user.pojo.param.*;
import com.nuocao.sys.modular.user.pojo.result.SysUserPageResult;
import com.nuocao.sys.modular.user.pojo.result.UserSelectorResult;
import com.nuocao.sys.modular.user.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
import org.noear.solon.validation.annotation.NotEmpty;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;


import java.io.IOException;
import java.util.List;

/**
 * 用户控制器
 **/
@Api(tags = "用户控制器")
@Controller
@Valid
public class SysUserController {

    @Inject
    private SysUserService sysUserService;

    @Inject
    private TenantConfig tenantConfig;
    /**
     * 获取用户分页
     */
    @ApiOperation("获取用户分页")
    @Get
    @SaCheckPermission("sys:user:query")
    @Mapping("/sys/user/page")
    public CommonResult<PageResult<SysUserPageResult>> page(SysUserPageParam sysUserPageParam) {
        PageResult<SysUserPageResult> page = sysUserService.pageResult(sysUserPageParam);
        return CommonResult.data(page);
    }

    /**
     * 添加用户
     */
    @ApiOperation("添加用户")
    @CommonLog("添加用户")
    @Post
    @SaCheckPermission("sys:user:add")
    @Mapping("/sys/user/add")
    public CommonResult<String> add(@Validated SysUserAddParam sysUserAddParam) {
        sysUserService.add(sysUserAddParam, SysUserSourceFromTypeEnum.SYSTEM_ADD.getValue());
        return CommonResult.ok();
    }

    /**
     * 编辑用户
     */
    @ApiOperation("编辑用户")
    @CommonLog("编辑用户")
    @Post
    @SaCheckPermission("sys:user:edit")
    @Mapping("/sys/user/edit")
    public CommonResult<String> edit(@Validated SysUserEditParam sysUserEditParam) {
        sysUserService.edit(sysUserEditParam);
        return CommonResult.ok();
    }

    /**
     * 编辑用户
     */
    @ApiOperation("给用户分配角色")
    @CommonLog("给用户分配角色")
    @Post
    @SaCheckPermission("sys:user:roleSet")
    @Mapping("/sys/user/setRoles")
    public CommonResult<String> setRoles(@Validated SysUserSetRolesParam setRolesParam) {
        sysUserService.setUserRoleIds(setRolesParam.getUserId(),setRolesParam.getRoleIds());
        return CommonResult.ok();
    }


    /**
     * 编辑用户
     */
    @ApiOperation("获取角色")
    @Post
    @Mapping("/sys/user/getRoleIds")
    public CommonResult<List<SysRoleDTO>> getRoles(@Validated SysUserIdParam sysUserIdParam) {
        ;
        return CommonResult.data(sysUserService.getUserRoles(sysUserIdParam.getId()));
    }


    /**
     * 删除用户
     */
    @ApiOperation("删除用户")
    @CommonLog("删除用户")
    @Post
    @SaCheckPermission("sys:user:delete")
    @Mapping("/sys/user/delete")
    public CommonResult<String> delete(@Validated @NotEmpty(message = "集合不能为空")
                                       CommonValidList<Long> ids) {
        sysUserService.deleteByIds(ids);
        return CommonResult.ok();
    }

    /**
     * 获取用户详情
     */
    @ApiOperation("获取用户详情")
    @Get
    @SaCheckPermission("sys:user:query")
    @Mapping("/sys/user/detail")
    public CommonResult<SysUser> detail(@Validated SysUserIdParam sysUserIdParam) {
        return CommonResult.data(sysUserService.detail(sysUserIdParam));
    }

    /**
     * 禁用用户
     **/
    @ApiOperation("禁用用户")
    @CommonLog("禁用用户")
    @Post
    @SaCheckPermission("sys:user:enable")
    @Mapping("/sys/user/disableUser")
    public CommonResult<String> disableUser(SysUserIdParam sysUserIdParam) {
        sysUserService.disableUser(sysUserIdParam);
        return CommonResult.ok();
    }

    /**
     * 启用用户
     **/
    @ApiOperation("启用用户")
    @CommonLog("启用用户")
    @Post
    @SaCheckPermission("sys:user:enable")
    @Mapping("/sys/user/enableUser")
    public CommonResult<String> enableUser(@Validated SysUserIdParam sysUserIdParam) {
        sysUserService.enableUser(sysUserIdParam);
        return CommonResult.ok();
    }

    /**
     * 重置用户密码
     **/
    @ApiOperation("重置用户密码")
    @CommonLog("重置用户密码")
    @Post
    @SaCheckPermission("sys:user:resetPwd")
    @Mapping("/sys/user/resetPassword")
    public CommonResult<String> resetPassword(@Validated SysUserResetPwdParam resetPwdParam) {
        if(tenantConfig.isEnabled()){
           throw new CommonExceptionI18n("sys.user.tenant.disableResetPwd");
        }
        String newPassword = sysUserService.updatePassword(resetPwdParam.getId(),resetPwdParam.getNewPassword());
        return CommonResult.ok("密码重置成功");
    }


    /**
     * 用户导出
     */
    @ApiOperation("用户导出")
    @CommonLog("用户导出")
    @Get
    @SaCheckPermission("sys:user:export")
    @Mapping(value = "/sys/user/export")
    public void exportUser(SysUserExportParam sysUserExportParam, Context response) throws IOException {
        sysUserService.exportUser(sysUserExportParam, response);
    }

    /**通用方法 **/


    /**
     * 用户导出
     */
    @ApiOperation(value = "获取用户选择列表(分页)",notes = "拥有sys:user:selector-all权限者，可以选择所有用户，否则只能按自己的角色权限选择")
    @Get
    @Mapping(value = "/sys/user/selectorPage")
    public CommonResult<PageResult<UserSelectorResult>> selectorPage(SysUserPageParam userPageParam)  {
        return CommonResult.data( sysUserService.selectorPage(userPageParam));
    }

    /**
     * 用户导出
     */
    @ApiOperation(value = "获取已选择的用户")
    @Get
    @Mapping(value = "/sys/user/getSelectedUsers")
    public CommonResult<List<UserSelectorResult>> getSelectedUsers(@Validated @NotEmpty(message = "用户ID集合不能为空")
                                                                         CommonValidList<Long> userIds)   {
        return CommonResult.data( sysUserService.getSelectedUserList(userIds));
    }

}
