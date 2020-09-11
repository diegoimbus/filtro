package co.moviired.support.domain.dto;

import co.moviired.support.domain.entity.redshift.ExtractData;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExtractDTO {
    private List<ExtractData> availableExtracts;
    private ResponseStatus status;
    private ExtractData availableExtract;
    private String documentUrl;
    private Boolean sendEmail;
}

