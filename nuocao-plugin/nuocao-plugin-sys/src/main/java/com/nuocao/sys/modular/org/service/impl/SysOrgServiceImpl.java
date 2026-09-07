package com.nuocao.sys.modular.org.service.impl;


import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.nuocao.common.cache.CacheUtils;
import com.nuocao.common.constants.Constants;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.mapper.LambdaQueryWrapperX;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.utils.*;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.data.access.datascope.annotation.DataScopeIgnore;
import com.nuocao.data.access.tenant.context.TenantContext;
import com.nuocao.sys.api.SysUserApi;
import com.nuocao.sys.api.dto.SysOrgTreeResult;
import com.nuocao.sys.modular.org.convert.SysOrgConvert;
import com.nuocao.sys.modular.org.entity.SysOrg;
import com.nuocao.sys.modular.org.mapper.SysOrgMapper;
import com.nuocao.sys.modular.org.pojo.param.SysOrgAddParam;
import com.nuocao.sys.modular.org.pojo.param.SysOrgEditParam;
import com.nuocao.sys.modular.org.pojo.param.SysOrgIdParam;
import com.nuocao.sys.modular.org.pojo.param.SysOrgPageParam;
import com.nuocao.sys.modular.org.pojo.result.SysOrgResult;
import com.nuocao.sys.modular.org.service.SysOrgService;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 组织Service接口实现类
 *
 **/
@Component
@Slf4j
public class SysOrgServiceImpl implements SysOrgService {


    //将组织列表缓存，方便后续调用,需要分租户存储
    private static final String SYS_ORG_TENANT_LIST_CACHE_KEY = "sys:org:list:{}";
    //组织名称缓存
    private static final String SYS_ORG_NAME_CACHE_KEY = "sys:org:name:{}";
    @Inject
    private SysOrgMapper sysOrgMapper;
    @Inject
    private SysUserApi sysUserApi;
    @Inject
    private CacheUtils cacheUtils;

    @Override
    public PageResult<SysOrg> page(SysOrgPageParam sysOrgPageParam) {
        LambdaQueryWrapperX<SysOrg> query = new LambdaQueryWrapperX<>();
        query.likeIfPresent(SysOrg::getAncestors, ConvertUtils.toStr(sysOrgPageParam.getParentId()))
                .likeIfPresent(SysOrg::getOrgName,sysOrgPageParam.getOrgName());
        return sysOrgMapper.selectPage(sysOrgPageParam,query);
    }

    @Override
    public List<SysOrgTreeResult> tree() {
        //得到所部门的tree
        List<SysOrgTreeResult> orgList = sysOrgMapper.selectListByQueryAs(new QueryWrapper(),SysOrgTreeResult.class);
        return buildTree(orgList);
    }

    @Override
    @DataScopeIgnore //忽略数据权限，返回所有
    public List<SysOrgTreeResult> treeAll() {
        //得到所部门的tree
        List<SysOrgTreeResult> orgList = sysOrgMapper.selectListByQueryAs(new QueryWrapper(),SysOrgTreeResult.class);
        return buildTree(orgList);
    }

    @Override
    public List<SysOrgTreeResult> buildTreeByList(List<SysOrg> orgList){
        return buildTree(SysOrgConvert.INSTANCE.convertTreeResultList(orgList));
    }

    /**
     * 创建组织tree
     * @param orgList
     * @return
     */
    private List<SysOrgTreeResult> buildTree(List<SysOrgTreeResult> orgList) {
        //无数据返回空
        if(orgList==null || orgList.isEmpty()) return new ArrayList<>();
        //给菜单按顺序排列
        orgList.sort(Comparator.comparing(SysOrgTreeResult::getSortCode).thenComparing(SysOrgTreeResult::getId));
        Long startTime = LocalDateTimeUtils.currentTimestamp();
        log.debug("构建最tree开始：{}",startTime);
        //菜单集合
        //使用map定义子菜单，循环一次
        Map<Long,SysOrgTreeResult> idToOrgMap =new HashMap<>();
        for (SysOrgTreeResult menu : orgList) {
            menu.setChildren(new ArrayList<>()); //保证有内容
            idToOrgMap.put(menu.getId(), menu);
        }
        //存储根节点，最后只返回根节点就小姑娘
        List<SysOrgTreeResult> rootList = new ArrayList<SysOrgTreeResult>();

        for (SysOrgTreeResult org:
                orgList) {
            Long parentId = org.getParentId();
            SysOrgTreeResult parent = idToOrgMap.get(parentId);
            //存在上级，添加到上级子菜单
            if (parent != null) {
                parent.getChildren().add(org);
            } else {
                // 顶级节点（即不在这批过滤后的数据里的父节点）
                rootList.add(org);
            }
        }
        log.debug("构建组织tree完成：{}ms",LocalDateTimeUtils.currentTimestamp()-startTime);

        return rootList;
    }

    @Override
    public Long add(SysOrgAddParam sysOrgAddParam) {
        SysOrg sysOrg = SysOrgConvert.INSTANCE.convert(sysOrgAddParam);
        validInfo(sysOrg);
        sysOrgMapper.insert(sysOrg);
        return sysOrg.getId();
    }

