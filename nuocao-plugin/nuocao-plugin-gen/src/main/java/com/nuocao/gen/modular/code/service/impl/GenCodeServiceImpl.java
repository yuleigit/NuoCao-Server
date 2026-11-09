package com.nuocao.gen.modular.code.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.*;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.FlexGlobalConfig;
import com.nuocao.common.enums.CommonYesNoEnum;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.language.LanguageUtils;
import com.nuocao.common.utils.http.ResponseUtils;
import com.nuocao.common.utils.PermissionCodeBuildUtils;
import com.nuocao.common.utils.StringUtils;
import com.nuocao.common.utils.uuid.IdUtils;
import com.nuocao.gen.modular.code.enums.GenGroupEnum;
import com.nuocao.gen.modular.code.service.GenCodeService;
import com.nuocao.gen.modular.table.entity.GenTable;
import com.nuocao.gen.modular.table.entity.GenTableColumn;
import com.nuocao.gen.modular.table.pojo.GenFilePathInfo;
import com.nuocao.gen.modular.code.result.GenCodePreviewResult;
import com.nuocao.gen.modular.table.service.GenTableService;
import com.nuocao.sys.api.SysMenuApi;
import lombok.extern.slf4j.Slf4j;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.nuocao.gen.modular.table.core.GenConstants.*;

/**
 * 代码生成服务接口实现
 */
@Component
@Slf4j
public class GenCodeServiceImpl implements GenCodeService {

    //前端目录
    private static final String GEN_WEB_PATH_KEY="nuocao-admin-web";

    //后端目录
    private static final String GEN_JAVA_PATH_KEY="nuocao-plugin";

    private static final String TEMPLATE_PATH_ROOT="template/";

    @Inject
    private GenTableService tableService;
    @Inject
    private FlexGlobalConfig globalConfig;
    @Inject
    private SysMenuApi sysMenuApi;


    //构建模板列表
    private static final List<GenFilePathInfo> TEMPLATE_LIST= Arrays.asList(
            //后端模板
            GenFilePathInfo.createJava("controller.java.btl","controller",getJavaFileName("Controller")),
            GenFilePathInfo.createJava("entity.java.btl","entity",getJavaFileName("")),
            GenFilePathInfo.createJava("convert.java.btl","convert",getJavaFileName("Convert")),
            GenFilePathInfo.createJava("mapper.java.btl","mapper",getJavaFileName("Mapper")),
            GenFilePathInfo.createJava("mapper.xml.btl","mapper/mapping",getXmlFileName("Mapper")),
            GenFilePathInfo.createJava("addParam.java.btl","param",getJavaFileName("AddParam")),
            GenFilePathInfo.createJava("editParam.java.btl","param",getJavaFileName("EditParam")),
            GenFilePathInfo.createJava("pageParam.java.btl","param",getJavaFileName("pageParam")),
            GenFilePathInfo.createJava("idParam.java.btl","param",getJavaFileName("idParam")),
            GenFilePathInfo.createJava("viewResult.java.btl","result",getJavaFileName("ViewResult")),
            GenFilePathInfo.createJava("pageResult.java.btl","result",getJavaFileName("PageResult")),
            GenFilePathInfo.createJava("service.java.btl","service",getJavaFileName("Service")),
            GenFilePathInfo.createJava("serviceImpl.java.btl","service/impl",getJavaFileName("ServiceImpl")),

            //前段模板
            GenFilePathInfo.createWeb("api.js.btl","api","${businessName}-api.js"),
            GenFilePathInfo.createWeb("index.vue.btl","views","index.vue"),
            GenFilePathInfo.createWeb("form.vue.btl","views","modules/form.vue"),

            //菜单SQL模板
            GenFilePathInfo.createSql("menu.sql.btl","","menu.sql")


    );


    @Override
    public void execGenTargetZip(Long tableId, Context response) throws IOException {
        try {
            File tempFolder = this.genTempFolder(tableId, response, true);
            if (tempFolder == null) {
                ResponseUtils.renderError(response, LanguageUtils.getMessage("gen.table.notExists" , tableId));
                return;
            }
            // 压缩
            File zip = ZipUtil.zip(tempFolder);
            // 压缩完毕删除临时目录
            FileUtil.del(tempFolder);
            // 下载
            ResponseUtils.download(zip, response);
        } catch (Exception e) {
            ResponseUtils.renderError(response, e.getMessage());
        }
    }

