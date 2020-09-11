package co.moviired.transpiler.jpa.movii.domain.dto.mahindra.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
        "txnId",
        "txnAmt",
        "from",
        "txndt",
        "payId",
        "serviceType",
        "txnType"
})
public class TransactionDetail implements Serializable {

    private static final long serialVersionUID = 2179760587925043177L;

    private String txnId;

    private String txnAmt;

    private String from;

    private String txndt;

    private String payId;

    private String serviceType;

    private String txnType;

}
