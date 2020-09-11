package co.moviired.register.providers.termsandconditions;

import co.moviired.register.domain.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/*
 * Copyright @2018. SBD, SAS. Todos los derechos reservados.
 *
 * @author RIVAS, Ronel
 * @version 1, 2018-01-28
 * @since 1.0
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Meta extends BaseModel {

    private String requestDate;

    private String customerId;

    private String deviceCode;

    private String userName;

    private String password;

    private String passwordHash;

    private String channel;

    private String systemId;

    private String requestReference;

    private String requestSource;

    private String originAddress;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append(this.customerId).append(this.userName).append(this.password).append(this.requestDate).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Meta)) {
            return false;
        }

        Meta otherCast = (Meta) o;
        return new EqualsBuilder().append(this.userName, otherCast.userName).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.userName).toHashCode();
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String pRequestDate) {
        this.requestDate = pRequestDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String pCustomerId) {
        this.customerId = pCustomerId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String pUserName) {
        this.userName = pUserName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pPassword) {
        this.password = pPassword;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String pPasswordHash) {
        this.passwordHash = pPasswordHash;
    }

    public String getRequestReference() {
        return requestReference;
    }

    public void setRequestReference(String pRequestReference) {
        this.requestReference = pRequestReference;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String pDeviceCode) {
        this.deviceCode = pDeviceCode;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String pChannel) {
        this.channel = pChannel;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String pSystemId) {
        this.systemId = pSystemId;
    }

    public String getRequestSource() {
        return requestSource;
    }

    public void setRequestSource(String pRequestSource) {
        this.requestSource = pRequestSource;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String pOriginAddress) {
        this.originAddress = pOriginAddress;
    }
}

