package com.nuocao.common.cache;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.cache.CacheService;

import java.util.List;
import java.util.function.Supplier;

/**
 * 缓存工具类
 * LEI
 *
 */
@Component
@Slf4j
public class CacheUtils {

    @Inject
    private CacheService cacheService;

    /**
     * 写入缓存
     * @param key key
     * @param val
     * @param expireSeconds
     */
    public <T> void setObject(String key,T val,int expireSeconds){
        cacheService.store(key,val,expireSeconds);
    }

    /**
     * 写入缓存
     * @param key
     * @param val
     * @param <T>
     */
    public <T> void setObject(String key,T val){
        setObject(key,val,0);
    }

    /**
     * 获取缓存对象
     * @param key
     * @return
     */
    public Object get(String key){
        return cacheService.get(key,Object.class);
    }

    /**
     * 获取缓存对象并转为指定类型
     * @param key
     * @param clazz
     * @return
     * @param <T>
     */
    public <T> T get(String key,Class<T> clazz ){
        return cacheService.get(key,clazz);
    }

    /**
     * 获取缓存，如果不存在则创建
     * @param key
     * @param clazz
     * @param supplier
     * @return
     * @param <T>
     */
    public <T> T getOrSetObject(String key, Class<T> clazz, Supplier<T> supplier){
        T obj =  get(key,clazz);
        //存在直接返回
        if(obj!=null){
            return obj;
        }
        //不存在创建
        obj = supplier.get();
        this.setObject(key,obj);
        return obj;
    }


    /**
     * 获取缓存列表，类型安全检测
     * @param key 缓存key
     * @param classz 如部位null则进行类型检测
     * @return
     * @param <T>
     */
    public <T> List<T> getList(String key,Class<T> classz) {
        Object cached= cacheService.get(key, List.class);
        if (cached instanceof List) {
            if(classz==null) {
                List<?> rawList = (List<?>) cached;

                // 类型检查
                for (Object item : rawList) {
                    if (item != null && !classz.isInstance(item)) {
                        log.warn("缓存类型不匹配，key: {}, 期望: {}, 实际: {}",
                                key, classz, item.getClass());
                        return null;
                    }
                }
            }
            // 简单的类型安全转换
            try {
                return (List<T>) cached;
            } catch (ClassCastException e) {
                log.warn("缓存类型不匹配，key: {}, 期望类型: List<{}>",key,classz==null?"?":classz.getSimpleName());
                return null;
            }
        }
        return null;
    }

    /**
     * 获取缓存信息，不进行安全检测
     * @param key
     * @return
     * @param <T>
     */
    public <T> List<T> getList(String key){
        return getList(key,null);
    }


    public <T> void  setList(String key,List<T> list,int expireSeconds){
        cacheService.store(key,list,expireSeconds);
    }

    public <T> void  setList(String key,List<T> list){
        setList(key,list,30*60);
    }

    /**
     * 获取列表缓存，如果不存在则创建（类型安全）
     * @param key 缓存键
     * @param classz 列表元素类型
     * @param supplier 列表数据提供者（当缓存不存在时调用）
     * @return 缓存或新创建的列表
     * @param <T> 列表元素类型
     */
    public <T> List<T> getOrSetList(String key, Class<T> classz, Supplier<List<T>> supplier){
        return getOrSetList(key,classz,supplier,30*60);
    }
    public <T> List<T> getOrSetList(String key, Class<T> classz, Supplier<List<T>> supplier,Integer expireSeconds) {
        // 尝试从缓存获取列表
        List<T> list = getList(key,classz);

        // 存在直接返回
        if (list != null && !list.isEmpty()) {
            return list;
        }

        // 不存在则创建
        list = supplier.get();

        // 如果获取到的列表不为空，则缓存
        if (list != null && !list.isEmpty()) {
            this.setObject(key, list,expireSeconds);
        }

        return list;
    }

    /**
     * 获取列表缓存，如果不存在则创建（类型安全）
     * @param key 缓存键
     * @param supplier 列表数据提供者（当缓存不存在时调用）
     * @return 缓存或新创建的列表
     * @param <T> 列表元素类型
     */
    public <T> List<T> getOrSetList(String key,  Supplier<List<T>> supplier) {
        return getOrSetList(key,supplier);
    }

    public void remove(String key){
        cacheService.remove(key);
    }

    public void removes(String... keys){
        for (String key:
             keys) {
            cacheService.remove(key);
        }
    }

    /**
     * 测试时移除key用，为了避免网络删掉，使用这个可快速定义到删除点
     * @param key
     */
    public void testRemove(String key){
        cacheService.remove(key);
    }

    /**
     * 判断 key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public Boolean hasKey(String key)
    {
        return cacheService.get(key,Object.class)!=null;
    }


}
