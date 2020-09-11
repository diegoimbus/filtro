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
public class RequestSignature extends BaseModel {

    private String systemSignature;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append(this.systemSignature).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RequestSignature)) {
            return false;
        }

        RequestSignature otherCast = (RequestSignature) o;
        return new EqualsBuilder().append(this.systemSignature, otherCast.systemSignature).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.systemSignature).toHashCode();
    }

    public String getSystemSignature() {
        return systemSignature;
    }

    public void setSystemSignature(String pSystemSignature) {
        this.systemSignature = pSystemSignature;
    }
}

