package com.nuocao.common.utils;

import java.util.List;

/**
 * 权限工具
 */
public class PermissionCodeBuildUtils {

    public static final String OPERATION_ADD="add";
    public static final String OPERATION_EDIT="edit";
    public static final String OPERATION_DELETE="delete";
    public static final String OPERATION_QUERY="query";
    public static final String OPERATION_EXPORT="query";

    /**
     * 获取权限字符串默认格式
     * @param moduleName
     * @param businessName
     * @param operation
     * @return
     */
    public static String code(String moduleName,String businessName,String operation){
        return String.format("%s:%s:%s",moduleName,businessName,operation);
    }

    public static String codeAdd(String moduleName,String businessName){
        return code(moduleName,businessName,OPERATION_ADD);
    }

    public static String codeDelete(String moduleName,String businessName){
        return code(moduleName,businessName,OPERATION_DELETE);
    }

    public static String codeEdit(String moduleName,String businessName){
        return code(moduleName,businessName,OPERATION_EDIT);
    }
    public static String codeQuery(String moduleName,String businessName){
        return code(moduleName,businessName,OPERATION_QUERY);
    }
    public static String codeExport(String moduleName,String businessName){
        return code(moduleName,businessName,OPERATION_EXPORT);
    }



}
