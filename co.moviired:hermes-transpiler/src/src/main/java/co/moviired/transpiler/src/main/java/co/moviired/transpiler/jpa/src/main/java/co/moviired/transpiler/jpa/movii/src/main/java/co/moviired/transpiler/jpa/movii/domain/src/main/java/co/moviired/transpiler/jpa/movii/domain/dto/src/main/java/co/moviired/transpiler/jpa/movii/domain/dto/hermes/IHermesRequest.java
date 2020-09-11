package co.moviired.transpiler.jpa.movii.domain.dto.hermes;

import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ClientHermes;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ProductHermes;
import co.moviired.transpiler.jpa.movii.domain.enums.Protocol;

import java.io.Serializable;

public interface IHermesRequest extends Serializable {

    Protocol getProtocol();

    void setProtocol(Protocol protocol);

    String getLogId();

    void setLogId(String logId);

    default ClientHermes getClient() {
        return null;
    }

    default ProductHermes getProduct() {
        return null;
    }

    String getClientTxnId();

    String getRequestDate();

}