    @Override
    public Long edit(SysOrgEditParam sysOrgEditParam) {
        SysOrg sysOrg = SysOrgConvert.INSTANCE.convert(sysOrgEditParam);
        validInfo(sysOrg);
        sysOrgMapper.updateById(sysOrg);
        return sysOrg.getId();
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)) return;
        //得到所有要删除的信息
        List<SysOrg> orgList = sysOrgMapper.selectListByIds(ids);
        for (SysOrg org:
             orgList) {
            //得到素有下级ID
            List<Long> childIds = new ArrayList<>(getOrgChildIds(org.getId()));
            if(CollectionUtils.isNotEmpty(childIds)) {
                throw new CommonExceptionI18n("sys.org.delete.existsChildren",org.getOrgName());
            }
            //将自己加进去，查询包含的信息，比如说用户
            childIds.add(org.getId());

            if(CollectionUtils.isNotEmpty(sysUserApi.getUserIdListByOrgIdList(childIds))){
                throw new CommonExceptionI18n("sys.org.delete.existsUser",org.getOrgName());
            }
        }

        sysOrgMapper.deleteBatchByIds(orgList.stream().map(SysOrg::getId).collect(Collectors.toList()));
    }

    @Override
    public SysOrgResult detail(SysOrgIdParam sysOrgIdParam) {
        return SysOrgConvert.INSTANCE.convertResult(getById(sysOrgIdParam.getId()));
    }

    @Override
    public SysOrg queryEntity(Long id) {
        SysOrg sysOrg=getById(id);
        CommonExceptionI18n.notNull(sysOrg,"sys.org.notExists",id);
        return sysOrg;
    }

    @Override
    public SysOrg getById(Long orgId) {
        return sysOrgMapper.selectOneById(orgId);
    }

    @Override
    @DataScopeIgnore
    public List<SysOrg> getAllOrgList() {
        return sysOrgMapper.selectList();
    }


    @Override
    public List<SysOrgTreeResult> orgTreeSelector() {
        return tree();
    }

    @Override
    public List<SysOrgTreeResult> orgListSelector(Long parentId) {
        LambdaQueryWrapperX<SysOrg> query = new LambdaQueryWrapperX<>();
        query.eqIfPresent(SysOrg::getParentId,parentId);
        return sysOrgMapper.selectListByQueryAs(query,SysOrgTreeResult.class);
    }

    @Override
    public String getNameByCache(Long orgId) {
        String key = StrFormatUtils.doFormat(SYS_ORG_NAME_CACHE_KEY,orgId);
        String name = cacheUtils.get(key,String.class);
        if(name==null){
            name = ConvertUtils.toStr(sysOrgMapper.selectObjectByQueryAs(new LambdaQueryWrapperX<SysOrg>().selectX(SysOrg::getOrgName).eqX(SysOrg::getId,orgId),String.class),StringUtils.EMPTY);
            cacheUtils.setObject(key,name);
        }
        return name;
    }

    private void removeCache(Long orgId){
        cacheUtils.remove(StrFormatUtils.doFormat(SYS_ORG_NAME_CACHE_KEY,orgId));
    }

    @Override
    public List<Long> getOrgChildIds(Long orgId) {
        QueryWrapper query = new QueryWrapper().select(SysOrg::getId);
        query.like(SysOrg::getAncestors,orgId);
        return sysOrgMapper.selectObjectListByQueryAs(query,Long.class);
    }


    @Override
    @DataScopeIgnore
    public List<SysOrg> getAllListByCache(){
        String key = StrFormatUtils.doFormat(SYS_ORG_TENANT_LIST_CACHE_KEY, ConvertUtils.toLong(TenantContext.getCurrentTenant(),0L));

        //得到数据
        List<SysOrg> orgList = cacheUtils.getList(key);
        if(orgList==null){
            orgList = sysOrgMapper.selectList();
            cacheUtils.setObject(key,orgList,60*60*24); //缓存1天
        }
        return orgList;
    }

    @Override
    public void clearDirectorByUserIds(List<Long> userIds) {
        if(CollectionUtils.isEmpty(userIds)) return;
        UpdateChain.of(SysOrg.class).set(SysOrg::getDirectorUserId,0L)
                .in(SysOrg::getDirectorUserId,userIds).update();
    }

    @Override
    public List<SysOrg> selectListByIds(List<Long> orgIds) {
        if(StringUtils.isEmpty(orgIds)) return new ArrayList<>();
        return sysOrgMapper.selectListByIds(orgIds);
    }

    /**
     * 验证信息更新项，并填充基础内容
     * @param info
     */
    private void validInfo(SysOrg info){

        info.setParentId(ConvertUtils.toLong(info.getParentId(),0L));

        SysOrg temp = sysOrgMapper.selectOne(SysOrg::getOrgName,info.getOrgName(),SysOrg::getParentId,info.getParentId());
        if(temp!=null && !temp.getId().equals(info.getId())){
            throw new CommonExceptionI18n("sys.org.name.exist",info.getOrgName());
        }

        temp = sysOrgMapper.selectOne(SysOrg::getOrgCode,info.getOrgCode());
        if(temp!=null && !temp.getId().equals(info.getId())){
            throw new CommonExceptionI18n("sys.org.code.exist",info.getOrgCode());
        }


        if(NumberUtils.isGtZero(info.getParentId())){
            SysOrg parentOrg = sysOrgMapper.selectOneById(info.getParentId());
            CommonExceptionI18n.notNull(parentOrg,"sys.org.parent.notExists");
            if(info.getParentId().equals(info.getId())){
               throw new CommonExceptionI18n("sys.org.parent.nonSelfChildren");
            }
            //修改时避免选择自己的下级
            List<Long> childOrgIds = getOrgChildIds(info.getParentId());
            if(childOrgIds.contains(info.getParentId())){
                throw new CommonExceptionI18n("sys.org.parent.nonSelfChildren");
            }
            info.setAncestors(parentOrg.getAncestors() + Constants.SPLIT_STR + info.getParentId());
        }else{
            info.setAncestors("0");
        }


    }
}
