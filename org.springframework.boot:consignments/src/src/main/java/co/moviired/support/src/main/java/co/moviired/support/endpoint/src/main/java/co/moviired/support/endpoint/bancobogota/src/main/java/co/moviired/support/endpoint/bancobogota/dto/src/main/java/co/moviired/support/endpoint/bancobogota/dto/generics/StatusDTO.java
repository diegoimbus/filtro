package co.moviired.support.endpoint.bancobogota.dto.generics;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "statusDTO",
        propOrder = {"code", "message"}
)
public class StatusDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String code = "00";
    private String message;

    public StatusDTO() {
        // Do nothing
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("StatusDTO [code=").append(this.code).append(", message=").append(this.message).append("]");
        return builder.toString();
    }
}

