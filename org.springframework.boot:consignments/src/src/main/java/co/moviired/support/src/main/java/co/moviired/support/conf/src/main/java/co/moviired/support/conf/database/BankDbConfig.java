package co.moviired.support.conf.database;

import co.moviired.support.conf.MahindraHikariDatasource;
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
        entityManagerFactoryRef = "bankEntityManagerFactory",
        transactionManagerRef = "bankTransactionManager",
        basePackages = {"co.moviired.support.domain.repository.mahindra"})
public class BankDbConfig {

    private static final int PREP_STMT_CACHE_SIZE = 250;
    private static final int PREP_STMT_CACHE_SQL_LIMIT = 2048;

    @Bean(name = "bankDataSource")
    public DataSource bankDataSource(Environment env) {

        HikariConfig confBank = new HikariConfig();
        confBank.setDriverClassName(env.getProperty("oracle.datasource.driver-class-name"));
        confBank.setPoolName(env.getProperty("oracle.datasource.hikari.pool-name"));
        confBank.setJdbcUrl(env.getProperty("oracle.datasource.jdbcUrl"));
        confBank.setUsername(env.getProperty("oracle.datasource.username"));
        confBank.setPassword(env.getProperty("oracle.datasource.password"));
        confBank.setMinimumIdle(Integer.parseInt(Objects.requireNonNull(env.getProperty("oracle.datasource.hikari.minimum-idle"))));
        confBank.setMaximumPoolSize(Integer.parseInt(Objects.requireNonNull(env.getProperty("oracle.datasource.hikari.maximum-pool-size"))));
        confBank.setConnectionTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("oracle.datasource.hikari.connection-timeout"))));
        confBank.setAutoCommit(Boolean.parseBoolean(env.getProperty("oracle.datasource.hikari.auto-commit")));
        confBank.setIdleTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("oracle.datasource.hikari.idle-timeout"))));
        confBank.setMaxLifetime(Long.parseLong(Objects.requireNonNull(env.getProperty("oracle.datasource.hikari.max-lifetime"))));
        confBank.setCatalog(env.getProperty("oracle.datasource.hikari.catalog"));
        confBank.setConnectionTestQuery(env.getProperty("oracle.datasource.hikari.connection-test-query"));

        // Optimización predefinida DEL POOL
        confBank.addDataSourceProperty("cachePrepStmts", Boolean.TRUE);
        confBank.addDataSourceProperty("prepStmtCacheSize", PREP_STMT_CACHE_SIZE);
        confBank.addDataSourceProperty("prepStmtCacheSqlLimit", PREP_STMT_CACHE_SQL_LIMIT);
        confBank.addDataSourceProperty("useServerPrepStmts", Boolean.TRUE);
        confBank.addDataSourceProperty("useLocalSessionState", Boolean.TRUE);
        confBank.addDataSourceProperty("rewriteBatchedStatements", Boolean.TRUE);
        confBank.addDataSourceProperty("cacheResultSetMetadata", Boolean.TRUE);
        confBank.addDataSourceProperty("elideSetAutoCommits", Boolean.TRUE);
        confBank.addDataSourceProperty("maintainTimeStats", Boolean.TRUE);

        return new MahindraHikariDatasource(confBank);
    }

    @Bean(name = "bankEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean bankEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("bankDataSource") DataSource bankDataSource, Environment env) {


        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));

        return builder
                .dataSource(bankDataSource)
                .packages("co.moviired.support.domain.entity.mahindra")
                .persistenceUnit("bank")
                .properties(properties)
                .build();

    }

    @Bean(name = "bankTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("bankEntityManagerFactory") EntityManagerFactory bankEntityManagerFactory) {
        return new JpaTransactionManager(bankEntityManagerFactory);
    }
}

