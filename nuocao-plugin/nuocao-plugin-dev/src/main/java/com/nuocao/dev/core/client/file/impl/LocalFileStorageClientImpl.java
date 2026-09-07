package com.nuocao.dev.core.client.file.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.common.utils.bean.BeanUtils;
import com.nuocao.dev.core.client.file.FileStorageClient;
import nuocao.plugin.dev.api.enums.file.DevFileBucketAuthEnum;
import nuocao.plugin.dev.api.enums.file.DevFileEngineTypeEnum;
import nuocao.plugin.dev.api.DevConfigApi;
import org.noear.solon.core.handle.UploadedFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

/**
 * 文件引擎 本地 实现
 */
public class LocalFileStorageClientImpl implements FileStorageClient {

    private static String uploadFileFolder = null;

    private static final String FILE_LOCAL_FOLDER_CONFIG_KEY = "file_local_folder"; //本地文件存储路径，

    @Override
    public String getType() {
        return DevFileEngineTypeEnum.LOCAL.getValue();
    }

    @Override
    public String getDefaultBucketName() {
        return "defaultBucketName";
    }

    @Override
    public String storageFileReturnUrl(String bucketName, String key, File file) {
        storageFile(bucketName, key, file);
        return getFileAuthUrl(bucketName, key);
    }

    @Override
    public String storageFileReturnUrl(String bucketName, String key, UploadedFile multipartFile) {
        storageFile(bucketName, key, multipartFile);
        return getFileAuthUrl(bucketName, key);      }

    @Override
    public String storageFileReturnUrl(String bucketName, String key, InputStream inputStream) {
        storageFile(bucketName, key, inputStream);
        return getFileAuthUrl(bucketName, key);
    }

    @Override
    public String storageFileReturnUrl(String bucketName, String key, byte[] bytes) {
        storageFile(bucketName, key, bytes);
        return getFileAuthUrl(bucketName, key);
    }

    @Override
    public void deleteFile(String bucketName, String key) {
        File file = getFileByBucketNameAndKey(bucketName, key);
        FileUtil.del(file);
    }

    @Override
    public void copyFile(String originBucketName, String originFileKey, String newBucketName, String newFileKey) {
        validConfig();
        File file = getFileByBucketNameAndKey(originBucketName, originFileKey);
        File newFile = FileUtil.file(uploadFileFolder + FileUtil.FILE_SEPARATOR + newBucketName + FileUtil.FILE_SEPARATOR + newFileKey);
        FileUtil.copy(file, newFile, true);
    }

    @Override
    public boolean fileExists(String bucketName, String key) {
        return FileUtil.exist(uploadFileFolder + FileUtil.FILE_SEPARATOR + bucketName + FileUtil.FILE_SEPARATOR + key);
    }

    @Override
    public boolean bucketExist(String bucketName) {
        return FileUtil.exist(uploadFileFolder + FileUtil.FILE_SEPARATOR + bucketName);
    }

    @Override
    public void setBucketAcl(String bucketName, DevFileBucketAuthEnum devFileBucketAuthEnum) {
        throw new CommonException("Local engine does not require operation:setBucketAcl");
    }

    @Override
    public void setFileAcl(String bucketName, String key, DevFileBucketAuthEnum devFileBucketAuthEnum) {
        throw new CommonException("Local engine does not require operation:setFileAcl");
    }

    @Override
    public String getFileAuthUrl(String bucketName, String key, Long timeoutMillis) {
        throw new CommonException("Local file, no expiration time required\n");
    }

    @Override
    public String getFileAuthUrl(String bucketName, String key) {
        File file = getFileByBucketNameAndKey(bucketName, key);
        return file.getAbsolutePath();
    }

    @Override
    public boolean isReady() {
        return StringUtils.isNotBlank(uploadFileFolder);
    }
    private void validConfig(){
        if(!this.isReady()){
            throw new CommonException("File Local configuration incomplete");
        }
    }
    @Override
    public void reloadConfig() {
        DevConfigApi devConfigApi = BeanUtils.getBean(DevConfigApi.class);
        uploadFileFolder=devConfigApi.getValueByKey(FILE_LOCAL_FOLDER_CONFIG_KEY);;
        if(!FileUtil.exist(uploadFileFolder)) {
            FileUtil.mkdir(uploadFileFolder);
        }
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
        FileUtil.writeBytes(bytes, uploadFileFolder + FileUtil.FILE_SEPARATOR + bucketName + FileUtil.FILE_SEPARATOR + key);
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
        FileUtil.writeFromStream(inputStream, uploadFileFolder + FileUtil.FILE_SEPARATOR + bucketName + FileUtil.FILE_SEPARATOR + key);
    }

    /**
     * 根据桶名称和文件key获取文件
     *
     * @param bucketName 文件桶
     * @param key        唯一标示id，例如a.txt, doc/a.txt
     */
    private static File getFileByBucketNameAndKey(String bucketName, String key) {
        String path = uploadFileFolder + FileUtil.FILE_SEPARATOR + bucketName + FileUtil.FILE_SEPARATOR + key;
        File file = FileUtil.file(path);
        if(!FileUtil.exist(file)) {
            throw new CommonException("file not exists:{}", path);
        }
        return file;
    }


}
