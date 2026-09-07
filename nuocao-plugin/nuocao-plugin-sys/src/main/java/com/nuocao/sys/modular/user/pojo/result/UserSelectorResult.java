package com.nuocao.sys.modular.user.pojo.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 用户选择器返回信息
 */
@Getter
@Setter
public class UserSelectorResult {
    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private Long id;

    /** 头像 */
    @ApiModelProperty(value = "头像", position = 3)
    private String avatar;

    /** 姓名 */
    @ApiModelProperty(value = "姓名", position = 7)
    private String name;

    /** 昵称 */
    @ApiModelProperty(value = "昵称", position = 8)
    private String nickname;

    /** 性别 */
    @ApiModelProperty(value = "性别", position = 9)
    private Integer gender;

    /** 组织ID */
    @ApiModelProperty(value = "组织ID", position = 9)
    private Long orgId;

    /** 性别 */
    @ApiModelProperty(value = "角色列表", position = 9)
    private List<String> roleNames;


    /** 性别 */
    @ApiModelProperty(value = "部门名称", position = 9)
    private String orgName;

}
