package co.moviired.support.endpoint.bancolombia.factory;

import co.moviired.support.domain.entity.mysql.ConsignmentWS;
import co.moviired.support.endpoint.bancobogota.dto.consignment.in.ConsignmentInDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.ConsignmentOutDTO;
import co.moviired.support.endpoint.bancolombia.dto.DataRevertRequest;
import co.moviired.support.endpoint.bancolombia.dto.DataRevertResponse;
import co.moviired.support.endpoint.bancolombia.interfaces.IRevertBillPaymentBancolombia;
import co.moviired.support.endpoint.util.exceptions.BusinessException;
import co.moviired.support.endpoint.util.exceptions.CodeErrorEnum;
import co.moviired.support.endpoint.util.generics.ErrorMessagesLoader;
import co.moviired.support.endpoint.util.generics.Validation;
import co.moviired.support.endpoint.util.util.ConsignmentUtilities;
import co.moviired.support.endpoint.util.util.MahindraUtils;
import co.moviired.support.endpoint.util.util.enums.MahindraOperationEnum;
import co.moviired.support.service.ConsignmentWSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class RevertBankBancolombia implements IRevertBillPaymentBancolombia {

    private static final String CLASS_NAME = RevertBankBancolombia.class.getSimpleName();

    private static final String PHONENUMBER_REVER = "phoneNumber";
    private static final String AMOUNT_REVER = "amount";
    private static final String BANKID_REVER = "bankId";
    private static final String REFERENCEID_REVER = "referenceId";
    private static final String BLOCKSMS_REVER = "blockSms";
    private static final String TXNMODE_REVER = "txnMode";
    private static final String CELLID_REVER = "cellId";
    private static final String FTXNID_REVER = "ftxnId";
    private static final String REMARKS_REVER = "remarks";
    private static final String ACCOUNTANTDATE_REVER = "accountantDate";

    private final ConsignmentWSService consignmentWSService;
    private final MahindraUtils mahindraUtils;
    private final ConsignmentUtilities consignmentUtilities;
    private final ErrorMessagesLoader errorMessagesLoader;

    public RevertBankBancolombia(@NotNull ConsignmentWSService consignmentWSService,
                                 @NotNull MahindraUtils mahindraUtils,
                                 @NotNull ConsignmentUtilities consignmentUtilities,
                                 @NotNull ErrorMessagesLoader errorMessagesLoader) {
        super();
        this.consignmentWSService = consignmentWSService;
        this.mahindraUtils = mahindraUtils;
        this.consignmentUtilities = consignmentUtilities;
        this.errorMessagesLoader = errorMessagesLoader;
    }

    public DataRevertResponse revert(DataRevertRequest dataRevertRequest) throws BusinessException {
        this.validateParameters(dataRevertRequest);
        String trnsId;
        DataRevertResponse revert = new DataRevertResponse();
        ConsignmentWS consignmentWSRevert = consignmentWSService.consignmentRevertMahindraBancolombia(dataRevertRequest);
        if(consignmentWSRevert == null) {
            if(consignmentWSService.consignmentVerRevertMahindraBancolombia(dataRevertRequest)) {
                revert.setResponseCode(CodeErrorEnum.ERROR_DUPLICATE_REVERT.getCode());
                revert.setResponseMessage(errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERROR_DUPLICATE_REVERT.getDescription()));
                revert.setPhoneNumber(dataRevertRequest.getPhoneNumber());
            } else {
                revert.setResponseCode(CodeErrorEnum.PAYMENT_NO_FOUND.getCode());
                revert.setResponseMessage(errorMessagesLoader.getErrorMensage(CodeErrorEnum.PAYMENT_NO_FOUND.getDescription()));
                revert.setPhoneNumber(dataRevertRequest.getPhoneNumber());
            }
        } else {
            ConsignmentInDTO inDTO = new ConsignmentInDTO();
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            DateFormat hourFormat = new SimpleDateFormat("HHmmss");
            String accountantDate = dateFormat.format(date);
            String accountantHour = hourFormat.format(date);

            StringBuilder movilredKey = new StringBuilder();
            movilredKey.append(dataRevertRequest.getPhoneNumber())
                    .append(dataRevertRequest.getAmount())
                    .append(accountantDate)
                    .append(accountantHour);

            inDTO.setAmount(dataRevertRequest.getAmount());
            inDTO.setBank(consignmentUtilities.loadPropertyMahindra("mahinbraBankIdBancolombia"));
            inDTO.setRegId(consignmentWSRevert.getId());
            inDTO.setClient(dataRevertRequest.getPhoneNumber());
            inDTO.setDocumentDate(accountantDate + accountantHour);
            inDTO.setExternalReference(dataRevertRequest.getReferenceId());
            inDTO.setMovilRedKey(movilredKey.toString());
            inDTO.setCorrelationId(consignmentWSRevert.getCorrelationIdRevert());

            try {
                ConsignmentOutDTO mahindraOutDTO = mahindraUtils.mahindraCommunication(inDTO, MahindraOperationEnum.REVERT);
                trnsId = mahindraOutDTO.getTransactionIdentificator();
            } catch (BusinessException var15) {
                var15.getErrorDTO().setDescription(errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERROR_MAHINDRA.getDescription()).concat(" ").concat(var15.getErrorDTO().getDescription()));
                throw var15;
            }

            revert.setResponseCode(CodeErrorEnum.SUCCESSFUL_NOTIFICATION.getCode());
            revert.setResponseMessage(errorMessagesLoader.getErrorMensage(CodeErrorEnum.SUCCESSFUL_REVERT.getDescription()));
            revert.setTxnId(trnsId);
            revert.setPhoneNumber(dataRevertRequest.getPhoneNumber());
            revert.setDate(accountantDate);
            revert.setTime(accountantHour);
            revert.setCorrelationId(inDTO.getCorrelationId());
        }

        log.info(CLASS_NAME + " - code: " + revert.getResponseCode() + " - messsage: " + revert.getResponseMessage());
        return revert;
    }

    private void validateParameters(DataRevertRequest request) throws BusinessException {

        log.info(CLASS_NAME + " - validateRevertParameters");
        Validation.validateRequiredFields(request, consignmentUtilities.loadPropertyBancolombia("requiredFieldsRevertBancolombia").split(","), errorMessagesLoader);

        Validation.valideParamInteger(PHONENUMBER_REVER, request.getPhoneNumber(), errorMessagesLoader);
        Validation.validateMaxLength(PHONENUMBER_REVER, request.getPhoneNumber(), 12, errorMessagesLoader);
        Validation.valideParamInteger(AMOUNT_REVER, request.getAmount(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(AMOUNT_REVER, request.getAmount(), errorMessagesLoader);
        Validation.validateMaxLength(AMOUNT_REVER, request.getAmount(), 15, errorMessagesLoader);
        Validation.validateMinLength(AMOUNT_REVER, request.getAmount(), 3, errorMessagesLoader);

        Validation.validateMaxLength(BANKID_REVER, request.getPhoneNumber(), 12, errorMessagesLoader);
        Validation.validateMaxLength(REFERENCEID_REVER, request.getReferenceId(), 15, errorMessagesLoader);
        Validation.validateMaxLength(BLOCKSMS_REVER, request.getBlockSms(), 40, errorMessagesLoader);

        Validation.validateMaxLength(TXNMODE_REVER, request.getTxnMode(), 25, errorMessagesLoader);
        Validation.validateMaxLength(CELLID_REVER, request.getCellId(), 25, errorMessagesLoader);
        Validation.validateMaxLength(FTXNID_REVER, request.getFtxnId(), 25, errorMessagesLoader);
        Validation.validateMaxLength(REMARKS_REVER, request.getRemarks(), 25, errorMessagesLoader);
        Validation.validateDateFormat(ACCOUNTANTDATE_REVER, request.getAccountantDate(), "yyyyMMdd", errorMessagesLoader);

    }

}

