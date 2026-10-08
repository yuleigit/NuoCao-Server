package com.nuocao.gen.modular.table.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.nuocao.common.enums.CommonYesNoEnum;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.mapper.LambdaQueryWrapperX;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.gen.core.utils.GenDbTypeUtils;
import com.nuocao.gen.modular.table.core.DbTableHelper;
import com.nuocao.gen.modular.table.entity.GenTable;
import com.nuocao.gen.modular.table.entity.GenTableColumn;
import com.nuocao.gen.modular.table.enums.GenHtmlTypeEnum;
import com.nuocao.gen.modular.table.mapper.GenTableColumnMapper;
import com.nuocao.gen.modular.table.mapper.GenTableMapper;
import com.nuocao.gen.modular.table.param.*;
import com.nuocao.gen.modular.table.param.column.GenColumnAddParam;
import com.nuocao.gen.modular.table.param.column.GenColumnEditParam;
import com.nuocao.gen.modular.table.result.GenDbTableColumnResult;
import com.nuocao.gen.modular.table.result.GenDbTableResult;
import com.nuocao.gen.modular.table.service.GenTableService;
import com.nuocao.sys.api.SysMenuApi;
import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;
import lombok.extern.slf4j.Slf4j;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nuocao.gen.modular.table.core.GenConstants.*;

/**
 * 代码生成表格 Service接口实现类
 **/
@Component
@Slf4j
public class GenTableServiceImpl implements GenTableService {


    @Inject
    private GenTableMapper tableMapper;
    @Inject
    private GenTableColumnMapper columnMapper;

    @Inject
    private DbTableHelper tableHelper;

    @Inject
    private SysMenuApi sysMenuApi;

    @Inject
    private FlexGlobalConfig globalConfig;

    @Override
    public PageResult<GenTable> page(GenTablePageParam pageParam) {
        LambdaQueryWrapperX<GenTable> query = new LambdaQueryWrapperX<>();
        query.likeIfPresent(GenTable::getTableName,pageParam.getTableName());
        if(StringUtils.isEmpty(pageParam.getSortingFields())){
            query.orderBy(GenTable::getId,false);
        }
        return tableMapper.selectPage(pageParam,query);
    }

    @Override
    @Transaction
    public GenTable add(GenTableAddParam addParam) {
        GenTable table = BeanUtils.toBean(addParam,GenTable.class);
        validInfo(table);
        tableMapper.insert(table);
        syncColumnsByDataScore(table);
        return table;
    }

    @Override
    public GenTable edit(GenTableEditParam editParam) {
        GenTable table = BeanUtils.toBean(editParam,GenTable.class);
        validInfo(table);
        tableMapper.updateById(table);
        //更新字段，先得到已有字段
        syncColumnsByDataScore(table);
        return table;
    }



    @Override
    @Transaction
    public void delete(List<GenTableIdParam> idParamList) {
        List<Long> ids = idParamList.stream().map(GenTableIdParam::getId).collect(Collectors.toList());;
        List<GenTable> tables = tableMapper.selectListByIds(ids);
        if(tables.isEmpty()){return;
        }
        tableMapper.deleteBatchByIds(ids);
        columnMapper.deleteColumnByTableIds(ids);
    }

    @Override
    public GenTable detail(GenTableIdParam idParam) {
        return this.queryEntity(idParam.getId());
    }

    @Override
    public GenTable queryEntity(Long tableId) {
        GenTable table = tableMapper.selectOneById(tableId);
        CommonExceptionI18n.notNull(table,"gen.table.notExists",table);
        return table;
    }

    @Override
    public GenTable getById(Long tableId) {
        return tableMapper.selectOneById(tableId);
    }

    @Override
    public List<GenDbTableResult> getDbTables() {
        return BeanUtils.toBean(tableHelper.getDbTables(),GenDbTableResult.class);
    }

    @Override
    public List<GenDbTableColumnResult> getDbTableColumns(String dataSourceName,String tableName) {
        return BeanUtils.toBean(tableHelper.getColumns(dataSourceName,tableName),GenDbTableColumnResult.class);
    }

    @Override
    public List<SysMenuTreeResultDTO> menuTree() {
        return sysMenuApi.getMenuTree();
    }

    @Override
    public List<GenTableColumn> getColumnList(Long tableId) {
        QueryWrapper query = new QueryWrapper();
        query.eq(GenTableColumn::getTableId,tableId);
        query.orderBy(GenTableColumn::getSortCode,true).orderBy(GenTableColumn::getId,true);
        return columnMapper.selectListByQuery(query);
    }

    @Override
    public void editColumns(Long tableId, List<GenColumnEditParam> editParams) {
        List<GenTableColumn> columns = BeanUtils.toBean(editParams,GenTableColumn.class);
        for (GenTableColumn column:
                columns) {
            validColumnInfo(column);
        }
        //更新
        for (GenTableColumn column:
                columns) {
            columnMapper.updateById(column);
        }

    }

    private void validColumnInfo(GenTableColumn column){
        if(StringUtils.isNotBlank(column.getValidAnnotation()) && column.getValidAnnotation().startsWith("@")){
            throw new CommonException("validAnnotation should start with @");
        }
    }

    private void validInfo(GenTable info){

    }

