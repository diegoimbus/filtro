package co.moviired.mahindrafacade.client.mahindra;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "COMMAND")
@XmlAccessorType(XmlAccessType.FIELD)
public final class Request implements Serializable {

    @XmlElement(name = "TYPE")
    private String type;

    @XmlElement(name = "SOURCE")
    private String source;

    @XmlElement(name = "MSISDN")
    private String msisdn;

    @XmlElement(name = "MPIN")
    private String mpin;

    @XmlElement(name = "PIN")
    private String pin;

    @XmlElement(name = "OTPREQ")
    private String otpreq;

    @XmlElement(name = "ISPINCHECKREQ")
    private String ispincheckreq;

    @XmlElement(name = "PROVIDER")
    private String provider;

    @XmlElement(name = "IMEI")
    private String imei;

    @XmlElement(name = "USERTYPE")
    private String usertype;


}


