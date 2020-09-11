package co.movii.auth.server.security.crypt;

import co.moviired.base.domain.exception.ServiceException;
import co.moviired.base.helper.CryptoHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Slf4j
@Service
public final class CryptoUtility implements Serializable {

    private final CryptoHelper cryptoHelper;

    public CryptoUtility(CryptoHelper pcryptoHelper) {
        this.cryptoHelper = pcryptoHelper;
    }

    public String encryptAES(String value) {
        try {
            return cryptoHelper.encoder(value);
        } catch (ServiceException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    public String decryptAES(String encrypted) {
        try {
            return cryptoHelper.decoder(encrypted);
        } catch (ServiceException e) {
            log.error("Error decryptAES dato", encrypted);
        }
        return null;
    }

}

