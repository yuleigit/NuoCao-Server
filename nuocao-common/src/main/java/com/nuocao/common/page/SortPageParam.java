package com.nuocao.common.page;

import com.nuocao.common.utils.collection.CollectionUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SortPageParam extends PageParam  {

    private List<SortingField> sortingFields;


    /**
     * 验证是否传入了排序字段
     * @return
     */
    public boolean hasSortingFields(){
        return CollectionUtils.isNotEmpty(this.sortingFields);
    }

}
