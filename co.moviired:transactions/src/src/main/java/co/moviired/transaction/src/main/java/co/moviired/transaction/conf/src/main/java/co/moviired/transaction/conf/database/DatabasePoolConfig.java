package co.moviired.transaction.conf.database;

import co.moviired.transaction.properties.ConvenioDbProperties;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Data
@Configuration
public class DatabasePoolConfig {

    @Bean(name = "poolConvenioDB")
    public DatabasePool poolConvenioDB(@NotNull ConvenioDbProperties properties) {
        return new DatabasePool(properties);
    }

}

