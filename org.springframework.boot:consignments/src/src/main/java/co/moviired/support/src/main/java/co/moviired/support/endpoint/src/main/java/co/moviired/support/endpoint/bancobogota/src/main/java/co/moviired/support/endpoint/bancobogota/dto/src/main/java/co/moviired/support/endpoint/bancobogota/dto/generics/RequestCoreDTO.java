package co.moviired.support.endpoint.bancobogota.dto.generics;

import java.io.Serializable;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "requestCoreDTO",
        propOrder = {"channel"}
)
@XmlSeeAlso({})
public class RequestCoreDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String channel;

    public RequestCoreDTO() {
        // Do nothing
    }

    public String getChannel() {
        return this.channel;
    }

    public void setChannel(String value) {
        this.channel = value;
    }

    public String toString() {
        return ", Channel: " + this.channel;
    }
}


