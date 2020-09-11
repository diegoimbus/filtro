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
public class Data extends BaseModel {

    private String transactionDate;
    private Long transactionTime;
    private String code;
    private String message;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append(this.code).append(this.message).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Data)) {
            return false;
        }

        Data otherCast = (Data) o;
        return new EqualsBuilder().append(this.code, otherCast.code).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.code).toHashCode();
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String pTransactionDate) {
        this.transactionDate = pTransactionDate;
    }

    public Long getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Long pTransactionTime) {
        this.transactionTime = pTransactionTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String pCode) {
        this.code = pCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String pMessage) {
        this.message = pMessage;
    }

}

