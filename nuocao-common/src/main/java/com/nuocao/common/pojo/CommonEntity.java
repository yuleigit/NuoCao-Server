package com.nuocao.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mybatisflex.annotation.Column;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 *     通用基础字段实体：创建时间、创建人、修改时间、修改人，需要此通用字段的实体可继承此类，
 *     继承此类要求数据表有对应的字段
 */
@Getter
@Setter
public class CommonEntity implements Serializable {

    /** 删除标志 */
    @Column(isLogicDelete = true)
    @ApiModelProperty(value = "删除标志", position = 999)
    @JsonIgnore
    private Integer deleteFlag;

    /** 创建时间 */
    @ApiModelProperty(value = "创建时间", position = 1000)
    private LocalDateTime createTime;

    /** 创建人 */
    @ApiModelProperty(value = "创建人", position = 1001)
    private String createUser;

    /** 更新时间 */
    @ApiModelProperty(value = "更新时间", position = 1002)
    private LocalDateTime updateTime;

    /** 更新人 */
    @ApiModelProperty(value = "更新人", position = 1003)
    private String updateUser;

}
