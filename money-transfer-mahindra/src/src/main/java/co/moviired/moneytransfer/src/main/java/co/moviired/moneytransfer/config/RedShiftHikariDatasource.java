package co.moviired.moneytransfer.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.HibernateException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RedShiftHikariDatasource extends HikariDataSource {

    private final String defaultSchema;

    public RedShiftHikariDatasource(HikariConfig configuration) {
        super(configuration);
        defaultSchema = configuration.getCatalog();
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn;
        PreparedStatement st = null;
        String sql = "select set_config('search_path', ?, false)";

        try {
            conn = super.getConnection();
            st = conn.prepareStatement(sql);
            st.setString(1, defaultSchema);
            st.execute();
        } catch (SQLException e) {
            throw new HibernateException("Problem setting schema to " + defaultSchema, e);
        } finally {
            if (st != null) {
                st.close();
            }
        }
        return conn;
    }

}

