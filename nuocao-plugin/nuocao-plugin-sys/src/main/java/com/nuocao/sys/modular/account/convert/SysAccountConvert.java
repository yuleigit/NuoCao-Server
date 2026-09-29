package com.nuocao.sys.modular.account.convert;

import com.nuocao.auth.api.dto.AccountLoginInfoDTO;
import com.nuocao.common.page.PageResult;
import com.nuocao.sys.api.dto.SysAccountDTO;
import com.nuocao.sys.modular.account.entity.SysAccount;
import com.nuocao.sys.modular.account.pojo.param.SysAccountAddParam;
import com.nuocao.sys.modular.account.pojo.param.SysAccountEditParam;
import com.nuocao.sys.modular.account.pojo.result.SysAccountDetailResult;
import com.nuocao.sys.modular.account.pojo.result.SysAccountPageResult;
import com.nuocao.sys.modular.account.pojo.result.SysAccountSimpleResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 系统账号 转换类
 **/
@Mapper
public interface SysAccountConvert {

    SysAccountConvert INSTANCE = Mappers.getMapper(SysAccountConvert.class);

     SysAccountSimpleResult convert(SysAccount bean);

     SysAccountDTO convertDto(SysAccount bean);
     AccountLoginInfoDTO convertLoginInfoDto(SysAccount bean);
     SysAccountSimpleResult convertSimple(SysAccount bean);

     SysAccountDetailResult convertDetail(SysAccount bean);

     PageResult<SysAccountPageResult>  convertPage(PageResult<SysAccount> page);

 SysAccount convert(SysAccountAddParam bean);
 SysAccount convert(SysAccountEditParam bean);
}
