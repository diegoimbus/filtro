package co.moviired.transaction.properties;

import co.moviired.transaction.conf.database.IDatabaseProperties;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
public class ConvenioDbProperties implements Serializable, IDatabaseProperties {

    @Value("${properties.connectionConvenioDB.driverDb}")
    private String driverDb;

    @Value("${properties.connectionConvenioDB.urlDb}")
    private String urlDb;

    @Value("${properties.connectionConvenioDB.poolNameDb}")
    private String poolNameDb;

    @Value("${properties.connectionConvenioDB.userDb}")
    private String userDb;

    @Value("${properties.connectionConvenioDB.passDb}")
    private String passDb;

    @Value("${properties.connectionConvenioDB.auto-commit}")
    private boolean autoCommit;

    @Value("${properties.connectionConvenioDB.allow-pool-suspension}")
    private boolean allowPoolSuspension;

    @Value("${properties.connectionConvenioDB.connection-timeout}")
    private Integer connectionTimeout;

    @Value("${properties.connectionConvenioDB.idle-timeout}")
    private Integer idleTimeout;

    @Value("${properties.connectionConvenioDB.maximum-pool-size}")
    private Integer maximumPoolSize;

    @Value("${properties.connectionConvenioDB.minimum-idle}")
    private Integer minimumIdle;

    @Value("${properties.connectionConvenioDB.max-lifetime}")
    private Integer maxLifetime;

    @Value("${properties.connectionConvenioDB.test-query}")
    private String testQuery;

}

