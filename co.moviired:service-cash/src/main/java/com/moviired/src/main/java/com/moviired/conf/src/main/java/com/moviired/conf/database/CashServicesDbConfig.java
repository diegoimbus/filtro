package com.moviired.conf.database;

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
 * @author carlossaul.ramirez
 */

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {"com.moviired.repository.jpa.cashservice"}
)
public class CashServicesDbConfig {

    private static final int PREP_STMT_CACHE_SIZE = 250;
    private static final int PREP_STMT_CACHE_SQL_LIMIT = 2048;

    /**
     * metodo datasource
     *
     * @param env
     * @return DataSource
     */
    @Primary
    @Bean(name = "dataSource")
    public DataSource datasource(Environment env) {
        HikariConfig conf = new HikariConfig();
        conf.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        conf.setPoolName(env.getProperty("spring.datasource.hikari.pool-name"));
        conf.setJdbcUrl(env.getProperty("spring.datasource.jdbcUrl"));
        conf.setUsername(env.getProperty("spring.datasource.username"));
        conf.setPassword(env.getProperty("spring.datasource.password"));
        conf.setMinimumIdle(Integer.parseInt(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.minimum-idle"))));
        conf.setMaximumPoolSize(Integer.parseInt(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.maximum-pool-size"))));
        conf.setConnectionTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.connection-timeout"))));
        conf.setAutoCommit(Boolean.parseBoolean(env.getProperty("spring.datasource.hikari.auto-commit")));
        conf.setIdleTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.idle-timeout"))));
        conf.setMaxLifetime(Long.parseLong(Objects.requireNonNull(env.getProperty("spring.datasource.hikari.max-lifetime"))));
        conf.setCatalog(env.getProperty("spring.datasource.hikari.catalog"));
        conf.setConnectionTestQuery(env.getProperty("spring.datasource.hikari.connection-test-query"));

        // Optimización predefinida DEL POOL
        conf.addDataSourceProperty("cachePrepStmts", Boolean.TRUE);
        conf.addDataSourceProperty("autoReconnect", Boolean.TRUE);
        conf.addDataSourceProperty("prepStmtCacheSize", PREP_STMT_CACHE_SIZE);
        conf.addDataSourceProperty("prepStmtCacheSqlLimit", PREP_STMT_CACHE_SQL_LIMIT);
        conf.addDataSourceProperty("useServerPrepStmts", Boolean.FALSE);
        conf.addDataSourceProperty("useLocalSessionState", Boolean.TRUE);
        conf.addDataSourceProperty("rewriteBatchedStatements", Boolean.TRUE);
        conf.addDataSourceProperty("cacheResultSetMetadata", Boolean.TRUE);
        conf.addDataSourceProperty("elideSetAutoCommits", Boolean.TRUE);
        conf.addDataSourceProperty("maintainTimeStats", Boolean.TRUE);

        return new HikariDataSource(conf);
    }

    /**
     * metodo entityManagerFactory
     *
     * @param builder,env,dataSource
     * @return LocalContainerEntityManagerFactoryBean
     */
    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dataSource") DataSource dataSource, Environment env
    ) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));

        return builder.dataSource(dataSource)
                .packages("com.moviired.model.entities.cashservice")
                .persistenceUnit("cashservice")
                .properties(properties)
                .build();
    }

    /**
     * metodo transactionManager
     *
     * @param entityManagerFactory
     * @return PlatformTransactionManager
     */
    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

