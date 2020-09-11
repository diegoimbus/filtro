package co.moviired.support.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

import static co.moviired.support.util.ConstantsHelper.PREFIX_CERTIFICATES;

@Data
@ConfigurationProperties(prefix = PREFIX_CERTIFICATES)
public class CertificatesProperties implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private String notFoundView;
    private String pathExtractTemplate;
}

