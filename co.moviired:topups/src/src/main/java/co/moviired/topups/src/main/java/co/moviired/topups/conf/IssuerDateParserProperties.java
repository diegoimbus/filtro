package co.moviired.topups.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "request.parser")
public class IssuerDateParserProperties implements Serializable {

    private String issuerDateIncomingPattern;

    private String issuerDateOutcomingPattern;

    private String componentDatePattern;
}




