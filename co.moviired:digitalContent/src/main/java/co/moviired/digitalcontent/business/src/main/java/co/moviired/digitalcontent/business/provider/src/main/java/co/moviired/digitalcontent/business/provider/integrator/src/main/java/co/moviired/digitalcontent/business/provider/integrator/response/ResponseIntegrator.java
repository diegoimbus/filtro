package co.moviired.digitalcontent.business.provider.integrator.response;

import co.moviired.digitalcontent.business.provider.IResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/*
 * Copyright @2019. MOVIIRED, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2018-01-28
 * @since 1.0
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@lombok.Data
public class ResponseIntegrator implements IResponse {
    private static final long serialVersionUID = -3023215829840709823L;
    private Data data;
    private Result outcome;

}

