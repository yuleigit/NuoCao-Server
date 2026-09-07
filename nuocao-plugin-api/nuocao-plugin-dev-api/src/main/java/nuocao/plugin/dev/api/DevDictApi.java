package nuocao.plugin.dev.api;


import com.nuocao.common.pojo.KeyValue;

import java.util.List;

/**
 * 字典API
 */
public interface DevDictApi {

    /**
     * 根据值得到label列表
     * @param parentKey
     * @param values
     * @return
     */
    List<String> getLabelList(String parentKey,List<String> values);

    /**
     * 得到value，label键值对
     * @param parentKey
     * @return
     */
    List<KeyValue<String,String>> getValueLabelist(String parentKey);

    /**
     * 根据value得到label
     * @param parentKey 上级
     * @param value
     * @return
     */
    String getLabel(String parentKey,String value);

    /**
     *从缓存中得到label
     * @param parentKey
     * @param value
     * @return
     */
    String getLabelByCache(String parentKey,String value);

    /**
     * 移除缓存
     * @param parentKey
     * @param value
     */
    void removeLabelCache(String parentKey,String value);

}
