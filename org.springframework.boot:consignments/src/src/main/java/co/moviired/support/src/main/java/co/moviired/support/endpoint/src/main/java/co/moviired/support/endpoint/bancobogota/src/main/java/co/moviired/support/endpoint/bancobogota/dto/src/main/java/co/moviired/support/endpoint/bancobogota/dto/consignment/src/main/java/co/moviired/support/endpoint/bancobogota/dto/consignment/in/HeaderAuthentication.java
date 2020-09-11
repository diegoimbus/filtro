package co.moviired.support.endpoint.bancobogota.dto.consignment.in;


import javax.xml.bind.annotation.*;

@XmlRootElement(name = "header")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "headerAuthentication",
        propOrder = {"userName","pass"}
)
public class HeaderAuthentication {

    @XmlElement(required = true)
    protected String userName;

    @XmlElement(required = true)
    protected String pass;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}

