package co.moviired.support.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class RequestAuth implements Serializable {

    private static final long serialVersionUID = 3014706042455506811L;
    private String authorization;
    private String correlationId;

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String pauthorization) {
        this.authorization = pauthorization;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String pcorrelationId) {
        this.correlationId = pcorrelationId;
    }
}

