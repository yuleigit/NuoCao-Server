package com.nuocao.gen.modular.code.enums;

import lombok.Getter;

@Getter
public enum GenGroupEnum {

    JAVA("java","com/nuocao/${moduleName}/modular/${businessName}/${targetPath}/"),
    WEB("web","${targetPath}/${moduleName}/${businessName}/"),
    SQL("sql","sql/");

    private String targetPath;

    private String value;

    GenGroupEnum(String value, String targetPath){
        this.targetPath=targetPath;
        this.value =value;
    }

}
