package com.nuocao.dev.modular.file.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.*;
import cn.hutool.http.HttpUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.nuocao.common.config.NuocaoConfig;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.language.LanguageUtils;
import com.nuocao.common.mapper.LambdaQueryWrapperX;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.utils.http.ResponseUtils;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.dev.core.client.file.DevFileUtils;
import com.nuocao.dev.core.client.file.FileStorageClient;
import com.nuocao.dev.core.client.file.FileStorageFactory;
import com.nuocao.dev.modular.file.entity.DevFile;
import nuocao.plugin.dev.api.enums.file.DevFileEngineTypeEnum;
import com.nuocao.dev.modular.file.mapper.DevFileMapper;
import com.nuocao.dev.modular.file.param.DevFileIdParam;
import com.nuocao.dev.modular.file.param.DevFileListParam;
import com.nuocao.dev.modular.file.param.DevFilePageParam;
import com.nuocao.dev.modular.file.param.DevFileUrlListParam;
import com.nuocao.dev.modular.file.service.DevFileService;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * 文件Service接口实现类
 *
 **/
@Component
@Slf4j
public class DevFileServiceImpl  implements DevFileService {


    @Inject
    private NuocaoConfig nuocaoConfig;

    @Inject
    private FileStorageFactory fileStorageFactory;

    @Inject
    private DevFileMapper mapper;

    @Override
    public String uploadReturnId(String engine, String businessDir, UploadedFile file) {
        return this.storageFile(engine,businessDir, file, true);
    }

    @Override
    public String uploadReturnUrl(String engine, String businessDir, UploadedFile file) {
        return this.storageFile(engine,businessDir, file, false);
    }

    @Override
    public String uploadReturnId(String engine, UploadedFile file) {
        return this.storageFile(engine, file, true);
    }

    @Override
    public String uploadReturnUrl(String engine, UploadedFile file) {
        return this.storageFile(engine, file, false);
    }

    @Override
    public String uploadReturnId(String engine, String businessDir,String fileName, InputStream inputStream) {
        return this.storageFile(engine,businessDir, fileName,inputStream, true);

    }

    @Override
    public String uploadReturnUrl(String engine, String businessDir,String fileName, InputStream inputStream) {
        return this.storageFile(engine,businessDir, fileName,inputStream, false);
    }

    @Override
    public String uploadReturnId(String engine,String fileName, InputStream inputStream) {
        return this.storageFile(engine,null, fileName,inputStream, true);
    }

    @Override
    public String uploadReturnUrl(String engine,String fileName, InputStream inputStream) {
        return this.storageFile(engine,null, fileName,inputStream, false);
    }


    @Override
    public PageResult<DevFile> page(DevFilePageParam devFilePageParam) {
        LambdaQueryWrapperX<DevFile> queryWrapper =new LambdaQueryWrapperX<>();
        queryWrapper.eqIfPresent(DevFile::getEngine,devFilePageParam.getEngine())
                .likeIfPresent(DevFile::getName,devFilePageParam.getName());
        if(!devFilePageParam.hasSortingFields()) {
            queryWrapper.orderBy(DevFile::getCreateTime, false);
        }
        return mapper.selectPage(devFilePageParam,queryWrapper);
    }

    @Override
    public List<DevFile> list(DevFileListParam devFileListParam) {
        LambdaQueryWrapperX<DevFile> queryWrapper =new LambdaQueryWrapperX<>();
        queryWrapper.eqIfPresent(DevFile::getEngine,devFileListParam.getEngine())
                .likeIfPresent(DevFile::getName,devFileListParam.getName());
        queryWrapper.orderBy(DevFile::getCreateTime,false);
        return mapper.selectListByQuery(queryWrapper);
    }

    @Override
    public void download(DevFileIdParam devFileIdParam, Context ctx) throws IOException {
        DevFile devFile;
        try {
            devFile = this.queryEntityByFileId(devFileIdParam.getId());
        } catch (Exception e) {
            ResponseUtils.renderError(ctx, e.getMessage());
            return;
        }
        if (!devFile.getEngine().equals(DevFileEngineTypeEnum.LOCAL.getValue())) {
            ResponseUtils.renderError(ctx, LanguageUtils.getMessage("dev.file.notExists" ,devFile.getFileId()));
            return;
        }
        File file = FileUtil.file(devFile.getStoragePath());
        if (!FileUtil.exist(file)) {
            ResponseUtils.renderError(ctx, LanguageUtils.getMessage("dev.file.notExists" ,devFile.getFileId()));
            return;
        }
        ResponseUtils.download(devFile.getName(), IoUtil.readBytes(FileUtil.getInputStream(file)), ctx);
    }

