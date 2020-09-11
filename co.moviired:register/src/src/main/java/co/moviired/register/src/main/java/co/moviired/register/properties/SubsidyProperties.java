package co.moviired.register.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "subsidies")
public final class SubsidyProperties implements Serializable {

    private Integer minLengthCC;
    private Integer maxLengthCC;
    private Integer uploadFilesSubsidyMaxRecordsSimultaneous;
    private String infraTokenChangeHashSubsidy;
    private boolean validateHash;
}

