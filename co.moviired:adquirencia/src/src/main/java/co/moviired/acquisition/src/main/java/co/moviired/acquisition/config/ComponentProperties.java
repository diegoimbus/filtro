package co.moviired.acquisition.config;

import co.moviired.acquisition.model.CodeProperty;
import co.moviired.acquisition.model.ComponentUser;
import co.moviired.acquisition.model.ReturnPolicies;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.List;

import static co.moviired.acquisition.common.util.ConstantsHelper.COMPONENT_PROPERTIES_PREFIX;

@Data
@ConfigurationProperties(prefix = COMPONENT_PROPERTIES_PREFIX)
public class ComponentProperties implements Serializable {

    private List<ComponentUser> basicAuthenticationUsers;
    private List<String> allowedUserTypes;

    private transient CodeProperty pin;
    private transient CodeProperty cardCode;
    private transient ReturnPolicies returnPolicies;
    private String securityCryptoPinSecret;
}

