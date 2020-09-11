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
public class DateTimeInfo extends IModel {

    @JsonProperty("DateValue")
    private String dateValue;
    @JsonProperty("TimeValue")
    private String timeValue;
    @JsonProperty("TimeZoneValue")
    private String timeZoneValue;

    @Override
    public final String protectedToString() {
        return toJson();
    }
}

