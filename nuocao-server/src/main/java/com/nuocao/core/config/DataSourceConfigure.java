package com.nuocao.core.config;

import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;

/**
 * @author noear 2022/10/7 created
 */
@Configuration
public class DataSourceConfigure {

    private static final String MYBATIS_FLEX_GLOBAL_CONFIG_LOG_ENABLED = "mybatis-flex.globalConfig.logEnabled";

    @Bean(name = "nuoao", typed = true)
    public DataSource master(@Inject("${nuocao.datasource.dynamic.master}") HikariDataSource ds) {
        // 是否打印sql
        Boolean logEnabled = Solon.cfg().getBool(MYBATIS_FLEX_GLOBAL_CONFIG_LOG_ENABLED, false);
        if (logEnabled) {
            AuditManager.setAuditEnable(true);
            ConsoleMessageCollector consoleMessageCollector = new ConsoleMessageCollector();
            AuditManager.setMessageCollector(consoleMessageCollector);
        }
        return ds;
    }

}
