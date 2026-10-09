package com.nuocao.dev.modular.file.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.pojo.CommonValidList;
import com.nuocao.dev.modular.file.entity.DevFile;
import com.nuocao.dev.modular.file.param.DevFileIdParam;
import com.nuocao.dev.modular.file.param.DevFileListParam;
import com.nuocao.dev.modular.file.param.DevFilePageParam;
import com.nuocao.dev.modular.file.param.DevFileUrlListParam;
import com.nuocao.dev.modular.file.service.DevFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.nuocao.plugin.dev.api.DevFileApi;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.validation.annotation.NotEmpty;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;


import java.io.IOException;
import java.util.List;

/**
 * 文件控制器
 **/
@Api(tags = "文件控制器")
@Controller
@Valid
public class DevFileController {

    @Inject
    private DevFileApi devFileApi;

    @Inject
    private DevFileService devFileService;

    /**
     * 动态上传文件返回id
     **/
    @ApiOperation("动态上传文件返回id")
    @Post
    @Mapping("/dev/file/uploadReturnId")
    public CommonResult<String> uploadDynamicReturnId(UploadedFile file) {
        return CommonResult.data(devFileApi.storageFileReturnIdDynamic(file));
    }

    /**
     * 动态上传文件返回url
     **/
    @ApiOperation("动态上传文件返回url")
    @Post
    @Mapping("/dev/file/uploadReturnUrl")
    public CommonResult<String> uploadDynamicReturnUrl(UploadedFile file) {
        return CommonResult.data(devFileApi.storageFileReturnUrlDynamic(file));
    }

    /**
     * 本地文件上传，返回文件id
     **/
    @ApiOperation("上传本地文件返回id")
    @Post
    @Mapping("/dev/file/uploadReturnId/{engine}")
    public CommonResult<String> uploadLocalReturnId(
            @Path String engine,
            UploadedFile file) {
        return CommonResult.data(devFileService.uploadReturnId(engine, file));
    }

    /**
     * 本地文件上传，返回文件Url
     **/
    @ApiOperation("上传本地文件返回url")
    @Post
    @Mapping("/dev/file/uploadReturnUrl/{engine}")
    public CommonResult<String> uploadLocalReturnUrl(
            @Path String engine,
            UploadedFile file) {
        return CommonResult.data(devFileService.uploadReturnUrl(engine, file));
    }

    /**
     * 获取文件分页列表
     */
    @ApiOperation("获取文件分页列表")
    @Get
    @SaCheckPermission("dev:file:query")
    @Mapping("/dev/file/page")
    public CommonResult<PageResult<DevFile>> page(DevFilePageParam devFilePageParam) {
        return CommonResult.data(devFileService.page(devFilePageParam));
    }

    /**
     * 获取文件列表
     */
    @ApiOperation("获取文件列表")
    @Get
    @SaCheckPermission("dev:file:query")
    @Mapping("/dev/file/list")
    public CommonResult<List<DevFile>> list(DevFileListParam devFileListParam) {
        return CommonResult.data(devFileService.list(devFileListParam));
    }

    /**
     * 下载文件
     **/
    @ApiOperation("下载文件")
    @CommonLog("下载文件")
    @Get
    @Mapping(value = "/dev/file/download", produces = MimeType.APPLICATION_OCTET_STREAM_VALUE)
    public void download(@Validated DevFileIdParam devFileIdParam, Context ctx) throws IOException {
        devFileService.download(devFileIdParam, ctx);
    }

    /**
     * 删除文件
     */
    @ApiOperation("删除文件")
    @CommonLog("删除文件")
    @Post
    @SaCheckPermission("dev:file:delete")
    @Mapping(value = "/dev/file/delete")
    public CommonResult<String> delete(@Validated @NotEmpty(message = "集合不能为空")
                                       CommonValidList<DevFileIdParam> devFileIdParamList) {
        devFileService.delete(devFileIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取文件详情
     **/
    @ApiOperation("获取文件详情")
    @Get
    @Mapping("/dev/file/detail")
    public CommonResult<DevFile> detail(@Validated DevFileIdParam devFileIdParam) {
        return CommonResult.data(devFileService.detail(devFileIdParam));
    }

    /**
     * 根据文件url集合获取文件集合
     **/
    @ApiOperation("根据文件url集合获取文件集合")
    @Post
    @Mapping("/dev/file/getFileListByUrlList")
    public CommonResult<List<DevFile>> getFileListByUrlList(@Validated DevFileUrlListParam devFileUrlListParam) {
        return CommonResult.data(devFileService.getFileListByUrlList(devFileUrlListParam));
    }
}
