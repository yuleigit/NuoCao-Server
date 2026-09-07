package com.nuocao.gen.modular.code.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 预览代码生成结果
 *
 **/
@Getter
@Setter
public class GenCodePreviewResult {

    /** SQL代码结果集 */
    @ApiModelProperty(value = "SQL代码结果集", position = 1)
    private List<GenCodeFileResult> sqlResultList;

    /** 前端代码结果集 */
    @ApiModelProperty(value = "前端代码结果集", position = 2)
    private List<GenCodeFileResult> webResultList;

    /** 后端代码结果集 */
    @ApiModelProperty(value = "后端代码结果集", position = 3)
    private List<GenCodeFileResult> javaResultList;

    /** 语言包生成结果 */
    @ApiModelProperty(value = "语言包生成结果", position = 4)
    private List<GenCodeFileResult> i18nResultList;

    @Getter
    @Setter
    public static class GenCodeFileResult {

        /**
         * 代码生成模板文件分组
         */
        @ApiModelProperty(value = "代码生成模板文件分组")
        private String group;

        /** 代码文件名称 */
        @ApiModelProperty(value = "代码文件名称", position = 1)
        private String codeFileName;

        /** 代码文件带路径名称 */
        @ApiModelProperty(value = "代码文件带路径名称", position = 2)
        private String codeFileWithPathName;

        /** 代码文件内容 */
        @ApiModelProperty(value = "代码文件内容", position = 2)
        private String codeFileContent;
    }
}
