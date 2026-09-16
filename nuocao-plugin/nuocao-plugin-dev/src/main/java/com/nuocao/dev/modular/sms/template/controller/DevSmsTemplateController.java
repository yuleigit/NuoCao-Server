package com.nuocao.dev.modular.sms.template.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.pojo.CommonValidList;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.dev.modular.sms.template.convert.DevSmsTemplateConvert;
import com.nuocao.dev.modular.sms.template.entity.DevSmsTemplate;
import com.nuocao.dev.modular.sms.template.param.DevSmsTemplateAddParam;
import com.nuocao.dev.modular.sms.template.param.DevSmsTemplateEditParam;
import com.nuocao.dev.modular.sms.template.param.DevSmsTemplateIdParam;
import com.nuocao.dev.modular.sms.template.param.DevSmsTemplatePageParam;
import com.nuocao.dev.modular.sms.template.result.DevSmsTemplateResult;
import com.nuocao.dev.modular.sms.template.service.DevSmsTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.NotEmpty;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

/**
 * 短信模板控制器
 **/
@Api(tags = "短信模板控制器")
@Controller
@Valid
public class DevSmsTemplateController {

    @Inject
    private DevSmsTemplateService devSmsTemplateService;

    /**
     * 获取短信模板分页
     */
    @ApiOperation("获取短信模板分页")
    @Get
    @SaCheckPermission("dev:sms:template:query")
    @Mapping("/dev/sms/template/page")
    public CommonResult<PageResult<DevSmsTemplateResult>> page(DevSmsTemplatePageParam pageParam) {
        return CommonResult.data(DevSmsTemplateConvert.INSTANCE.convertPage(devSmsTemplateService.page(pageParam)));
    }



    /**
     * 添加短信模板
     */
    @ApiOperation("添加短信模板")
    @CommonLog("添加短信模板")
    @Post
    @SaCheckPermission("dev:sms:template:add")
    @Mapping("/dev/sms/template/add")
    public CommonResult<Long> add(@Validated DevSmsTemplateAddParam addParam) {
        ;
        return CommonResult.data(devSmsTemplateService.add(addParam));
    }

    /**
     * 编辑短信模板
     */
    @ApiOperation("编辑短信模板")
    @CommonLog("编辑短信模板")
    @Post
    @SaCheckPermission("dev:sms:template:edit")
    @Mapping("/dev/sms/template/edit")
    public CommonResult<Void> edit(@Validated DevSmsTemplateEditParam editParam) {
        devSmsTemplateService.edit(editParam);
        return CommonResult.ok();
    }

    /**
     * 删除短信模板
     */
    @ApiOperation("删除短信模板")
    @CommonLog("删除短信模板")
    @Post
    @SaCheckPermission("dev:sms:template:delete")
    @Mapping("/dev/sms/template/delete")
    public CommonResult<String> delete(@Validated @NotEmpty(message = "集合不能为空")
                                       CommonValidList<DevSmsTemplateIdParam> idParams) {
        devSmsTemplateService.deleteByIds(CollectionUtils.toList(idParams,DevSmsTemplateIdParam::getTemplateId));
        return CommonResult.ok();
    }

    /**
     * 获取短信模板详情
     */
    @ApiOperation("获取短信模板详情")
    @Post
    @Mapping("/dev/sms/template/detail")
    public CommonResult<DevSmsTemplate> detail(@Validated DevSmsTemplateIdParam idParam) {
        return CommonResult.data(devSmsTemplateService.detail(idParam));
    }
}
