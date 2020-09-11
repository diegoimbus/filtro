package co.moviired.transpiler.jpa.movii.domain.dto.hermes.request;

import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.enums.Protocol;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EchoHermesRequest implements IHermesRequest {

    private static final long serialVersionUID = 5890986482035265847L;

    @JsonIgnore
    private String logId;

    @NotBlank
    private String originalRequest;

    @NotNull
    private Protocol protocol;

    @NotBlank
    private String clientTxnId;

    @NotBlank
    private Date date;

    @NotBlank
    private String nit;

    @NotBlank
    private Integer red;

    private String requestDate;

    public Date getDate() {
        return date != null ? (Date) date.clone() : null;
    }

    public void setDate(Date date) {
        this.date = date != null ? (Date) date.clone() : null;
    }

}

