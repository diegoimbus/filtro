package co.moviired.business.provider.bankingswitch.request;

import co.moviired.business.provider.IRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CashOutbankingRequest implements IRequest {

    private transient Map<String, Object> data;

}

