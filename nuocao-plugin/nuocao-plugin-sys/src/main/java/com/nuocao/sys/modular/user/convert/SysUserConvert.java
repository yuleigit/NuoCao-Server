package com.nuocao.sys.modular.user.convert;


import com.nuocao.auth.api.dto.AuthLoginUserDTO;
import com.nuocao.common.enums.CommonEnableStatusEnum;
import com.nuocao.common.page.PageResult;
import com.nuocao.sys.api.dto.SysUserDTO;
import com.nuocao.sys.api.utils.SysOrgApiUtils;
import com.nuocao.sys.api.utils.SysUserApiUtils;
import com.nuocao.sys.modular.user.entity.SysUser;
import com.nuocao.sys.modular.user.pojo.param.SysUserAddParam;
import com.nuocao.sys.modular.user.pojo.param.SysUserEditParam;
import com.nuocao.sys.modular.user.pojo.param.SysUserExportParam;
import com.nuocao.sys.modular.user.pojo.param.SysUserPageParam;
import com.nuocao.sys.modular.user.pojo.result.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


/**
 * 系统用户 转换器
 */
@Mapper
public interface SysUserConvert {

    SysUserConvert INSTANCE = Mappers.getMapper(SysUserConvert.class);

    PageResult<SysUserPageResult> convertPage(PageResult<SysUser> page);
    SysUserPageResult convertPageResult(SysUser bean);

    SysUserDTO convertDto(SysUser bean);

    SaSysLoginUser convertLoginUser(SysUser bean);


    SysUser convert(SysUserAddParam param);
    SysUser convert(SysUserEditParam param);

    SysUserResult convert(SysUser user);

    SysUserPageParam convertQueryParam(SysUserExportParam param);

    /**
     * 转为导出数据
     * @param bean
     * @return
     */
    SysUserExportResult convertExportResultBean(SysUser bean);

    default SysUserExportResult convertExportResult(SysUser bean){
        SysUserExportResult result =convertExportResultBean(bean);
        if(result==null) return null;
        result.setOrgName(SysOrgApiUtils.getName(bean.getOrgId()));
        result.setRoleName(SysUserApiUtils.getRoleNames(bean.getId()));
        result.setStatus(CommonEnableStatusEnum.getCodeByValue(bean.getStatus()));
        return result;
    }



    /**
     * 转为登录用的租户用户列表
     * @param list
     * @return
     */
    List<AuthLoginUserDTO> convertAuthLoginUser(List<SysUserDTO> list);


    List<UserSelectorResult> convertSelectorResultList(List<SysUser> list);


    SysUserProfileResult convertProfile(SysUser bean);
}
