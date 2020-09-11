package co.moviired.mahindrafacade.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@Component
public class GlobalProperties implements Serializable {

    private static final long serialVersionUID = -3395163916611319213L;

    // Versión de la aplicación
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.version}")
    private String applicationVersion;

    // Puertos de comunicación
    @Value("${server.port}")
    private int restPort;


    // Propiedades Netty
    @Value("${reactor.netty.ioWorkerCount}")
    private int ioWorkerCount;

    @Value("${reactor.netty.ioSelectCount}")
    private String ioSelectCount;

    @Value("${reactor.pool.maxConnections}")
    private String maxConnections;

    @Value("${reactor.pool.maxIdleTime}")
    private String maxIdleTime;

    @Value("${reactor.pool.leasingStrategy}")
    private String leasingStrategy;
}

