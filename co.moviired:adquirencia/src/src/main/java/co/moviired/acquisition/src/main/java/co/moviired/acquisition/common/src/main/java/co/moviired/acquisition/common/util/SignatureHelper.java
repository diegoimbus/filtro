package co.moviired.acquisition.common.util;

import co.moviired.acquisition.common.config.StatusCodeConfig;
import co.moviired.acquisition.model.entity.ProductCode;
import co.moviired.acquisition.model.entity.Transaction;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.base.util.Security;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static co.moviired.acquisition.common.util.ConstantsHelper.*;
import static co.moviired.acquisition.common.util.StatusCodesHelper.REGISTER_ALTERED_CODE;

@Slf4j
public class SignatureHelper implements Serializable {

    private final SecretKeySpec keySpec;
    private final StatusCodeConfig statusCodeConfig;

    private static final int TRANSACTION_SIGNATURE_VERSION = 1;
    private static final int PRODUCT_CODE_SIGNATURE_VERSION = 1;

    private static final int DATE_SUBSTRING_SEVEN = 7;
    private static final int DATE_SUBSTRING_TEN = 10;

    public SignatureHelper(@NotNull String secret, @NotNull StatusCodeConfig statusCodeConfigI) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        super();
        this.keySpec = Security.generateKeyFrom(secret);
        this.statusCodeConfig = statusCodeConfigI;
    }

    public final String sign(ProductCode productCode) {
        try {
            ProductCode productCodeCopy = new ProductCode(productCode);

            if (productCodeCopy.getUpdateDate() == null) {
                productCodeCopy.setUpdateDate(productCodeCopy.getCreationDate());
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(productCodeCopy.getCreationDate());
            StringBuilder dateHash = new StringBuilder()
                    .append(calendar.get(Calendar.HOUR))
                    .append(calendar.get(Calendar.YEAR)).append(calendar.get(Calendar.HOUR_OF_DAY)).append(calendar.get(Calendar.DAY_OF_MONTH))
                    .append(calendar.get(Calendar.MONTH)).append(calendar.get(Calendar.DATE));

            String dataHash1 = productCodeCopy.getPin().substring(ZERO_INT, (productCodeCopy.getPin().length() / TWO_INT)) +
                    dateHash.substring(DATE_SUBSTRING_SEVEN) +
                    productCodeCopy.getPin().substring((productCodeCopy.getPin().length() / TWO_INT)) +
                    dateHash.substring(ZERO_INT, DATE_SUBSTRING_SEVEN);
            String hash1 = Security.encrypt(dataHash1, keySpec);

            SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM);
            StringBuilder signature = new StringBuilder();

            signature.append(productCodeCopy.getStatus().name());
            signature.append(SEPARATOR_PIPE);

            signature.append(productCodeCopy.getLotIdentifier());
            signature.append(SEPARATOR_PIPE);

            signature.append(productCodeCopy.getCardCode());
            signature.append(SEPARATOR_PIPE);

            signature.append(sdf.format(productCodeCopy.getUpdateDate()), ZERO_INT, DATE_SUBSTRING_TEN);
            signature.append(SEPARATOR_PIPE);

            signature.append(hash1, ZERO_INT, (hash1.length() / TWO_INT));
            signature.append(SEPARATOR_PIPE);

            signature.append(productCodeCopy.getStatus());
            signature.append(SEPARATOR_PIPE);

            signature.append(productCodeCopy.getPin());
            signature.append(SEPARATOR_PIPE);

            signature.append(productCodeCopy.getPinHash());
            signature.append(SEPARATOR_PIPE);

            signature.append(hash1, (hash1.length() / TWO_INT), hash1.length());
            signature.append(SEPARATOR_PIPE);

            signature.append(productCodeCopy.getProduct().getId());
            signature.append(SEPARATOR_PIPE);

            signature.append(sdf.format(productCodeCopy.getCreationDate()), ZERO_INT, DATE_SUBSTRING_TEN);

            signature.trimToSize();

            return Security.encrypt(signature.toString(), this.keySpec);
        } catch (ParsingException e) {
            log.error("Error generating signature of product code: {}", e.getMessage());
            return EMPTY_STRING;
        }
    }

    public final ProductCode assignSignatureVersion(ProductCode productCode) {
        productCode.setSignatureVersion(PRODUCT_CODE_SIGNATURE_VERSION);
        return productCode;
    }

    public final void validate(ProductCode productCode) throws DataException {
        String signature = sign(productCode);
        if (!signature.equals(productCode.getSignature())) {
            throw new DataException(statusCodeConfig.of(REGISTER_ALTERED_CODE));
        }
    }

    public final String sign(Transaction transaction) {
        try {
            Transaction transactionCopy = new Transaction(transaction);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(transactionCopy.getDateTransaction());
            StringBuilder dateHash = new StringBuilder()
                    .append(calendar.get(Calendar.HOUR))
                    .append(calendar.get(Calendar.YEAR)).append(calendar.get(Calendar.HOUR_OF_DAY)).append(calendar.get(Calendar.DAY_OF_MONTH))
                    .append(calendar.get(Calendar.MONTH)).append(calendar.get(Calendar.DATE));

            String dataHash1 = transactionCopy.getTransactionType().name().substring(ZERO_INT, (transactionCopy.getTransactionType().name().length() / TWO_INT)) +
                    dateHash.substring(DATE_SUBSTRING_SEVEN) +
                    transactionCopy.getTransactionType().name().substring((transactionCopy.getTransactionType().name().length() / TWO_INT)) +
                    dateHash.substring(ZERO_INT, DATE_SUBSTRING_SEVEN);
            String hash1 = Security.encrypt(dataHash1, keySpec);

            SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM);
            StringBuilder signature = new StringBuilder();

            signature.append(transactionCopy.getState().name());
            signature.append(SEPARATOR_PIPE);

            signature.append(transactionCopy.getStoreId());
            signature.append(SEPARATOR_PIPE);

            signature.append(transactionCopy.getCardNumber());
            signature.append(SEPARATOR_PIPE);

            signature.append(transactionCopy.getCurrencyCode());
            signature.append(SEPARATOR_PIPE);

            signature.append(transactionCopy.getIncommRefNum());
            signature.append(SEPARATOR_PIPE);

            signature.append(sdf.format(transactionCopy.getDateTransaction()), ZERO_INT, DATE_SUBSTRING_TEN);
            signature.append(SEPARATOR_PIPE);

            signature.append(hash1, ZERO_INT, (hash1.length() / TWO_INT));
            signature.append(SEPARATOR_PIPE);

            signature.append(transactionCopy.getMerchName());
            signature.append(SEPARATOR_PIPE);

            signature.append(transactionCopy.getValue());
            signature.append(SEPARATOR_PIPE);

            signature.append(transactionCopy.getMerchRefNum());
            signature.append(SEPARATOR_PIPE);

            if (transactionCopy.getIncommDateTime() != null) {
                signature.append(sdf.format(transactionCopy.getIncommDateTime()), ZERO_INT, DATE_SUBSTRING_TEN);
                signature.append(SEPARATOR_PIPE);
            }

            signature.append(transactionCopy.getIncommTimeZone());
            signature.append(SEPARATOR_PIPE);

            signature.append(hash1, (hash1.length() / TWO_INT), hash1.length());
            signature.append(SEPARATOR_PIPE);

            signature.append(transactionCopy.getProductCode().getId());
            signature.append(SEPARATOR_PIPE);

            signature.append(transactionCopy.getStoreId());
            signature.append(SEPARATOR_PIPE);

            signature.append(transactionCopy.getRespCode());

            signature.trimToSize();

            return Security.encrypt(signature.toString(), this.keySpec);
        } catch (ParsingException e) {
            log.error("Error generating signature of transaction: {}", e.getMessage());
            return EMPTY_STRING;
        }
    }

    public final Transaction assignSignatureVersion(Transaction transaction) {
        transaction.setSignatureVersion(TRANSACTION_SIGNATURE_VERSION);
        return transaction;
    }

    public final void validate(Transaction transaction) throws DataException {
        String signature = sign(transaction);
        if (!signature.equals(transaction.getSignature())) {
            throw new DataException(statusCodeConfig.of(REGISTER_ALTERED_CODE));
        }
    }
}
