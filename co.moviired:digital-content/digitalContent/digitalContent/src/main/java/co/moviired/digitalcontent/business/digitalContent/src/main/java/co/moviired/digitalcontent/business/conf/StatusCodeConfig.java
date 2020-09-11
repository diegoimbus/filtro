package co.moviired.digitalcontent.business.conf;

import co.moviired.digitalcontent.business.domain.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Constants;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "status-codes")
public class StatusCodeConfig implements Serializable {
    private static final long serialVersionUID = -7804851867091541454L;

    private Map<String, StatusCodeConfigItem> success;
    private Map<String, StatusCodeConfigItem> fails;

    public StatusCode resolve(String code) {

        for (Map.Entry<String, StatusCodeConfigItem> entry : success.entrySet()) {
            if (entry.getValue().containCode(code)) {
                return new StatusCode(StatusCode.Level.SUCCESS, entry.getKey(), entry.getValue().getMessage(), code);
            }
        }

        for (Map.Entry<String, StatusCodeConfigItem> entry : fails.entrySet()) {
            if (entry.getValue().containCode(code)) {
                return new StatusCode(StatusCode.Level.FAIL, entry.getKey(), entry.getValue().getMessage(), code);
            }
        }

        throw new Constants.ConstantException(StatusCodeConfig.class.getSimpleName(), code, "does not present homologation");
    }

    public StatusCode of(String code, String message) {
        try {
            return resolve(code);
        } catch (Constants.ConstantException ce) {
            return new StatusCode(StatusCode.Level.FAIL, StatusCode.Level.FAIL.value(), message, code);
        }
    }

    public StatusCode of(String code) {
        try {
            return resolve(code);
        } catch (Constants.ConstantException ce) {
            return new StatusCode(StatusCode.Level.FAIL, StatusCode.Level.FAIL.value(), ce.getMessage(), code);
        }
    }

    public StatusCode newInstance(StatusCode.Level level, String code, String message) {
        return new StatusCode(level, code, message, code);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatusCodeConfigItem implements Serializable {

        private String message;
        private List<String> codes;

        public boolean containCode(String code) {
            return codes.stream().anyMatch(s -> s.equals(code));
        }
    }

}

