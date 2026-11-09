package com.nuocao.gen.modular.table.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.nuocao.common.annotation.CommonLog;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.pojo.CommonValidList;
import com.nuocao.gen.modular.table.entity.GenTable;
import com.nuocao.gen.modular.table.entity.GenTableColumn;
import com.nuocao.gen.modular.table.param.GenTableAddParam;
import com.nuocao.gen.modular.table.param.GenTableColumnParam;
import com.nuocao.gen.modular.table.param.GenTableEditParam;
import com.nuocao.gen.modular.table.param.GenTableIdParam;
import com.nuocao.gen.modular.table.param.GenTablePageParam;
import com.nuocao.gen.modular.table.param.column.GenColumnEditParam;
import com.nuocao.gen.modular.table.result.GenDbTableColumnResult;
import com.nuocao.gen.modular.table.result.GenDbTableResult;
import com.nuocao.gen.modular.table.service.GenTableService;
import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Param;
import org.noear.solon.annotation.Post;
import org.noear.solon.validation.annotation.NotEmpty;
import org.noear.solon.validation.annotation.Valid;
import org.noear.solon.validation.annotation.Validated;

import java.util.List;

/**
 * 代码生成表格控制器
 **/
@Api(tags = "代码生成表格控制器")
@Controller
@Valid
public class GenTableController {

    @Inject
    private GenTableService genTableService;

    /**
     * 获取表格分页
     */
    @ApiOperation("获取表格分页")
    @Get
    @SaCheckPermission("gen:table:query")
    @Mapping("/gen/table/page")
    public CommonResult<PageResult<GenTable>> page(GenTablePageParam pageParam) {
        return CommonResult.data(genTableService.page(pageParam));
    }

    /**
     * 添加表格
     */
    @ApiOperation("添加表格")
    @CommonLog("添加代码生成表格")
    @Post
    @SaCheckPermission("gen:table:add")
    @Mapping("/gen/table/add")
    public CommonResult<GenTable> add(@Validated GenTableAddParam addParam) {
        return CommonResult.data(genTableService.add(addParam));
    }

    /**
     * 编辑表格基础信息
     */
    @ApiOperation("编辑表格基础信息")
    @CommonLog("编辑代码生成表格")
    @Post
    @SaCheckPermission("gen:table:edit")
    @Mapping("/gen/table/edit")
    public CommonResult<GenTable> edit(@Validated GenTableEditParam editParam) {
        return CommonResult.data(genTableService.edit(editParam));
    }

    /**
     * 删除表格
     */
    @ApiOperation("删除表格")
    @CommonLog("删除代码生成表格")
    @Post
    @SaCheckPermission("gen:table:delete")
    @Mapping("/gen/table/delete")
    public CommonResult<String> delete(@Validated @NotEmpty(message = "集合不能为空")
                                       CommonValidList<GenTableIdParam> idParamList) {
        genTableService.delete(idParamList);
        return CommonResult.ok();
    }

    /**
     * 获取表格详情
     */
    @ApiOperation("获取表格详情")
    @Get
    @SaCheckPermission("gen:table:query")
    @Mapping("/gen/table/detail")
    public CommonResult<GenTable> detail(@Validated GenTableIdParam idParam) {
        return CommonResult.data(genTableService.detail(idParam));
    }

    /**
     * 获取所有数据库表信息
     */
    @ApiOperation("获取所有数据库表信息")
    @Get
    @SaCheckPermission("gen:table:add")
    @Mapping("/gen/table/dbTables")
    public CommonResult<List<GenDbTableResult>> dbTables() {
        return CommonResult.data(genTableService.getDbTables());
    }

    /**
     * 获取数据库表字段信息
     */
    @ApiOperation("获取数据库表字段信息")
    @Get
    @SaCheckPermission("gen:table:add")
    @Mapping("/gen/table/dbTableColumns")
    public CommonResult<List<GenDbTableColumnResult>> dbTableColumns(@Param("dataSourceName") String dataSourceName,
                                                                     @Validated GenTableColumnParam columnParam) {
        return CommonResult.data(genTableService.getDbTableColumns(dataSourceName, columnParam.getTableName()));
    }

    /**
     * 获取生成器可用菜单
     */
    @ApiOperation("获取生成器可用菜单")
    @Get
    @Mapping("/gen/table/menuTree")
    public CommonResult<List<SysMenuTreeResultDTO>> menuTree() {
        return CommonResult.data(genTableService.menuTree());
    }

    /**
     * 获取表格字段列表
     */
    @ApiOperation("获取表格字段列表")
    @Get
    @SaCheckPermission("gen:table:query")
    @Mapping("/gen/table/columnList")
    public CommonResult<List<GenTableColumn>> columnList(@Param("tableId") Long tableId) {
        return CommonResult.data(genTableService.getColumnList(tableId));
    }

    /**
     * 批量保存字段配置
     */
    @ApiOperation("批量保存字段配置")
    @CommonLog("保存代码生成字段配置")
    @Post
    @SaCheckPermission("gen:table:edit")
    @Mapping("/gen/table/editColumns")
    public CommonResult<String> editColumns(@Param("tableId") Long tableId,
                                            @Body List<GenColumnEditParam> editParams) {
        genTableService.editColumns(tableId, editParams);
        return CommonResult.ok();
    }

}
