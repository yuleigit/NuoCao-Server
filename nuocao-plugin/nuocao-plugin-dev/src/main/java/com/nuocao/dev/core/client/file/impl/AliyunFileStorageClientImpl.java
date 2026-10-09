package com.nuocao.dev.core.client.file.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import com.alibaba.excel.util.StringUtils;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.dev.core.client.file.DevFileUtils;
import com.nuocao.dev.core.client.file.FileStorageClient;
import com.nuocao.plugin.dev.api.enums.file.DevFileBucketAuthEnum;
import com.nuocao.plugin.dev.api.enums.file.DevFileEngineTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.nuocao.plugin.dev.api.DevConfigApi;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.core.handle.UploadedFile;

import java.io.*;
import java.net.URL;
import java.util.Date;

/**
 * 阿里云文件客户端实现
 */
@Slf4j
@Component
public class AliyunFileStorageClientImpl implements FileStorageClient {

    //key配置key
    private final static String FILE_ALIYUN_ACCESS_KEY_ID_KEY= "file_aliyun_access_key_id";
    //secret配置key
    private final static String FILE_ALIYUN_ACCESS_KEY_SECRET_KEY= "file_aliyun_access_key_secret";
    private final static String FILE_ALIYUN_END_POINT_KEY= "file_aliyun_end_point";
    private final static String FILE_ALIYUN_BUCKET_NAME_KEY= "file_aliyun_bucket_name";

    private final static String FILE_ALIYUN_BIND_DOMAIN_URL_KEY= "file_aliyun_bind_domain_url";

    // 内部配置类：仅服务于当前实现
    @Data
    private static class AliyunConfig {
        private String accessKeyId;
        private String accessKeySecret;
        private String endpoint;
        private String bucketName;
        private String bindDomainUrl;
    }

    private static com.aliyun.oss.OSS client;

    private static AliyunConfig config; // 所有的配置都在这里


    @Override
    public String getType() {
        return DevFileEngineTypeEnum.ALIYUN.getValue();
    }

    @Override
    public String getDefaultBucketName() {
        return config.getBucketName();
    }

    @Override
    public String storageFileReturnUrl(String bucketName, String key, File file) {
        storageFile(bucketName, key, file);
        setFileAcl(bucketName, key, DevFileBucketAuthEnum.PUBLIC_READ);
        return getFileAuthUrl(bucketName, key);
    }

    @Override
    public String storageFileReturnUrl(String bucketName, String key, UploadedFile multipartFile) {
        storageFile(bucketName, key, multipartFile);
        setFileAcl(bucketName, key, DevFileBucketAuthEnum.PUBLIC_READ);
        return getFileAuthUrl(bucketName, key);
    }

    @Override
    public String storageFileReturnUrl(String bucketName, String key, InputStream inputStream) {
        storageFile(bucketName, key, inputStream);
        setFileAcl(bucketName, key, DevFileBucketAuthEnum.PUBLIC_READ);
        return getFileAuthUrl(bucketName, key);
    }

    @Override
    public String storageFileReturnUrl(String bucketName, String key, byte[] bytes) {
        storageFile(bucketName, key, bytes);
        setFileAcl(bucketName, key, DevFileBucketAuthEnum.PUBLIC_READ);
        return getFileAuthUrl(bucketName, key);
    }

