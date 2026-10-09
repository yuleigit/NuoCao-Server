package com.nuocao.dev.core.client.file;

import com.nuocao.plugin.dev.api.enums.file.DevFileBucketAuthEnum;
import org.noear.solon.core.handle.UploadedFile;

import java.io.File;
import java.io.InputStream;

/**
 * 文件引擎接口，所有文件上传服务器都需要实现这个接口
 */
public interface FileStorageClient {
    /** 获取引擎类型标识 */
    String getType();

    /** 获取默认存储桶 */
    String getDefaultBucketName();


    /**
     * 存储file对象
     * @param bucketName
     * @param key 一般为存储桶中的文件路径，文件唯一标识
     * @param file 文件
     * @return
     */
    String storageFileReturnUrl(String bucketName, String key, File file);

    /**
     * 存储file对象
     * @param bucketName
     * @param key 一般为存储桶中的文件路径，文件唯一标识
     * @param multipartFile 上传文件
     * @return
     */
    String storageFileReturnUrl(String bucketName, String key, UploadedFile multipartFile);


    /** 存储文件并返回URL
     * @param bucketName
     * @param key 一般为存储桶中的文件路径，文件唯一标识
     * */
    String storageFileReturnUrl(String bucketName, String key, InputStream inputStream);

    /**
     * 存储文件，返回外网地址
     *
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt，一般为文件在桶下的存储路径
     * @param bytes      文件字节数组
     */
    String storageFileReturnUrl(String bucketName, String key, byte[] bytes);

    /** 删除文件 */
    void deleteFile(String bucketName, String key);

    /**
     * 拷贝文件
     *
     * @param originBucketName 源文件桶
     * @param originFileKey    源文件名称
     * @param newBucketName    新文件桶
     * @param newFileKey       新文件名称
     */
    void copyFile(String originBucketName, String originFileKey, String newBucketName, String newFileKey);
    /**
     * 判断文件是否存在
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt，一般为bucketName下的具体存储路径
     * @return
     */
    boolean fileExists(String bucketName, String key);

    /**
     * 查询存储桶是否存在
     *
     * @param bucketName 桶名称，请参见各引擎规则
     */
    boolean bucketExist(String bucketName);

    /**
     * 设置预定义策略
     * 预定义策略如公有读、公有读写、私有读
     */
    void setBucketAcl(String bucketName, DevFileBucketAuthEnum devFileBucketAuthEnum);

    /**
     * 设置文件策略
     * @param bucketName
     * @param key
     * @param devFileBucketAuthEnum
     */
    void setFileAcl(String bucketName,String key, DevFileBucketAuthEnum devFileBucketAuthEnum);

    /**
     * 获取带鉴权的文件路径
     * @param bucketName
     * @param key
     * @param timeoutMillis 过期时间
     * @return
     */
    String getFileAuthUrl(String bucketName,String key,Long timeoutMillis);

    String getFileAuthUrl(String bucketName,String key);
    /**
     * 是否已经载入配置
     * @return
     */
    boolean isReady();

    /**
     * 重载配置，一般在修改万配置文件后调用
     */
    void reloadConfig();

}
