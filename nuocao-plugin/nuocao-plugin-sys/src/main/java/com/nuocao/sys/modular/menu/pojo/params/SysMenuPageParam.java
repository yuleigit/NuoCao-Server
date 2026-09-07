package com.nuocao.sys.modular.menu.pojo.params;

import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 菜单查询参数
 **/
@Getter
@Setter
public class SysMenuPageParam extends SortPageParam {

    /** 模块 */
    @ApiModelProperty(value = "顶级ID")
    private String topId;
}
