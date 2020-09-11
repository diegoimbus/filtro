package co.moviired.support.otp.helper;

import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.helper.CryptoHelper;
import co.moviired.base.util.Generator;
import co.moviired.base.util.Security;
import co.moviired.support.otp.properties.OtpProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Copyright @2019. MOVIIRED, SAS. Todos los derechos reservados.
 *
 * Utility to generate and validate the Otp
 *
 * @author RIVAS, Ronel
 * @version 1, 2019-06-27
 * @since 1.0
 */

@Data
@Slf4j
@Component
public class OtpHelper implements Serializable {

    private final SecretKeySpec keySpec;
    private final OtpProperties otpProperties;
    private final CryptoHelper cryptoHelper;

    public OtpHelper(@NotNull OtpProperties otpProperties,@NotNull CryptoHelper cryptoHelper) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        super();
        this.cryptoHelper = cryptoHelper;
        this.otpProperties = otpProperties;
        this.keySpec = Security.generateKeyFrom(this.otpProperties.getSecret());
    }

    // Generar la Otp encriptada
    public String generate() throws ParsingException {
        return generate(null, null);
    }

    public String generate(@NotNull Integer otpLength) throws ParsingException {
        return generate(otpLength, null);
    }

    public String generate(Integer otpLength, Boolean otpAlphanumeric) throws ParsingException {
        // Longitud de la Otp
        if (otpLength == null) {
            otpLength = otpProperties.getDefaultLength();
        }

        // Otp Alfanumérica
        if (otpAlphanumeric == null) {
            otpAlphanumeric = otpProperties.getDefaultAlpha();
        }

        // Generar el Otp
        boolean valid;
        String otp;
        do {
            otp = Generator.pin(otpLength, otpAlphanumeric);
            valid = isValidOtp(otp);
        } while (!valid);

        // Encriptar el Otp
        return encryptOtp(otp);
    }

    // Validar la Otp encriptada
    public String decryptOtp(@NotNull String encriptedOtp) throws ParsingException {
        //return Security.decrypt(encriptedOtp, this.keySpec);
        return cryptoHelper.decoder(encriptedOtp);
    }

    // Encriptar una OTP
    public String encryptOtp(@NotNull String otp) throws ParsingException {
        //return Security.encrypt(otp, this.keySpec);
        return cryptoHelper.encoder(otp);
    }

    // Validar la Otp encriptada
    public boolean validate(@NotNull String otp, @NotNull String encriptedOtp) throws ParsingException {
        //return otp.equals(decryptOtp(encriptedOtp));
        return otp.equals(cryptoHelper.decoder(encriptedOtp));
    }

    // Verifficar que la OTP generada cumpla con los requisitos mínimos de seguridad
    private boolean isValidOtp(String otp) {
        boolean valid = true;
        try {
            // Validate repeated digits
            Pattern pattern = Pattern.compile("^([0-9])\\1{" + (otp.length() - 1) + ",}$");
            Matcher matcher = pattern.matcher(otp);
            if (matcher.find())
                throw new DataException();

            // Validate ascendent repeated digits
            char[] p = otp.toCharArray();
            String firstDigit = otp.substring(0, 1);
            List<Integer> listotp = new ArrayList<>();
            List<Integer> consecutiveotp = new ArrayList<>();
            for (int i = 0; i < otp.length(); i++) {
                listotp.add(Integer.parseInt(String.valueOf(p[i])));
                consecutiveotp.add((Integer.parseInt(firstDigit) + i) % 10);
            }
            if (listotp.equals(consecutiveotp)) {
                throw new DataException();
            }

            // Validate descendent repeated digits
            p = otp.toCharArray();
            String lastDigit = otp.substring(otp.length() - 1);
            listotp = new ArrayList<>();
            consecutiveotp = new ArrayList<>();
            for (int i = 0; i < otp.length(); i++) {
                listotp.add(Integer.parseInt(String.valueOf(p[i])));
                consecutiveotp.add((Integer.parseInt(lastDigit) + i) % 10);
            }
            Collections.reverse(consecutiveotp);
            if (listotp.equals(consecutiveotp)) {
                throw new DataException();
            }
        } catch (DataException e) {
            valid = false;
        }

        return valid;
    }

}