    /**
     * 从数据源同步数据库表字段
     * @param table 表实体
     */
    private void syncColumnsByDataScore(GenTable table) {
        //得到所有已有字段
        List<GenTableColumn> oldColumneList = columnMapper.selectListByQuery(new QueryWrapper().eq(GenTableColumn::getTableId,table.getId()));
        GenTableColumnParam tableColumnParam = new GenTableColumnParam();
        tableColumnParam.setTableName(table.getTableName());
        //得到现阶段表字段
        List<GenDbTableColumnResult> resultList = this.getDbTableColumns(table.getDataSourceName(),tableColumnParam.getTableName());
        List<GenColumnAddParam> addParams = new ArrayList<>();
        List<String> newColumnNameList = new ArrayList<>();
        Map<Long,Integer> oldColumnSortMap= new HashMap<>();
        for (int i = 0; i < resultList.size(); i++) {
            GenDbTableColumnResult item = resultList.get(i);
            GenColumnAddParam addParam = new GenColumnAddParam();
            fillColumnDefault(addParam,table,item, i);
            String columnNameLower = addParam.getColumnName().toLowerCase();
            //查询已经存在的字段
            GenTableColumn oldColumn = oldColumneList.stream().filter(o->o.getColumnName().equalsIgnoreCase(columnNameLower)).findFirst().orElse(null);
            newColumnNameList.add(addParam.getColumnName().toLowerCase());

            if(oldColumn==null){ //不存在才添加
                addParams.add(addParam);
            }else{
                //存在更新排序
                oldColumnSortMap.put(oldColumn.getId(),addParam.getSortCode());
            }
        }

        //批量插入
        for (GenColumnAddParam addParam:
                addParams) {
            GenTableColumn tableColumn = BeanUtil.toBean(addParam, GenTableColumn.class);
            columnMapper.insert(tableColumn);
        }
        //删除不存在的数据
        List<Long> deleteColumnIds = oldColumneList.stream().filter(o->!newColumnNameList.contains(o.getColumnName().toLowerCase())).map(GenTableColumn::getId).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(deleteColumnIds)) {
            columnMapper.deleteBatchByIds(deleteColumnIds);
        }

        //更新已存在的字段排序
        if(!oldColumnSortMap.isEmpty()){
            for (Long columnId:
                    oldColumnSortMap.keySet()) {
                UpdateChain.of(GenTableColumn.class).eq(GenTableColumn::getId,columnId)
                        .set(GenTableColumn::getSortCode,oldColumnSortMap.get(columnId)).update();
            }
        }
    }
    /**
     * 填充字段默认信息
     * @param addParam
     * @param table
     */
    private void fillColumnDefault(GenColumnAddParam addParam,GenTable table,GenDbTableColumnResult item,Integer sortCode){
        String logicDeleteField = globalConfig.getLogicDeleteColumn();
        if (ObjectUtil.isEmpty(logicDeleteField)) {
            logicDeleteField = DELETE_FLAG_COLUMN_NAME;
        }

        addParam.setTableId(table.getId());
        addParam.setIsPk(item.getColumnName().toLowerCase().equals(table.getPkColumnName().toLowerCase())?CommonYesNoEnum.YES.getValue():CommonYesNoEnum.NO.getValue());
        addParam.setColumnName(item.getColumnName());
        addParam.setColumnType(item.getColumnType());
        addParam.setColumnRemark(item.getColumnRemark());
        addParam.setJavaType(GenDbTypeUtils.getJavaTypeBySqlType(item.getColumnType()));
        addParam.setHtmlType(GenHtmlTypeEnum.INPUT.getValue().toLowerCase());

        // 除主键、删除标志、租户ID、创建人、创建时间、修改人和修改时间外，所有默认在列表显示、在增改显示、非列省略、非必填，非查询
        if (table.getPkColumnName().equalsIgnoreCase(item.getColumnName()) ||
                logicDeleteField.equalsIgnoreCase(item.getColumnName()) ||
                CREATE_USER_COLUMN_NAME.equalsIgnoreCase(item.getColumnName()) ||
                CREATE_TIME_COLUMN_NAME.equalsIgnoreCase(item.getColumnName()) ||
                UPDATE_TIME_COLUMN_NAME.equalsIgnoreCase(item.getColumnName()) ||
                UPDATE_USER_COLUMN_NAME.equalsIgnoreCase(item.getColumnName()) ||
                TENANT_ID_COLUMN_NAME.equalsIgnoreCase(item.getColumnName())
        ) {
            addParam.setIsList(CommonYesNoEnum.NO.getValue());
            addParam.setIsAdd(CommonYesNoEnum.NO.getValue());
            addParam.setIsEdit(CommonYesNoEnum.NO.getValue());
            addParam.setIsView(CommonYesNoEnum.NO.getValue());
        }else
        {
            addParam.setIsList(CommonYesNoEnum.YES.getValue());
            addParam.setIsAdd(CommonYesNoEnum.YES.getValue());
            addParam.setIsEdit(CommonYesNoEnum.YES.getValue());
            addParam.setIsView(CommonYesNoEnum.YES.getValue());

        }
        addParam.setIsIgnore(CommonYesNoEnum.YES.getValue());
        addParam.setIsRequired(item.getNullable());
        addParam.setIsQuery(CommonYesNoEnum.YES.getValue());
        addParam.setSortCode(sortCode);
        if(addParam.getJavaType().equals("String")){
            addParam.setValueSizeMax(BigDecimal.valueOf(item.getSizeMax()));
        }
    }


}
