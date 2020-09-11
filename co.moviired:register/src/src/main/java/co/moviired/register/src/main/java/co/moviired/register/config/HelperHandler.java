package co.moviired.register.config;

import co.moviired.base.helper.CryptoHelper;
import co.moviired.register.helper.CryptHelper;
import co.moviired.register.helper.OTPHelper;
import co.moviired.register.helper.SignatureHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@Component
public class HelperHandler {

    private final SignatureHelper signatureHelper;
    private final CryptHelper supportAuthCryptHelper;
    private final CryptoHelper cryptoHelper;
    private final OTPHelper otpHelper;

}

