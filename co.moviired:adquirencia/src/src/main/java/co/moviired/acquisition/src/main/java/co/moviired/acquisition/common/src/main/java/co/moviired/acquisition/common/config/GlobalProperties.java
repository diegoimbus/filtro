package co.moviired.acquisition.common.config;

import co.moviired.acquisition.common.model.method.Scheduler;
import co.moviired.acquisition.common.model.method.ServicesRestContainer;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.Map;

import static co.moviired.acquisition.common.util.ConstantsHelper.SPRING_CONFIG_PREFIX;

@Data
@ConfigurationProperties(prefix = SPRING_CONFIG_PREFIX)
public class GlobalProperties implements Serializable {

    private String name;
    private String version;
    private Integer restPort;
    private String secret;
    private String baseUrl;

    private transient ServicesRestContainer services;
    private transient Map<String, Scheduler> schedulers;
}

