package co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "errorType",
        "errorCode",
        "errorMessage"
})
public class Error implements Serializable {

    private static final long serialVersionUID = -1463612606363571078L;

    private String errorType;

    private String errorCode;

    private String errorMessage;

}

