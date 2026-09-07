package com.nuocao.dev.modular.dict.result;

import com.mybatisflex.annotation.Id;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 字典tree 实体
 */
@Setter
@Getter
public class DevDictTreeResult {
    @ApiModelProperty(value = "id", position = 1)
    @Id
    private Long id;

    /** 父id */
    @ApiModelProperty(value = "父id", position = 2)
    private Long parentId;

    /** 字典文字 */
    @ApiModelProperty(value = "字典i18nKey", position = 3)
    private String i18nKey;

    /** 字典文字 */
    @ApiModelProperty(value = "字典文字", position = 4)
    private String dictLabel;

    /** 字典值 */
    @ApiModelProperty(value = "字典值", position = 5)
    private String dictValue;

    /** 分类 */
    @ApiModelProperty(value = "分类",notes = "sys:系统，biz=业务，自己定义",position = 6)
    private String category;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 7)
    private Integer sortCode;


    /** 排序码 */
    @ApiModelProperty(value = "值类型S=字符串,N=数字", position = 8)
    private String valueType;

    /** 扩展信息 */
    @ApiModelProperty(value = "显示颜色", position = 9)
    private String color;

    private List<DevDictTreeResult> children ;

}