    @Override
    public void deleteFile(String bucketName, String key) {
        validConfig();
        try{
            client.deleteObject(bucketName, key);
        } catch (OSSException | ClientException e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public void copyFile(String originBucketName, String originFileKey, String newBucketName, String newFileKey) {
        validConfig();
        try {
            client.copyObject(originBucketName, originFileKey, newBucketName, newFileKey);
        } catch (OSSException | ClientException e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public boolean fileExists(String bucketName, String key) {
        validConfig();
        try {
            return client.doesObjectExist(bucketName, key);
        } catch (OSSException | ClientException e) {
            log.warn("ALIYUN fileExists error：{}",e.getMessage());
            return false;
        }
    }

    @Override
    public boolean bucketExist(String bucketName) {
        validConfig();
        try {
            return client.doesBucketExist(bucketName);
        } catch (OSSException | ClientException e) {
            log.warn("ALIYUN bucketExist error：{}",e.getMessage());
            return false;
        }
    }

    @Override
    public void setBucketAcl(String bucketName, DevFileBucketAuthEnum devFileBucketAuthEnum) {
        try {
            client.setBucketAcl(bucketName, getAcl(devFileBucketAuthEnum));
        } catch (OSSException | ClientException e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public boolean isReady() {

        return isSetConfig() && client!=null;
    }
    private void validConfig(){
        if(!this.isReady()){
            throw new CommonException("Alibaba Cloud OOS configuration incomplete");
        }
    }

    private boolean isSetConfig(){
        return config!=null && StringUtils.isNotBlank(config.getBucketName())
                && StringUtils.isNotBlank(config.getEndpoint())
                && StringUtils.isNotBlank(config.getAccessKeyId())
                && StringUtils.isNotBlank(config.getAccessKeySecret())
                && StringUtils.isNotBlank(config.getBindDomainUrl());
    }

    @Override
    @Init
    public void reloadConfig() {
        AliyunConfig newConfig = new AliyunConfig();
        DevConfigApi api = BeanUtils.getBean(DevConfigApi.class);

        newConfig.setAccessKeyId(api.getValueByKey(FILE_ALIYUN_ACCESS_KEY_ID_KEY));
        newConfig.setAccessKeySecret(api.getValueByKey(FILE_ALIYUN_ACCESS_KEY_SECRET_KEY));
        newConfig.setEndpoint(api.getValueByKey(FILE_ALIYUN_END_POINT_KEY));
        newConfig.setBucketName(api.getValueByKey(FILE_ALIYUN_BUCKET_NAME_KEY));
        newConfig.setBindDomainUrl(api.getValueByKey(FILE_ALIYUN_BIND_DOMAIN_URL_KEY));

        config=newConfig;

        destroyClient(); //先销毁
        if(!isSetConfig()){
            return;
        }
        client = new OSSClientBuilder().build(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret());
    }

    /**
     * 销毁操作的客户端
     */
    public void destroyClient() {
        if(client!=null) {
            client.shutdown();
        }
        client=null;
    }


    //文件存储就方法实现

    /**
     * 设置文件访问权限管理
     *
     * @param bucketName     桶名称
     * @param key            唯一标示id，例如a.txt, doc/a.txt
     * @param devFileBucketAuthEnum 文件权限
     */
    @Override
    public void setFileAcl(String bucketName, String key, DevFileBucketAuthEnum devFileBucketAuthEnum) {
         validConfig();
         try {
             client.setObjectAcl(bucketName,key, getAcl(devFileBucketAuthEnum));
        } catch (OSSException | ClientException e) {
            throw new CommonException(e.getMessage());
        }
    }


    @Override
    public String getFileAuthUrl(String bucketName, String key, Long timeoutMillis) {
        validConfig();
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key, HttpMethod.GET);
        if(timeoutMillis!=null) {
            Date expirationDate = new Date(System.currentTimeMillis() + timeoutMillis);
            request.setExpiration(expirationDate);
        }
//        String bindDomain = config.getBindDomainUrl();
//        if(StringUtils.isNotBlank(bindDomain)){
//            return bindDomain+ StrUtil.SLASH+key;
//        }
        URL url;
        try {
            url = client.generatePresignedUrl(request);
        } catch (OSSException | ClientException e) {
            throw new CommonException(e.getMessage());
        }
        return url.toString();
    }

    @Override
    public String getFileAuthUrl(String bucketName, String key) {
        return getFileAuthUrl(bucketName,key,null);
    }

    /**
     * 存储文件，不返回地址
     *
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt,一般为桶中存储路径
     * @param file      文件
     */
    private void storageFile(String bucketName, String key, File file) {
        BufferedInputStream inputStream;
        try {
            inputStream = FileUtil.getInputStream(file);
        } catch (IORuntimeException e) {
            throw new CommonException("Get file stream exception, name is:{}", file.getName());
        }
        storageFile(bucketName, key, inputStream);
    }

    /**
     * 存储文件，不返回地址
     *
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt,一般为桶中存储路径
     * @param multipartFile      文件
     */
    private void storageFile(String bucketName, String key, UploadedFile multipartFile) {
        InputStream inputStream;
        inputStream = multipartFile.getContent();
        storageFile(bucketName, key, inputStream);
    }

    /**
     * 存储文件，不返回地址
     *
     * @param bucketName 桶名称
     * @param key        唯一标示id，例如a.txt, doc/a.txt,一般为桶中存储路径
     * @param bytes      文件字节数组
     */
    public void storageFile(String bucketName, String key, byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = null;
        validConfig();
        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(DevFileUtils.getFileContentType(key));
            client.putObject(bucketName, key, byteArrayInputStream, objectMetadata);
        } catch (OSSException | ClientException e) {
            throw new CommonException(e.getMessage());
        } finally {
            IoUtil.close(byteArrayInputStream);
        }
    }

    /**
     * 存储文件，不返回地址
     *
     * @param bucketName  桶名称
     * @param key         唯一标示id，例如a.txt, doc/a.txt,一般为桶中存储路径
     * @param inputStream 文件流
     */
    public void storageFile(String bucketName, String key, InputStream inputStream) {
        validConfig();
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(DevFileUtils.getFileContentType(key));
            client.putObject(bucketName, key, inputStream, objectMetadata);
        } catch (OSSException | ClientException e) {
            throw new CommonException(e.getMessage());
        } finally {
            IoUtil.close(inputStream);
        }
    }

    private CannedAccessControlList getAcl(DevFileBucketAuthEnum devFileBucketAuthEnum){
        if (devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PRIVATE)) {
            return CannedAccessControlList.Private;
        } else if (devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PUBLIC_READ)) {
            return CannedAccessControlList.PublicRead;
        } else if (devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PUBLIC_READ_WRITE)) {
            return CannedAccessControlList.PublicReadWrite;
        }
        return CannedAccessControlList.PublicRead;
    }


}
