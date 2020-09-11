package co.moviired.support.endpoint.bancobogota.dto.generics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "consignmentResponseDTO",
        propOrder = {"code", "statusCode", "message", "severity"}
)
@XmlRootElement
public class ConsignmentResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String code;
    protected String statusCode;
    protected String message;
    protected String severity;

    public ConsignmentResponseDTO() {
        // Do nothing
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String value) {
        this.code = value;
    }

    public String getStatusCode() {return statusCode;   }

    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public String getSeverity() {
        return this.severity;
    }

    public void setSeverity(String value) {
        this.severity = value;
    }
}

