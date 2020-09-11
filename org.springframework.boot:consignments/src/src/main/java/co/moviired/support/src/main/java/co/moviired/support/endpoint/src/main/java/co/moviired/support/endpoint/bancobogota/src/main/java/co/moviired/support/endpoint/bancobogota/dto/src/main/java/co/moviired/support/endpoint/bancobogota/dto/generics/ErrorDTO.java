package co.moviired.support.endpoint.bancobogota.dto.generics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "errorDTO",
        propOrder = {"code", "description"}
)
public class ErrorDTO implements Serializable {
    protected String code;
    protected String description;

    public ErrorDTO() {
    }

    public ErrorDTO(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String value) {
        this.code = value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }
}

