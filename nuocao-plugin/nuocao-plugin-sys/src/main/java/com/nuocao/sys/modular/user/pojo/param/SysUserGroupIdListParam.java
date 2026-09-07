package com.nuocao.sys.modular.user.pojo.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户组id集合参数
 *
 * @author yubaoshan
 * @date 2025/1/12 02:36
 **/
@Getter
@Setter
public class SysUserGroupIdListParam {

    /** id集合 */
    @ApiModelProperty(value = "id集合")
    @NotNull(message = "idList不能为空")
    private List<String> idList;
}
