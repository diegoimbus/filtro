package co.moviired.support.properties;

import co.moviired.support.conf.StatusCodeConfig;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Component
public final class PropertiesFactory implements Serializable {
    private static final long serialVersionUID = 1905122041950251209L;
    private final MahindraProperties mahindraProperties;
    private final CertificatesProperties certificatesProperties;
    private final EmailGeneratorProperties emailGeneratorProperties;
    private final ServiceManagerProperties serviceManagerProperties;
    private final GlobalProperties globalProperties;
    private final StatusCodeConfig statusCodeConfig;

    public PropertiesFactory(MahindraProperties mahindraProperties,
                             CertificatesProperties certificatesProperties,
                             EmailGeneratorProperties emailGeneratorProperties,
                             ServiceManagerProperties serviceManagerProperties,
                             GlobalProperties pglobalProperties,
                             @NotNull @Qualifier("statusCodeConfig") StatusCodeConfig pstatusCodeConfig) {
        this.mahindraProperties = mahindraProperties;
        this.certificatesProperties = certificatesProperties;
        this.emailGeneratorProperties = emailGeneratorProperties;
        this.serviceManagerProperties = serviceManagerProperties;
        this.globalProperties = pglobalProperties;
        this.statusCodeConfig = pstatusCodeConfig;
    }
}

