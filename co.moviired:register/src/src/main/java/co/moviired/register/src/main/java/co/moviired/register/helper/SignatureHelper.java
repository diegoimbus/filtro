package co.moviired.register.helper;

import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.util.Security;
import co.moviired.register.config.StatusCodeConfig;
import co.moviired.register.domain.enums.register.ServiceStatusCode;
import co.moviired.register.domain.model.entity.PendingUser;
import co.moviired.register.domain.model.entity.User;
import co.moviired.register.domain.model.entity.UserMoviired;
import org.apache.commons.lang3.SerializationUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static co.moviired.register.helper.ConstantsHelper.*;

public final class SignatureHelper implements Serializable {

    private final SecretKeySpec keySpec;
    private final StatusCodeConfig statusCodeConfig;

    public SignatureHelper(@NotNull String secret, @NotNull StatusCodeConfig pStatusCodeConfig) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        super();
        this.keySpec = Security.generateKeyFrom(secret);
        this.statusCodeConfig = pStatusCodeConfig;
    }

    // Get the signature of transaction
    public String signTransaction(@NotNull User userToSign) throws ParsingException {
        User user = SerializationUtils.clone(userToSign).setFillInNull();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(user.getRegistrationDate());
        StringBuilder dateHash = new StringBuilder()
                .append(calendar.get(Calendar.HOUR))
                .append(calendar.get(Calendar.YEAR)).append(calendar.get(Calendar.HOUR_OF_DAY)).append(calendar.get(Calendar.DAY_OF_MONTH))
                .append(calendar.get(Calendar.MONTH)).append(calendar.get(Calendar.DATE));

        String dataHash1 = user.getIdentificationName().substring(0, (user.getIdentificationName().length() / 2)) +
                dateHash.substring(7) +
                user.getFirstName().substring((user.getFirstName().length() / 2)) +
                user.getGender() + user.getFirstSurname().substring(0, (user.getFirstSurname().length() / 2)) +
                dateHash.substring(0, 7);
        String hash1 = Security.encrypt(dataHash1, keySpec);

        SimpleDateFormat sdf = new SimpleDateFormat(MAHINDRA_DB_DATE_FORMAT);
        StringBuilder signature = new StringBuilder();

        signature.append(user.getIsActive());
        signature.append(SEPARATOR);

        signature.append(user.getAdoStatus());
        signature.append(SEPARATOR);

        signature.append(user.getPhoneNumber());
        signature.append(SEPARATOR);

        signature.append(sdf.format(user.getDateUpdate()), 0, 10);
        signature.append(SEPARATOR);

        signature.append(user.getProcess());
        signature.append(SEPARATOR);

        signature.append(hash1, 0, (hash1.length() / 2));
        signature.append(SEPARATOR);

        signature.append(user.getPhoneSerialNumber());
        signature.append(SEPARATOR);

        signature.append(user.getStatus());
        signature.append(SEPARATOR);

        signature.append(user.getAdoTransactionId());
        signature.append(SEPARATOR);

        signature.append(user.getIdentificationTypeId());
        signature.append(SEPARATOR);

        signature.append(hash1, (hash1.length() / 2), hash1.length());
        signature.append(SEPARATOR);

        signature.append(user.isOrdinaryDepositFormCompleted());
        signature.append(SEPARATOR);

        signature.append(sdf.format(user.getRegistrationDate()), 0, 10);
        signature.append(SEPARATOR);

        signature.append(user.getIdentificationNumber());
        signature.append(SEPARATOR);

        signature.append(user.getBirthDate());
        signature.append(SEPARATOR);

        signature.trimToSize();
        return Security.encrypt(signature.toString(), this.keySpec);
    }

    // Validate signature of transaction
    public void validateSignature(User user) throws ParsingException, DataException {
        String signature = signTransaction(user);
        if (!signature.equals(user.getSignature())) {
            throw new DataException(this.statusCodeConfig.of(ServiceStatusCode.REGISTER_ALTERED.getStatusCode()));
        }
    }

    public String signMoviiredRegister(@NotNull UserMoviired userMoviiredToSign) throws ParsingException {
        UserMoviired userMoviired = SerializationUtils.clone(userMoviiredToSign).setFillInNull();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(userMoviired.getRegistrationDate());

        StringBuilder dateHash = new StringBuilder()
                .append(calendar.get(Calendar.YEAR))
                .append(calendar.get(Calendar.HOUR)).append(calendar.get(Calendar.HOUR_OF_DAY)).append(calendar.get(Calendar.DAY_OF_MONTH))
                .append(calendar.get(Calendar.DATE)).append(calendar.get(Calendar.MONTH));

        String dataHash1 = dateHash.substring(5) +
                userMoviired.getFirstName().substring((userMoviired.getFirstName().length() / 2)) + userMoviired.getFirstSurname().substring(0, (userMoviired.getFirstSurname().length() / 2)) +
                dateHash.substring(0, 7);
        String hash1 = Security.encrypt(dataHash1, keySpec);

        SimpleDateFormat sdf = new SimpleDateFormat(MAHINDRA_DB_DATE_FORMAT);
        StringBuilder signature = new StringBuilder();

        signature.append(sdf.format(userMoviired.getDateUpdate()), 0, 10);
        signature.append(hash1, 0, (hash1.length() / 2));
        signature.append(userMoviired.getStatus());
        signature.append(hash1, (hash1.length() / 2), hash1.length());
        signature.append(sdf.format(userMoviired.getRegistrationDate()), 0, 10);
        signature.trimToSize();

        return Security.encrypt(signature.toString(), this.keySpec);
    }

    // Validate signature of transaction
    public void validateMoviiredRegisterSignature(UserMoviired userMoviired) throws ParsingException, DataException {
        String signature = signMoviiredRegister(userMoviired);
        if (!signature.equals(userMoviired.getSignature())) {
            throw new DataException(this.statusCodeConfig.of(ServiceStatusCode.REGISTER_ALTERED.getStatusCode()));
        }
    }

    public String sign(@NotNull PendingUser pendingUserWithoutSignature, boolean oldSignature) throws ParsingException {
        PendingUser pendingUser = SerializationUtils.clone(pendingUserWithoutSignature);
        if (pendingUser.getDateUpdate() == null) {
            pendingUser.setDateUpdate(pendingUser.getRegistrationDate());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pendingUser.getRegistrationDate());

        StringBuilder dateHash = new StringBuilder()
                .append(calendar.get(Calendar.YEAR))
                .append(calendar.get(Calendar.HOUR)).append(calendar.get(Calendar.HOUR_OF_DAY)).append(calendar.get(Calendar.DAY_OF_MONTH))
                .append(calendar.get(Calendar.DATE)).append(calendar.get(Calendar.MONTH));

        if (pendingUser.getPhoneNumber() == null && !oldSignature) {
            pendingUser.setPhoneNumber(pendingUser.getDocumentType() + pendingUser.getDocumentNumber());
        }

        String dataHash1 = dateHash.substring(5) +
                pendingUser.getPhoneNumber().substring((pendingUser.getPhoneNumber().length() / 2)) + pendingUser.getPhoneNumber().substring(0, (pendingUser.getPhoneNumber().length() / 2)) +
                dateHash.substring(0, 7);
        String hash1 = Security.encrypt(dataHash1, keySpec);

        // Validate Signature
        StringBuilder signature = assembleSignature(oldSignature, pendingUser, hash1);
        signature.trimToSize();

        return Security.encrypt(signature.toString(), this.keySpec);
    }

    private StringBuilder assembleSignature(boolean oldSignature, PendingUser pendingUser, String hash1) {
        StringBuilder signature = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat(MAHINDRA_DB_DATE_FORMAT);

        signature.append(pendingUser.getType());

        if (!oldSignature && pendingUser.getSubsidyValue() != null) {
            signature.append(new DecimalFormat("#0.##").format(pendingUser.getSubsidyValue()));
        }
        if (!oldSignature) {
            signature.append(pendingUser.getDocumentType());
        }
        signature.append(sdf.format(pendingUser.getDateUpdate()), 0, 10);
        if (!oldSignature) {
            signature.append(pendingUser.getSubsidyCode());
        }
        signature.append(hash1, 0, (hash1.length() / 2));
        if (!oldSignature && pendingUser.isSubsidyApplied() && pendingUser.getSubsidyValue() != null) {
            signature.append(pendingUser.isSubsidyApplied());
        }
        if (!oldSignature && pendingUser.getTransactionId() != null && pendingUser.getSubsidyValue() != null) {
            signature.append(pendingUser.getTransactionId());
        }
        if (!oldSignature) {
            signature.append(pendingUser.getPhoneNumberHash());
        }
        signature.append(pendingUser.isStatus());
        if (!oldSignature && pendingUser.getStatusTransaction() != null && pendingUser.getSubsidyValue() != null) {
            signature.append(pendingUser.getStatusTransaction());
        }
        if (!oldSignature) {
            signature.append(pendingUser.getDocumentNumber());
        }
        signature.append(hash1, (hash1.length() / 2), hash1.length());
        if (!oldSignature) {
            signature.append(pendingUser.getProcessType());
        }
        signature.append(sdf.format(pendingUser.getRegistrationDate()), 0, 10);
        if (!oldSignature) {
            signature.append(pendingUser.isInfoPersonIsComplete());
        }
        if (!oldSignature) {
            signature.append(pendingUser.getValidationBlackList());
        }

        return signature;
    }

    // Validate signature of transaction
    public void validate(PendingUser pendingUser) throws ParsingException, DataException {
        String signature = sign(pendingUser, false);
        String signatureOld;
        try {
            signatureOld = sign(pendingUser, true);
        } catch (Exception e) {
            signatureOld = EMPTY_STRING;
        }
        if (!signature.equals(pendingUser.getSignature()) && (!signatureOld.equals(pendingUser.getSignature()) || signatureOld.equals(EMPTY_STRING))) {
            throw new DataException(this.statusCodeConfig.of(ServiceStatusCode.REGISTER_ALTERED.getStatusCode()));
        }
    }
}

