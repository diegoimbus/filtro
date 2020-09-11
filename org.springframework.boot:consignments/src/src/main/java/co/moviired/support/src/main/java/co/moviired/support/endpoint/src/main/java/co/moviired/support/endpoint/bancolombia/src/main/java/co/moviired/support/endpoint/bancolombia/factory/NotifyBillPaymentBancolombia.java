package co.moviired.support.endpoint.bancolombia.factory;

import co.moviired.support.domain.enums.ConsignmentWSStatus;
import co.moviired.support.endpoint.bancobogota.dto.consignment.in.ConsignmentInDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.ConsignmentOutDTO;
import co.moviired.support.endpoint.bancobogota.dto.generics.ErrorDTO;
import co.moviired.support.endpoint.bancolombia.dto.DataConsignmentRequest;
import co.moviired.support.endpoint.bancolombia.dto.DataConsignmentResponse;
import co.moviired.support.endpoint.bancolombia.interfaces.INotifyBillPaymentBancolombia;
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

@Slf4j
@Component
public class NotifyBillPaymentBancolombia implements INotifyBillPaymentBancolombia {

    private static final String CLASS_NAME = NotifyBillPaymentBancolombia.class.getSimpleName();
    private static final String LOG_COMPONENT = "PROCESS NotifyBillPaymentBogota";
    private static final String PHONENUMBER = "phoneNumber";
    private static final String AMOUNT = "amount";
    private static final String BANKID = "bankId";
    private static final String REFERENCEID = "referenceId";
    private static final String BLOCKSMS = "blockSms";
    private static final String TXNMODE = "txnMode";
    private static final String CELLID = "cellId";
    private static final String FTXNID = "ftxnId";
    private static final String REMARKS = "remarks";

    private final ConsignmentWSService consignmentWSService;
    private final ConsignmentUtilities consignmentUtilities;
    private final ErrorMessagesLoader errorMessagesLoader;
    private final MahindraUtils mahindraUtils;

    public NotifyBillPaymentBancolombia(@NotNull ConsignmentWSService consignmentWSService,
                                        @NotNull ConsignmentUtilities consignmentUtilities,
                                        @NotNull ErrorMessagesLoader errorMessagesLoader,
                                        @NotNull MahindraUtils pmahindraUtils) {

        super();
        this.consignmentWSService = consignmentWSService;
        this.consignmentUtilities = consignmentUtilities;
        this.errorMessagesLoader = errorMessagesLoader;
        this.mahindraUtils = pmahindraUtils;
    }

