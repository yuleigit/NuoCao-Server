package nuocao.plugin.dev.api;

import java.util.Map;

/**
 * 系统配置接口
 */
public interface DevConfigApi {

    /**
     * 获取配置值
     * @param key
     * @return
     */
    String getValueByKey(String key);

    /**
     * 获取数字值
     * @param key
     * @return
     */
    Integer getIntegerValue(String key);

    /**
     * 获取数字值
     * @param key
     * @param defaultValue
     * @return
     */
   Integer getIntegerValue(String key,Integer defaultValue);

    /**
     * 获取布尔值
     * @param key
     * @param defaultValue
     * @return
     */
    Boolean getBooleanValue(String key,Boolean defaultValue);

    /**
     * 获取值并转为map对象
     * @param key
     * @return
     */
    Map<String,Object> getValueToMap(String key);

    /**
     * 验证是否为true
     * @param key
     * @return
     */
    boolean isTrueByKey(String key);



}
