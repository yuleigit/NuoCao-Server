package com.nuocao.sys.modular.menu.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.nuocao.common.cache.CacheUtils;
import com.nuocao.common.constants.Constants;
import com.nuocao.common.enums.CommonEnableStatusEnum;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.mapper.LambdaQueryWrapperX;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.utils.*;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.sys.api.dto.menu.SysMenuDTO;
import com.nuocao.sys.api.dto.menu.SysMenuTreeResultDTO;
import com.nuocao.sys.modular.menu.convert.SysMenuConvert;
import com.nuocao.sys.modular.menu.entity.SysMenu;
import com.nuocao.sys.modular.menu.enums.SysMenuTypeEnum;
import com.nuocao.sys.modular.menu.mapper.SysMenuMapper;
import com.nuocao.sys.modular.menu.pojo.params.*;
import com.nuocao.sys.modular.menu.pojo.result.SysMenuDetailResult;
import com.nuocao.sys.modular.menu.service.SysMenuButtonService;
import com.nuocao.sys.modular.menu.service.SysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Transaction;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 菜单Service接口实现类
 *
 **/
@Component
@Slf4j
public class SysMenuServiceImpl implements SysMenuService {


    private final static String MENU_NAME_EXISTS_CACHE_KEY_PREFIX="menu:exists:name:{}";
    private final static String MENU_TITLE_PATH_CACHE_KEY_PREFIX ="menu:title-path:{}";

    @Inject
    private SysMenuMapper sysMenuMapper;

    @Inject
    private SysMenuButtonService sysMenuButtonService;

    @Inject
    private CacheUtils cacheUtils;



    @Override
    public PageResult<SysMenu> page(SysMenuPageParam pageParam) {
        LambdaQueryWrapperX<SysMenu> query = new LambdaQueryWrapperX<>();
        query.eqIfPresent(SysMenu::getTopId,pageParam.getTopId());
        return sysMenuMapper.selectPage(pageParam,query);
    }

    @Override
    public List<SysMenuTreeResultDTO> tree(SysMenuTreeParam treeParam) {
        LambdaQueryWrapperX<SysMenu> query = new LambdaQueryWrapperX<>();
        query.eqIfPresent(SysMenu::getTopId,treeParam.getTopId());
        List<SysMenu> menuList = sysMenuMapper.selectListByQuery(query);
        this.fillParentMenuData(menuList);

        return buildTree(menuList);
    }

    @Override
    public List<SysMenuTreeResultDTO> treeMenu(SysMenuTreeParam treeParam) {
        LambdaQueryWrapperX<SysMenu> query = new LambdaQueryWrapperX<>();
        query.eqIfPresent(SysMenu::getTopId,treeParam.getTopId());
        query.neX(SysMenu::getType,SysMenuTypeEnum.BUTTON.getType());
        List<SysMenu> menuList = sysMenuMapper.selectListByQuery(query);
        this.fillParentMenuData(menuList);

        return buildTree(menuList);
    }

    @Override
    public List<SysMenuTreeResultDTO> treeByIds(List<Long> menuIds) {
        LambdaQueryWrapperX<SysMenu> query = new LambdaQueryWrapperX<>();
        query.inX(SysMenu::getId,menuIds);
        //得到所有菜单
        List<SysMenu> menuList = selectTreeMenuListByQuery(query);
        //去掉菜单中的按钮级
        //menuList=menuList.stream().filter(o->!o.getType().equals(SysMenuTypeEnum.BUTTON.getType())).collect(Collectors.toList());
        return buildTree(menuList);
    }

    @Override
    public List<SysMenuTreeResultDTO> treeSelector(SysMenuTreeParam treeParam) {
        LambdaQueryWrapperX<SysMenu> query = new LambdaQueryWrapperX<>();
        query.eqIfPresent(SysMenu::getTopId,treeParam.getTopId());
        query.ne(SysMenu::getType,SysMenuTypeEnum.BUTTON.getType()); //去掉按钮
        List<SysMenu> menuList = selectTreeMenuListByQuery(query);
        return buildTree(menuList);
    }