    @Override
    public void delete(List<DevFileIdParam> devFileIdParamList) {
        List<String> ids = CollectionUtils.toList(devFileIdParamList,DevFileIdParam::getId);
        List<DevFile> fileList = mapper.selectListByIds(ids);
        for (DevFile df:
                fileList) {
            storeDeleteFile(df);
        }
        mapper.deleteBatchByIds(ids);
    }

    /**
     * 存储文件
     * @param engine {@like DevFileEngineTypeEnum.class}
     * @param file
     * @param returnFileId
     * @return
     */
    private String storageFile(String engine, UploadedFile file, boolean returnFileId){
        return storageFile(engine,"",file,returnFileId);
    }

    /**
     * 存储文件
     *
     * @param engine 存储引擎 {@like DevFileEngineTypeEnum.class}
     * @param businessDir 业务目录
     * @param file 存储位置
     * @param returnFileId 是否返回ID，true=fileId,false=url
     **/
    private String storageFile(String engine,String businessDir, UploadedFile file, boolean returnFileId) {

        // 如果引擎为空，默认使用本地
        if (ObjectUtil.isEmpty(engine)) {
            engine = DevFileEngineTypeEnum.LOCAL.getValue();
        }

        // 生成id
        String fileId = IdUtil.getSnowflake().nextIdStr();


        FileStorageClient fileStorageClient = fileStorageFactory.getClient(engine);

        // 存储桶名称
        String bucketName = fileStorageClient.getDefaultBucketName();
        // 定义存储的url，本地文件返回文件实际路径，其他引擎返回网络地址
        String storageUrl = fileStorageClient.storageFileReturnUrl(bucketName,genFileKey(fileId,businessDir, file), file);


        DevFile devFile = genDevFile(engine,fileId,bucketName, file.getName(), file.getContentSize(),storageUrl);
        // 如果是返回id则返回文件id
        if (returnFileId) {
            return fileId;
        } else {
            // 否则返回下载地址
            return devFile.getFileUrl();
        }
    }


    /**
     * 存储文件
     * @param engine
     * @param inputStream
     * @param returnFileId
     * @return
     */
    private String storageFile(String engine, String fileName,InputStream inputStream, boolean returnFileId){
        return storageFile(engine,"",fileName,inputStream,returnFileId);
    }

    /**
     * 存储文件
     *
     * @param engine 存储引擎
     * @param businessDir 业务目录
     * @param inputStream 存储位置
     * @param returnFileId 是否返回ID，true=fileId,false=url
     **/
    private String storageFile(String engine,String businessDir,String fileName,InputStream inputStream, boolean returnFileId) {

        // 如果引擎为空，默认使用本地
        if (ObjectUtil.isEmpty(engine)) {
            engine = DevFileEngineTypeEnum.LOCAL.getValue();
        }

        // 生成id
        String fileId = IdUtil.getSnowflake().nextIdStr();


        FileStorageClient fileStorageClient = fileStorageFactory.getClient(engine);

        // 存储桶名称
        String bucketName = fileStorageClient.getDefaultBucketName();
        // 定义存储的url，本地文件返回文件实际路径，其他引擎返回网络地址
        String storageUrl = fileStorageClient.storageFileReturnUrl(bucketName,getFileKeyByFileName(fileId,businessDir, fileName), inputStream);



        DevFile devFile = genDevFile(engine,fileId,bucketName, fileName, DevFileUtils.getInputStreamSize(inputStream),storageUrl);
        // 如果是返回id则返回文件id
        if (returnFileId) {
            return fileId;
        } else {
            // 否则返回下载地址
            return devFile.getFileUrl();
        }
    }




