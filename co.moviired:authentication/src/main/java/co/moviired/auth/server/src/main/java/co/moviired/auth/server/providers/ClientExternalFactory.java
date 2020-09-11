package co.moviired.auth.server.providers;

import co.moviired.connector.connector.ReactiveConnector;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Getter
@Component
public final class ClientExternalFactory implements Serializable {

    private static final long serialVersionUID = -8111145829471817085L;

    private final ReactiveConnector mhTransactionalClient;
    private final ReactiveConnector userClientLogin;
    private final ReactiveConnector userClientGetUser;
    private final ReactiveConnector userClientChangePassword;
    private final ReactiveConnector userClientGenerateOTP;
    private final ReactiveConnector userClientResetPassword;
    private final ReactiveConnector profileClient;

    public ClientExternalFactory(@Qualifier("mhTransactionalClient") ReactiveConnector pmhTransactionalClient,
                                 @Qualifier("userClientLogin") ReactiveConnector puserClientLogin,
                                 @Qualifier("userClientGetUser") ReactiveConnector puserClientGetUser,
                                 @Qualifier("userClientChangePassword") ReactiveConnector puserClientChangePassword,
                                 @Qualifier("userClientGenerateOTP") ReactiveConnector puserClientGenerateOTP,
                                 @Qualifier("userClientResetPassword") ReactiveConnector puserClientResetPassword,
                                 @Qualifier("profileClient") ReactiveConnector pprofileClient) {
        super();
        this.mhTransactionalClient = pmhTransactionalClient;
        this.userClientLogin = puserClientLogin;
        this.userClientGetUser = puserClientGetUser;
        this.userClientChangePassword = puserClientChangePassword;
        this.userClientGenerateOTP = puserClientGenerateOTP;
        this.userClientResetPassword = puserClientResetPassword;
        this.profileClient = pprofileClient;
    }


}

