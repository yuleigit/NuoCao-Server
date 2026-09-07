package com.nuocao.sys.modular.menu.pojo.result;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.core.handler.JacksonTypeHandler;
import com.nuocao.common.pojo.CommonEntity;
import com.nuocao.common.pojo.KeyValue;
import com.nuocao.common.pojo.KeyValueData;
import com.nuocao.common.utils.NumberUtils;
import com.nuocao.sys.modular.menu.pojo.SysMenuBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 菜单实体详情试题
 *
 **/
@Getter
@Setter
public class SysMenuDetailResult extends SysMenuBase {

    /** id */
    @ApiModelProperty(value = "menuId", position = 1)
    private Long id;


    public String parentTitle;


}
