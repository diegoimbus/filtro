package co.moviired.supportp2pvalidatortransaction.provider.supportsms;

import co.moviired.supportp2pvalidatortransaction.common.model.IModel;
import co.moviired.supportp2pvalidatortransaction.common.model.dto.IComponentDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupportSMSDTO extends IComponentDTO {

    private transient SMSData data;
    private transient SMSOutcome outcome;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SMSData extends IModel {
        private String phoneNumber;
        private String messageContent;
        private String transactionId;

        @Override
        public String protectedToString() {
            return toJson("messageContent");
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SMSOutcome extends IModel {
        private String statusCode;
        private String message;
        private SMSError error;

        @Override
        public String protectedToString() {
            return toJson();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SMSError extends IModel {
        private Integer errorType;
        private String errorCode;
        private String errorMessage;

        @Override
        public String protectedToString() {
            return toJson();
        }
    }
}

