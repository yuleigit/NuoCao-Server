package com.nuocao.sys.api.dto;

import lombok.Data;

/**
 * 简单的用户数据,用于前端用户相关交互显示
 */
@Data
public class SysUserSampleResultDTO {

    private Long id;

    private String name="";

    private String avatar="";

    private String nickname="";


}
