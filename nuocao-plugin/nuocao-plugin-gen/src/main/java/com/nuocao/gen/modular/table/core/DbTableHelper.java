package com.nuocao.gen.modular.table.core;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.util.StringUtils;
import com.nuocao.common.enums.CommonYesNoEnum;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.gen.modular.table.core.pojo.DbTableColumnInfo;
import com.nuocao.gen.modular.table.core.pojo.DbTableInfo;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DbTableHelper {

    private static Map<String,DataSource> DATA_SOURCE_MAP= null;

    @Init
    public void initDataSources(){
        DATA_SOURCE_MAP= Solon.context().getBeansMapOfType(DataSource.class);
    }

    private DataSource getDataSource(String dataSourceName){
        if(DATA_SOURCE_MAP==null || !DATA_SOURCE_MAP.containsKey(dataSourceName)){
            throw new CommonException("data source not find");
        }
        return DATA_SOURCE_MAP.get(dataSourceName);
    }

    /**
     * 获取数据表
     * @return
     */
    public List<DbTableInfo> getDbTables(){
        List<DbTableInfo> tableInfos = new ArrayList<>();
        for (String dataSourceName:DATA_SOURCE_MAP.keySet()
             ) {
            tableInfos.addAll(getDbTables(dataSourceName));
        }
        return tableInfos;
    }

    /**
     * 从指定数据源中得到表
     * @param dataSourceName
     * @return
     */
    public List<DbTableInfo> getDbTables(String dataSourceName){
        DataSource dataSource=getDataSource(dataSourceName);
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection(); //todo: 改成直接从数据源拿连接，就不用管配置了,by noear
            DatabaseMetaData metaData = conn.getMetaData();
            String url = metaData.getURL();
            String schema = getSchema(conn);
            List<DbTableInfo> tables = new ArrayList<>();
            rs = metaData.getTables(null, schema, "%", new String[]{"TABLE"});
            metaData.getDatabaseProductName();
            String datgaSourceName= getDatabaseName(conn);
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (!StrUtil.startWithIgnoreCase(tableName, "ACT_")) {
                    String remarks = rs.getString("REMARKS");
                    if(StringUtils.isBlank(remarks)){
                        remarks=tableName;
                    }
                    tables.add(new DbTableInfo(dataSourceName,tableName,remarks));
                }
            }
            return tables;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            throw new CommonExceptionI18n("gen.dbTable.getFailed",sqlException.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 根据表明成获取数据信息
     * @param tableName
     * @return
     */
    public List<DbTableColumnInfo> getColumns(String dataSourceName,String tableName)
    {
        DataSource dataSource=getDataSource(dataSourceName);
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            String url = metaData.getURL();
            String schema = getSchema(conn);
            List<DbTableColumnInfo> columns = new ArrayList<>();
            rs = metaData.getColumns(null, schema, tableName, "%");
            if (!rs.isBeforeFirst()) {
                rs = metaData.getColumns(null, schema, tableName.toLowerCase(), "%");
            }
            while (rs.next()) {
                String ColumnName = rs.getString("COLUMN_NAME").toUpperCase();
                int columnSize = rs.getInt("COLUMN_SIZE");
                int nullable = rs.getInt("NULLABLE");
                DbTableColumnInfo columnInfo = new DbTableColumnInfo();
                columnInfo.setColumnName(ColumnName);
                columnInfo.setNullable(nullable==0? CommonYesNoEnum.YES.getValue():CommonYesNoEnum.NO.getValue());

                String remarks = rs.getString("REMARKS");
                if (ObjectUtil.isEmpty(remarks)) {
                    columnInfo.setColumnRemark(ColumnName);
                } else {
                    columnInfo.setColumnRemark(remarks);
                }
                String typeName = rs.getString("TYPE_NAME").toUpperCase();
                if (ObjectUtil.isEmpty(typeName)) {
                    columnInfo.setTypeName("NONE");
                } else {
                    columnInfo.setTypeName(typeName);
                }

                columnInfo.setSizeMax(columnSize);

                columns.add(columnInfo);
            }
            return columns;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            throw new CommonExceptionI18n("gen.dbType.getColumnsFailed", tableName);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取数据库的schema名称
     * @param conn 数据库连接
     * @return schema名称，如果没有则为null
     */
    private String getSchema(Connection conn) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        String url = metaData.getURL().toLowerCase();
        String schema = null;

        if (url.contains("jdbc:oracle")) {
            // Oracle: schema 通常等于用户名
            schema = metaData.getUserName();
            // Oracle用户名通常是大写，但有些情况可能是小写
            if (schema != null) {
                schema = schema.toUpperCase();
            }
        } else if (url.contains("jdbc:sqlserver")) {
            // SQL Server: 使用默认schema，通常是dbo
            schema = "dbo";

            // 或者从当前连接获取
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT SCHEMA_NAME()")) {
                if (rs.next()) {
                    schema = rs.getString(1);
                }
            }
        } else if (url.contains("jdbc:postgresql")) {
            // PostgreSQL: 默认schema是public
            schema = "public";

            // 或者获取当前schema
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT current_schema()")) {
                if (rs.next()) {
                    schema = rs.getString(1);
                }
            }
        } else if (url.contains("jdbc:mysql")) {
            // MySQL: MySQL 5.7+ 支持schema，通常是数据库名
            // 或者可以通过查询获取
            try {
                schema = conn.getCatalog(); // MySQL中catalog就是数据库名
            } catch (Exception e) {
                // 如果失败，尝试查询
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT DATABASE()")) {
                    if (rs.next()) {
                        schema = rs.getString(1);
                    }
                }
            }
        } else if (url.contains("jdbc:db2")) {
            // DB2: 当前schema
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("VALUES CURRENT SCHEMA")) {
                if (rs.next()) {
                    schema = rs.getString(1);
                }
            }
        }

        return schema;
    }


    /**
     * 获取连接的数据名称
     * @param conn
     * @return
     * @throws SQLException
     */
    private static String getDatabaseName(Connection conn) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        String url = metaData.getURL();
        String databaseName = null;
        //getDatabaseProductName() - 获取数据库产品名（如"MySQL"）
        String productName = metaData.getDatabaseProductName();
        System.out.println("数据库产品: " + productName);

        // getDatabaseProductVersion() - 获取版本
        String version = metaData.getDatabaseProductVersion();
        System.out.println("数据库版本: " + version);

        // 对于MySQL，可以使用catalog
        String catalog = conn.getCatalog();  // MySQL返回数据库名
        System.out.println("数据库名(catalog): " + catalog);


        try (Statement stmt = conn.createStatement()) {
            if (url.contains("jdbc:mysql")) {
                try (ResultSet rs = stmt.executeQuery("SELECT DATABASE()")) {
                    if (rs.next()) {
                        databaseName = rs.getString(1);
                    }
                }
            } else if (url.contains("jdbc:postgresql")) {
                try (ResultSet rs = stmt.executeQuery("SELECT current_database()")) {
                    if (rs.next()) {
                        databaseName = rs.getString(1);
                    }
                }
            } else if (url.contains("jdbc:sqlserver")) {
                try (ResultSet rs = stmt.executeQuery("SELECT DB_NAME()")) {
                    if (rs.next()) {
                        databaseName = rs.getString(1);
                    }
                }
            } else if (url.contains("jdbc:oracle")) {
                // Oracle没有"数据库"概念，有"实例"和"服务名"
                try (ResultSet rs = stmt.executeQuery(
                        "SELECT SYS_CONTEXT('USERENV','DB_NAME') FROM DUAL")) {
                    if (rs.next()) {
                        databaseName = rs.getString(1);
                    }
                }
            }
        }

        return databaseName;
    }

}
