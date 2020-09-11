package co.moviired.support.endpoint.util.generics;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.validation.constraints.NotNull;

@Slf4j
@Component
public class LoadProperty{

    private final SystemProperties config;
    private final MessageProperties errorMessage;

    public LoadProperty(@NotNull SystemProperties psystemProperties,
                        @NotNull MessageProperties pmessageProperties) {
        super();
        this.config = psystemProperties;
        this.errorMessage = pmessageProperties;
    }

    public String systemPropertyBogota(String property){
        return config.getBogota().get(property);
    }

    public String systemPropertyBancolombia(String property){
        return config.getBancolombia().get(property);
    }

    public String systemPropertyMahindra(String property){
        return config.getMahindra().get(property);
    }

    public String messageProperties(String errorCode, String... desc) {

        String code;
        if (errorCode.length() > 3) {
            code = errorCode;
        } else{
            code = desc[0];
        }

        return errorMessage.getErrors().get(code);

    }
}

