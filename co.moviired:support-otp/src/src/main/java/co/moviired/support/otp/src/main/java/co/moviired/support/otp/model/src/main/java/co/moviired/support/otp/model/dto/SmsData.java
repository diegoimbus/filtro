package co.moviired.support.otp.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SmsData implements Serializable {

    private String phoneNumber;

    private String operatorId;

    private String messageContent;

    private String templateCode;

    private Map<String, String> variables;

}

