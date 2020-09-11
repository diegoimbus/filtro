package co.moviired.support.conf;

import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Data
@Configuration
@ComponentScan(basePackages = {"co.moviired.*"})
@EnableJpaRepositories(basePackages = {"co.moviired.*"})
@EntityScan(basePackages = {"co.moviired.*"})
@EnableConfigurationProperties
public class AppConfig {

}