    public DataConsignmentResponse notifyBillPayment(DataConsignmentRequest dataConsignmentRequest) throws BusinessException {

        try {
            log.debug(CLASS_NAME + " - notifyBillPayment");
            this.validateNotifyParameters(dataConsignmentRequest);

            DataConsignmentResponse responseDTO = consignmentWSService.consignmentRegistryBancolombia(dataConsignmentRequest);
            if (!consignmentWSService.consignmentStatusMahindraBancolombia(dataConsignmentRequest)) {
                int consigId = Integer.parseInt(responseDTO.getTxnId());
                responseDTO.setResponseCode(CodeErrorEnum.ERROR_DUPLICATE_TRANSACTION.getCode());
                responseDTO.setResponseMessage(errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERROR_DUPLICATE_TRANSACTION.getDescription()));
                responseDTO.setPhoneNumber(dataConsignmentRequest.getPhoneNumber());

                log.info(LOG_COMPONENT + " - code: " + responseDTO.getResponseCode() + " - messsage: " + responseDTO.getResponseMessage());
                consignmentWSService.consignmentUpdate(consigId, ConsignmentWSStatus.DUPLICATED.getId(), null);
                return responseDTO;
            }
            if (responseDTO.getResponseCode().equals(CodeErrorEnum.SUCCESSFUL_NOTIFICATION.getCode())) {
                String trnsId = null;
                String status = null;
                String documentDate = responseDTO.getDate() + responseDTO.getTime();
                StringBuilder movilredKey = new StringBuilder();
                movilredKey.append(dataConsignmentRequest.getPhoneNumber())
                        .append(dataConsignmentRequest.getAmount())
                        .append(responseDTO.getDate())
                        .append(responseDTO.getTime());

                ConsignmentInDTO inDTOM = new ConsignmentInDTO();
                inDTOM.setAmount(dataConsignmentRequest.getAmount());
                inDTOM.setRegId(Integer.parseInt(responseDTO.getTxnId()));
                inDTOM.setBank(consignmentUtilities.loadPropertyMahindra("mahinbraBankIdBancolombia"));
                inDTOM.setClient(dataConsignmentRequest.getPhoneNumber());
                inDTOM.setDocumentDate(documentDate);
                inDTOM.setExternalReference(dataConsignmentRequest.getBlockSms());
                inDTOM.setMovilRedKey(movilredKey.toString());
                inDTOM.setCorrelationId(responseDTO.getCorrelationId());

                ConsignmentOutDTO mahindraOutDTO = mahindraUtils.mahindraCommunication(inDTOM, MahindraOperationEnum.APPROVAL);
                responseDTO.setTxnId(Validation.isNullOrEmpty(mahindraOutDTO) ? null : mahindraOutDTO.getTransactionIdentificator());
                status = String.valueOf(mahindraOutDTO.getStatus());
                trnsId = mahindraOutDTO.getTransactionIdentificator();

                if (Validation.isNullOrEmpty(status)) {
                    throw new BusinessException(CodeErrorEnum.ERRORDISPERSE);
                }


                responseDTO.setResponseCode(CodeErrorEnum.SUCCESSFUL_NOTIFICATION.getCode());
                responseDTO.setResponseMessage(errorMessagesLoader.getErrorMensage(CodeErrorEnum.SUCCESSFUL_NOTIFICATION.getDescription()));
                responseDTO.setTxnId(trnsId);
            } else {
                responseDTO.setResponseCode(CodeErrorEnum.FAILED_NOTIFICATION.getCode());
                responseDTO.setResponseMessage(errorMessagesLoader.getErrorMensage(CodeErrorEnum.FAILED_NOTIFICATION.getDescription()));
            }

            return responseDTO;
        } catch (BusinessException var17) {
            ErrorDTO errorDTO = var17.getErrorDTO();
            throw new BusinessException(var17, errorDTO);
        }
    }

    private void validateNotifyParameters(DataConsignmentRequest request) throws BusinessException {
        log.info(CLASS_NAME + " - validateNotifyParameters");
        Validation.validateRequiredFields(request, consignmentUtilities.loadPropertyBancolombia("requiredFieldsNotifyBancolombia").split(","), errorMessagesLoader);

        Validation.valideParamInteger(PHONENUMBER, request.getPhoneNumber(), errorMessagesLoader);
        Validation.validateMaxLength(PHONENUMBER, request.getPhoneNumber(), 12, errorMessagesLoader);
        Validation.valideParamInteger(AMOUNT, request.getAmount(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(AMOUNT, request.getAmount(), errorMessagesLoader);
        Validation.validateMaxLength(AMOUNT, request.getAmount(), 15, errorMessagesLoader);
        Validation.validateMinLength(AMOUNT, request.getAmount(), 3, errorMessagesLoader);

        Validation.validateMaxLength(BANKID, request.getPhoneNumber(), 12, errorMessagesLoader);
        Validation.validateMaxLength(REFERENCEID, request.getReferenceId(), 15, errorMessagesLoader);
        Validation.validateMaxLength(BLOCKSMS, request.getBlockSms(), 40, errorMessagesLoader);

        Validation.validateMaxLength(TXNMODE, request.getTxnMode(), 25, errorMessagesLoader);
        Validation.validateMaxLength(CELLID, request.getCellId(), 25, errorMessagesLoader);
        Validation.validateMaxLength(FTXNID, request.getFtxnId(), 25, errorMessagesLoader);
        Validation.validateMaxLength(REMARKS, request.getRemarks(), 25, errorMessagesLoader);

    }

}

