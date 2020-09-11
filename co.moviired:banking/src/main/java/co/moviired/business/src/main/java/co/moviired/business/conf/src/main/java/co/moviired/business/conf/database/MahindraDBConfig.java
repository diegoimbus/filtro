package co.moviired.business.conf.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "mahindraEntityManagerFactory",
        transactionManagerRef = "mahindraTransactionManager",
        basePackages = {"co.moviired.business.domain.jpa.mahindra.repository"})
public class MahindraDBConfig {

    private static final int PREP_STMT_CACHE_SIZE = 250;
    private static final int PREP_STMT_CACHE_SQL_LIMIT = 2048;

    @Bean(name = "mahindraDataSource")
    public DataSource dataSource(Environment env) {
        HikariConfig confOracle = new HikariConfig();
        confOracle.setDriverClassName(env.getProperty("mahindradb.datasource.driver-class-name"));
        confOracle.setPoolName(env.getProperty("mahindradb.datasource.hikari.pool-name"));
        confOracle.setJdbcUrl(env.getProperty("mahindradb.datasource.jdbcUrl"));
        confOracle.setUsername(env.getProperty("mahindradb.datasource.username"));
        confOracle.setPassword(env.getProperty("mahindradb.datasource.password"));
        confOracle.setSchema(env.getProperty("mahindradb.datasource.schema"));

        confOracle.setCatalog(env.getProperty("mahindradb.datasource.hikari.catalog"));
        confOracle.setConnectionTestQuery(env.getProperty("mahindradb.datasource.hikari.connection-test-query"));
        confOracle.setAutoCommit(Boolean.parseBoolean(env.getProperty("mahindradb.datasource.hikari.auto-commit")));
        confOracle.setAllowPoolSuspension(Boolean.parseBoolean(env.getProperty("mahindradb.datasource.hikari.allow-pool-suspension")));
        confOracle.setConnectionTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("mahindradb.datasource.hikari.connection-timeout"))));
        confOracle.setMaximumPoolSize(Integer.parseInt(Objects.requireNonNull(env.getProperty("mahindradb.datasource.hikari.maximum-pool-size"))));
        confOracle.setMinimumIdle(Integer.parseInt(Objects.requireNonNull(env.getProperty("mahindradb.datasource.hikari.minimum-idle"))));
        confOracle.setMaxLifetime(Long.parseLong(Objects.requireNonNull(env.getProperty("mahindradb.datasource.hikari.max-lifetime"))));
        confOracle.setIdleTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("mahindradb.datasource.hikari.idle-timeout"))));

        // Optimización predefinida DEL POOL
        confOracle.addDataSourceProperty("cachePrepStmts", Boolean.TRUE);
        confOracle.addDataSourceProperty("prepStmtCacheSize", PREP_STMT_CACHE_SIZE);
        confOracle.addDataSourceProperty("prepStmtCacheSqlLimit", PREP_STMT_CACHE_SQL_LIMIT);
        confOracle.addDataSourceProperty("useServerPrepStmts", Boolean.TRUE);
        confOracle.addDataSourceProperty("useLocalSessionState", Boolean.TRUE);
        confOracle.addDataSourceProperty("rewriteBatchedStatements", Boolean.TRUE);
        confOracle.addDataSourceProperty("cacheResultSetMetadata", Boolean.TRUE);
        confOracle.addDataSourceProperty("elideSetAutoCommits", Boolean.TRUE);
        confOracle.addDataSourceProperty("maintainTimeStats", Boolean.TRUE);

        return new HikariDataSource(confOracle);
    }

    @Bean(name = "mahindraEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mahindraEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("mahindraDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource)
                .packages("co.moviired.business.domain.jpa.mahindra.entity")
                .persistenceUnit("mahindra")
                .build();
    }

    @Bean(name = "mahindraTransactionManager")
    public PlatformTransactionManager mahindraTransactionManager(
            @Qualifier("mahindraEntityManagerFactory") EntityManagerFactory mahindraEntityManagerFactory) {
        return new JpaTransactionManager(mahindraEntityManagerFactory);
    }

}

