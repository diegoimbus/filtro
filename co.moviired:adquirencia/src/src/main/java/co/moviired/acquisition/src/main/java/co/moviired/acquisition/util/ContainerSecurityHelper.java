package co.moviired.acquisition.util;

import co.moviired.acquisition.common.util.SignatureHelper;
import co.moviired.base.helper.CryptoHelper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

import static co.moviired.acquisition.common.util.ConstantsHelper.CRYPTO_HELPER;

@Data
@Component
public class ContainerSecurityHelper {

    private final SignatureHelper signatureHelper;
    private final CryptoHelper cryptoHelper;

    public ContainerSecurityHelper(@NotNull SignatureHelper signatureHelperI,
                                   @NotNull @Qualifier(CRYPTO_HELPER) CryptoHelper cryptoHelperI) {
        this.signatureHelper = signatureHelperI;
        this.cryptoHelper = cryptoHelperI;
    }
}

