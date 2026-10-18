
package com.nuocao.sys.modular.user.pojo.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 用户结果集
 **/
@Getter
@Setter
public class SysUserResult {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    /** 头像 */
    @ApiModelProperty(value = "头像", position = 3)
    private String avatar;
    /** 账号 */
    @ApiModelProperty(value = "账号", position = 5)
    private String username;

    /** 姓名 */
    @ApiModelProperty(value = "姓名", position = 7)
    private String name;

    /** 昵称 */
    @ApiModelProperty(value = "昵称", position = 8)
    private String nickname;

    /** 性别 */
    @ApiModelProperty(value = "性别", position = 9)
    private Integer gender;

    /** 出生日期 */
    @ApiModelProperty(value = "出生日期", position = 11)
    private String birthday;

    /** 手机 */
    @ApiModelProperty(value = "手机", position = 24)
    private String phone;

    /** 邮箱 */
    @ApiModelProperty(value = "邮箱", position = 25)
    private String email;


    /** 组织id */
    @ApiModelProperty(value = "组织id", position = 33)
    private Long orgId;


}
