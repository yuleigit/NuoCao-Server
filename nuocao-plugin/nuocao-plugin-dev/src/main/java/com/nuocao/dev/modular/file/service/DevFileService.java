package com.nuocao.dev.modular.file.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.nuocao.common.page.PageResult;
import com.nuocao.dev.modular.file.entity.DevFile;
import com.nuocao.dev.modular.file.param.DevFileIdParam;
import com.nuocao.dev.modular.file.param.DevFileListParam;
import com.nuocao.dev.modular.file.param.DevFilePageParam;
import com.nuocao.dev.modular.file.param.DevFileUrlListParam;
import nuocao.plugin.dev.api.enums.file.DevFileEngineTypeEnum;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 文件Service接口
 *
 **/
public interface DevFileService  {

    /**
     * MultipartFile文件上传，返回文件id
     **/
    String uploadReturnId(String engine,String businessDir, UploadedFile file);

    /**
     * MultipartFile文件上传，返回文件Url
     **/
    String uploadReturnUrl(String engine,String businessDir, UploadedFile file);


    /**
     * MultipartFile文件上传，返回文件id
     **/
    String uploadReturnId(String engine, UploadedFile file);

    /**
     * MultipartFile文件上传，返回文件Url
     **/
    String uploadReturnUrl(String engine, UploadedFile file);

    /**
     *inputStream文件上传，返回文件id
     **/
    String uploadReturnId(String engine,String businessDir,String fileName, InputStream inputStream);

    /**
     *inputStream文件上传，返回文件Url
     **/
    String uploadReturnUrl(String engine,String businessDir,String fileName, InputStream inputStream);


    /**
     * MultipartFile文件上传，返回文件id
     **/
    String uploadReturnId(String engine,String fileName, InputStream inputStream);

    /**
     * MultipartFile文件上传，返回文件Url
     **/
    String uploadReturnUrl(String engine,String fileName, InputStream inputStream);


    /**
     * 文件分页列表接口
     **/
    PageResult<DevFile> page(DevFilePageParam devFilePageParam);

    /**
     * 文件列表接口
     **/
    List<DevFile> list(DevFileListParam devFileListParam);

    /**
     * 下载文件
     **/
    void download(DevFileIdParam devFileIdParam, Context ctx) throws IOException;

    /**
     * 删除文件
     **/
    void delete(List<DevFileIdParam> devFileIdParamList);

    /**
     * 获取文件详情
     */
    DevFile detail(DevFileIdParam devFileIdParam);

    /**
     * 根据文件url集合获取文件集合
     **/
    List<DevFile> getFileListByUrlList(DevFileUrlListParam devFileUrlListParam);

    /**
     * 获取文件详情
     */
    DevFile queryEntity(String id);


    DevFile queryEntityByFileId(String fileId);

    /**根据url删除文件
     */
    void storeDeleteFileByUrl(List<String> urlList);

}