    @Override
    public void execGenTargetProject(Long tableId, Context response) throws IOException {
        File tempFolder = this.genTempFolder(tableId, response, false);
        // 定义前端生成的目录
        String genProjectFrontendPath = System.getProperty("user.dir") + File.separator + GEN_WEB_PATH_KEY + File.separator + "src";

        if (!FileUtil.exist(genProjectFrontendPath)) {
            throw new CommonException("前端代码生成位置：{}不存在，请检查位置", genProjectFrontendPath);
        }

        GenTable table = tableService.queryEntity(tableId);
        String genModuleName = table.getModuleName();
        String genPluginName = table.getPluginName();
        String GEN_PROJECT_PLUGIN_BIZ_KEY = GEN_JAVA_PATH_KEY + File.separator + genPluginName;

        // 定义后端生成的目录
        String genProjectBackendPath = System.getProperty("user.dir") + File.separator + GEN_PROJECT_PLUGIN_BIZ_KEY + File.separator + "src" +
                File.separator + "main" + File.separator + "java";

        if (!FileUtil.exist(genProjectBackendPath)) {
            throw new CommonException("后端代码生成位置：{}不存在，请检查位置", genProjectBackendPath);
        }
        try {
            // 生成菜单
            Long menuId = sysMenuApi.addMenuForGen(table.getMenuParentId(), table.getBusinessName(), table.getModuleName(), table.getFunctionName(),
                    StrUtil.SLASH + genModuleName + StrUtil.SLASH + table.getBusinessName());

            // 生成按钮
            sysMenuApi.addButtonFormGen(menuId, table.getBusinessName(),table.getBusinessName(), table.getFunctionName());

            //前端代码移动到前端
            FileUtil.moveContent(FileUtil.file(tempFolder + File.separator + GenGroupEnum.WEB.getValue()), FileUtil.file(genProjectFrontendPath), true);

            // 后端代码移动到后端
            FileUtil.moveContent(FileUtil.file(tempFolder + File.separator + GenGroupEnum.WEB.getValue()), FileUtil.file(genProjectBackendPath), true);

            // 移动完毕删除临时目录
            FileUtil.del(tempFolder);
        } catch (Exception e) {
            log.error(">>> 代码生成异常：", e);
            throw new CommonException("代码生成异常");
        }
    }


    /**
     * 获取临时目录
     */
    private File genTempFolder(Long tableId, Context response, boolean isZip) throws IOException {
        GenTable genBasic =tableService.getById(tableId);
        if (ObjectUtil.isEmpty(genBasic)) {
            // 如果是压缩包下载应该使用CommonResponseUtil渲染异常
            if (isZip) {
                return null;
            } else {
                // 否则可以直接抛出异常
                throw new CommonException("gen.table.notExists：{}", tableId);
            }
        }
        GenCodePreviewResult previewResult = this.previewGen(tableId);
        // 先删除压缩包
        FileUtil.del(FileUtil.getTmpDirPath() + File.separator + genBasic.getFunctionName() + ".zip");
        // 生成临时目录
        File tempFolder = FileUtil.file(FileUtil.getTmpDirPath() + File.separator + genBasic.getFunctionName());
        // 生成SQL代码到临时目录
        previewResult.getSqlResultList().forEach(genBasicCodeResult ->
                FileUtil.writeUtf8String(genBasicCodeResult.getCodeFileContent(), FileUtil.file(tempFolder + File.separator +
                        genBasicCodeResult.getCodeFileWithPathName())));
        // 生成前端代码到临时目录
        previewResult.getWebResultList().forEach(genBasicCodeResult ->
                FileUtil.writeUtf8String(genBasicCodeResult.getCodeFileContent(), FileUtil.file(tempFolder + File.separator
                        + GenGroupEnum.WEB.getValue()+ File.separator + genBasicCodeResult.getCodeFileWithPathName())));
        // 生成后端代码到临时目录
        previewResult.getJavaResultList().forEach(genBasicCodeResult ->
                FileUtil.writeUtf8String(genBasicCodeResult.getCodeFileContent(), FileUtil.file(tempFolder + File.separator
                        + GenGroupEnum.JAVA.getValue() + File.separator + genBasicCodeResult.getCodeFileWithPathName())));

        return tempFolder;
    }


