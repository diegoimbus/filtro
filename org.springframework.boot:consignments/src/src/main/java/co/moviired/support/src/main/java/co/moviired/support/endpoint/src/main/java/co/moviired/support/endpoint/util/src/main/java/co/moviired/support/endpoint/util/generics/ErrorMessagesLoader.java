package co.moviired.support.endpoint.util.generics;

import org.springframework.stereotype.Component;
import java.text.MessageFormat;

@Component
public class ErrorMessagesLoader {

    private final LoadProperty loadProperty;

    public ErrorMessagesLoader(LoadProperty loadProperty) {
        super();
        this.loadProperty = loadProperty;
    }

    public String getErrorMensage(String key, String... args) {
        String message = loadProperty.messageProperties(key,args);
        MessageFormat formater;
        if (Validation.isNotEmpty(message) && Validation.isNotNull(args)) {
            formater = new MessageFormat(message);
            message = formater.format(args);
            return message;
        } else {
            return Validation.isNotEmpty(message) ? message : key;
        }
    }

}

