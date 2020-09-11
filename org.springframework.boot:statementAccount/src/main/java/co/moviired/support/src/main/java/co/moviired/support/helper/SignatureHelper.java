package co.moviired.support.helper;

import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.util.Security;
import co.moviired.support.conf.StatusCodeConfig;
import co.moviired.support.domain.entity.account.Document;

import javax.crypto.spec.SecretKeySpec;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static co.moviired.support.util.ConstantsHelper.*;

public final class SignatureHelper implements Serializable {

    private static final int NUMBER_7 = 7;
    private static final int NUMBER_10 = 10;

    private final SecretKeySpec keySpec;
    private final StatusCodeConfig statusCodeConfig;

    public SignatureHelper(@NotNull String psecret, @NotNull StatusCodeConfig pstatusCodeConfig) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        super();
        this.keySpec = Security.generateKeyFrom(psecret);
        this.statusCodeConfig = pstatusCodeConfig;
    }

    // Get the signature of payment
    public String sign(@NotNull Document documentWithOutSignature) throws ParsingException {
        Document document = new Document(documentWithOutSignature);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(document.getCreationDate());
        StringBuilder dateHash = new StringBuilder()
                .append(calendar.get(Calendar.HOUR))
                .append(calendar.get(Calendar.YEAR)).append(calendar.get(Calendar.HOUR_OF_DAY)).append(calendar.get(Calendar.DAY_OF_MONTH))
                .append(calendar.get(Calendar.MONTH)).append(calendar.get(Calendar.DATE));

        String dataHash1 = document.getToken().substring(0, (document.getToken().length() / 2)) +
                dateHash.substring(0, NUMBER_7);
        String hash1 = Security.encrypt(dataHash1, keySpec);

        SimpleDateFormat sdf = new SimpleDateFormat(MAHINDRA_DB_DATE_FORMAT);
        StringBuilder signature = new StringBuilder();

        signature.append(document.getToken());
        signature.append(SEPARATOR);

        signature.append(sdf.format(document.getCreationDate()), 0, NUMBER_10);
        signature.append(SEPARATOR);

        signature.append(document.isAltered());
        signature.append(SEPARATOR);

        signature.append(hash1, 0, (hash1.length() / 2));
        signature.append(SEPARATOR);

        signature.append(document.getYear());
        signature.append(SEPARATOR);

        signature.append(document.getPhoneNumber());
        signature.append(SEPARATOR);

        signature.append(document.getType());
        signature.append(SEPARATOR);

        signature.append(document.getMonth());
        signature.append(SEPARATOR);

        signature.append(sdf.format(document.getCreationDate()), 0, NUMBER_10);

        signature.trimToSize();
        return Security.encrypt(signature.toString(), this.keySpec);
    }

    // Validate signature of payment
    public void validate(Document document) throws ParsingException, DataException {
        String signature = sign(document);
        if (!signature.equals(document.getSignature())) {
            throw new DataException(statusCodeConfig.of(ALTERED_CODE));
        }
    }
}

