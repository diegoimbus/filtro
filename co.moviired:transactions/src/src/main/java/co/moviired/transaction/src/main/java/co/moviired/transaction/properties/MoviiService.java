package co.moviired.transaction.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collection;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "services")
public class MoviiService {

    private String paymentTypes;

    private String cashInPurposeServiceType;
    private String cashInPurposeCategoryCode;
    private String cashInPurposeEntryType;
    private String cashOutPurposeServiceType;
    private String cashOutPurposeCategoryCode;
    private String cashOutPurposeEntryType;

    private Map<String, String> types;
    private Map<String, String> states;
    private Map<String, String> managers;

    public Collection<String> getTypesId() {
        return types.keySet();
    }

}

