package co.moviired.register.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static co.moviired.register.helper.ConstantsHelper.CLEVERTAP_CONFIG_PREFIX;

@Data
@ConfigurationProperties(prefix = CLEVERTAP_CONFIG_PREFIX)
public final class ClevertapProperties implements Serializable {

    // Connection
    private String url;
    private Integer timeoutConnect;
    private Integer timeoutRead;
    private String cleverTapAccountId;
    private String cleverTapPasscode;

    // operations
    private String uploadEventsPath;

    //Parameters tags
    private String accountIdTag;
    private String passcodeTag;
    private String eventType;

    //Events name
    private String adoApiApprovalEvent;
    private String adoApiRejectEvent;
    private String adoDoApprovalEvent;
    private String adoDoRejectEvent;

    public String getUploadEventUrl() {
        return url.concat(uploadEventsPath);
    }

    public Map<String, String> getAuthenticationHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put(accountIdTag, cleverTapAccountId);
        headers.put(passcodeTag, cleverTapPasscode);
        return headers;
    }
}

