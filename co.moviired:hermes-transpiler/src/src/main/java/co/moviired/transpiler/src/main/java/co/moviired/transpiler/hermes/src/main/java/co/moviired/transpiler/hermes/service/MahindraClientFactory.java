package co.moviired.transpiler.hermes.service;

import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.transpiler.conf.DigitalContentProperties;
import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.request.DigitalContentHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.enums.OperationType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class MahindraClientFactory implements Serializable {

    private static final long serialVersionUID = -8111145829471817085L;

    private final ReactiveConnector mhTransactionalClient;
    private final ReactiveConnector validateBillPaymentByReferenceClient;
    private final ReactiveConnector validateBillPaymentByEANClient;
    private final ReactiveConnector digitalContentClientActivate;
    private final ReactiveConnector digitalContentClientInactivate;
    private final ReactiveConnector digitalContentClientPinesSales;
    private final ReactiveConnector digitalContentClientPinesInactivate;
    private final DigitalContentProperties config;

    public final ReactiveConnector getClient(@NotNull OperationType operationType, @NotNull IHermesRequest hermesRequest) throws ParseException {
        ReactiveConnector client;

        switch (operationType) {
            case TOPUP:
            case CASH_OUT:
            case BILL_PAY:
                client = this.mhTransactionalClient;
                break;

            case VALIDATE_BILL_REFERENCE:
                client = this.validateBillPaymentByReferenceClient;
                break;

            case VALIDATE_BILL_EAN:
                client = this.validateBillPaymentByEANClient;
                break;

            case DIGITAL_CONTENT_CARD:
            case DIGITAL_CONTENT_PINES:
                DigitalContentHermesRequest request = (DigitalContentHermesRequest) hermesRequest;
                if (config.getCodeActivate().contains(request.getOperation())) {
                    client = this.digitalContentClientActivate;
                } else if (config.getCodeInactivate().equals(request.getOperation())) {
                    client = this.digitalContentClientInactivate;
                } else if (config.getCodePinesSale().equals(request.getOperation())) {
                    client = this.digitalContentClientPinesSales;
                } else if (config.getCodePinesInactivate().equals(request.getOperation())) {
                    client = this.digitalContentClientPinesInactivate;
                } else {
                    throw new ParseException("Operación inválida");
                }
                break;

            default:
                throw new ParseException("Operación inválida");
        }

        return client;
    }

    public final Map<String, String> getHeaders(@NotNull OperationType operationType, @NotNull IHermesRequest hermesRequest) {
        HashMap<String, String> headers;

        switch (operationType) {
            case DIGITAL_CONTENT_CARD:
            case DIGITAL_CONTENT_PINES:
                DigitalContentHermesRequest request = (DigitalContentHermesRequest) hermesRequest;

                headers = new HashMap<>();
                headers.put("Authorization", request.getClient().getUsername().trim() + ":" + request.getClient().getPassword().trim());
                headers.put("posId", request.getDeviceId());
                headers.put("merchantId", request.getClient().getUsername().trim());
                break;

            default:
                headers = null;
        }

        return headers;
    }

    public final HttpMethod getHttpMethod(@NotNull OperationType operationType, @NotNull IHermesRequest hermesRequest) {
        HttpMethod method;

        switch (operationType) {
            case DIGITAL_CONTENT_CARD:
            case DIGITAL_CONTENT_PINES:
                method = HttpMethod.POST;
                DigitalContentHermesRequest request = (DigitalContentHermesRequest) hermesRequest;
                if ((config.getCodeInactivate().equals(request.getOperation())) ||
                        (config.getCodePinesInactivate().equals(request.getOperation()))) {
                    method = HttpMethod.DELETE;
                }
                break;

            default:
                method = HttpMethod.POST;
        }

        return method;
    }

}

