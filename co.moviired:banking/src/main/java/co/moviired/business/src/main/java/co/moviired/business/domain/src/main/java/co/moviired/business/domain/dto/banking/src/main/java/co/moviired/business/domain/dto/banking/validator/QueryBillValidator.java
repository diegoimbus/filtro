package co.moviired.business.domain.dto.banking.validator;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.DataException;
import co.moviired.business.conf.StatusCodeConfig;
import co.moviired.business.domain.dto.banking.BankingValidator;
import co.moviired.business.domain.dto.banking.request.RequestFormatBanking;
import co.moviired.business.domain.enums.CollectionType;
import co.moviired.business.domain.jpa.movii.entity.Biller;
import co.moviired.business.domain.jpa.movii.entity.JsonItemField;
import co.moviired.business.domain.jpa.movii.repository.IBillerRepository;
import co.moviired.business.properties.BankingProperties;
import org.springframework.stereotype.Service;

@Service
public class QueryBillValidator extends BankingValidator {

    private final StatusCodeConfig statusCodeConfig;
    private final BankingProperties bankingProperties;
    private final IBillerRepository iBillerRepository;

    protected QueryBillValidator(StatusCodeConfig statusCodeConfig, BankingProperties bankingProperties, IBillerRepository iBillerRepository) {
        super(statusCodeConfig, bankingProperties);
        this.statusCodeConfig = statusCodeConfig;
        this.bankingProperties = bankingProperties;
        this.iBillerRepository = iBillerRepository;
    }

    public final void validationInput(RequestFormatBanking request, String merchantId, String posId, String userpass) throws DataException {
        StatusCode statusCode;
        if (request.getReferenceNumber() == null) {
            statusCode = statusCodeConfig.of("11");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getTypePayBill() == null) {
            statusCode = statusCodeConfig.of("15");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getTypePayBill().equals(CollectionType.AUTOMATIC)
                && request.getReferenceNumber().length() <= bankingProperties.getMaxlengthManual()) {
            statusCode = statusCodeConfig.of("42");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());

        } else if (request.getTypePayBill().equals(CollectionType.MANUAL)
                && request.getReferenceNumber().length() > bankingProperties.getMaxlengthManual()) {
            statusCode = statusCodeConfig.of("43");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }
        if (request.getTypePayBill().equals(CollectionType.MANUAL) && request.getServiceCode() == null) {
            statusCode = statusCodeConfig.of("12");
            throw new DataException(statusCode.getCode(), statusCode.getMessage());
        }

        validateAuthorization(request, userpass);
        validatePosIdMerchant(request, merchantId, posId);
        validateParametersBill(request);
        validateSpecialFields(request);
    }

    public final void validateSpecialFields(RequestFormatBanking request) throws DataException {
        Biller biller;
        if (request.getSpecialFields() != null && !request.getSpecialFields().isBlank()) {
            if (request.getTypePayBill().equals(CollectionType.MANUAL)) {
                biller = iBillerRepository.getByBillerCode(request.getServiceCode());
            } else {
                biller = iBillerRepository.getByEanCode(request.getReferenceNumber().substring(3, 16));
            }
            if (biller != null) {
                biller.fieldsAsJsonArray();
                JsonItemField[] items = biller.getListFields();
                String[] placeHolder = biller.getPlaceHolder().split("\\|");
                String[] specialFields = request.getSpecialFields().split("\\|");
                validateRegularExpresion(items, placeHolder, specialFields);
            } else {
                StatusCode statusCode = statusCodeConfig.of("44");
                throw new DataException(statusCode.getCode(), statusCode.getMessage());
            }
        }
    }

    public final void validateRegularExpresion(JsonItemField[] items, String[] placeHolder, String[] specialFields) throws DataException {
        for (int i = 0; i < placeHolder.length; i++) {
            for (JsonItemField item : items) {
                if (placeHolder[i].equals(item.getAlias())) {
                    if (!specialFields[i].matches(item.getFormat())) {
                        StatusCode statusCode = statusCodeConfig.of("45");
                        throw new DataException(statusCode.getCode(), statusCode.getMessage());
                    }
                    break;
                }
            }
        }
    }

}
