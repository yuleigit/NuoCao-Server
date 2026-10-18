package com.nuocao.sys.modular.role.provider;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.sys.api.SysRoleApi;
import com.nuocao.sys.api.dto.SysRoleDTO;
import com.nuocao.sys.modular.menu.service.SysMenuService;
import com.nuocao.sys.modular.role.service.SysRoleService;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色API接口提供者
 **/
@Component
public class SysRoleApiProvider implements SysRoleApi {

    @Inject
    private SysRoleService sysRoleService;


    @Override
    public String getNameById(Long roleId) {
        return StringUtils.join(sysRoleService.getNames(Arrays.asList(roleId)),"");
    }

    @Override
    public List<SysRoleDTO> getRoleListByUserId(Long userId) {
        return sysRoleService.getRoleListByUserId(userId);
    }

    @Override
    public String getRoleNamesUserId(Long userId) {
        return sysRoleService.getNamesByUserId(userId);
    }
}
