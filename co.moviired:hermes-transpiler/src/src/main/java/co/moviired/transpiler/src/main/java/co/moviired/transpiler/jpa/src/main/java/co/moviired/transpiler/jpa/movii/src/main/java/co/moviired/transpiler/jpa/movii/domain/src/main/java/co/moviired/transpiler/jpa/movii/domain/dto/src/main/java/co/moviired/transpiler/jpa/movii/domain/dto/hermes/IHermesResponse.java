package co.moviired.transpiler.jpa.movii.domain.dto.hermes;

import co.moviired.transpiler.jpa.movii.domain.dto.hermes.general.ResponseHermes;

import java.io.Serializable;

public interface IHermesResponse extends Serializable {

    default ResponseHermes getResponse() {
        return null;
    }

    void setResponse(ResponseHermes response);

    void setRequest(IHermesRequest request);

}

