package com.nuocao.gen.modular.table.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.nuocao.common.mapper.BaseMapperX;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.gen.modular.table.entity.GenTableColumn;

import java.util.List;

/**
 * 代码生成 表字段Mapper接口
 *
 **/
public interface GenTableColumnMapper extends BaseMapperX<GenTableColumn> {

    default void deleteColumnByTableIds(List<Long> tableIds){
        if(CollectionUtils.isEmpty(tableIds)) return;
        QueryWrapper query = new QueryWrapper();
        query.in(GenTableColumn::getTableId,tableIds);
        this.deleteByQuery(query);
    }


}
