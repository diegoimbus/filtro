package co.moviired.transpiler.conf;

import co.moviired.transpiler.integration.iso.model.IsoClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.List;

@Data
@ConfigurationProperties(value = "spring.application.services.iso")
public class IsoProperties implements Serializable {

    private static final long serialVersionUID = -3395163916611319213L;

    private int timeoutConnection;
    private int timeoutRead;
    private List<IsoClient> clients;

}

