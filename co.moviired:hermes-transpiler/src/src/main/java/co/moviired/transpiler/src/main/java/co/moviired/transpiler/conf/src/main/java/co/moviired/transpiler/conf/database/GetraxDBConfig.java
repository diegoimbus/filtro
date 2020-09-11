package co.moviired.transpiler.conf.database;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "getraxEntityManagerFactory",
        transactionManagerRef = "getraxTransactionManager",
        basePackages = {"co.moviired.transpiler.jpa.getrax.repository"})
public class GetraxDBConfig {

    @Bean(name = "getraxDataSource")
    @ConfigurationProperties(prefix = "getrax.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

    @Bean(name = "getraxEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean getraxEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("getraxDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource)
                .packages("co.moviired.transpiler.jpa.getrax.domain")
                .persistenceUnit("getrax")
                .build();
    }

    @Bean(name = "getraxTransactionManager")
    public PlatformTransactionManager getraxTransactionManager(
            @Qualifier("getraxEntityManagerFactory") EntityManagerFactory getraxEntityManagerFactory) {
        return new JpaTransactionManager(getraxEntityManagerFactory);
    }
}

