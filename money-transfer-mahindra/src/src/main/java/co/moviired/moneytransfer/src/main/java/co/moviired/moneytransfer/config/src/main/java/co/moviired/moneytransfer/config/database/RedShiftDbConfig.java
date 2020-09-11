package co.moviired.moneytransfer.config.database;

import co.moviired.moneytransfer.config.RedShiftHikariDatasource;
import com.zaxxer.hikari.HikariConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
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

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "redShiftEntityManagerFactory",
        transactionManagerRef = "redShiftTransactionManager",
        basePackages = {"co.moviired.moneytransfer.domain.repository.redshift"})
public class RedShiftDbConfig {

    private static final int PREP_STMT_CACHE_SIZE = 250;
    private static final int PREP_STMT_CACHE_SQL_LIMIT = 2048;

    @Primary
    @Bean(name = "redShiftDataSource")
    public DataSource redShiftDataSource(Environment env) {

        HikariConfig conf = new HikariConfig();
        conf.setDriverClassName(env.getProperty("spring.datasource.redshift.driver-class-name"));
        conf.setPoolName(env.getProperty("spring.datasource.redshift.hikari.pool-name"));
        conf.setJdbcUrl(env.getProperty("spring.datasource.redshift.jdbcUrl"));
        conf.setUsername(env.getProperty("spring.datasource.redshift.username"));
        conf.setPassword(env.getProperty("spring.datasource.redshift.password"));
        conf.setMinimumIdle(Integer.parseInt(Objects.requireNonNull(env.getProperty("spring.datasource.redshift.hikari.minimum-idle"))));
        conf.setMaximumPoolSize(Integer.parseInt(Objects.requireNonNull(env.getProperty("spring.datasource.redshift.hikari.maximum-pool-size"))));
        conf.setConnectionTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("spring.datasource.redshift.hikari.connection-timeout"))));
        conf.setAutoCommit(Boolean.parseBoolean(env.getProperty("spring.datasource.redshift.hikari.auto-commit")));
        conf.setIdleTimeout(Long.parseLong(Objects.requireNonNull(env.getProperty("spring.datasource.redshift.hikari.idle-timeout"))));
        conf.setMaxLifetime(Long.parseLong(Objects.requireNonNull(env.getProperty("spring.datasource.redshift.hikari.max-lifetime"))));
        conf.setCatalog(Objects.requireNonNull(env.getProperty("spring.datasource.redshift.hikari.catalog")));

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

    @Primary
    @Bean(name = "redShiftEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean redShiftEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                               @Qualifier("redShiftDataSource") DataSource redShiftDataSource,
                                                                               Environment env) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        properties.put("hibernate.temp.use_jdbc_metadata_defaults", false);
        properties.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
        properties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());

        return builder
                .dataSource(redShiftDataSource)
                .packages("co.moviired.moneytransfer.domain.entity.redshift")
                .persistenceUnit("redShift")
                .properties(properties)
                .build();
    }

    @Primary
    @Bean(name = "redShiftTransactionManager")
    public PlatformTransactionManager redShiftTransactionManager(@Qualifier("redShiftEntityManagerFactory") EntityManagerFactory redShiftEntityManagerFactory) {
        return new JpaTransactionManager(redShiftEntityManagerFactory);
    }
}

