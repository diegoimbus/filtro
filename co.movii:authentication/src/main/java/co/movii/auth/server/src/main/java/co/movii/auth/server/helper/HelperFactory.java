package co.movii.auth.server.helper;

import co.movii.auth.server.conf.StatusCodeConfig;
import co.movii.auth.server.security.crypt.CryptoUtility;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public final class HelperFactory {

    private final OTPHelper otpHelper;
    private final CryptoUtility cryptoUtility;
    private final StatusCodeConfig statusCodeConfig;

    public HelperFactory(@NotNull OTPHelper potpHelper,
                         @NotNull CryptoUtility pcryptoUtility,
                         @NotNull StatusCodeConfig pstatusCodeConfig) {
        this.otpHelper = potpHelper;
        this.cryptoUtility = pcryptoUtility;
        this.statusCodeConfig = pstatusCodeConfig;
    }

    public OTPHelper getOtpHelper() {
        return otpHelper;
    }

    public CryptoUtility getCryptoUtility() {
        return cryptoUtility;
    }

    public StatusCodeConfig getStatusCodeConfig() {
        return statusCodeConfig;
    }
}