    @Override
    public GenCodePreviewResult previewGen(Long tableId) {
        GenTable table = tableService.queryEntity(tableId);
        JSONObject jsonParams = this.buildJsonParams(table);
        GenCodePreviewResult previewResult = new GenCodePreviewResult();
        try {
            //得到所有category列表去重，生成时内容以category分组
            List<String> groupList = TEMPLATE_LIST.stream().map(GenFilePathInfo::getGroup).distinct().collect(Collectors.toList());
            List<GenCodePreviewResult.GenCodeFileResult> fileResultList = CollectionUtil.newArrayList();

            for(String group : groupList){
                //模板组
                GroupTemplate groupTemplate = new GroupTemplate(new ClasspathResourceLoader(TEMPLATE_PATH_ROOT+group),
                        Configuration.defaultConfiguration());
                //模板文件列表

                for (GenFilePathInfo template:
                        TEMPLATE_LIST.stream().filter(o->o.getGroup().equals(group)).collect(Collectors.toList())) {
                    GenCodePreviewResult.GenCodeFileResult fileResult = new GenCodePreviewResult.GenCodeFileResult();
                    Template templateContent = groupTemplate.getTemplate(template.getTemplateFileName());
                    templateContent.binding(jsonParams);
                    fileResult.setGroup(group);
                    fileResult.setCodeFileName(replaceFileName(template.getTargetFileName(),table));
                    fileResult.setCodeFileWithPathName(replaceFileName(template.getTargetFilePath(),table));
                    fileResult.setCodeFileContent(templateContent.render());
                    fileResultList.add(fileResult);
                }

            }

            previewResult.setSqlResultList(fileResultList.stream().filter(o->o.getGroup().equals(GenGroupEnum.SQL.getValue())).collect(Collectors.toList()));
            previewResult.setJavaResultList(fileResultList.stream().filter(o->o.getGroup().equals(GenGroupEnum.JAVA.getValue())).collect(Collectors.toList()));
            previewResult.setWebResultList(fileResultList.stream().filter(o->o.getGroup().equals(GenGroupEnum.WEB.getValue())).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error(">>> 代码生成异常：", e);
            throw new CommonException("code gen error:{}",e.getMessage());
        }
        return previewResult;
    }


    /**
     * 根据表格设置生成参数JSON
     */
    public JSONObject buildJsonParams(GenTable table) {
        JSONObject jsonParams = JSONUtil.createObj();
        // 代码模块名
        jsonParams.set("moduleName", table.getModuleName());
        // 功能名
        jsonParams.set("functionName", table.getFunctionName());
        // 业务名
        jsonParams.set("businessName", table.getBusinessName());
        // 包名
        jsonParams.set("packageName", table.getPackageName());
        // 表名称
        jsonParams.set("tableName", table.getTableName());
        // 类名
        jsonParams.set("className", table.getClassName());
        // 类首字母小写名
        jsonParams.set("classNameFirstLower", StringUtils.lowerFirst(table.getClassName()));
        // 权限字符前缀
        jsonParams.set("permissionCodePrefix", String.format("%s:%s",table.getModuleName(),table.getBusinessName()));

//        jsonParams.set("permissionCodeAdd", PermissionCodeBuildUtils.codeAdd(table.getModuleName(),table.getBusinessName()));
//        jsonParams.set("permissionCodeEdit", PermissionCodeBuildUtils.codeEdit(table.getModuleName(),table.getBusinessName()));
//        jsonParams.set("permissionCodeDelete", PermissionCodeBuildUtils.codeDelete(table.getModuleName(),table.getBusinessName()));
//        jsonParams.set("permissionCodeQuery", PermissionCodeBuildUtils.codeQuery(table.getModuleName(),table.getBusinessName()));
//        jsonParams.set("permissionCodeExport", PermissionCodeBuildUtils.codeExport(table.getModuleName(),table.getBusinessName()));
        // 类小写且以横线分割
        jsonParams.set("classNameLowerLine", StrUtil.toSymbolCase(table.getClassName(), '-'));
        // 主键名
        jsonParams.set("dbPkName", table.getPkColumnName());
        // 主键Java类型
        jsonParams.set("dbPkJavaType", "Long"); //默认
        // 主键名驼峰
        jsonParams.set("dbPkCamelCase", StrUtil.toCamelCase(table.getPkColumnName().toLowerCase()));
        // 主键首字母大写名
        jsonParams.set("dbPkFirstUpper", StrUtil.upperFirst(StrUtil.toCamelCase(table.getPkColumnName().toLowerCase())));
        // 主键注释
        jsonParams.set("dbPkRemark", table.getPkColumnName());
        // 表单布局
        jsonParams.set("formLayout", table.getFormLayout());
        // 使用栅格
        jsonParams.set("gridWhether", table.getIsGrid());
        // 父菜单ID
        jsonParams.set("parentId", ObjectUtil.defaultIfNull(table.getMenuParentId(), 0L));
        // 菜单ID
        jsonParams.set("menuId", IdUtils.snowflakeId());
        // 菜单编码
        jsonParams.set("menuCode", RandomUtil.randomString(10));
        // 菜单路径
        jsonParams.set("menuPath", StrUtil.SLASH + table.getModuleName() + StrUtil.SLASH + table.getBusinessName());
        // 菜单组件
        jsonParams.set("menuComponent", "view." + table.getModuleName() + StrUtil.UNDERLINE + table.getBusinessName() + "_index");
        // 菜单排序
        jsonParams.set("sortCode", ObjectUtil.defaultIfNull(table.getSortCode(), 0));
        // 查询权限按钮ID
        jsonParams.set("queryButtonId", IdUtils.snowflakeId());
        // 添加按钮ID
        jsonParams.set("addButtonId", IdUtils.snowflakeId());
        // 编辑按钮ID
        jsonParams.set("editButtonId", IdUtils.snowflakeId());
        // 删除按钮ID
        jsonParams.set("deleteButtonId", IdUtils.snowflakeId());

        // 作者
        jsonParams.set("authorName", table.getAuthorName());
        // 生成时间
        jsonParams.set("genTime", DateUtil.format(DateTime.now(), " yyyy/MM/dd HH:mm"));
        // 定义配置详情列表
        List<JSONObject> columnList = CollectionUtil.newArrayList();
        // 定义是否有排序字段
        AtomicBoolean hasSortCode = new AtomicBoolean(false);
        List<GenTableColumn> columns = tableService.getColumnList(table.getId());
        columns.forEach(column -> {
                // 定义字段信息
                JSONObject columnConfig = JSONUtil.createObj();
                if (column.getColumnName().equalsIgnoreCase(SORT_COLUMN_NAME)) {
                    hasSortCode.set(true);
                }
                // 如果是主键，则无需作为添加参数，需要作为编辑参数，需要主键注解
                if (column.getColumnName().equalsIgnoreCase(table.getPkColumnName())) {
                    columnConfig.set("needAdd", false);
                    columnConfig.set("needEdit", true);
                    columnConfig.set("needQuery", false);
                    columnConfig.set("needQueryType", "none");
                    columnConfig.set("required", true);
                    columnConfig.set("needTableId", true);
                    jsonParams.set("dbPkJavaType", column.getJavaType());
                    jsonParams.set("dbPkRemark", column.getColumnRemark());
                } else {
                    // 排除删除标志
                    String logicDeleteField = globalConfig.getLogicDeleteColumn();
                    if (ObjectUtil.isEmpty(logicDeleteField)) {
                        logicDeleteField = DELETE_FLAG_COLUMN_NAME;
                    }
                    if (column.getColumnName().equalsIgnoreCase(logicDeleteField)) {
                        columnConfig.set("needAdd", false);
                        columnConfig.set("needEdit", false);
                        columnConfig.set("needQuery", false);
                        columnConfig.set("needQueryType", "none");
                        columnConfig.set("required", false);
                        columnConfig.set("needTableId", false);
                    } else {
                        boolean needAdd = column.getIsAdd().equals(CommonYesNoEnum.YES.getValue());
                        boolean needUpdate = column.getIsEdit().equals(CommonYesNoEnum.YES.getValue());
                        columnConfig.set("needAdd", needAdd);
                        columnConfig.set("needEdit", needUpdate);
                        columnConfig.set("needQuery", column.getIsQuery().equals(CommonYesNoEnum.YES.getValue()));
                        columnConfig.set("needQueryType", column.getQueryType());
                        columnConfig.set("required", column.getIsRequired().equals(CommonYesNoEnum.YES.getValue()));
                        columnConfig.set("needTableId", false);

                    }
                }
                //最大值或长度
                columnConfig.set("valueSizeMax", column.getValueSizeMax());
                //最小值或长度
                columnConfig.set("valueSizeMin", column.getValueSizeMin());
                //验证注解
                columnConfig.set("validAnnotation", column.getValidAnnotation());
                // 列显示
                columnConfig.set("isList", column.getIsList().equals(CommonYesNoEnum.YES.getValue()));
                // 列省略
                columnConfig.set("isListIgnore", column.getIsIgnore().equals(CommonYesNoEnum.YES.getValue()));
                //详情显示
                columnConfig.set("isView", column.getIsView().equals(CommonYesNoEnum.YES.getValue()));
                // 增改
                columnConfig.set("isAdd", column.getIsAdd().equals(CommonYesNoEnum.YES.getValue()));
                columnConfig.set("isEdit", column.getIsEdit().equals(CommonYesNoEnum.YES.getValue()));
                //唯一值
                columnConfig.set("isOnly", column.getIsOnly().equals(CommonYesNoEnum.YES.getValue()));
                // 作用类型
                columnConfig.set("htmlType", column.getHtmlType());
                // 字典值
                columnConfig.set("dictTypeCode", column.getDictType());
                // 实体类型
                columnConfig.set("javaType", column.getJavaType());
                // 字段驼峰名
                columnConfig.set("columnNameCamelCase", StrUtil.toCamelCase(column.getColumnName().toLowerCase()));
                // 字段驼峰首字母大写名
                columnConfig.set("columnNameCamelCaseFirstUpper", StrUtil.upperFirst(StrUtil.toCamelCase(column.getColumnName().toLowerCase())));
                // 字段注释
                columnConfig.set("columnRemark", column.getColumnRemark());
                // 是否需要自动插入
                columnConfig.set("needAutoInsert", CREATE_USER_COLUMN_NAME.equalsIgnoreCase(column.getColumnName()) ||
                        CREATE_TIME_COLUMN_NAME.equalsIgnoreCase(column.getColumnName()));
                // 是否需要自动更新
                columnConfig.set("needAutoUpdate", UPDATE_USER_COLUMN_NAME.equalsIgnoreCase(column.getColumnName()) ||
                        UPDATE_TIME_COLUMN_NAME.equalsIgnoreCase(column.getColumnName()));
                // 是否需要逻辑删除
                columnConfig.set("needLogicDelete", DELETE_FLAG_COLUMN_NAME.equalsIgnoreCase(column.getColumnName()));
            columnList.add(columnConfig);

            });
        // 配置信息
        jsonParams.set("columnList", columnList);
        // 有排序字段
        jsonParams.set("hasSortCode", hasSortCode.get());
        return jsonParams;
    }

    private String replaceFileName(String str,GenTable table)
    {
        String result = StringUtils.replace(str,"${className}",table.getClassName());
        result = StringUtils.replace(result,"${moduleName}",table.getModuleName());
        result = StringUtils.replace(result,"${businessName}",table.getBusinessName());
        return result;
    }



    ///**************** 模板文件相关

    private static String getJavaFileName(String file){
        return "${className}"+file+".java";
    }
    private static String getXmlFileName(String file){
        return "${className}"+file+".xml";
    }
}
