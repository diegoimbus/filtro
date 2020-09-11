package co.moviired.transaction.conf.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class DatabasePool implements Serializable {

    private static final long serialVersionUID = 6070870319427985188L;

    private static final int PREP_STMT_CACHE_SIZE = 250;
    private static final int PREP_STMT_CACHE_SQL_LIMIT = 2048;

    private final transient HikariDataSource datasource;

    public DatabasePool(@NotNull IDatabaseProperties config) {
        super();

        // Crear un POOL de conexiones
        HikariConfig conf = new HikariConfig();
        conf.setDriverClassName(config.getDriverDb());
        conf.setPoolName(config.getPoolNameDb());
        conf.setJdbcUrl(config.getUrlDb());
        conf.setUsername(config.getUserDb());
        conf.setPassword(config.getPassDb());
        conf.setMinimumIdle(config.getMinimumIdle());
        conf.setMaximumPoolSize(config.getMaximumPoolSize());
        conf.setConnectionTimeout(config.getConnectionTimeout());
        conf.setIdleTimeout(config.getIdleTimeout());
        conf.setMaxLifetime(config.getMaxLifetime());
        conf.setAutoCommit(config.isAutoCommit());
        conf.setConnectionTestQuery(config.getTestQuery());

        // Optimización predefinida DEL POOL
        conf.addDataSourceProperty("cachePrepStmts", Boolean.TRUE);
        conf.addDataSourceProperty("prepStmtCacheSize", PREP_STMT_CACHE_SIZE);
        conf.addDataSourceProperty("prepStmtCacheSqlLimit", PREP_STMT_CACHE_SQL_LIMIT);
        conf.addDataSourceProperty("useServerPrepStmts", Boolean.FALSE);
        conf.addDataSourceProperty("useLocalSessionState", Boolean.TRUE);
        conf.addDataSourceProperty("rewriteBatchedStatements", Boolean.TRUE);
        conf.addDataSourceProperty("cacheResultSetMetadata", Boolean.TRUE);
        conf.addDataSourceProperty("elideSetAutoCommits", Boolean.TRUE);
        conf.addDataSourceProperty("maintainTimeStats", Boolean.TRUE);

        datasource = new HikariDataSource(conf);
    }

    public final Connection getConnection() throws SQLException {
        return datasource.getConnection();
    }

}

