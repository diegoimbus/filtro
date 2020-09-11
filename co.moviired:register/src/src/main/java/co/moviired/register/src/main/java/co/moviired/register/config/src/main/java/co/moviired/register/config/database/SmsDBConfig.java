package co.moviired.register.config.database;

import co.moviired.register.properties.SmsProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

/*
 * Copyright @2019. MOVIIRED. Todos los derechos reservados.
 *
 * @author JAP, SBD
 * @version 1, 2019-08-30
 * @since 2.0
 */

@Component
public class SmsDBConfig implements Serializable {
    private static final int PREP_STMT_CACHE_SIZE = 250;
    private static final int PREP_STMT_CACHE_SQL_LIMIT = 2048;

    private final transient DataSource supportSmsDataSource;

    SmsDBConfig(@NotNull SmsProperties smsProperties) {
        super();
        // Crear un POOL de conexiones
        HikariConfig hikConfig = new HikariConfig();
        hikConfig.setDriverClassName(smsProperties.getDriverDb());
        hikConfig.setPoolName(smsProperties.getPoolNameDb());
        hikConfig.setJdbcUrl(smsProperties.getUrlDb());
        hikConfig.setUsername(smsProperties.getUserDb());
        hikConfig.setPassword(smsProperties.getPassDb());
        hikConfig.setConnectionTimeout(smsProperties.getConnectionTimeoutDb());
        hikConfig.setMinimumIdle(smsProperties.getMinimumIdleDb());
        hikConfig.setMaximumPoolSize(smsProperties.getMaximumPoolSizeDb());
        hikConfig.setIdleTimeout(smsProperties.getIdleTimeoutDb());
        hikConfig.setMaxLifetime(smsProperties.getMaxLifetimeDb());
        hikConfig.setAllowPoolSuspension(smsProperties.getAllowPoolSuspensionDb());
        hikConfig.setAutoCommit(smsProperties.getAutoCommitDb());
        hikConfig.setCatalog(smsProperties.getCatalogDb());

        // Optimización predefinida DEL POOL
        hikConfig.addDataSourceProperty("cachePrepStmts", Boolean.TRUE);
        hikConfig.addDataSourceProperty("prepStmtCacheSize", PREP_STMT_CACHE_SIZE);
        hikConfig.addDataSourceProperty("prepStmtCacheSqlLimit", PREP_STMT_CACHE_SQL_LIMIT);
        hikConfig.addDataSourceProperty("useLocalSessionState", Boolean.TRUE);
        hikConfig.addDataSourceProperty("rewriteBatchedStatements", Boolean.TRUE);
        hikConfig.addDataSourceProperty("cacheResultSetMetadata", Boolean.TRUE);
        hikConfig.addDataSourceProperty("elideSetAutoCommits", Boolean.TRUE);
        hikConfig.addDataSourceProperty("maintainTimeStats", Boolean.TRUE);

        supportSmsDataSource = new HikariDataSource(hikConfig);
    }

    public final Connection getConnection() throws SQLException {
        return supportSmsDataSource.getConnection();
    }

}

