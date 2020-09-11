package co.moviired.digitalcontent.business.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusCode {

    private Level level;
    private String code;
    private String message;
    private String extCode;

    public enum Level {

        SUCCESS("00"),
        FAIL("99");

        private String codeDefault;

        Level(String pcode) {
            this.codeDefault = pcode;
        }

        public String value() {
            return codeDefault;
        }

    }
}

