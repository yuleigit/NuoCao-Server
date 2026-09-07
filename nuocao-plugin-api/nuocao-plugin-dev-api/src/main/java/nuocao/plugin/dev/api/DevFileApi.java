package nuocao.plugin.dev.api;

import nuocao.plugin.dev.api.enums.file.DevFileEngineTypeEnum;
import org.noear.solon.core.handle.UploadedFile;

import java.io.InputStream;
import java.util.List;

/**
 * 文件接口
 */
public interface DevFileApi {

    /*动态上传文件*/
    /**
     * 上传文件返回Url
     *
     * @param file 文件
     * @param businessDir 业务所在文件夹
     **/
    String storageFileReturnUrlDynamic(String businessDir, UploadedFile file);
    default String storageFileReturnUrlDynamic(UploadedFile file){
        return storageFileReturnUrlDynamic(null,file);
    }

    /**
     * 上传文件返回Id
     *
     * @param file 文件
     * @param businessDir 业务所在文件夹
     **/
    String storageFileReturnIdDynamic(String businessDir,UploadedFile file);
    default String storageFileReturnIdDynamic(UploadedFile file){
        return storageFileReturnIdDynamic(null,file);
    }



    /**
     * 上传文件返回Url
     *
     * @param inputStream 文件
     * @param fileName 自定义文件名
     * @param businessDir 业务所在文件夹
     **/
    String storageFileReturnUrlDynamic(String businessDir, String fileName, InputStream inputStream);

    /**
     *
     * @param fileName 自定义文件名
     * @param inputStream 文件
     * @return
     */
    default String storageFileReturnUrlDynamic(String fileName, InputStream inputStream){
        return storageFileReturnUrlDynamic(null,fileName,inputStream);
    }

    /**
     * 上传文件返回Id
     *
     * @param inputStream 文件
     **/
    String storageFileReturnIdDynamic(String businessDir,String fileName, InputStream inputStream);
    default String storageFileReturnIdDynamic(String fileName, InputStream inputStream){
        return storageFileReturnIdDynamic(null,fileName,inputStream);
    }


    /* =========上传文件方法========= */

    /**
     * 上传文件返回Url
     *
     * @param file 文件
     **/
    String storageFileReturnUrl(DevFileEngineTypeEnum engine, UploadedFile file);

    /**
     * 上传文件返回Id
     *
     * @param file 文件
     **/
    String storageFileReturnId(DevFileEngineTypeEnum engine,UploadedFile file);


    void storeDeleteFileByUrl(List<String> urlList);

}
