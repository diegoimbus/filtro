package co.moviired.support.conf.database;

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
import java.util.HashMap;
import java.util.Objects;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 */

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"co.moviired.support.domain.repository.mysql"})
public class MysqlDbConfig {

    private static final int PREP_STMT_CACHE_SIZE = 250;
    private static final int PREP_STMT_CACHE_SQL_LIMIT = 2048;

    @Bean(name = "dataSource")
    @Primary
    public DataSource datasource(Environment env) {

        HikariConfig confMysql = new HikariConfig();
        confMysql.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        confMysql.setPoolName(env.getProperty("spring.datasource.hikari.pool-name"));
        confMysql.setJdbcUrl(env.getProperty("spring.datasource.jdbcUrl"));
        confMysql.setUsername(env.getProperty("spring.datasource.username"));
        confMysql.setPassword(env.getProperty("spring.datasource.password"));
        confMysql.setMinimumIdle(Integer.parseInt(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.minimum-idle"))));
        confMysql.setMaximumPoolSize(Integer.parseInt(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.maximum-pool-size"))));
        confMysql.setConnectionTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.connection-timeout"))));
        confMysql.setAutoCommit(Boolean.parseBoolean(env.getProperty("spring.datasource.hikari.auto-commit")));
        confMysql.setIdleTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.idle-timeout"))));
        confMysql.setMaxLifetime(Long.parseLong(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.max-lifetime"))));
        confMysql.setConnectionTestQuery(env.getProperty("spring.datasource.hikari.connection-test-query"));

        // Optimización predefinida DEL POOL
        confMysql.addDataSourceProperty("cachePrepStmts", Boolean.TRUE);
        confMysql.addDataSourceProperty("prepStmtCacheSize", PREP_STMT_CACHE_SIZE);
        confMysql.addDataSourceProperty("prepStmtCacheSqlLimit", PREP_STMT_CACHE_SQL_LIMIT);
        confMysql.addDataSourceProperty("useServerPrepStmts", Boolean.TRUE);
        confMysql.addDataSourceProperty("useLocalSessionState", Boolean.TRUE);
        confMysql.addDataSourceProperty("rewriteBatchedStatements", Boolean.TRUE);
        confMysql.addDataSourceProperty("cacheResultSetMetadata", Boolean.TRUE);
        confMysql.addDataSourceProperty("elideSetAutoCommits", Boolean.TRUE);
        confMysql.addDataSourceProperty("maintainTimeStats", Boolean.TRUE);

        return new HikariDataSource(confMysql);
    }


    @Bean(name = "entityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dataSource") DataSource dataSource, Environment env
    ) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));

        return builder.dataSource(dataSource)
                .packages("co.moviired.support.domain.entity.mysql")
                .persistenceUnit("cashservice")
                .properties(properties)
                .build();
    }


    @Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

