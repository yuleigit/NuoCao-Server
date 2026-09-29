package com.nuocao.dev.modular.file.provider;

import nuocao.plugin.dev.api.enums.file.DevFileEngineTypeEnum;
import com.nuocao.dev.modular.file.service.DevFileService;
import nuocao.plugin.dev.api.DevConfigApi;
import nuocao.plugin.dev.api.DevFileApi;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.UploadedFile;

import java.io.InputStream;
import java.util.List;


/**
 * 文件API接口提供者
 **/
@Component
public class DevFileApiProvider implements DevFileApi {
    /** 默认文件引擎 */
    private static final String DEFAULT_FILE_ENGINE_KEY = "sys_default_file_engine";

    @Inject
    private DevFileService devFileService;
    @Inject
    private DevConfigApi devConfigApi;


    @Override
    public String storageFileReturnUrlDynamic(String businessDir, UploadedFile file) {
        return devFileService.uploadReturnUrl(devConfigApi.getValueByKey(DEFAULT_FILE_ENGINE_KEY), file);
    }

    @Override
    public String storageFileReturnIdDynamic(String businessDir, UploadedFile file) {
        return devFileService.uploadReturnId(devConfigApi.getValueByKey(DEFAULT_FILE_ENGINE_KEY), file);
    }

    @Override
    public String storageFileReturnUrlDynamic(String businessDir, String fileName, InputStream inputStream) {
        return devFileService.uploadReturnUrl(devConfigApi.getValueByKey(DEFAULT_FILE_ENGINE_KEY), businessDir,fileName,inputStream);
    }

    @Override
    public String storageFileReturnIdDynamic(String businessDir, String fileName, InputStream inputStream) {
        return devFileService.uploadReturnId(devConfigApi.getValueByKey(DEFAULT_FILE_ENGINE_KEY), businessDir,fileName,inputStream);
    }

    @Override
    public String storageFileReturnUrl(DevFileEngineTypeEnum engine, UploadedFile file) {
        return devFileService.uploadReturnUrl(engine.getValue(), file);
    }

    @Override
    public String storageFileReturnId(DevFileEngineTypeEnum engine, UploadedFile file) {
        return devFileService.uploadReturnId(engine.getValue(), file);
    }

    @Override
    public void storeDeleteFileByUrl(List<String> urlList) {
         devFileService.storeDeleteFileByUrl(urlList);
    }
}