    @Override
    public List<SysMenuTreeResultDTO> buildTree(List<SysMenu> menuList) {
        //无数据返回空
        if(menuList==null || menuList.isEmpty()) return new ArrayList<>();
        //给菜单按顺序排列
        menuList.sort(Comparator.comparing(SysMenu::getSortCode).thenComparing(SysMenu::getId));
        Long startTime = DateUtils.getTimestampMillis();
        log.debug("构建菜单tree开始：{}",startTime);
        //菜单集合
        List<SysMenuTreeResultDTO> dtoList = SysMenuConvert.INSTANCE.convertTreeResultList(menuList);
        //使用map定义子菜单，循环一次
        Map<Long,SysMenuTreeResultDTO> idToMenuMap =new HashMap<>();
        for (SysMenuTreeResultDTO menu : dtoList) {
            //param.setChildren(new ArrayList<>()); //保证有内容
            idToMenuMap.put(menu.getId(), menu);
        }
        //存储根节点，最后只返回根节点就小姑娘
        List<SysMenuTreeResultDTO> rootList = new ArrayList<SysMenuTreeResultDTO>();

        for (SysMenuTreeResultDTO menu:
                dtoList) {
            Long parentId = menu.getParentId();
            SysMenuTreeResultDTO parent = idToMenuMap.get(parentId);
            //存在上级，添加到上级子菜单
            if (parent != null) {
                if(parent.getChildren()==null){
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(menu);
            } else {
                // 顶级节点（即不在这批过滤后的数据里的父节点）
                rootList.add(menu);
            }
        }
        log.debug("构建菜单tree完成：{}ms",DateUtils.getTimestampMillis()-startTime);


        return rootList;
    }

    /**
     * 填充所有父级菜单
     * @param menuList
     * @return
     */
    private void fillParentMenuData(List<SysMenu> menuList) {
        if(CollectionUtils.isEmpty(menuList)){
            return ;
        }
        List<SysMenu> parentSysMenuList = menuList.stream().filter(distinctByKey(SysMenu::getParentId)).collect(Collectors.toList());
        List<Long> menuIds = menuList.stream().map(SysMenu::getId).collect(Collectors.toList());
        //存储所有父级ID
        List<Long> parentIds = null;
        if (CollUtil.isNotEmpty(parentSysMenuList)) {
            parentIds = CollUtil.newArrayList();
            for (SysMenu menu : parentSysMenuList) {
                //不是
                if (!menu.getParentId().equals(0L) && !menuIds.contains(menu.getParentId())) {
                    parentIds.add(menu.getParentId());
                }
            }
        }
        //又不存在的父级，则查询并添加到list中
        if (CollUtil.isNotEmpty(parentIds)) {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.in(SysMenu::getId, parentIds);
            List<SysMenu> parentSysMenus = sysMenuMapper.selectListByQuery(queryWrapper);
            if (CollUtil.isNotEmpty(parentSysMenus)) {
                this.fillParentMenuData(parentSysMenus);
                menuList.addAll(parentSysMenus);
            }
        }
    }

    private List<SysMenu> selectTreeMenuListByQuery(QueryWrapper query){
        List<SysMenu> menuList = sysMenuMapper.selectListByQuery(query);
        this.fillParentMenuData(menuList);
        return menuList;
    }


    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    @Override
    @Transaction
    public Long add(SysMenuAddParam addParam) {
        SysMenu info = SysMenuConvert.INSTANCE.convert(addParam);
        validatdInfo(info);
        sysMenuButtonService.validButtons(addParam.getButtons());
        sysMenuMapper.insert(info);
        clearMenuCache(info.getId(),info.getName());
        sysMenuButtonService.updateSyncMenuButtons(info.getId(),addParam.getButtons());
        return info.getId();
    }

    @Override
    @Transaction
    public Long edit(SysMenuEditParam sysMenuEditParam) {
        SysMenu info = SysMenuConvert.INSTANCE.convert(sysMenuEditParam);
        validatdInfo(info);
        sysMenuButtonService.validButtons(sysMenuEditParam.getButtons());
        sysMenuMapper.updateById(info);
        sysMenuButtonService.updateSyncMenuButtons(info.getId(),sysMenuEditParam.getButtons());
        clearMenuCache(info.getId(),info.getName());
        updateSyncChildMenuAncestors(info.getId(),info.getAncestors());
        return info.getId();
    }

    void updateSyncChildMenuAncestors(Long parentId,String parentAncestors){

        //找到所有下级列表，不光直属的，时所有下级菜单和按钮
        List<SysMenu> childs = sysMenuMapper.selectListByQuery(new LambdaQueryWrapperX<SysMenu>().likeX(SysMenu::getAncestors,parentId));
        for (SysMenu menu:
             childs) {
            SysMenu up = new SysMenu();
            up.setId(menu.getId());
            if(parentId.equals(menu.getParentId())){
                up.setAncestors(parentAncestors+Constants.SPLIT_STR+parentId);
                menu.setAncestors(up.getAncestors());
            }else {
                SysMenu parentMenu = childs.stream().filter(o->o.getId().equals(menu.getParentId())).findFirst().orElse(null);
                if(parentMenu==null){
                    up.setAncestors(parentAncestors+Constants.SPLIT_STR+parentId);
                }else{
                    up.setAncestors(parentMenu.getAncestors()+Constants.SPLIT_STR+parentMenu.getParentId());
                }
                menu.setAncestors(up.getAncestors());
            }
            sysMenuMapper.updateById(up);
        }

    }


    @Override
    public void deleteByIds(List<Long> menuIds) {
        if(CollectionUtils.isEmpty(menuIds)){
            return;
        }
        //查询所有菜单
        List<SysMenu> deleteMenuList = sysMenuMapper.selectList(SysMenu::getId,menuIds);
        if(CollectionUtils.isEmpty(deleteMenuList)){
            return;
        }

        for (SysMenu menu:
             deleteMenuList) {
            List<SysMenu> childrenList = sysMenuMapper.selectList(SysMenu::getParentId,menu.getId());
            if(CollectionUtils.isNotEmpty(childrenList.stream().filter(o->!o.getType().equals(SysMenuTypeEnum.BUTTON.getType())).collect(Collectors.toList()))){
                CommonExceptionI18n.notEmpty(childrenList,"sys.menu.delete.existsChildren",menu.getName());
            }
        }
        List<Long> deleteIds= CollectionUtils.convertList(deleteMenuList,SysMenu::getId);
        sysMenuMapper.deleteBatchByIds(deleteIds);
        //清理缓存
        for (SysMenu menu:
                deleteMenuList) {
            clearMenuCache(menu.getId(),menu.getName());
        }

        sysMenuButtonService.deleteMenuButton(deleteIds);
    }

    @Override
    public void delete(List<SysMenuIdParam> menuIdParams) {
        deleteByIds(menuIdParams.stream().map(SysMenuIdParam::getId).collect(Collectors.toList()));
    }

    @Override
    public SysMenuDetailResult detail(SysMenuIdParam menuIdParam) {
        SysMenuDetailResult result= SysMenuConvert.INSTANCE.convertDetail(sysMenuMapper.selectOneById(menuIdParam.getId()));
        result.setParentTitle(getTitlePathByCache(result.getParentId()));
        result.setButtons(sysMenuButtonService.selectMenuButton(result.getId()));
        return result;
    }

    @Override
    public SysMenu queryEntity(Long menuId) {
        SysMenu menu = sysMenuMapper.selectOneById(menuId);
        CommonExceptionI18n.notNull(menu,"sys.menu.notExists",menu.getId());
        return menu;
    }

    @Override
    public List<Long> getMenuChildIds(Long menuId) {
        QueryWrapper query = new QueryWrapper();
        query.eq(SysMenu::getId,menuId);
        return sysMenuMapper.selectObjectListByQueryAs(query,Long.class);
    }

    @Override
    public List<SysMenuDTO> getMenuDtoList(List<Long> menuIds) {
        if(CollectionUtils.isEmpty(menuIds)) return new ArrayList<>();
        LambdaQueryWrapperX<SysMenu> query = new LambdaQueryWrapperX<>();
        query.select(SysMenu::getId,SysMenu::getCode,SysMenu::getTitle,SysMenu::getType);
        query.in(SysMenu::getId,menuIds);
        return sysMenuMapper.selectListByQueryAs(query,SysMenuDTO.class);
    }

    @Override
    public List<String> getMenuCodeList(List<Long> menuIds) {
        if(CollectionUtils.isEmpty(menuIds)) return new ArrayList<>();
        LambdaQueryWrapperX<SysMenu> query = new LambdaQueryWrapperX<>();
        query.select(SysMenu::getCode);
        query.in(SysMenu::getId,menuIds)
                .ne(SysMenu::getCode,Constants.EMPTY_STR);
        return sysMenuMapper.selectObjectListByQueryAs(query,String.class).stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    @Override
    public List<String> getAllMenuCodeList() {
        return sysMenuMapper.selectObjectListByQueryAs(
                new LambdaQueryWrapperX<SysMenu>().eqX(SysMenu::getEnable, CommonEnableStatusEnum.ENABLE.getValue())
                        .neX(SysMenu::getCode,"")
                        .selectX(SysMenu::getCode)
                ,String.class);
    }

    @Override
    public boolean existsName(String name) {
        return cacheUtils.getOrSetObject(StringUtils.format(MENU_NAME_EXISTS_CACHE_KEY_PREFIX,name),Boolean.class,()->{
            return sysMenuMapper.exists(SysMenu::getName,name);
        }) ;
    }

    @Override
    public String getTitlePathByCache(Long id) {
        return cacheUtils.getOrSetObject(StringUtils.format(MENU_NAME_EXISTS_CACHE_KEY_PREFIX,id),String.class,()->{
            //得到菜单信息
            SysMenu menu =sysMenuMapper.selectOneById(id);
            if(menu==null) return "";
            //得到所有上级
            List<Long> parentIds = Arrays.stream(ConvertUtils.toLongArray(menu.getAncestors())).filter(NumberUtils::isGtZero).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(parentIds)){
                return menu.getTitle();
            }
            //获得所有上级
            List<SysMenu> parentList = sysMenuMapper.selectListByQuery(new LambdaQueryWrapperX<SysMenu>()
                    .selectX(SysMenu::getId,SysMenu::getTitle).inX(SysMenu::getId,parentIds));
            String titlePath = "";
            for (Long pmId:
            parentIds) {
                SysMenu pm = parentList.stream().filter(o->o.getId().equals(pmId)).findFirst().orElse(null);
                if(pm!=null){
                    titlePath+=pm.getTitle()+StrUtil.SLASH;
                }
            }
            return titlePath+menu.getTitle();
        });
    }


    @Override
    public List<Long> getAllMenuIdList() {
        return sysMenuMapper.selectObjectListByQueryAs(
                new LambdaQueryWrapperX<SysMenu>().eqX(SysMenu::getEnable, CommonEnableStatusEnum.ENABLE.getValue())
                        .selectX(SysMenu::getId)
        ,Long.class);
    }



    /**
     * 验证新增、修改参数
     * @param info
     */
    private void validatdInfo(SysMenu info){
        if(info.getTitle().contains(StrUtil.DASHED)) {
            throw new CommonExceptionI18n("sys.menu.title.cannotContainChar");
        }
        if (SysMenuTypeEnum.MENU.getType().equals(info.getType()) || SysMenuTypeEnum.DIR.getType().equals(info.getType())) {
            CommonExceptionI18n.notBlank(info.getName(),"sys.menu.name.notBlank");
            CommonExceptionI18n.notBlank(info.getComponent(),"sys.menu.component.notBlank");
        } else if (SysMenuTypeEnum.IFRAME.getType().equals(info.getType()) ||
                SysMenuTypeEnum.LINK.getType().equals(info.getType())) {
            info.setComponent(null);
            CommonExceptionI18n.notBlank(info.getComponent(),"sys.menu.href.notBlank");
            CommonExceptionI18n.notBlank(info.getCode(),"sys.menu.code.notBlank");
        }if(SysMenuTypeEnum.BUTTON.getType().equals(info.getType()))
        {
            CommonExceptionI18n.notBlank(info.getCode(),"sys.menu.code.notBlank");
        }

        //验证上级菜单
        if(NumberUtils.isGtZero(info.getParentId())) {
            if(info.getParentId().equals(info.getId())) {
                throw new CommonExceptionI18n("sys.menu.parent.nonSelfChildren", info.getTitle());
            }
            SysMenu parentMenu = sysMenuMapper.selectOneById(info.getParentId());
            CommonExceptionI18n.notNull(parentMenu, "sys.menu.parent.NotExists");

            //如果是修改，看是不是改到自己下级了
            if(info.getId()!=null) {
                List<Long> ancestors = Arrays.asList(ConvertUtils.toLongArray(parentMenu.getAncestors()));
                if(ancestors.contains(info.getId())){
                    throw new CommonExceptionI18n("sys.menu.parent.nonSelfChildren");
                }
            }
            info.setTopId(parentMenu.getTopId());
            info.setAncestors(parentMenu.getAncestors()+ Constants.SPLIT_STR+info.getParentId());
        }
        else{
            info.setParentId(0L);
            info.setTopId(0L);
            info.setAncestors("0");
        }


        //验证菜单是否重复
        if(StringUtils.isNotBlank(info.getCode())) {
            SysMenu temp = sysMenuMapper.selectOne(SysMenu::getCode, info.getCode());
            if(temp!=null && !temp.getId().equals(info.getId())){
                throw new CommonExceptionI18n("sys.menu.code.nonRepeat",info.getCode());
            }
        }
        if(StringUtils.isNotBlank(info.getName())) {
            SysMenu temp2 = sysMenuMapper.selectOne(SysMenu::getName, info.getName());
            if (temp2 != null && !temp2.getId().equals(info.getId())) {
                throw new CommonExceptionI18n("sys.menu.name.nonRepeat", info.getName());
            }
        }
        if(info.getId()==null){
            if(info.getI18nKey()==null){
                info.setI18nKey(StringUtils.EMPTY);
            }
        }
        //info.setActiveMenu(info.getName());


    }

    /**
     * 清理菜单缓存
     * @param menuId
     * @param name
     */
    private void clearMenuCache(Long menuId,String name)
    {
        cacheUtils.removes(
                StringUtils.format(MENU_NAME_EXISTS_CACHE_KEY_PREFIX,name),
                StringUtils.format(MENU_TITLE_PATH_CACHE_KEY_PREFIX,menuId)
        );
    }

}
