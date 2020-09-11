package co.moviired.supportp2pvalidatortransaction.model.dto;

import co.moviired.supportp2pvalidatortransaction.common.model.dto.IComponentDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidateP2pTransactionDTO extends IComponentDTO {


    @Override
    public String protectedToString() {
        return super.toJson();
    }
}

