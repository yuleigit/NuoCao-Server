package com.nuocao.sys.modular.account.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.pojo.CommonValidList;
import com.nuocao.common.utils.DesensitizationUtils;
import com.nuocao.sys.modular.account.enums.SysAccountFromSourceEnum;
import com.nuocao.sys.modular.account.param.SysAccountAddParam;
import com.nuocao.sys.modular.account.param.SysAccountEditParam;
import com.nuocao.sys.modular.account.param.SysAccountIdParam;
import com.nuocao.sys.modular.account.param.SysAccountPageParam;
import com.nuocao.sys.modular.account.result.SysAccountDetailResult;
import com.nuocao.sys.modular.account.result.SysAccountPageResult;
import com.nuocao.sys.modular.account.result.SysAccountSimpleResult;
import com.nuocao.sys.modular.account.service.SysAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotEmpty;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.util.stream.Collectors;


/**
 * 系统登录账户控制器
 **/
@Api(tags = "系统登录账户控制器")
@Controller
@Valid
public class SysAccountController {

    @Inject
    private SysAccountService sysAccountService;

    /**
     * 获取系统登录账户分页
     */
    @ApiOperation("获取系统登录账户分页")
    @SaCheckPermission("sys:account:query")
    @Get
    @Mapping("/sys/account/page")
    public CommonResult<PageResult<SysAccountPageResult>> page(SysAccountPageParam pageParam) {
        return CommonResult.data(sysAccountService.page(pageParam));
    }

    /**
     * 添加系统登录账户
     */
    @ApiOperation("添加系统登录账户")
    @CommonLog("添加系统登录账户")
    @SaCheckPermission("sys:account:add")
    @Post
    @Mapping("/sys/account/add")
    public CommonResult<String> add(@Validated SysAccountAddParam addParam) {
        sysAccountService.add(addParam, SysAccountFromSourceEnum.SYS_ADD.getValue(),true);
        return CommonResult.ok();
    }

    /**
     * 编辑系统登录账户
     */
    @ApiOperation("编辑系统登录账户")
    @CommonLog("编辑系统登录账户")
    @SaCheckPermission("sys:account:edit")
    @Post
    @Mapping("/sys/account/edit")
    public CommonResult<String> edit(@Validated SysAccountEditParam sysAccountEditParam) {
        sysAccountService.edit(sysAccountEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除系统登录账户
     */
    @ApiOperation("删除系统登录账户")
    @CommonLog("删除系统登录账户")
    @SaCheckPermission("sys:account:delete")
    @Post
    @Mapping("/sys/account/delete")
    public CommonResult<String> delete(@Validated @NotEmpty(message = "集合不能为空")
                                       CommonValidList<SysAccountIdParam> sysAccountIdParamList) {
        sysAccountService.delete(sysAccountIdParamList.stream().map(SysAccountIdParam::getId).collect(Collectors.toList()));
        return CommonResult.ok();
    }

    /**
     * 获取系统登录账户详情
     */
    @ApiOperation("获取系统登录账户详情")
    @SaCheckPermission("sys:account:query")
    @Get
    @Mapping("/sys/account/detail")
    public CommonResult<SysAccountDetailResult> detail(@Validated SysAccountIdParam sysAccountIdParam) {
        return CommonResult.data(sysAccountService.detail(sysAccountIdParam.getId()));
    }

    /**
     * 禁用用户
     **/
    @ApiOperation("禁用会员")
    @CommonLog("禁用会员")
    @SaCheckPermission("sys:account:disable")
    @Post
    @Mapping("/sys/account/disable")
    public CommonResult<String> disableAccount(@Validated SysAccountIdParam sysAccountIdParam) {
        sysAccountService.disableAccount(sysAccountIdParam.getId());
        return CommonResult.ok();
    }

    /**
     * 启用用户
     **/
    @ApiOperation("启用会员")
    @CommonLog("启用会员")
    @SaCheckPermission("sys:account:enable")
    @Post
    @Mapping("/sys/account/enable")
    public CommonResult<String> enableAccount(@Validated SysAccountIdParam sysAccountIdParam) {
        sysAccountService.enableAccount(sysAccountIdParam.getId());
        return CommonResult.ok();
    }


    /**
     * 启用用户
     **/
    @ApiOperation("重置密码")
    @CommonLog("重置密码")
    @SaCheckPermission("sys:account:rePwd")
    @Post
    @Mapping("/sys/account/resetPassword")
    public CommonResult<String> resetPwd(@Validated SysAccountIdParam sysAccountIdParam) {
        return CommonResult.ok(sysAccountService.resetPassword(sysAccountIdParam.getId()));
    }


    /**
     * 获取系统登录账户详情
     */
    @ApiOperation("查询用户基础信息")
    @Get
    @Mapping("/fileclient/queryUserByAccount")
    public CommonResult<SysAccountSimpleResult> queryUserByAccount(@Validated @NotBlank(message = "必须输入账户") String account) {
        SysAccountSimpleResult user =sysAccountService.getUserSimpleByAccount(account);
        CommonExceptionI18n.notNull(user,"sys.account.notExists",account);
        user.setNickname(DesensitizationUtils.desensitizeChineseName(user.getNickname()));
        user.setPhone(DesensitizationUtils.desensitizePhone(user.getPhone()));
        return CommonResult.data(user);
    }

}
