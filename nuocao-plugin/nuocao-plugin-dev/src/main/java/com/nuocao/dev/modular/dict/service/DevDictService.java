package com.nuocao.dev.modular.dict.service;

import cn.hutool.core.lang.tree.Tree;

import com.nuocao.common.page.PageResult;
import com.nuocao.common.pojo.KeyValue;
import com.nuocao.dev.modular.dict.entity.DevDict;
import com.nuocao.dev.modular.dict.param.*;
import com.nuocao.dev.modular.dict.result.DevDictTreeResult;
import com.nuocao.sys.api.dto.SysOrgTreeResult;

import java.util.List;

/**
 * 字典Service接口
 **/
public interface DevDictService {

    /**
     * 获取字典分页
     */
    PageResult<DevDict> page(DevDictPageParam devDictPageParam);

    /**
     * 获取字典列表
     */
    List<DevDict> list(DevDictListParam devDictListParam);

    /**
     * 获取字典树
d     */
    List<DevDictTreeResult> tree(String category);

    /**
     * 添加字典
     */
    void add(DevDictAddParam devDictAddParam);

    /**
     * 编辑字典
     */
    void edit(DevDictEditParam devDictEditParam);

    /**
     * 删除字典
     */
    void deleteByIds(List<Long> ids);

    /**
     * 获取字典详情
     */
    DevDict detail(DevDictIdParam devDictIdParam);

    /**
     * 获取字典详情
     */
    DevDict queryEntity(Long dictId);

    /**
     * 得到值列表
     * @param parentKey
     * @param values
     * @return
     */
     List<String> getLabelList(String parentKey, List<String> values);


    /**
     * 得到value,lable键值对
     * @param parentKey
     * @return
     */
    List<KeyValue<String,String>> getValueLabelList(String parentKey);

    /**
     * 创建tree
     * @param dictList
     * @return
     */
    List<DevDictTreeResult> buildTreeByList(List<DevDict> dictList);

}
