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
        entityManagerFactoryRef = "getraxEntityManagerFactory",
        transactionManagerRef = "getraxTransactionManager",
        basePackages = {"co.moviired.business.domain.jpa.getrax.repository"})
public class GetraxDBConfig {

    private static final int PREP_STMT_CACHE_SIZE = 250;
    private static final int PREP_STMT_CACHE_SQL_LIMIT = 2048;

    @Bean(name = "getraxDataSource")
    public DataSource dataSource(Environment env) {
        HikariConfig confMysqlGetrax = new HikariConfig();
        confMysqlGetrax.setDriverClassName(env.getProperty("getrax.datasource.driver-class-name"));
        confMysqlGetrax.setPoolName(env.getProperty("getrax.datasource.hikari.pool-name"));
        confMysqlGetrax.setJdbcUrl(env.getProperty("getrax.datasource.jdbcUrl"));
        confMysqlGetrax.setUsername(env.getProperty("getrax.datasource.username"));
        confMysqlGetrax.setPassword(env.getProperty("getrax.datasource.password"));

        confMysqlGetrax.setCatalog(env.getProperty("getrax.datasource.hikari.catalog"));
        confMysqlGetrax.setConnectionTestQuery(env.getProperty("getrax.datasource.hikari.connection-test-query"));
        confMysqlGetrax.setAutoCommit(Boolean.parseBoolean(env.getProperty("getrax.datasource.hikari.auto-commit")));
        confMysqlGetrax.setAllowPoolSuspension(Boolean.parseBoolean(env.getProperty("getrax.datasource.hikari.allow-pool-suspension")));
        confMysqlGetrax.setConnectionTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("getrax.datasource.hikari.connection-timeout"))));
        confMysqlGetrax.setMaximumPoolSize(Integer.parseInt(Objects.requireNonNull(env.getProperty("getrax.datasource.hikari.maximum-pool-size"))));
        confMysqlGetrax.setMinimumIdle(Integer.parseInt(Objects.requireNonNull(env.getProperty("getrax.datasource.hikari.minimum-idle"))));
        confMysqlGetrax.setMaxLifetime(Long.parseLong(Objects.requireNonNull(env.getProperty("getrax.datasource.hikari.max-lifetime"))));
        confMysqlGetrax.setIdleTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("getrax.datasource.hikari.idle-timeout"))));

        // Optimización predefinida DEL POOL
        confMysqlGetrax.addDataSourceProperty("cachePrepStmts", Boolean.TRUE);
        confMysqlGetrax.addDataSourceProperty("prepStmtCacheSize", PREP_STMT_CACHE_SIZE);
        confMysqlGetrax.addDataSourceProperty("prepStmtCacheSqlLimit", PREP_STMT_CACHE_SQL_LIMIT);
        confMysqlGetrax.addDataSourceProperty("useServerPrepStmts", Boolean.TRUE);
        confMysqlGetrax.addDataSourceProperty("useLocalSessionState", Boolean.TRUE);
        confMysqlGetrax.addDataSourceProperty("rewriteBatchedStatements", Boolean.TRUE);
        confMysqlGetrax.addDataSourceProperty("cacheResultSetMetadata", Boolean.TRUE);
        confMysqlGetrax.addDataSourceProperty("elideSetAutoCommits", Boolean.TRUE);
        confMysqlGetrax.addDataSourceProperty("maintainTimeStats", Boolean.TRUE);

        return new HikariDataSource(confMysqlGetrax);
    }

    @Bean(name = "getraxEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean getraxEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("getraxDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource)
                .packages("co.moviired.business.domain.jpa.getrax.entity")
                .persistenceUnit("getrax")
                .build();
    }

    @Bean(name = "getraxTransactionManager")
    public PlatformTransactionManager getraxTransactionManager(
            @Qualifier("getraxEntityManagerFactory") EntityManagerFactory getraxEntityManagerFactory) {
        return new JpaTransactionManager(getraxEntityManagerFactory);
    }
}


