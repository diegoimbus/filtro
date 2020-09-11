package co.moviired.register.config;

import co.moviired.register.config.database.SmsDBConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@Component
public class ConfigHandler {

    private final SmsDBConfig smsDBConfig;
    private final StatusCodeConfig statusCodeConfig;
}

