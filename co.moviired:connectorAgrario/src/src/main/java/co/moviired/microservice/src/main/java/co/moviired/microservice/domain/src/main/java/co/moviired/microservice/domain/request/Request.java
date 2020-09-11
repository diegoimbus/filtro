package co.moviired.microservice.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Request implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient Map<String, Object> meta;
    private Input data;
    private RequestSignature requestSignature;

}
