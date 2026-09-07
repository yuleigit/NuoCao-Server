package com.nuocao.sys.modular.org.pojo.result;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nuocao.sys.api.utils.SysUserApiUtils;
import com.nuocao.sys.modular.org.pojo.SysOrgBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * 组织实体
 **/
@Getter
@Setter
public class SysOrgResult extends SysOrgBase {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private Long id;


    @JsonGetter("directorName")
    private String getDirectorName(){
        return SysUserApiUtils.getNameByCache(this.getDirectorUserId());
    }


}
