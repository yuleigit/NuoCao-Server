package com.nuocao.dev.modular.config.controller;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.annotation.PublicMapping;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.pojo.CommonValidList;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.data.access.tenant.annotation.TenantIgnore;
import com.nuocao.dev.modular.config.entity.DevConfig;
import com.nuocao.dev.modular.config.param.*;
import com.nuocao.dev.modular.config.result.DevConfigKeyValueResult;
import com.nuocao.dev.modular.config.service.DevConfigService;
import com.nuocao.dev.modular.sms.template.param.DevSmsTemplatePageParam;
import com.nuocao.dev.modular.sms.template.result.DevSmsTemplateSelectorResult;
import com.nuocao.dev.modular.sms.template.service.DevSmsTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.NotEmpty;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.util.List;

/**
 * 系统配置控制器
 * 此系统配置多为通用配置，切不涉及多语言情况的设置
 * 如短信通道、文件存储引擎、系统各类策略等，多语言配置建议使用字典模块或自行扩展
 **/
@Api(tags = "配置控制器")
@Controller
@Valid
public class DevConfigController {

    @Inject
    private DevConfigService devConfigService;

    @Inject
    private DevSmsTemplateService smsTemplateService;

    /**
     * 获取配置分页
     */
    @ApiOperation("获取配置分页")
    @Get
    @SaCheckPermission("dev:config:query")
    @Mapping("/dev/config/page")
    public CommonResult<PageResult<DevConfig>> page(DevConfigPageParam devConfigPageParam) {
        return CommonResult.data(devConfigService.page(devConfigPageParam));
    }

    /**
     * 系统配置值列表
     */
    @ApiOperation("获取系统基础配置")
    @Get
    @Mapping("/dev/config/sysConfigList")
    @PublicMapping
    @TenantIgnore
    public CommonResult<List<DevConfigKeyValueResult>> sysConfigList() {
        return CommonResult.data(devConfigService.sysConfigList());
    }

    /**
     * 获取配置列表
     */
    @ApiOperation("获取配置列表")
    @Get
    @Mapping("/dev/config/list")
    public CommonResult<List<DevConfig>> list(DevConfigListParam devConfigListParam) {
        return CommonResult.data(devConfigService.list(devConfigListParam));
    }

    /**
     * 添加配置
     */
    @ApiOperation("添加配置")
    @CommonLog("添加配置")
    @Post
    @SaCheckPermission("dev:config:add")
    @Mapping("/dev/config/add")
    public CommonResult<String> add(@Validated DevConfigAddParam devConfigAddParam) {
        devConfigService.add(devConfigAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑配置
     */
    @ApiOperation("编辑配置")
    @CommonLog("编辑配置")
    @Post
    @SaCheckPermission("dev:config:edit")
    @Mapping("/dev/config/edit")
    public CommonResult<String> edit(@Validated DevConfigEditParam devConfigEditParam) {
        devConfigService.edit(devConfigEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除配置
     */
    @ApiOperation("删除配置")
    @CommonLog("删除配置")
    @Post
    @SaCheckPermission("dev:config:delete")
    @Mapping("/dev/config/delete")
    public CommonResult<String> delete(@Validated @NotEmpty(message = "集合不能为空")
                                       CommonValidList<DevConfigIdParam> devConfigIdParamList) {
        devConfigService.deleteByIds(CollectionUtils.toList(devConfigIdParamList,DevConfigIdParam::getId));
        return CommonResult.ok();
    }

    /**
     * 获取配置详情
     */
    @ApiOperation("获取配置详情")
    @Get
    @SaCheckPermission("dev:config:query")
    @Mapping("/dev/config/detail")
    public CommonResult<DevConfig> detail(@Validated DevConfigIdParam devConfigIdParam) {
        return CommonResult.data(devConfigService.detail(devConfigIdParam));
    }

    /**
     * 配置批量更新
     */
    @ApiOperation("配置批量更新")
    @CommonLog("配置批量更新")
    @Post
    @SaCheckPermission("dev:config:edit")
    @Mapping("/dev/config/editBatch")
    public CommonResult<String> editBatch(@Validated @NotEmpty(message = "集合不能为空")
                                          CommonValidList<DevConfigBatchParam> devConfigBatchParamList) {
        devConfigService.editBatch(devConfigBatchParamList);
        return CommonResult.ok();
    }

    /**
     * 配置批量更新
     */
    @ApiOperation("配置可选短信通道")
    @Post
    @SaCheckPermission("dev:config:edit")
    @Mapping("/dev/config/smsTemplateList")
    public CommonResult<List<DevSmsTemplateSelectorResult>> smsTemplateList() {
        return CommonResult.data(smsTemplateService.selectorList(new DevSmsTemplatePageParam()));
    }

}
