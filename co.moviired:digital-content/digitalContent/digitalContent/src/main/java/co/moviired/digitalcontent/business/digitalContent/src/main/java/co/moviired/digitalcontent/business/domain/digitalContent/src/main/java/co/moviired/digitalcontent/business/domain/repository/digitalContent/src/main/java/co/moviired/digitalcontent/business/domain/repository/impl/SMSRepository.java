package co.moviired.digitalcontent.business.domain.repository.impl;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.digitalcontent.business.properties.SMSProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Slf4j
@Service
public class SMSRepository implements Serializable {
    private static final long serialVersionUID = -5600927115510259399L;

    private static final int PREP_STMT_CACHE_SIZE = 250;
    private static final int PREP_STMT_CACHE_SQL_LIMIT = 2048;
    private static final String QUERY = "INSERT INTO SMS(srcAddress,destAddress,messageText,inserted_at,channel) VALUES ('DigitalContent',?,?,now(),1)";

    private final DataSource dataSource;
    private final SMSProperties config;

    public SMSRepository(@NotNull SMSProperties config) {
        super();
        this.config = config;

        // Crear un POOL de conexiones
        HikariConfig hikConfig = new HikariConfig();
        hikConfig.setDriverClassName(config.getDriverSmsDb());
        hikConfig.setPoolName(config.getPoolNameSmsDb());
        hikConfig.setJdbcUrl(config.getUrlSmsDb());
        hikConfig.setUsername(config.getUserSmsDb());
        hikConfig.setPassword(config.getPassSmsDb());

        // Optimización predefinida DEL POOL
        hikConfig.addDataSourceProperty("cachePrepStmts", Boolean.TRUE);
        hikConfig.addDataSourceProperty("prepStmtCacheSize", PREP_STMT_CACHE_SIZE);
        hikConfig.addDataSourceProperty("prepStmtCacheSqlLimit", PREP_STMT_CACHE_SQL_LIMIT);
        hikConfig.addDataSourceProperty("useServerPrepStmts", Boolean.TRUE);
        hikConfig.addDataSourceProperty("useLocalSessionState", Boolean.TRUE);
        hikConfig.addDataSourceProperty("rewriteBatchedStatements", Boolean.TRUE);
        hikConfig.addDataSourceProperty("cacheResultSetMetadata", Boolean.TRUE);
        hikConfig.addDataSourceProperty("cacheServerConfiguration", Boolean.TRUE);
        hikConfig.addDataSourceProperty("elideSetAutoCommits", Boolean.TRUE);
        hikConfig.addDataSourceProperty("maintainTimeStats", Boolean.TRUE);

        this.dataSource = new HikariDataSource(hikConfig);
    }

    private Connection getConnection() throws SQLException {
        Connection conn;
        try {
            conn = this.dataSource.getConnection();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new SQLException(e);
        }

        return conn;
    }


    public final void sendSMS(String phoneNumber, String mensaje) throws ServiceException {
        try (Connection connection = this.getConnection();
             PreparedStatement cstmt = connection.prepareCall(QUERY)) {

            cstmt.setString(1, this.config.getSufijoCelular() + phoneNumber);
            cstmt.setString(2, mensaje);
            cstmt.executeUpdate();

        } catch (Exception e) {
            throw new ServiceException(ErrorType.COMMUNICATION, "-20", e.getMessage(), e);
        }
    }


}

