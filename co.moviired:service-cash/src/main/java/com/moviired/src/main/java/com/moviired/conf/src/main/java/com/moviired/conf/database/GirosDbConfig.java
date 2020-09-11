package com.moviired.conf.database;

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
import java.util.HashMap;
import java.util.Objects;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author jap, sbd
 * @category srv-cash
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "girosEntityManagerFactory",
        transactionManagerRef = "girosTransactionManager",
        basePackages = {
                "com.moviired.repository.jpa.giros"
        })
public class GirosDbConfig {

    private static final int PREP_STMT_CACHE_SIZE = 250;
    private static final int PREP_STMT_CACHE_SQL_LIMIT = 2048;

    /**
     * metodo datasource
     *
     * @param env
     * @return DataSource
     */
    @Bean(name = "girosDataSource")
    public DataSource girosDatasource(Environment env) {
        HikariConfig conf = new HikariConfig();
        conf.setDriverClassName(env.getProperty("giros.datasource.driver-class-name"));
        conf.setPoolName(env.getProperty("giros.datasource.hikari.pool-name"));
        conf.setJdbcUrl(env.getProperty("giros.datasource.jdbcUrl"));
        conf.setUsername(env.getProperty("giros.datasource.username"));
        conf.setPassword(env.getProperty("giros.datasource.password"));
        conf.setMinimumIdle(Integer.parseInt(Objects.requireNonNull(env.getProperty("giros.datasource.hikari.minimum-idle"))));
        conf.setMaximumPoolSize(Integer.parseInt(Objects.requireNonNull(env.getProperty("giros.datasource.hikari.maximum-pool-size"))));
        conf.setConnectionTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("giros.datasource.hikari.connection-timeout"))));
        conf.setAutoCommit(Boolean.parseBoolean(env.getProperty("giros.datasource.hikari.auto-commit")));
        conf.setIdleTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("giros.datasource.hikari.idle-timeout"))));
        conf.setMaxLifetime(Long.parseLong(Objects.requireNonNull(env.getProperty("giros.datasource.hikari.max-lifetime"))));
        conf.setCatalog(env.getProperty("giros.datasource.hikari.catalog"));
        conf.setConnectionTestQuery(env.getProperty("giros.datasource.hikari.connection-test-query"));

        // Optimización predefinida DEL POOL
        conf.addDataSourceProperty("cachePrepStmts", Boolean.TRUE);
        conf.addDataSourceProperty("prepStmtCacheSize", PREP_STMT_CACHE_SIZE);
        conf.addDataSourceProperty("useServerPrepStmts", Boolean.FALSE);
        conf.addDataSourceProperty("autoReconnect", Boolean.TRUE);
        conf.addDataSourceProperty("prepStmtCacheSqlLimit", PREP_STMT_CACHE_SQL_LIMIT);
        conf.addDataSourceProperty("useLocalSessionState", Boolean.TRUE);
        conf.addDataSourceProperty("rewriteBatchedStatements", Boolean.TRUE);
        conf.addDataSourceProperty("cacheResultSetMetadata", Boolean.TRUE);
        conf.addDataSourceProperty("elideSetAutoCommits", Boolean.TRUE);
        conf.addDataSourceProperty("maintainTimeStats", Boolean.TRUE);

        return new HikariDataSource(conf);
    }

    /**
     * metodo girosEntityManagerFactory
     *
     * @param builder,env,dataSource
     * @return LocalContainerEntityManagerFactoryBean
     */
    @Bean(name = "girosEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean girosEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("girosDataSource") DataSource girosDataSource, Environment env) {

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));

        return builder
                .dataSource(girosDataSource)
                .packages("com.moviired.model.entities.giros")
                .persistenceUnit("giros")
                .properties(properties)
                .build();
    }

    /**
     * metodo girosTransactionManager
     *
     * @param girosEntityManagerFactory
     * @return PlatformTransactionManager
     */
    @Bean(name = "girosTransactionManager")
    public PlatformTransactionManager girosTransactionManager(
            @Qualifier("girosEntityManagerFactory") EntityManagerFactory girosEntityManagerFactory) {
        return new JpaTransactionManager(girosEntityManagerFactory);
    }

}

