package com.nuocao.dev.modular.config.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.nuocao.common.pojo.CommonEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 配置实体
 **/
@Getter
@Setter
@Table(value = "dev_config")
public class DevConfig extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "configId", position = 1)
    @Id
    private Long id;

    /** 配置分组 */
    @ApiModelProperty(value = "配置分组", position = 2)

    private String configGroup;


    /** 配置键 */
    @ApiModelProperty(value = "配置KEY", position = 2)
    private String configKey;

    /** 配置值 */
    @ApiModelProperty(value = "配置值", position = 3)
    private String configValue;

    /** 分类 */
    @ApiModelProperty(value = "配置分类",notes = "也可以理解为配置对象，比如阿里云短信、腾讯云短信就是两个对象，但是他们可用都属于sms组", position = 4)
    private String configCategory;

    /** 备注 */
    @ApiModelProperty(value = "备注", position = 5)
    private String remark;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 6)
    private Integer sortCode;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 7)
    private String extJson;
}
