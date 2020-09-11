package co.moviired.support.endpoint.bancobogota.dto.generics;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "responseDTO",
        propOrder = {"statusDTO"}
)
@XmlRootElement
@XmlSeeAlso({})
public class ResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private StatusDTO statusDTO = new StatusDTO();

    public ResponseDTO() {
        // Do nothing
    }

    public StatusDTO getStatusDTO() {
        return this.statusDTO;
    }

    public void setStatusDTO(StatusDTO statusDTO) {
        this.statusDTO = statusDTO;
    }

    public void setStatusResponse(String code, String message) {
        this.statusDTO.setCode(code);
        this.statusDTO.setMessage(message);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ResponseDTO [statusDTO=").append(this.statusDTO).append("]");
        return builder.toString();
    }
}

