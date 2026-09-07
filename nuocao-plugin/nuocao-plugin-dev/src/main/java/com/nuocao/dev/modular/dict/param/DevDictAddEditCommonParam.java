package com.nuocao.dev.modular.dict.param;

import com.nuocao.common.validation.InStrEnum;
import com.nuocao.dev.modular.dict.enums.DevDictCategoryEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

import java.io.Serializable;


/**
 * 字典添加参数
 *
 */
@Getter
@Setter
public class DevDictAddEditCommonParam implements Serializable {

    /** 父id */
    @ApiModelProperty(value = "父id", position = 1)
    @NotNull
    private Long parentId;

    /** 字典文字 */
    @ApiModelProperty(value = "字典文字", position = 2)
    private String i18nKey;

    /** 字典文字 */
    @ApiModelProperty(value = "字典文字", position = 2)
    @NotBlank
    private String dictLabel;

    /** 字典值 */
    @ApiModelProperty(value = "字典值", position = 3)
    @NotBlank
    private String dictValue;


    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 5)
    @NotNull
    private Integer sortCode;

    /** 扩展信息 */
    @ApiModelProperty(value = "值类型s=字符串，n=数字", position = 6)
    private String valueType;
    /** 扩展信息 */
    @ApiModelProperty(value = "显示颜色", position = 6)
    private String color;

}
