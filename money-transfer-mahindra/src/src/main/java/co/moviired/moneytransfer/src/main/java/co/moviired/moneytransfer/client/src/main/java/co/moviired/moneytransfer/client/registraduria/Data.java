package co.moviired.moneytransfer.client.registraduria;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@lombok.Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Data implements Serializable {

    private String documentNumber;
    private int forced;
    private String identificationType;
}

