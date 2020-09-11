package co.moviired.digitalcontent.incomm.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/*
 * Copyright @2019. MOVIIRED, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2019-01-22
 * @since 1.0
 */

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Request implements Serializable {

    private static final long serialVersionUID = -442345214821885920L;

    @JsonSerialize(keyUsing = MapSerializer.class)
    private transient Map<String, Object> meta;

    @JsonSerialize(keyUsing = MapSerializer.class)
    private transient Map<String, Object> data;

    private RequestSignature requestSignature;

}

