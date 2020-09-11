package co.moviired.support.helper;

import co.moviired.base.helper.CryptoHelper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Component
public final class HelperFactory implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private final SignatureHelper signatureHelper;
    private final CryptoHelper cryptoHelper;

    public HelperFactory(@NotNull SignatureHelper psignatureHelper,
                         @Qualifier(value = "cryptoHelperAuthorization") @NotNull CryptoHelper pcryptoHelper
    ) {
        this.signatureHelper = psignatureHelper;
        this.cryptoHelper = pcryptoHelper;
    }
}

