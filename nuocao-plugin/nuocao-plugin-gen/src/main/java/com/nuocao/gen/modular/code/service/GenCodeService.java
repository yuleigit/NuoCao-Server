package com.nuocao.gen.modular.code.service;

import com.nuocao.gen.modular.table.param.GenTableIdParam;
import com.nuocao.gen.modular.code.result.GenCodePreviewResult;
import org.noear.solon.core.handle.Context;

import java.io.IOException;

/**
 * 代码生成服务 接口
 */
public interface GenCodeService {

    /**
     * 执行代码生成,并返回zip包下载
     **/
    void execGenTargetZip(Long tableId, Context response) throws IOException;

    /**
     * 执行代码生成，在项目目录中
     **/
    void execGenTargetProject(Long tableId, Context response) throws IOException;

    /**
     * 预览代码生成
     **/
    GenCodePreviewResult previewGen(Long tableId);
}
