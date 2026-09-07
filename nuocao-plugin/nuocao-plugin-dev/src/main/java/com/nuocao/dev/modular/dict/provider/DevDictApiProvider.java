package com.nuocao.dev.modular.dict.provider;

import cn.hutool.cache.CacheUtil;
import cn.hutool.core.util.StrUtil;
import com.nuocao.common.cache.CacheUtils;
import com.nuocao.common.constants.Constants;
import com.nuocao.common.pojo.KeyValue;
import com.nuocao.common.utils.ConvertUtils;
import com.nuocao.dev.modular.dict.service.DevDictService;
import nuocao.plugin.dev.api.DevDictApi;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.cache.CacheService;

import java.util.Arrays;
import java.util.List;

/**
 * 字典API接口实现类
 */
@Component
public class DevDictApiProvider implements DevDictApi {

    @Inject
    private DevDictService devDictService;

    @Inject
    private CacheUtils cacheUtils;

    @Override
    public List<String> getLabelList(String parentKey, List<String> values) {
        return  devDictService.getLabelList(parentKey,values);
    }

    @Override
    public List<KeyValue<String, String>> getValueLabelist(String parentKey) {
        return devDictService.getValueLabelList(parentKey);
    }

    @Override
    public String getLabel(String parentKey, String value){
        List<String> strList = getLabelList(parentKey, Arrays.asList(value));
        return strList==null || strList.isEmpty()?"": StrUtil.join(Constants.SPLIT_STR,strList);
    }

    @Override
    public String getLabelByCache(String parentKey, String value) {

        String key =getLabelCacheKey(parentKey,value);
        return cacheUtils.getOrSetObject(key,String.class,()->{
            String newLabel = ConvertUtils.toStr(getLabel(parentKey,value),"");
            cacheUtils.setObject(key,newLabel,60*60);
            return newLabel;
        });
    }

    @Override
    public void removeLabelCache(String parentKey, String value) {
        cacheUtils.remove(getLabelCacheKey(parentKey,value));
    }


    private String getLabelCacheKey(String parentKey, String value)
    {
        return "cache:dict_label:"+parentKey+":"+value;
    }
}
