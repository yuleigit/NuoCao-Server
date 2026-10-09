package com.nuocao.dev.core.client.file.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.dev.core.client.file.DevFileUtils;
import com.nuocao.dev.core.client.file.FileStorageClient;
import com.nuocao.plugin.dev.api.enums.file.DevFileBucketAuthEnum;
import com.nuocao.plugin.dev.api.enums.file.DevFileEngineTypeEnum;
import io.minio.*;
import io.minio.http.Method;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.nuocao.plugin.dev.api.DevConfigApi;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.core.handle.UploadedFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

/**
 * 阿里云文件客户端实现
 */
@Slf4j
@Component
public class MinIoFileStorageClientImpl implements FileStorageClient {

    //key配置key
    private final static String FILE_MINIO_ACCESS_KEY_KEY= "file_minio_access_key";
    //secret配置key
    private final static String FILE_MINIO_SECRET_KEY= "file_minio_secret";
    private final static String FILE_MINIO_END_POINT_KEY= "file_minio_end_point";
    private final static String FILE_MINIO_BUCKET_NAME_KEY= "file_minio_bucket_name";

    private final static String FILE_MINIO_BIND_DOMAIN_URL_KEY= "file_minio_bind_domain_url";

    // 内部配置类：仅服务于当前实现
    @Data
    private static class MinioConfig {
        private String accessKey;
        private String accessSecret;
        private String endpoint;
        private String bucketName;
        private String bindDomainUrl;
    }

    private static MinioClient client;

    private static MinioConfig config; // 所有的配置都在这里


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
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(bucketName).object(key).build();
            client.removeObject(removeObjectArgs);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public void copyFile(String originBucketName, String originFileKey, String newBucketName, String newFileKey) {
        validConfig();
        try {
            CopySource copySource = CopySource.builder().bucket(originBucketName).object(originFileKey).build();
            CopyObjectArgs copyObjectArgs = CopyObjectArgs.builder().source(copySource).bucket(newBucketName).object(newFileKey).build();
            client.copyObject(copyObjectArgs);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public boolean fileExists(String bucketName, String key) {
        validConfig();
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(bucketName).object(key).build();
            InputStream object = client.getObject(getObjectArgs);
            return !ObjectUtil.isEmpty(object);
        } catch (Exception e) {
            log.warn("MINIO fileExists error：{}",e.getMessage());
            return false;
        }
    }

    @Override
    public boolean bucketExist(String bucketName) {
        validConfig();
        try {
            BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
            return client.bucketExists(bucketExistsArgs);
        } catch (Exception e) {
            log.warn("MINIO bucketExist error：{}",e.getMessage());
            return false;
        }
    }

    @Override
    public void setBucketAcl(String bucketName, DevFileBucketAuthEnum devFileBucketAuthEnum) {
        setFileAcl(bucketName, "*", devFileBucketAuthEnum);
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
        return  config!=null && StringUtils.isNotBlank(config.getBucketName())
                && StringUtils.isNotBlank(config.getEndpoint())
                && StringUtils.isNotBlank(config.getAccessKey())
                && StringUtils.isNotBlank(config.getAccessSecret())
                && StringUtils.isNotBlank(config.getBindDomainUrl());
    }
    @Override
    @Init
    public void reloadConfig() {
        MinioConfig newConfig = new MinioConfig();
        DevConfigApi api = BeanUtils.getBean(DevConfigApi.class);

        newConfig.setAccessKey(api.getValueByKey(FILE_MINIO_ACCESS_KEY_KEY));
        newConfig.setAccessSecret(api.getValueByKey(FILE_MINIO_SECRET_KEY));
        newConfig.setEndpoint(api.getValueByKey(FILE_MINIO_END_POINT_KEY));
        newConfig.setBucketName(api.getValueByKey(FILE_MINIO_BUCKET_NAME_KEY));
        newConfig.setBindDomainUrl(api.getValueByKey(FILE_MINIO_BIND_DOMAIN_URL_KEY));

        config=newConfig;
        destroyClient(); //先销毁
        if(!isSetConfig()){
            return;
        }
        client = MinioClient.builder().endpoint(config.getEndpoint()).credentials(config.getAccessKey(), config.getAccessSecret()).build();
    }

    /**
     * 销毁操作的客户端
     */
    public void destroyClient() {

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
            JSONObject configObject = JSONUtil.createObj().set("Version", "2012-10-17");
            JSONArray statementArray = JSONUtil.createArray();
            JSONArray actionArray = JSONUtil.createArray();
            if(devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PUBLIC_READ)) {
                actionArray.put("s3:GetObject");
            }
            if(devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PUBLIC_READ_WRITE)) {
                actionArray.put("s3:GetObject");
                actionArray.put("s3:PutObject");
            }
            JSONObject statementObject = JSONUtil.createObj();
            statementObject.set("Effect", "Allow").set("Principal", JSONUtil.createObj().set("AWS", JSONUtil.createArray().put("*")))
                    .set("Action", actionArray).set("Resource", JSONUtil.createArray().put("arn:aws:s3:::" + bucketName + "/*"));
            statementArray.put(statementObject);
            configObject.set("Statement", statementArray);
            String config = JSONUtil.toJsonStr(configObject);
            SetBucketPolicyArgs setBucketPolicyArgs = SetBucketPolicyArgs.builder().bucket(bucketName).config(config).build();
            client.setBucketPolicy(setBucketPolicyArgs);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }


    @Override
    public String getFileAuthUrl(String bucketName, String key, Long timeoutMillis) {
        validConfig();
        try {
            // 先初始化 builder
            GetPresignedObjectUrlArgs.Builder builder = GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .object(key)
                    .method(Method.GET);
            if (timeoutMillis != null) {
                // 注意：expiry 的单位通常是秒 (seconds)，如果是毫秒记得转换
                // Minio 默认最大过期时间通常是 7 天
                builder.expiry((int) (timeoutMillis / 1000));
            }
            // 構建args
            GetPresignedObjectUrlArgs args = builder.build();
            return client.getPresignedObjectUrl(args);
        }catch (Exception ex){
            throw new CommonException(ex.getMessage());
        }
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
            PutObjectArgs putObjectArgs = PutObjectArgs.builder().bucket(bucketName).object(key)
                    .contentType(DevFileUtils.getFileContentType(key)).stream(byteArrayInputStream, bytes.length, -1).build();
            client.putObject(putObjectArgs);
        } catch (Exception e) {
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
            PutObjectArgs putObjectArgs = PutObjectArgs.builder().bucket(bucketName).object(key)
                    .contentType(DevFileUtils.getFileContentType(key)).stream(inputStream, inputStream.available(), -1).build();
            client.putObject(putObjectArgs);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        } finally {
            IoUtil.close(inputStream);
        }
    }



}
