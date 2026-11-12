package com.nuocao.gen.modular.code.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.gen.modular.code.result.GenCodePreviewResult;
import com.nuocao.gen.modular.code.service.GenCodeService;
import com.nuocao.gen.modular.table.param.GenTableIdParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.core.handle.Context;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.io.IOException;

/**
 * 代码生成控制器
 **/
@Api(tags = "代码生成控制器")
@Controller
@Valid
public class GenCodeController {

    @Inject
    private GenCodeService genCodeService;

    /**
     * 执行代码生成，返回压缩包下载
     */
    @ApiOperation("执行代码生成（压缩包下载）")
    @CommonLog("执行代码生成（压缩包下载）")
    @Get
    @SaCheckPermission("gen:code:exec")
    @Mapping("/gen/code/execGenZip")
    public void execGenZip(@Validated GenTableIdParam idParam, Context response) throws IOException {
        genCodeService.execGenTargetZip(idParam.getId(), response);
    }

    /**
     * 执行代码生成，生成到项目目录
     */
    @ApiOperation("执行代码生成（生成到项目目录）")
    @CommonLog("执行代码生成（生成到项目目录）")
    @Post
    @SaCheckPermission("gen:code:exec")
    @Mapping("/gen/code/execGenProject")
    public CommonResult<String> execGenProject(@Validated GenTableIdParam idParam, Context response) throws IOException {
        genCodeService.execGenTargetProject(idParam.getId(), response);
        return CommonResult.ok();
    }

    /**
     * 预览代码生成
     */
    @ApiOperation("预览代码生成")
    @Get
    @SaCheckPermission("gen:code:exec")
    @Mapping("/gen/code/preview")
    public CommonResult<GenCodePreviewResult> preview(@Validated GenTableIdParam idParam) {
        return CommonResult.data(genCodeService.previewGen(idParam.getId()));
    }

}