    private DevFile genDevFile(String engine,String fileId,String bucketName,
                               String fileName,long fileContentSize,
                               String storageUrl

    ){
        // 将文件信息保存到数据库
        DevFile devFile = new DevFile();

        // 设置文件id
        devFile.setFileId(fileId);

        // 设置存储引擎类型
        devFile.setEngine(engine);
        devFile.setBucket(bucketName);
        devFile.setName(fileName);
        String suffix = ObjectUtil.isNotEmpty(fileName) ? StrUtil.subAfter(fileName,
                StrUtil.DOT, true) : null;
        devFile.setSuffix(suffix);
        devFile.setSizeB(fileContentSize);
        devFile.setSizeInfo(FileUtil.readableFileSize(fileContentSize));
        devFile.setObjName(ObjectUtil.isNotEmpty(devFile.getSuffix()) ? fileId + StrUtil.DOT + devFile.getSuffix() : null);
        // 如果是图片，则压缩生成缩略图
        if (ObjectUtil.isNotEmpty(suffix) && StrUtil.isNotBlank(storageUrl)) {
            if (isPic(suffix)) {
                try {
                    // byte[] bytes = IoUtil.readBytes(file.getContent());
                    //**暂时这样读取，最优解还是file.getContent可以服用，本地文件还好，OSS文件的话压力比较大
                    BufferedImage img = HttpUtil.isHttp(storageUrl) || HttpUtil.isHttps(storageUrl) ?ImgUtil.read(new URL(storageUrl)) : ImgUtil.read(storageUrl);
                    devFile.setThumbnail(ImgUtil.toBase64DataUri(ImgUtil.scale(img,
                            100, 100, null), suffix));
                } catch (Exception ignored) {
                }
            }
        }
        // 存储路径
        devFile.setStoragePath(storageUrl);

        // 定义下载地址
        String downloadUrl;

        // 下载路径，注意：本地文件下载地址设置为下载接口地址 + 文件id
        if (engine.equals(DevFileEngineTypeEnum.LOCAL.getValue())) {
            String apiUrl = nuocaoConfig.getBackendUrl();
            if (ObjectUtil.isEmpty(apiUrl)) {
                throw new CommonException("后端域名地址未正确配置：nuocao.config.backend-url为空");
            }
            downloadUrl = apiUrl + "/dev/file/download?id=" + fileId;
            devFile.setFileUrl(downloadUrl);
        } else {
            // 阿里云、腾讯云、MINIO可以直接使用存储地址（公网）作为下载地址
            devFile.setFileUrl(devFile.getStoragePath());
        }

        mapper.insert(devFile);
        return devFile;
    }

    public String genFileKey(String fileId, UploadedFile file){
        return genFileKey(fileId,null,file);
    }

    /**
     * 生成文件的key，格式如 2021/10/11/1377109572375810050.docx
     *
     * @param fileId 文件ID
     * @param businessDir 业务目录
     **/
    public String genFileKey(String fileId,String businessDir, UploadedFile file) {

        // 获取文件原始名称
        return getFileKeyByFileName(fileId,businessDir,file.getName());
    }

    public String getFileKeyByFileName(String fileId,String businessDir,String originalFileName){
        // 获取文件原始名称

        // 获取文件后缀
        String fileSuffix = FileUtil.getSuffix(originalFileName);

        // 生成文件的对象名称，格式如:1377109572375810050.docx
        String fileObjectName = fileId + StrUtil.DOT + fileSuffix;

        // 获取日期文件夹，格式如，2021/10/11/
        String dateFolderPath =(StrUtil.isNotBlank(businessDir)?businessDir+StrUtil.SLASH:"") +
                DateUtil.thisYear() + StrUtil.SLASH +
                (DateUtil.thisMonth() + 1) + StrUtil.SLASH +
                DateUtil.thisDayOfMonth() + StrUtil.SLASH;

        // 返回
        return dateFolderPath + fileObjectName;
    }

    @Override
    public DevFile detail(DevFileIdParam devFileIdParam) {
        return this.queryEntity(devFileIdParam.getId());
    }

