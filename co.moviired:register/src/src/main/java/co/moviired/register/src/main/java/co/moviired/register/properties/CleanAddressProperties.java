package co.moviired.register.properties;

import co.moviired.register.domain.model.register.CleanAddressReplace;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.List;

import static co.moviired.register.helper.ConstantsHelper.CLEAN_ADDRESS_PROPERTIES;

@Data
@ConfigurationProperties(prefix = CLEAN_ADDRESS_PROPERTIES)
public final class CleanAddressProperties implements Serializable {
    private static final long serialVersionUID = -6498309817262719675L;

    private String cleanAddressRegex;
    private String cleanMultipleSpacesRegex;
    private List<CleanAddressReplace> cleanAddressReplaces;
}
