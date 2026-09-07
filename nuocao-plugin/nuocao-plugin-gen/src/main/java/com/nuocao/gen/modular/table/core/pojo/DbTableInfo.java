package com.nuocao.gen.modular.table.core.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * 数据库 表信息
 */
@Getter
@Setter
public class DbTableInfo {


    /**
     * 数据库名
     */
    private String dataSource;

    /**
     * 表名称
     */
    private String tableName;


    /**
     * 备注
     */
    private String tableRemark;

    public DbTableInfo(String dataSource,String tableName,String tableRemark){
        this.dataSource=dataSource;
        this.tableRemark=tableRemark;
        this.tableName=tableName;
    }
}