    @Override
    public List<DevFile> getFileListByUrlList(DevFileUrlListParam devFileUrlListParam) {
        QueryWrapper queryWrapper = new QueryWrapper();
        // 只查询部分字段
        queryWrapper.select(DevFile::getFileId, DevFile::getSuffix, DevFile::getFileUrl,DevFile::getName, DevFile::getThumbnail,
                        DevFile::getSizeB, DevFile::getSizeInfo, DevFile::getObjName)
                .in(DevFile::getFileUrl, devFileUrlListParam.getUrlList());
        return mapper.selectListByQuery(queryWrapper);
    }

    @Override
    public DevFile queryEntity(String id) {
        DevFile devFile = mapper.selectOneById(id);
        if (ObjectUtil.isEmpty(devFile)) {
            throw new CommonExceptionI18n("dev.file.notExists", id);
        }
        return devFile;
    }
    @Override
    public DevFile queryEntityByFileId(String fileId) {
        DevFile devFile = mapper.selectOne(DevFile::getFileId,fileId);
        if (ObjectUtil.isEmpty(devFile)) {
            throw new CommonExceptionI18n("dev.file.notExists", fileId);
        }
        return devFile;
    }
    @Override
    public void storeDeleteFileByUrl(List<String> urlList) {
        if(urlList==null || urlList.isEmpty()) return;

        QueryWrapper queryWrapper = new QueryWrapper();
        // 只查询部分字段
        queryWrapper.select(DevFile::getFileId, DevFile::getSuffix,
                        DevFile::getStoragePath,DevFile::getName,
                        DevFile::getBucket,DevFile::getEngine,
                        DevFile::getSizeB, DevFile::getSizeInfo, DevFile::getObjName)
                .in(DevFile::getFileUrl, urlList);
        List<DevFile> fileList = mapper.selectListByQuery(queryWrapper);
        if(fileList.isEmpty()) return;
        //删除
        for (DevFile df:
                fileList) {
            if(storeDeleteFile(df)){
                this.mapper.deleteById(df.getFileId());
            }
        }
    }

    /**
     * 从存储器中删除文件
     * @param df
     * @return
     */
    private boolean storeDeleteFile(DevFile df){
        //删除网络文件
        String fileKey="";
        boolean isDelete=false;

        String engine= df.getEngine();
        // 存储桶名称
        String bucketName=df.getBucket();

        // 定义存储的url，本地文件返回文件实际路径，其他引擎返回网络地址
        String storagePath=df.getStoragePath();
        //得到key
        if(engine.equals(DevFileEngineTypeEnum.LOCAL.getValue())){ //本地方案
            String normalizedPath = storagePath.replace(StrUtil.BACKSLASH, StrUtil.SLASH);
            List<String> ary = StrUtil.split(normalizedPath, StrUtil.SLASH+bucketName+ StrUtil.SLASH);
            fileKey=ary.get(ary.size()-1);
        }else{ //按oss处理
            int protocolEnd = storagePath.indexOf("://")+3;
            if(protocolEnd<storagePath.length()) {
                fileKey = storagePath.substring(storagePath.indexOf(StrUtil.SLASH, protocolEnd + 3)); //从第八个字符开始找/，得到根目录之后的
            }
        }
        // 去除开头的斜杠，就得到了文件的key也就是在存储桶中的位置路径
        if (StringUtils.isNotBlank(fileKey) && fileKey.startsWith(StrUtil.SLASH)) {
            fileKey = fileKey.substring(1);
        }

        if(StrUtil.isBlank(fileKey)){
           return false;
       }

        FileStorageClient fileStorageClient = fileStorageFactory.getClient(engine);

        fileStorageClient.deleteFile(fileStorageClient.getDefaultBucketName(),fileKey);


        return isDelete;
    }


    /**
     * 根据文件后缀判断是否图片
     */
    private static boolean isPic(String fileSuffix) {
        fileSuffix = fileSuffix.toLowerCase();
        return ImgUtil.IMAGE_TYPE_GIF.equals(fileSuffix)
                || ImgUtil.IMAGE_TYPE_JPG.equals(fileSuffix)
                || ImgUtil.IMAGE_TYPE_JPEG.equals(fileSuffix)
                || ImgUtil.IMAGE_TYPE_BMP.equals(fileSuffix)
                || ImgUtil.IMAGE_TYPE_PNG.equals(fileSuffix)
                || ImgUtil.IMAGE_TYPE_PSD.equals(fileSuffix);
    }
}
