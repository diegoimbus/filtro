package co.moviired.transpiler.jpa.movii.domain.dto.mahindra.validatebill.request;

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
        "billerCode",
        "shortReferenceNumber",
        "valueToPay"
})
public class DataValidateBillByReference implements Serializable {

    private static final long serialVersionUID = -6498309817262719675L;

    private String billerCode;

    private String shortReferenceNumber;

    private String valueToPay;

}

