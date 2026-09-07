package com.nuocao.sys.modular.menu.convert;

import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;
import com.nuocao.sys.modular.menu.entity.SysMenu;
import com.nuocao.sys.modular.menu.pojo.params.SysMenuAddParam;
import com.nuocao.sys.modular.menu.pojo.params.SysMenuEditParam;
import com.nuocao.sys.modular.menu.pojo.result.SysMenuDetailResult;
import com.nuocao.sys.modular.menu.pojo.result.SysMenuSelectorResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 系统菜单 转换类
 **/
@Mapper
public interface SysMenuConvert {

    SysMenuConvert INSTANCE = Mappers.getMapper(SysMenuConvert.class);


    SysMenu convert(SysMenuAddParam bean);

    SysMenu convert(SysMenuEditParam bean);

     SysMenuTreeResultDTO convertTreeResult(SysMenu bean);

     List<SysMenuTreeResultDTO> convertTreeResultList(List<SysMenu> bean);

     SysMenuDetailResult convertDetail(SysMenu bean);

     SysMenuSelectorResult convertSelectorResult(SysMenuTreeResultDTO bean);
     List<SysMenuSelectorResult> convertSelector(List<SysMenuTreeResultDTO> list);


}
