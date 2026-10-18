package com.nuocao.sys.modular.account.param;

import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 系统账户 查询参数

 **/
@Getter
@Setter
public class SysAccountPageParam extends SortPageParam {


    /** 账号 */
    @ApiModelProperty(value = "账号")
    private String username;

    /** 姓名 */
    @ApiModelProperty(value = "昵称")
    private String nickname;

    /** 手机 */
    @ApiModelProperty(value = "手机")
    private String phone;

    /** 用户状态 */
    @ApiModelProperty(value = "用户状态")
    private Integer status;
}
