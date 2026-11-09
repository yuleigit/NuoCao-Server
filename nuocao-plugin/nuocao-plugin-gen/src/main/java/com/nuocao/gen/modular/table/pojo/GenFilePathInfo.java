package com.nuocao.gen.modular.table.pojo;

import com.nuocao.common.utils.StringUtils;
import com.nuocao.gen.modular.code.enums.GenGroupEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 生成模板文件与存储位置对应关系配置类
 */
@Getter
@Setter
public class GenFilePathInfo {

    /**
     * 模板类型，比如web就在web目录下，java就在java目录下
     */
    private String group;

    /**
     * 模板文件名，获取时为category/template
     */
    private String templateFileName;

    /**
     * 输出位置，可传入变量
     */
    private String targetFilePath;

    /**
     * 生成文件名称
     */
    private String targetFileName;


    public static GenFilePathInfo create(GenGroupEnum category, String templateFileName, String targetPath, String targetFileName){
        //String template="template/"+category.getValue()+"/"+templateFileName;
        String target = category.getTargetPath()+targetFileName;
        if(StringUtils.isNotBlank(targetPath)){
            target= StringUtils.replace(target,"${targetPath}",targetPath);
        }
        GenFilePathInfo result =  new GenFilePathInfo();
        result.setGroup(category.getValue());
        result.setTemplateFileName(templateFileName);
        result.setTargetFilePath(target);
        result.setTargetFileName(targetFileName);
        return result;
    }


    public static GenFilePathInfo createJava(String templateFileName,String targetPath, String targetFileName)
    {
        return create(GenGroupEnum.JAVA,templateFileName,targetPath,targetFileName);
    }

    public static GenFilePathInfo createWeb(String templateFileName,String targetPath, String targetFileName)
    {
        return create(GenGroupEnum.WEB,templateFileName,targetPath,targetFileName);
    }

    public static GenFilePathInfo createSql(String templateFileName,String targetPath, String targetFileName)
    {
        return create(GenGroupEnum.SQL,templateFileName,targetPath,targetFileName);
    }
}
