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

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "registerEntityManagerFactory",
        transactionManagerRef = "registerTransactionManager",
        basePackages = {"com.moviired.repository.jpa.moviiregister"}
)
public class MoviiRegisterDataSource {


    private static final int PREP_STMT_CACHE_SIZE = 250;
    private static final int PREP_STMT_CACHE_SQL_LIMIT = 2048;

    /**
     * metodo datasource
     *
     * @param env
     * @return DataSource
     */
    @Bean(name = "registerDataSource")
    public DataSource registerDataSource(Environment env) {
        HikariConfig conf = new HikariConfig();
        conf.setDriverClassName(env.getProperty("moviiregister.datasource.driver-class-name"));
        conf.setPoolName(env.getProperty("moviiregister.datasource.hikari.pool-name"));
        conf.setJdbcUrl(env.getProperty("moviiregister.datasource.url"));
        conf.setUsername(env.getProperty("moviiregister.datasource.username"));
        conf.setPassword(env.getProperty("moviiregister.datasource.password"));
        conf.setMinimumIdle(Integer.parseInt(Objects.requireNonNull(env.getProperty("moviiregister.datasource.hikari.minimum-idle"))));
        conf.setMaximumPoolSize(Integer.parseInt(Objects.requireNonNull(env.getProperty("moviiregister.datasource.hikari.maximum-pool-size"))));
        conf.setConnectionTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("moviiregister.datasource.hikari.connection-timeout"))));
        conf.setAutoCommit(Boolean.parseBoolean(env.getProperty("moviiregister.datasource.hikari.auto-commit")));
        conf.setIdleTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("moviiregister.datasource.hikari.idle-timeout"))));
        conf.setCatalog(env.getProperty("moviiregister.datasource.hikari.catalog"));
        conf.setConnectionTestQuery(env.getProperty("moviiregister.datasource.hikari.connection-test-query"));

        // Optimización predefinida DEL POOL
        conf.addDataSourceProperty("cachePrepStmts", Boolean.TRUE);
        conf.addDataSourceProperty("prepStmtCacheSize", PREP_STMT_CACHE_SIZE);
        conf.addDataSourceProperty("autoReconnect", Boolean.TRUE);
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
    @Bean(name = "registerEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean registerEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("registerDataSource") DataSource registerDataSource, Environment env
    ) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));

        return builder.dataSource(registerDataSource)
                .packages("com.moviired.model.entities.moviiregister")
                .persistenceUnit("moviiregister")
                .properties(properties)
                .build();
    }

    /**
     * metodo transactionManager
     *
     * @param registerEntityManagerFactory
     * @return PlatformTransactionManager
     */

    @Bean(name = "registerTransactionManager")
    public PlatformTransactionManager registerTransactionManager(
            @Qualifier("registerEntityManagerFactory") EntityManagerFactory registerEntityManagerFactory) {
        return new JpaTransactionManager(registerEntityManagerFactory);
    }


}

