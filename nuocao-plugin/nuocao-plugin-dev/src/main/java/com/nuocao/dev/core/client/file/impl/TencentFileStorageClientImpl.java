package com.nuocao.dev.core.client.file.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.dev.core.client.file.DevFileUtils;
import com.nuocao.dev.core.client.file.FileStorageClient;
import com.nuocao.plugin.dev.api.enums.file.DevFileBucketAuthEnum;
import com.nuocao.plugin.dev.api.enums.file.DevFileEngineTypeEnum;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.CannedAccessControlList;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
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
import java.net.URL;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 阿里云文件客户端实现
 */
@Slf4j
@Component
public class TencentFileStorageClientImpl implements FileStorageClient {


    private static TransferManager transferManager;
    //key配置key
    //用户的 SecretId，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
    private final static String FILE_TENCENT_SECRET_ID_KEY = "file_tencent_secret_id";
    //secret配置key
    //用户的 SecretKey，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
    private final static String FILE_TENCENT_SECRET_KEY_KEY = "file_tencent_secret_key";
    //bucket 的地域，请参见 https://cloud.tencent.com/document/product/436/62
    private final static String FILE_TENCENT_REGION_ID_KEY= "file_tencent_region_id";
    private final static String FILE_TENCENT_BUCKET_NAME_KEY= "file_tencent_bucket_name";

    private final static String FILE_TENCENT_BIND_DOMAIN_URL_KEY= "file_tencent_bind_domain_url";

    // 内部配置类：仅服务于当前实现
    @Data
    private static class TencentConfig {
        private String secretId;
        private String secretKey;
        private String regionId;
        private String bucketName;
        private String bindDomainUrl;
    }

    private static COSClient client;

    private static TencentConfig config; // 所有的配置都在这里


    @Override
    public String getType() {
        return DevFileEngineTypeEnum.TENCENT.getValue();
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
        } catch (CosClientException e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public void copyFile(String originBucketName, String originFileKey, String newBucketName, String newFileKey) {
        validConfig();
        try {
            transferManager.copy(originBucketName, originFileKey, newBucketName, newFileKey);
        } catch (CosClientException e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public boolean fileExists(String bucketName, String key) {
        validConfig();
        try {
            return client.doesObjectExist(bucketName, key);
        } catch (CosClientException e) {
            log.warn("TENCENT fileExists error：{}",e.getMessage());
            return false;
        }
    }

    @Override
    public boolean bucketExist(String bucketName) {
        validConfig();
        try {
            return client.doesBucketExist(bucketName);
        } catch (CosClientException e) {
            log.warn("MINIO bucketExist error：{}",e.getMessage());
            return false;
        }
    }

    @Override
    public void setBucketAcl(String bucketName, DevFileBucketAuthEnum devFileBucketAuthEnum) {
        try {
            if (devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PRIVATE)) {
                client.setBucketAcl(bucketName, CannedAccessControlList.Private);
            } else if (devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PUBLIC_READ)) {
                client.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
            } else if (devFileBucketAuthEnum.equals(DevFileBucketAuthEnum.PUBLIC_READ_WRITE)) {
                client.setBucketAcl(bucketName,CannedAccessControlList.PublicReadWrite);
            }
        } catch (CosClientException e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public boolean isReady() {

        return  isSetConfig() && client!=null;
    }
    private void validConfig(){
        if(!this.isReady()){
            throw new CommonException("Alibaba Cloud OOS configuration incomplete");
        }
    }

    private boolean isSetConfig(){
      return  config!=null && StringUtils.isNotBlank(config.getBucketName())
                && StringUtils.isNotBlank(config.getRegionId())
                && StringUtils.isNotBlank(config.getSecretId())
                && StringUtils.isNotBlank(config.getSecretKey())
                && StringUtils.isNotBlank(config.getBindDomainUrl());
    }

    @Override
    @Init
    public void reloadConfig() {
        TencentConfig newConfig = new TencentConfig();
        DevConfigApi api = BeanUtils.getBean(DevConfigApi.class);

        newConfig.setSecretId(api.getValueByKey(FILE_TENCENT_SECRET_ID_KEY));
        newConfig.setSecretKey(api.getValueByKey(FILE_TENCENT_SECRET_KEY_KEY));
        newConfig.setRegionId(api.getValueByKey(FILE_TENCENT_REGION_ID_KEY));
        newConfig.setBucketName(api.getValueByKey(FILE_TENCENT_BUCKET_NAME_KEY));
        newConfig.setBindDomainUrl(api.getValueByKey(FILE_TENCENT_BIND_DOMAIN_URL_KEY));

        config=newConfig;
        destroyClient(); //先销毁
        if(!isSetConfig()){
            return;
        }
        // 1.初始化用户身份信息
        Region region = new Region(config.getRegionId());
        COSCredentials cred = new BasicCOSCredentials(config.getSecretId(), config.getSecretKey());

        // 2.设置 bucket 的区域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(region);

        // 3.生成 cos 客户端。
        client = new COSClient(cred, clientConfig);

        // 4.线程池大小，建议在客户端与 COS 网络充足（例如使用腾讯云的 CVM，同地域上传 COS）的情况下，设置成16或32即可，可较充分的利用网络资源
        // 对于使用公网传输且网络带宽质量不高的情况，建议减小该值，避免因网速过慢，造成请求超时。
        ExecutorService threadPool = Executors.newFixedThreadPool(32);

        // 5.传入一个线程池, 若不传入线程池，默认 TransferManager 中会生成一个单线程的线程池。
        transferManager = new TransferManager(client, threadPool);

                        // 6.设置高级接口的分块上传阈值和分块大小为10MB
                        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
                        transferManagerConfiguration.setMultipartUploadThreshold(10 * 1024 * 1024);
                        transferManagerConfiguration.setMinimumUploadPartSize(10 * 1024 * 1024);
        transferManager.setConfiguration(transferManagerConfiguration);
    }

    /**
     * 销毁操作的客户端
     */
    public void destroyClient() {
        if(client!=null) {
            client.shutdown();
        }
        if(transferManager!=null){
            transferManager.shutdownNow();
        }
        client=null;
        transferManager=null;
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
        } catch (CosClientException e) {
            throw new CommonException(e.getMessage());
        }
    }


    @Override
    public String getFileAuthUrl(String bucketName, String key, Long timeoutMillis) {
        validConfig();
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key, HttpMethodName.GET);
        if(timeoutMillis!=null) {
            Date expirationDate = new Date(System.currentTimeMillis() + timeoutMillis);
            request.setExpiration(expirationDate);
        }
        URL url;
        try {
            url = client.generatePresignedUrl(request);
        } catch (CosClientException e) {
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
        validConfig();
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(DevFileUtils.getFileContentType(key));
            client.putObject(bucketName, key, byteArrayInputStream, objectMetadata);
        } catch (CosClientException e) {
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
        } catch (CosClientException e) {
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
