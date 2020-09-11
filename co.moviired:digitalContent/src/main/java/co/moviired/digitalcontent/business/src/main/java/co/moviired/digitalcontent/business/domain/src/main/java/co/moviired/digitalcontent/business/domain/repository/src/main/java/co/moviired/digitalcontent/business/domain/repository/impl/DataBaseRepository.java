package co.moviired.digitalcontent.business.domain.repository.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public abstract class DataBaseRepository implements Serializable {

    private static final long serialVersionUID = -5600927115510259399L;

    private static final int PREP_STMT_CACHE_SIZE = 250;
    private static final int PREP_STMT_CACHE_SQL_LIMIT = 2048;

    protected final transient DataSource dataSource;

    protected DataBaseRepository(String driver, String poolName, String url, String user, String key) {
        // Create connections pool
        HikariConfig hikConfig = new HikariConfig();
        hikConfig.setDriverClassName(driver);
        hikConfig.setPoolName(poolName);
        hikConfig.setJdbcUrl(url);
        hikConfig.setUsername(user);
        hikConfig.setPassword(key);

        // Pool configuration
        hikConfig.addDataSourceProperty("cachePrepStmts", Boolean.TRUE);
        hikConfig.addDataSourceProperty("prepStmtCacheSize", PREP_STMT_CACHE_SIZE);
        hikConfig.addDataSourceProperty("prepStmtCacheSqlLimit", PREP_STMT_CACHE_SQL_LIMIT);
        hikConfig.addDataSourceProperty("useServerPrepStmts", Boolean.FALSE);
        hikConfig.addDataSourceProperty("useLocalSessionState", Boolean.TRUE);
        hikConfig.addDataSourceProperty("rewriteBatchedStatements", Boolean.TRUE);
        hikConfig.addDataSourceProperty("cacheResultSetMetadata", Boolean.TRUE);
        hikConfig.addDataSourceProperty("cacheServerConfiguration", Boolean.TRUE);
        hikConfig.addDataSourceProperty("elideSetAutoCommits", Boolean.TRUE);
        hikConfig.addDataSourceProperty("maintainTimeStats", Boolean.TRUE);

        this.dataSource = new HikariDataSource(hikConfig);
    }

    protected Connection getConnection() throws SQLException {
        try {
            return this.dataSource.getConnection();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new SQLException(e);
        }
    }
}

