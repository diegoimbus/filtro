package co.moviired.supportp2pvalidatortransaction.common.config;

import co.moviired.supportp2pvalidatortransaction.common.model.method.Scheduler;
import co.moviired.supportp2pvalidatortransaction.common.model.method.ServicesRestContainer;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.HashMap;

import static co.moviired.supportp2pvalidatortransaction.common.util.Constants.SPRING_CONFIG_PREFIX;

@Data
@ConfigurationProperties(prefix = SPRING_CONFIG_PREFIX)
public class GlobalProperties implements Serializable {

    private String name;
    private String version;
    private Integer restPort;
    private String secret;
    private String baseUrl;

    private transient ServicesRestContainer services;
    private transient HashMap<String, Scheduler> schedulers;
}

