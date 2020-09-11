package co.moviired.support.conf;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MahindraHikariDatasource extends HikariDataSource {

    private final String defaultSchema;

    public MahindraHikariDatasource(HikariConfig configuration) {
        super(configuration);
        defaultSchema = configuration.getCatalog();
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = super.getConnection();
        try (Statement statement = conn.createStatement()) {
            final String SQL = "ALTER SESSION SET CURRENT_SCHEMA = " + defaultSchema;
            statement.execute(SQL);
        }
        return conn;
    }

}

