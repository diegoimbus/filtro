package co.moviired.support.conf.database;

import co.moviired.support.conf.RedShiftHikariDatasource;
import com.zaxxer.hikari.HikariConfig;
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
 * @author Rodolfo.rivas0
 */

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "redShiftEntityManagerFactory",
        transactionManagerRef = "redShiftTransactionManager",
        basePackages = {"co.moviired.support.domain.repository.redshift"})
public class RedShiftDbConfig {

    private static final int PREP_STMT_CACHE_SIZE = 250;
    private static final int PREP_STMT_CACHE_SQL_LIMIT = 2048;

    @Bean(name = "redShiftDataSource")
    public DataSource redShiftDataSource(Environment env) {

        HikariConfig conf = new HikariConfig();
        conf.setDriverClassName(env.getProperty("redshift.datasource.driver-class-name"));
        conf.setPoolName(env.getProperty("redshift.datasource.hikari.pool-name"));
        conf.setJdbcUrl(env.getProperty("redshift.datasource.jdbcUrl"));
        conf.setUsername(env.getProperty("redshift.datasource.username"));
        conf.setPassword(env.getProperty("redshift.datasource.password"));
        conf.setMinimumIdle(Integer.parseInt(Objects.requireNonNull(env.getProperty("redshift.datasource.hikari.minimum-idle"))));
        conf.setMaximumPoolSize(Integer.parseInt(Objects.requireNonNull(env.getProperty("redshift.datasource.hikari.maximum-pool-size"))));
        conf.setConnectionTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("redshift.datasource.hikari.connection-timeout"))));
        conf.setAutoCommit(Boolean.parseBoolean(env.getProperty("redshift.datasource.hikari.auto-commit")));
        conf.setIdleTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("redshift.datasource.hikari.idle-timeout"))));
        conf.setMaxLifetime(Long.parseLong(Objects.requireNonNull(env.getProperty("redshift.datasource.hikari.max-lifetime"))));
        conf.setCatalog(Objects.requireNonNull(env.getProperty("redshift.datasource.hikari.catalog")));

        // Optimización predefinida DEL POOL
        conf.addDataSourceProperty("cachePrepStmts", Boolean.TRUE);
        conf.addDataSourceProperty("prepStmtCacheSize", PREP_STMT_CACHE_SIZE);
        conf.addDataSourceProperty("prepStmtCacheSqlLimit", PREP_STMT_CACHE_SQL_LIMIT);
        conf.addDataSourceProperty("useServerPrepStmts", Boolean.TRUE);
        conf.addDataSourceProperty("useLocalSessionState", Boolean.TRUE);
        conf.addDataSourceProperty("rewriteBatchedStatements", Boolean.TRUE);
        conf.addDataSourceProperty("cacheResultSetMetadata", Boolean.TRUE);
        conf.addDataSourceProperty("elideSetAutoCommits", Boolean.TRUE);
        conf.addDataSourceProperty("maintainTimeStats", Boolean.TRUE);

        return new RedShiftHikariDatasource(conf);
    }

    @Bean(name = "redShiftEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean redShiftEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("redShiftDataSource") DataSource redShiftDataSource, Environment env) {

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        properties.put("hibernate.dialect", "co.moviired.support.conf.database.CustomRedshiftDialect");

        return builder
                .dataSource(redShiftDataSource)
                .packages("co.moviired.support.domain.entity.redshift")
                .persistenceUnit("redShift")
                .properties(properties)
                .build();

    }

    @Bean(name = "redShiftTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("redShiftEntityManagerFactory") EntityManagerFactory redShiftEntityManagerFactory) {
        return new JpaTransactionManager(redShiftEntityManagerFactory);
    }
}

