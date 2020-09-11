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
public class Product extends IModel {

    @JsonProperty("Fee")
    private Double fee;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("Balance")
    private Value balance;
    @JsonProperty("Track1")
    private String track1;
    @JsonProperty("Track2")
    private String track2;
    @JsonProperty("UPC")
    private String upc;
    @JsonProperty("SPNumber")
    private String spNumber;
    @JsonProperty("Value")
    private transient Value value;

    @Override
    public final String protectedToString() {
        return toJson();
    }
}

