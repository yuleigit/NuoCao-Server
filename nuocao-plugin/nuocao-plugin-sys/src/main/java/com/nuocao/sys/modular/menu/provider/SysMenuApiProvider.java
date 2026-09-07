package com.nuocao.sys.modular.menu.provider;

import com.nuocao.common.exception.CommonException;
import com.nuocao.sys.api.SysMenuApi;
import com.nuocao.sys.api.dto.menu.SysMenuDTO;
import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;
import com.nuocao.sys.modular.menu.pojo.params.SysMenuTreeParam;
import com.nuocao.sys.modular.menu.service.SysMenuService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.List;

/**
 * 菜单API接口实现类
 **/
@Component
public class SysMenuApiProvider implements SysMenuApi {

    @Inject
    private SysMenuService sysMenuService;

    @Override
    public List<SysMenuTreeResultDTO> getMenuTree(Long topId) {
        SysMenuTreeParam param = new SysMenuTreeParam();
        param.setTopId(topId);
        return sysMenuService.tree(param);
    }

    @Override
    public String getTitlePathByCache(Long id) {
        return sysMenuService.getTitlePathByCache(id);
    }

    @Override
    public List<SysMenuDTO> getMenuDtoList(List<Long> menuIds) {
        return sysMenuService.getMenuDtoList(menuIds);
    }

    @Override
    public List<String> getMenuCodeList(List<Long> menuIds) {
        return sysMenuService.getMenuCodeList(menuIds);
    }

    @Override
    public Long addMenuForGen(Long parentId, String businessName, String modelName, String functionName, String path) {
        throw new CommonException("暂不支持自动创建");
    }

    @Override
    public Long addButtonFormGen(Long menuId, String businessName, String modelName, String functionName) {
        throw new CommonException("暂不支持自动创建");
    }
}
