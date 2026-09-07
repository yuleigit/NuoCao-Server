package com.nuocao.gen.modular.table.service;

import com.nuocao.common.page.PageResult;
import com.nuocao.gen.modular.table.entity.GenTable;
import com.nuocao.gen.modular.table.entity.GenTableColumn;
import com.nuocao.gen.modular.table.param.*;
import com.nuocao.gen.modular.table.param.column.GenColumnEditParam;
import com.nuocao.gen.modular.table.result.GenDbTableColumnResult;
import com.nuocao.gen.modular.table.result.GenDbTableResult;
import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;


import java.util.List;

/**
 * 代码生成 数据表 Service接口
 **/
public interface GenTableService  {

    /**
     * 数据表分页分页
     */
    PageResult<GenTable> page(GenTablePageParam pageParam);

    /**
     * 添加数据表
     */
    GenTable add(GenTableAddParam addParam);

    /**
     * 编辑数据表基础信息
     */
    GenTable edit(GenTableEditParam editParam);

    /**
     * 删除代码生成基础
     */
    void delete(List<GenTableIdParam> idParamList);

    /**
     * 获取代码生成基础详情
     */
    GenTable detail(GenTableIdParam idParam);

    /**
     * 获取代码生成基础详情
     **/
    GenTable queryEntity(Long tableId);

    /**
     * 获取代码生成基础详情
     **/
    GenTable getById(Long tableId);

    /**
     * 获取所有数据库中的表信息
     **/
    List<GenDbTableResult> getDbTables();

    /**
     * 获取表内所有字段信息
     **/
    List<GenDbTableColumnResult> getDbTableColumns(String dataSourceName,String tableName);

    /**
     * 获取生成器可用菜单（全量获取，忽视权限限制），不启用的除外
     **/
    List<SysMenuTreeResultDTO> menuTree();

    /**
     * 获取欧标的字段列表
     * @param tableId
     * @return
     */
    List<GenTableColumn> getColumnList(Long tableId);


    /**
     * 修改字段属性信息
     * @param tableId
     * @param editParams
     */
    void editColumns(Long tableId, List<GenColumnEditParam> editParams);
}
