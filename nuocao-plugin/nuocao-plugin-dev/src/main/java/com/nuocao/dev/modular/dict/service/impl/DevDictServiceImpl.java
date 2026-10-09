package com.nuocao.dev.modular.dict.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.mybatisflex.core.query.QueryWrapper;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.mapper.LambdaQueryWrapperX;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.KeyValue;
import com.nuocao.common.utils.LocalDateTimeUtils;
import com.nuocao.common.utils.NumberUtils;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.dev.modular.dict.convert.DevDictConvert;
import com.nuocao.dev.modular.dict.entity.DevDict;
import com.nuocao.dev.modular.dict.enums.DevDictValueTypeEnum;
import com.nuocao.dev.modular.dict.mapper.DevDictMapper;
import com.nuocao.dev.modular.dict.param.*;
import com.nuocao.dev.modular.dict.result.DevDictTreeResult;
import com.nuocao.dev.modular.dict.service.DevDictService;
import lombok.extern.slf4j.Slf4j;
import com.nuocao.plugin.dev.api.DevDictApi;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 字典Service接口实现类
 **/
@Component
@Slf4j
public class DevDictServiceImpl implements DevDictService {

    private static final Long ROOT_PARENT_ID = 0L;

    @Inject
    private DevDictMapper mapper;

    @Inject
    private DevDictApi devDictApi;

    @Override
    public PageResult<DevDict> page(DevDictPageParam devDictPageParam) {
        QueryWrapper queryWrapper = buildQueryWrapper(devDictPageParam);

        return mapper.selectPage(devDictPageParam, queryWrapper);
    }

    @Override
    public List<DevDict> list(DevDictListParam devDictListParam) {
        QueryWrapper queryWrapper = buildQueryWrapper(DevDictConvert.INSTANCE.convertPageParam(devDictListParam));

        return mapper.selectListByQuery(queryWrapper);
    }

    @Override
    public List<DevDictTreeResult> tree(String category) {
        LambdaQueryWrapperX<DevDict> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eqIfPresent(DevDict::getCategory,category);
        queryWrapper.orderBy(DevDict::getSortCode);

        // 查询部分字段
        queryWrapper.selectX(DevDict::getId, DevDict::getParentId, DevDict::getCategory,
                DevDict::getDictLabel, DevDict::getDictValue,
                DevDict::getSortCode, DevDict::getValueType,
                DevDict::getColor
        );
        List<DevDict> devDictList = mapper.selectListByQuery(queryWrapper);
        return buildTreeByList(devDictList);
    }

    @Override
    public void add(DevDictAddParam devDictAddParam) {
        DevDict dict = DevDictConvert.INSTANCE.convert(devDictAddParam);
        validInfo(dict);
        mapper.insert(dict);
    }



    @Override
    public void edit(DevDictEditParam devDictEditParam) {
        //得到老数据
        DevDict oldDict = queryEntity(devDictEditParam.getId());
        DevDict dict = DevDictConvert.INSTANCE.convert(devDictEditParam);
        validInfo(dict);
        mapper.updateById(dict);
        devDictApi.removeLabelCache(oldDict.getCategory(), dict.getDictValue());
    }


    @Override
    public void deleteByIds(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)) return;
        //查询所有字典信息
        List<DevDict> devDictList = mapper.selectListByIds(ids);

        if(devDictList.isEmpty()) return;

