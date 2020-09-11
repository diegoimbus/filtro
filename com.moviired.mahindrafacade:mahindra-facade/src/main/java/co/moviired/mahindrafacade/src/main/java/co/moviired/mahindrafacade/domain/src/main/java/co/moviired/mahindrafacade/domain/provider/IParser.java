package co.moviired.mahindrafacade.domain.provider;

import co.moviired.mahindrafacade.client.mahindra.Request;
import co.moviired.mahindrafacade.client.mahindra.Response;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public interface IParser extends Serializable {

    default Request getRequest(@NotNull String requestMh) {
        return null;
    }

    default Response getResponse(@NotNull String responseMh) {
        return null;
    }

}

