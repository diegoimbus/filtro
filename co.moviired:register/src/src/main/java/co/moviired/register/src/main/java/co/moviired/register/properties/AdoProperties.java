package co.moviired.register.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static co.moviired.register.helper.ConstantsHelper.ADO_CONFIG_PREFIX;

@Data
@ConfigurationProperties(prefix = ADO_CONFIG_PREFIX)
public final class AdoProperties implements Serializable {

    // Connection
    private String url;
    private Integer timeoutConnect;
    private Integer timeoutRead;
    private String projectName;
    private String apiKey;

    // operations
    private String validateStatusUrl;
    private Integer validateStatusRate;
    private Integer registersLifeTimeMillis;

    private String apiKeyTag;
    private String projectNameTag;
    private String transactionIdTag;

    public String getValidationUrl(Integer transactionId) {
        return url.concat(validateStatusUrl)
                .replace(projectNameTag, projectName)
                .replace(transactionIdTag, transactionId.toString());
    }

    public Map<String, String> getAuthenticationHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put(apiKeyTag, apiKey);
        return headers;
    }
}