//        DevDict tempDict = devDictList.stream().filter(o->o.getCategory().equals(DevDictCategoryEnum.SYS.getValue())).findFirst().orElse(null);
//
//        if(tempDict!=null){
//            throw new CommonExceptionI18n("dev.dict.sys.delete.disable",tempDict.getDictLabel());
//        }

        //得到所有子集
        List<Long> childrenIds = mapper.selectObjectListByQueryAs(new QueryWrapper().in(DevDict::getParentId,ids).select(DevDict::getId),Long.class);
        List<Long> allIds = new ArrayList<>(ids);

        if(CollectionUtils.isNotEmpty(childrenIds)){
            allIds.addAll(childrenIds);
        }
        // 删除
        mapper.deleteBatchByIds(allIds);
    }

    @Override
    public DevDict detail(DevDictIdParam devDictIdParam) {
        return this.queryEntity(devDictIdParam.getId());
    }

    @Override
    public DevDict queryEntity(Long dictId) {
        DevDict devDict = mapper.selectOneById(dictId);
        CommonExceptionI18n.notNull(devDict,"dev.dict.notExists", dictId);
        return devDict;
    }

    @Override
    public List<String> getLabelList(String parentKey, List<String> values) {
        List<KeyValue<String,String>> list = getValueLabelList(parentKey);

        return list==null || list.isEmpty()?new ArrayList<>():list.stream().filter(o->values.contains(o.getValue())).map(KeyValue::getKey).collect(Collectors.toList());
    }

    @Override
    public List<KeyValue<String, String>> getValueLabelList(String parentKey) {
        QueryWrapper dictQuery = new QueryWrapper();
        dictQuery
                .in(DevDict::getParentId, QueryWrapper.create().from(DevDict.class).select(DevDict::getId).eq(DevDict::getDictValue, parentKey))
                .orderBy(DevDict::getSortCode,true).orderBy(DevDict::getId,true);
        List<DevDict> list = mapper.selectListByQuery(dictQuery);
        return list == null || list.isEmpty() ? new ArrayList<>() : list.stream()
                .map(dict -> new KeyValue<>(dict.getDictValue(), dict.getDictLabel()))
                .collect(Collectors.toList());
    }

    @Override
    public List<DevDictTreeResult> buildTreeByList(List<DevDict> dictList) {
        if(dictList==null) return new ArrayList<>();
        return buildTree(DevDictConvert.INSTANCE.convertTreeResultList(dictList));
    }

    /**
     * 创建组织tree
     * @param orgList
     * @return
     */
    private List<DevDictTreeResult> buildTree(List<DevDictTreeResult> orgList) {
        //无数据返回空
        if(orgList==null || orgList.isEmpty()) return new ArrayList<>();
        //给菜单按顺序排列
        orgList.sort(Comparator.comparing(DevDictTreeResult::getSortCode).thenComparing(DevDictTreeResult::getId));
        Long startTime = LocalDateTimeUtils.currentTimestamp();
        log.debug("构建最dict - tree开始：{}",startTime);
        //菜单集合
        //使用map定义子菜单，循环一次
        Map<Long,DevDictTreeResult> idToDictMap =new HashMap<>();
        for (DevDictTreeResult menu : orgList) {
            menu.setChildren(new ArrayList<>()); //保证有内容
            idToDictMap.put(menu.getId(), menu);
        }
        //存储根节点，最后只返回根节点就小姑娘
        List<DevDictTreeResult> rootList = new ArrayList<DevDictTreeResult>();

        for (DevDictTreeResult org:
                orgList) {
            Long parentId = org.getParentId();
            DevDictTreeResult parent = idToDictMap.get(parentId);
            //存在上级，添加到上级子菜单
            if (parent != null) {
                parent.getChildren().add(org);
            } else {
                // 顶级节点（即不在这批过滤后的数据里的父节点）
                rootList.add(org);
            }
        }
        log.debug("构建组织dict-tree完成：{}ms",LocalDateTimeUtils.currentTimestamp()-startTime);

        return rootList;
    }

    /**
     * 数据添加、修改验证数据
     * @param info
     */
    private void validInfo(DevDict info){
        //验证的是否支持该类型

        DevDict temp = mapper.selectOne(DevDict::getParentId,info.getParentId(),DevDict::getDictLabel,info.getDictLabel());
        if(temp!=null && !temp.getId().equals(info.getId()))
        {
            throw new CommonExceptionI18n("dev.dict.label.repeat", info.getDictLabel(),temp.getCategory());
        }

        temp = mapper.selectOne(DevDict::getParentId,info.getParentId(),DevDict::getDictValue,info.getDictValue());
        if(temp!=null && !temp.getId().equals(info.getId()))
        {
            throw new CommonExceptionI18n("dev.dict.value.repeat", info.getDictValue(),temp.getCategory());
        }

        if(NumberUtils.isGtZero(info.getParentId())){
            DevDict parentDict = mapper.selectOneById(info.getParentId());
            CommonExceptionI18n.notNull(parentDict,"dev.dict.parent.notExists",info.getParentId());
            info.setValueType(info.getValueType()); //先这样写，其实子字典的valueType用不上，都以父级为准
        }else{
            info.setParentId(ROOT_PARENT_ID);
            if(StringUtils.isBlank(info.getValueType())){
                throw new CommonExceptionI18n("dev.dict.valueType.notBlank");
            }
            DevDictValueTypeEnum.validate(info.getValueType());
        }
    }


    /**
     * 组装查询条件
     * @param param
     * @return
     */
    private QueryWrapper buildQueryWrapper(DevDictPageParam param){
        LambdaQueryWrapperX<DevDict> queryWrapper = new LambdaQueryWrapperX<>();

        queryWrapper.eqIfPresent(DevDict::getCategory,param.getCategory());
        queryWrapper.select(DevDict::getId, DevDict::getParentId, DevDict::getCategory,
                DevDict::getDictLabel, DevDict::getValueType,
                DevDict::getDictValue, DevDict::getSortCode,
                DevDict::getColor
        );

        //检索关键词
        if(StringUtils.isNotBlank(param.getSearchKey())){
            queryWrapper.and(q->{
                q.like(DevDict::getDictLabel, param.getSearchKey());
                q.or(q1->{
                    q1.eq(DevDict::getDictValue, param.getSearchKey());
                });
            });
        }

        if (NumberUtils.isGtZero(param.getParentId())) {
            queryWrapper.and(q->{
                q.eq(DevDict::getParentId, param.getParentId());
                q.or(q1->{
                    q1.eq(DevDict::getId, param.getParentId());
                });
            });
        }


        if (!param.hasSortingFields()) {
            queryWrapper.orderBy(DevDict::getSortCode);
        }
        return  queryWrapper;
    }
}
