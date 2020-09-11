package co.moviired.microservice.domain.request;

/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Cindy Bejarano, Oscar Lopez
 * @since 1.0.0
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Request implements Serializable {

    private Map<String, Object> meta;
    private Map<String, Object> data;
    private RequestSignature requestSignature;

    public Request(Map<String, Object> pmeta, RequestSignature prequestSignature, Map<String, Object> pdata) {
        super();
        this.meta = pmeta;
        this.data = pdata;
        this.requestSignature = prequestSignature;
    }
}
