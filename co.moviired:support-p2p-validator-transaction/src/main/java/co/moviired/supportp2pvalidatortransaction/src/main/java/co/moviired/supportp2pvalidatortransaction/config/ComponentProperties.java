package co.moviired.supportp2pvalidatortransaction.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "properties")
public class ComponentProperties implements Serializable {

    private String securityPassword;
    private Integer queryLimitResults;
}

