package com.nuocao.gen.modular.table.controller;

import com.mybatisflex.core.paginate.Page;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.gen.modular.table.entity.GenTable;
import com.nuocao.gen.modular.table.param.GenTablePageParam;
import com.nuocao.gen.modular.table.service.GenTableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.validation.annotation.Valid;

@Api(tags = "代码生成表格 控制器")
@Controller
@Valid
public class GenTableController {

    @Inject
    private GenTableService genTableService;

    @ApiOperation("获取表格分页列表")
    @Get
    @Mapping("/sys/basic/page")
    public CommonResult<PageResult<GenTable>> page(GenTablePageParam pageParam) {
        return CommonResult.data(genTableService.page(pageParam));
    }

}
