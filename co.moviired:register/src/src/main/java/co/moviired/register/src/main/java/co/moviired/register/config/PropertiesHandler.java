package co.moviired.register.config;

import co.moviired.register.properties.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Component
public class PropertiesHandler implements Serializable {
    private static final long serialVersionUID = -6498309817262719675L;

    private final ClevertapProperties clevertapProperties;
    private final GlobalProperties globalProperties;
    private final AdoProperties adoProperties;
    private final SubsidyProperties subsidyProperties;
    private final MahindraProperties mahindraProperties;
    private final BlackListProperties blackListProperties;
    private final RegistraduriaProperties registraduriaProperties;
    private final CleanAddressProperties cleanAddressProperties;
    private final SmsProperties smsProperties;
    private final CmlProperties cmlProperties;

}

