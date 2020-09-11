package com.moviired.client.balance;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Request {

    @Builder.Default
    private Map<String, String> variables = new HashMap<>();
    private String correlationId;
    @JsonProperty("Authorization")
    private String authorization;

}

