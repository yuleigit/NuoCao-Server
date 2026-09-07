package com.nuocao.sys.modular.user.pojo.param;

import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 站内信分页参数

 */
@Getter
@Setter
public class SysUserMessagePageParam extends SortPageParam {


    /** 站内信分类 */
    @ApiModelProperty(value = "站内信分类")
    private String category;
}
