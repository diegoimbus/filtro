package com.moviired.helper;

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.util.Security;
import com.moviired.model.entities.cashservice.CashOut;

import javax.crypto.spec.SecretKeySpec;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

import static com.moviired.helper.Constant.SEVEN;
import static com.moviired.helper.Constant.TEN;

public class SignatureHelper implements Serializable {

    private final SecretKeySpec keySpec;

    public SignatureHelper(@NotNull String secret) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        super();
        this.keySpec = Security.generateKeyFrom(secret);
    }


    /**
     * metodo signCashOut (Generador de firma Helper)
     *
     * @param cashOut
     * @return String
     */
    public String signCashOut(@NotNull CashOut cashOut) throws ParsingException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-yyyy-mm HH:mm:ss");

        StringBuilder signature = new StringBuilder();
        signature.append(cashOut.getAmountCashOut() / TEN);
        signature.append(cashOut.getPhoneNumberCashOut(), 1, cashOut.getPhoneNumberCashOut().length());
        signature.append(sdf.format(cashOut.getCreationDateCashOut()), 0, SEVEN);
        signature.trimToSize();

        return Security.encrypt(signature.toString(), this.keySpec);
    }
}
