package co.moviired.acquisition.model.dto;

import co.moviired.acquisition.common.model.IModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocaleInfo extends IModel {

    @JsonProperty("CountryCode")
    private String countryCode;
    @JsonProperty("CurrencyCode")
    private String currencyCode;
    @JsonProperty("Language")
    private String language;

    @Override
    public final String protectedToString() {
        return toJson();
    }
}

