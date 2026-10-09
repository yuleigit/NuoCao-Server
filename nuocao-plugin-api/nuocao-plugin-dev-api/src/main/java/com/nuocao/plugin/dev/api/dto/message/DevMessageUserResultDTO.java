package com.nuocao.plugin.dev.api.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 消息返回结果DTO
 */
@Getter
@Setter
public class DevMessageUserResultDTO {

    @ApiModelProperty("用户ID")
    private Long id;

    @ApiModelProperty("用户名称")
    private String name="";
    @ApiModelProperty("用户头像")
    private String avatar="";
    @ApiModelProperty("用户昵称")
    private String nickname="";

}
