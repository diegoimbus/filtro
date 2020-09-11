package co.moviired.register.config;

import co.moviired.connector.connector.ReactiveConnector;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@Component
public class ClientHandler {

    private final ReactiveConnector mahindraClient;
    private final ReactiveConnector supportOtpClient;
    private final ReactiveConnector supportAuthClient;
    private final ReactiveConnector adoClient;
    private final ReactiveConnector cleverTapClient;
    private final ReactiveConnector supportSmsClient;
    private final ReactiveConnector cmlClient;

}

