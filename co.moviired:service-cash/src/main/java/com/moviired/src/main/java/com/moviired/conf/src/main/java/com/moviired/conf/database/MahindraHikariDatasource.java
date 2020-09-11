package com.moviired.conf.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MahindraHikariDatasource extends HikariDataSource {

    private String defaultSchema;

    MahindraHikariDatasource(HikariConfig configuration) {
        super(configuration);
        defaultSchema = configuration.getCatalog();
    }

    /**
     * metodo getConnection
     */
    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = super.getConnection();
        try (Statement statement = conn.createStatement()) {
            final String sql = "ALTER SESSION SET CURRENT_SCHEMA =" + defaultSchema;
            statement.execute(sql);
        }

        return conn;
    }


}

