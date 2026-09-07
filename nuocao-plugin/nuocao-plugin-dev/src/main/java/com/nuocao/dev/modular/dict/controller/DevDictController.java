package com.nuocao.dev.modular.dict.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.pojo.CommonValidList;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.dev.modular.dict.entity.DevDict;
import com.nuocao.dev.modular.dict.param.*;
import com.nuocao.dev.modular.dict.result.DevDictTreeResult;
import com.nuocao.dev.modular.dict.service.DevDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.NotEmpty;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;


import java.util.List;

/**
 * 字典控制器
 **/
@Api(tags = "字典控制器")
@Controller
@Valid
public class DevDictController {

    @Inject
    private DevDictService devDictService;

    /**
     * 获取字典分页
     */
    @ApiOperation("获取字典分页")
    @Get
    @SaCheckPermission("dev:dict:query")
    @Mapping("/dev/dict/page")
    public CommonResult<PageResult<DevDict>> page(DevDictPageParam devDictPageParam) {
        return CommonResult.data(devDictService.page(devDictPageParam));
    }

    /**
     * 获取字典列表
     */
    @ApiOperation("获取字典列表")
    @Get
    @SaCheckPermission("dev:dict:query")
    @Mapping("/dev/dict/list")
    public CommonResult<List<DevDict>> list(DevDictListParam devDictListParam) {
        return CommonResult.data(devDictService.list(devDictListParam));
    }

    /**
     * 获取字典树
     */
    @ApiOperation("获取字典树")
    @Get
    @Mapping("/dev/dict/tree")
    public CommonResult<List<DevDictTreeResult>> tree(DevDictTreeParam devDictTreeParam) {
        return CommonResult.data(devDictService.tree(devDictTreeParam.getCategory()));
    }

    /**
     * 添加字典
     */
    @ApiOperation("添加字典")
    @CommonLog("添加字典")
    @Post
    @SaCheckPermission("dev:dict:add")
    @Mapping("/dev/dict/add")
    public CommonResult<String> add(@Validated DevDictAddParam devDictAddParam) {
        devDictService.add(devDictAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑字典
     */
    @ApiOperation("编辑字典")
    @CommonLog("编辑字典")
    @Post
    @SaCheckPermission("dev:dict:edit")
    @Mapping("/dev/dict/edit")
    public CommonResult<String> edit(@Validated DevDictEditParam devDictEditParam) {
        devDictService.edit(devDictEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除字典
     */
    @ApiOperation("删除字典")
    @CommonLog("删除字典")
    @Post
    @SaCheckPermission("dev:dict:delete")
    @Mapping("/dev/dict/delete")
    public CommonResult<String> delete(@Validated @NotEmpty(message = "集合不能为空")
                                       CommonValidList<DevDictIdParam> devDictIdParamList) {
        devDictService.deleteByIds(CollectionUtils.toList(devDictIdParamList,DevDictIdParam::getId));
        return CommonResult.ok();
    }

    /**
     * 获取字典详情
     */
    @ApiOperation("获取字典详情")
    @Get
    @SaCheckPermission("dev:dict:query")
    @Mapping("/dev/dict/detail")
    public CommonResult<DevDict> detail(@Validated DevDictIdParam devDictIdParam) {
        return CommonResult.data(devDictService.detail(devDictIdParam));
    }



}
