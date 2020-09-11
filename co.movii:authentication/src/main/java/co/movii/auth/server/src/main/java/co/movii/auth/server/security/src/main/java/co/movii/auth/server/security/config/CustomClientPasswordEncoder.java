package co.movii.auth.server.security.config;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.helper.CryptoHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public final class CustomClientPasswordEncoder implements PasswordEncoder {

    private final CryptoHelper cryptoHelper;

    CustomClientPasswordEncoder(CryptoHelper pcryptoHelper) {
        this.cryptoHelper = pcryptoHelper;
    }

    @Override
    public String encode(CharSequence charSequence) {
        return charSequence.toString();
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        try {
            return s.equals(cryptoHelper.decoder(charSequence.toString()));
        } catch (ParsingException e) {
            return Boolean.FALSE;
        }
    }
}

