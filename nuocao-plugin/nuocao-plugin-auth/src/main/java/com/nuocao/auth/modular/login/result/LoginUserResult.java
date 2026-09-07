package com.nuocao.auth.modular.login.result;

import com.nuocao.common.page.CommonPicValidCodeResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 前端获取用户信息时返回的用户信息
 **/
@Getter
@Setter
public class LoginUserResult  {
    /** id */
    @ApiModelProperty(value = "用户ID", position = 1)
    private Long userId;

    /** 头像 */
    @ApiModelProperty(value = "头像", position = 2)
    private String avatar;

    /** 账号 */
    @ApiModelProperty(value = "账号", position = 3)
    private String username;

    /** 姓名 */
    @ApiModelProperty(value = "姓名", position = 4)
    private String name;

    /** 昵称 */
    @ApiModelProperty(value = "昵称", position = 5)
    private String nickname;

    /** 性别 */
    @ApiModelProperty(value = "性别", position = 6)
    private Integer gender;
    /** 用户状态 */
    @ApiModelProperty(value = "用户状态", position = 20)
    private Integer status;
    /** 权限码集合 */
    @ApiModelProperty(value = "权限码集合", position = 23, hidden = true)
    private List<String> permissionCodeList;

}
