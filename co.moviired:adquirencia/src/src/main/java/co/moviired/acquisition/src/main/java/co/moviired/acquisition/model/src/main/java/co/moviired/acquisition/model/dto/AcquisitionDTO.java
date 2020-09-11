package co.moviired.acquisition.model.dto;

import co.moviired.acquisition.common.model.dto.IComponentDTO;
import co.moviired.acquisition.model.LotIdentifier;
import co.moviired.acquisition.model.entity.ProductCode;
import co.moviired.acquisition.model.entity.Transaction;
import co.moviired.acquisition.model.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AcquisitionDTO extends IComponentDTO {

    //Incomm Requests/Responses
    @JsonProperty("ServiceProviderTxn")
    private ServiceProviderTxn serviceProviderTxn;

    //Movii
    private String pin;
    private Long numberOfCodesToCreate;
    private String fileName;
    private String lotIdentifier;
    private List<String> lotIdentifiers;
    private Long codesCreated;
    private transient List<LotIdentifier> lotIdentifiersList;
    private String delimiter;
    private Boolean useQuotes;

    @JsonIgnore
    private transient byte[] productCodesCSV;

    // Transaction fields
    private TransactionType transactionType;
    private co.moviired.acquisition.model.entity.Product product;
    private ProductCode productCode;
    private Transaction transaction;
    private Date transactionDate;
    private String correlative;

    @Override
    public final String protectedToString() {
        return toJson("pin");
    }
}

