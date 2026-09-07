package com.nuocao.common.page;

import com.nuocao.common.enums.CommonExceptionEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ApiModel("分页返回实体")
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    @ApiModelProperty(value="总数量", example = "100")
    private Long total;

    /** 列表数据 */
    @ApiModelProperty(value = "返回数据")
    private List<T> records;


    /**
     * 表格数据对象
     */
    public PageResult()
    {
    }

    /**
     * 分页
     *
     * @param list 列表数据
     * @param total 总记录数
     */
    public PageResult(List<T> list, Long total)
    {
        this.records = list;
        this.total = total;
    }

    public PageResult( Long total)
    {
        this.records = new ArrayList<>();
        this.total = total;
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public List<T> getRecords()
    {
        return records;
    }

    public void setRecords(List<T> records)
    {
        this.records = records;
    }


    public static <T> PageResult<T> empty() {
        return new PageResult<>(0L);
    }

    public static <T> PageResult<T> empty(Long total) {
        return new PageResult<>(total);
    }
}
