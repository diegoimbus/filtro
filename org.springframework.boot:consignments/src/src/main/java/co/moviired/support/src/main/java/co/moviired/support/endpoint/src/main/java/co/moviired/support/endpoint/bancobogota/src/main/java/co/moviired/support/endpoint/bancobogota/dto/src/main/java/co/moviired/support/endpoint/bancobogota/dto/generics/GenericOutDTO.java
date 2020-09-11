package co.moviired.support.endpoint.bancobogota.dto.generics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "genericOutDTO",
        propOrder = {"doubleValue", "integerValue", "longValue", "stringValue", "booleanValue"}
)
@XmlRootElement
public class GenericOutDTO extends ResponseDTO {
    private static final long serialVersionUID = 1L;
    protected Double doubleValue;
    protected Integer integerValue;
    protected Long longValue;
    protected String stringValue;
    protected Boolean booleanValue;

    public GenericOutDTO() {
        // Do nothing
    }

    public Double getDoubleValue() {
        return this.doubleValue;
    }

    public void setDoubleValue(Double value) {
        this.doubleValue = value;
    }

    public Integer getIntegerValue() {
        return this.integerValue;
    }

    public void setIntegerValue(Integer value) {
        this.integerValue = value;
    }

    public Long getLongValue() {
        return this.longValue;
    }

    public void setLongValue(Long value) {
        this.longValue = value;
    }

    public String getStringValue() {
        return this.stringValue;
    }

    public void setStringValue(String value) {
        this.stringValue = value;
    }

    public boolean getBooleanValue() {
        return this.booleanValue;
    }

    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }
}

