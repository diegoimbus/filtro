package co.moviired.business.conf.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
@EnableJpaRepositories(basePackages = {"co.moviired.business.domain.jpa.movii.repository"})
public class MoviiDBConfig {

    private static final int PREP_STMT_CACHE_SIZE = 250;
    private static final int PREP_STMT_CACHE_SQL_LIMIT = 2048;

    @Primary
    @Bean(name = "dataSource")
    public DataSource dataSource(Environment env) {
        HikariConfig confMysqlMovii = new HikariConfig();
        confMysqlMovii.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        confMysqlMovii.setPoolName(env.getProperty("spring.datasource.hikari.pool-name"));
        confMysqlMovii.setJdbcUrl(env.getProperty("spring.datasource.jdbcUrl"));
        confMysqlMovii.setUsername(env.getProperty("spring.datasource.username"));
        confMysqlMovii.setPassword(env.getProperty("spring.datasource.password"));

        confMysqlMovii.setCatalog(env.getProperty("spring.datasource.hikari.catalog"));
        confMysqlMovii.setConnectionTestQuery(env.getProperty("spring.datasource.hikari.connection-test-query"));
        confMysqlMovii.setAutoCommit(Boolean.parseBoolean(env.getProperty("spring.datasource.hikari.auto-commit")));
        confMysqlMovii.setAllowPoolSuspension(Boolean.parseBoolean(env.getProperty("spring.datasource.hikari.allow-pool-suspension")));
        confMysqlMovii.setConnectionTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.connection-timeout"))));
        confMysqlMovii.setMaximumPoolSize(Integer.parseInt(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.maximum-pool-size"))));
        confMysqlMovii.setMinimumIdle(Integer.parseInt(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.minimum-idle"))));
        confMysqlMovii.setMaxLifetime(Long.parseLong(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.max-lifetime"))));
        confMysqlMovii.setIdleTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.idle-timeout"))));

        // Optimización predefinida DEL POOL
        confMysqlMovii.addDataSourceProperty("cachePrepStmts", Boolean.TRUE);
        confMysqlMovii.addDataSourceProperty("prepStmtCacheSize", PREP_STMT_CACHE_SIZE);
        confMysqlMovii.addDataSourceProperty("prepStmtCacheSqlLimit", PREP_STMT_CACHE_SQL_LIMIT);
        confMysqlMovii.addDataSourceProperty("useLocalSessionState", Boolean.TRUE);
        confMysqlMovii.addDataSourceProperty("rewriteBatchedStatements", Boolean.TRUE);
        confMysqlMovii.addDataSourceProperty("cacheResultSetMetadata", Boolean.TRUE);
        confMysqlMovii.addDataSourceProperty("elideSetAutoCommits", Boolean.TRUE);
        confMysqlMovii.addDataSourceProperty("maintainTimeStats", Boolean.TRUE);

        return new HikariDataSource(confMysqlMovii);
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dataSource") DataSource dataSource) {
        return builder.dataSource(dataSource)
                .packages("co.moviired.business.domain.jpa.movii.entity")
                .persistenceUnit("movii")
                .build();
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}


