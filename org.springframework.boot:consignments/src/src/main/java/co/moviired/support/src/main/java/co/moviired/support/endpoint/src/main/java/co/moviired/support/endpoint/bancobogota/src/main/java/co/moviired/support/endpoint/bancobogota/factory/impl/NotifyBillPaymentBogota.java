package co.moviired.support.endpoint.bancobogota.factory.impl;

import co.moviired.support.domain.enums.ConsignmentWSStatus;
import co.moviired.support.endpoint.bancobogota.dto.consignment.in.ConsignmentInDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.in.NotifyBillPaymentInDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.ConsignmentOutDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.DisperseConsignmentOutDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.NotifyBillPaymentOutDTO;
import co.moviired.support.endpoint.bancobogota.dto.generics.ErrorDTO;
import co.moviired.support.endpoint.bancobogota.dto.generics.GenericOutDTO;
import co.moviired.support.endpoint.bancobogota.interfaces.INotifyBillPayment;
import co.moviired.support.endpoint.util.enums.SeverityEnum;
import co.moviired.support.endpoint.util.exceptions.BusinessException;
import co.moviired.support.endpoint.util.exceptions.CodeErrorEnum;
import co.moviired.support.endpoint.util.generics.ErrorMessagesLoader;
import co.moviired.support.endpoint.util.generics.Utilities;
import co.moviired.support.endpoint.util.generics.Validation;
import co.moviired.support.endpoint.util.util.ConsignmentUtilities;
import co.moviired.support.endpoint.util.util.MahindraUtils;
import co.moviired.support.endpoint.util.util.enums.MahindraOperationEnum;
import co.moviired.support.service.ConsignmentWSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Slf4j
@Component
public class NotifyBillPaymentBogota implements INotifyBillPayment {

    private static final String CLASS_NAME = NotifyBillPaymentBogota.class.getSimpleName();
    private static final String LOG_COMPONENT = "PROCESS NotifyBillPaymentBogota";
    private static final String BILLTOTALAMOUNT = "billTotalAmount";
    private static final String EFFECTIVEAMOUNT = "effectiveAmount";
    private static final String BILLINGCOMPANYAGREEMENTONE = "billingCompanyAgreementOne";
    private static final String BILLINGCOMPANYAGREEMENTTWO = "billingCompanyAgreementTwo";
    private static final String BANKNITONE = "bankNitOne";
    private static final String BANKNITTWO = "bankNitTwo";
    private static final String TRANSACTIONDATE = "transactionDate";
    private static final String UUID = "uuid";
    private static final String OFFICECODE = "officeCode";
    private static final String COMPENSATIONCODEONE = "compensationCodeOne";
    private static final String COMPENSATIONCODETWO = "compensationCodeTwo";
    private static final String EANCODE = "eanCode";
    private static final String AGREEMENTCODE = "agreementCode";
    private static final String ACCOUNTNUMBER = "accountNumber";
    private static final String WORKINGDAY = "workingDay";
    private static final String ACCOUNTTYPE = "accountType";
    private static final String ACCOUNTANTDATE = "accountantDate";
    private static final String BILLNUMBER = "billNumber";
    private static final String REFERENCEFIELDTWO = "referenceFieldTwo";
    private static final String REFERENCEFIELDTHREE = "referenceFieldThree";
    private static final String REFERENCEFIELDFOUR = "referenceFieldFour";
    private static final String REFERENCEFIELDFIVE = "referenceFieldFive";
    private static final String TRANSACCIONCHANNEL = "transaccionChannel";
    private static final String CURRENCYTYPE = "currencyType";
    private static final String BILLINGCOMPANYNAMEONE = "billingCompanyNameOne";
    private static final String BILLINGCOMPANYNAMETWO = "billingCompanyNameTwo";
    private static final String CHECKAMOUNT = "checkAmount";

    private final ConsignmentWSService consignmentWSService;
    private final ConsignmentUtilities consignmentUtilities;
    private final ErrorMessagesLoader errorMessagesLoader;
    private final MahindraUtils mahindraUtils;

    public NotifyBillPaymentBogota(@NotNull ConsignmentWSService consignmentWSService,
                                   @NotNull ConsignmentUtilities consignmentUtilities,
                                   @NotNull ErrorMessagesLoader errorMessagesLoader,
                                   @NotNull MahindraUtils pmahindraUtils) {

        super();
        this.consignmentWSService = consignmentWSService;
        this.consignmentUtilities = consignmentUtilities;
        this.errorMessagesLoader = errorMessagesLoader;
        this.mahindraUtils = pmahindraUtils;
    }

    public NotifyBillPaymentOutDTO notifyBillPayment(NotifyBillPaymentInDTO notifyBillPaymentInDTO) throws BusinessException {
        new NotifyBillPaymentOutDTO();
        new GenericOutDTO();
        new DisperseConsignmentOutDTO();

        try {
            log.debug(CLASS_NAME + " - notifyBillPayment");
            String[] channelsAvailable = new String[]{consignmentUtilities.loadPropertyBogota("channelTat"), consignmentUtilities.loadPropertyBogota("channelAlianzas"), consignmentUtilities.loadPropertyBogota("channelMahindra")};
            this.validateNotifyParameters(notifyBillPaymentInDTO);
            Map<String, String> channelInfo = ExtractChannelCommon.extractConsignmentChannel(notifyBillPaymentInDTO.getBillNumber());
            String channel = channelInfo.get("CONSIGNMENT_CHANNEL");
            String client = channelInfo.get("CONSIGNMENT_CLIENT");
            Validation.validateConsignmentChannel(channel, channelsAvailable, errorMessagesLoader);
            NotifyBillPaymentOutDTO responseDTO = consignmentWSService.consignmentRegistryBogota(notifyBillPaymentInDTO);
            if (!consignmentWSService.consignmentStatusMahindraBogota(notifyBillPaymentInDTO)) {
                int consigId = Integer.parseInt(responseDTO.getAuthorizationNumber());
                responseDTO.setCode(CodeErrorEnum.ERROR_DUPLICATE_TRANSACTION.getCode());
                responseDTO.setMessage(errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERROR_DUPLICATE_TRANSACTION.getDescription()));
                responseDTO.setBankNit(notifyBillPaymentInDTO.getBankNitOne());
                responseDTO.setAuthorizationCode(notifyBillPaymentInDTO.getEanCode());

                log.info(LOG_COMPONENT + " - code: " + responseDTO.getCode() + " - messsage: " + responseDTO.getMessage());
                consignmentWSService.consignmentUpdate(consigId, ConsignmentWSStatus.DUPLICATED.getId(), null);
                return responseDTO;
            }
            if (responseDTO.getCode().equals(CodeErrorEnum.SUCCESSFUL_NOTIFICATION.getCode())) {
                String trnsId = null;
                String status = null;
                StringBuilder movilredKey = new StringBuilder();
                movilredKey.append(notifyBillPaymentInDTO.getCompensationCodeOne()).append(notifyBillPaymentInDTO.getBillNumber()).append(notifyBillPaymentInDTO.getBillTotalAmount()).append(notifyBillPaymentInDTO.getTransactionDate());

                ConsignmentInDTO inDTOM = new ConsignmentInDTO();
                inDTOM.setAmount(notifyBillPaymentInDTO.getBillTotalAmount());
                inDTOM.setRegId(Integer.parseInt(responseDTO.getAuthorizationNumber()));
                inDTOM.setBank(consignmentUtilities.loadPropertyMahindra("mahinbraBankIdBogota"));
                inDTOM.setClient(client);
                inDTOM.setDocumentDate(notifyBillPaymentInDTO.getTransactionDate());
                inDTOM.setExternalReference(notifyBillPaymentInDTO.getUuid());
                inDTOM.setMovilRedKey(movilredKey.toString());
                inDTOM.setCorrelationId(responseDTO.getCorrelationId());

                ConsignmentOutDTO mahindraOutDTO = mahindraUtils.mahindraCommunication(inDTOM, MahindraOperationEnum.APPROVAL);
                responseDTO.setAuthorizationNumber(Validation.isNullOrEmpty(mahindraOutDTO) ? null : mahindraOutDTO.getTransactionIdentificator());
                status = String.valueOf(mahindraOutDTO.getStatus());
                trnsId = mahindraOutDTO.getTransactionIdentificator();

                if (Validation.isNullOrEmpty(status)) {
                    throw new BusinessException(CodeErrorEnum.ERRORDISPERSE);
                }


                responseDTO.setCode(CodeErrorEnum.SUCCESSFUL_NOTIFICATION.getCode());
                responseDTO.setMessage(errorMessagesLoader.getErrorMensage(CodeErrorEnum.SUCCESSFUL_NOTIFICATION.getDescription()));
                responseDTO.setAuthorizationNumber(trnsId);
                responseDTO.setBankNit(notifyBillPaymentInDTO.getBankNitOne());
                responseDTO.setAuthorizationCode(notifyBillPaymentInDTO.getEanCode());
                log.info(LOG_COMPONENT + " - code: " + responseDTO.getCode() + " - messsage: " + responseDTO.getMessage());
            } else {
                responseDTO.setBankNit(notifyBillPaymentInDTO.getBankNitOne());
                responseDTO.setSystemDate(Utilities.getCurrentDateFormat("yyyyMMddHHmmss"));
                responseDTO.setTransactionDate(notifyBillPaymentInDTO.getTransactionDate());
                if (Validation.isNullOrEmpty(responseDTO.getMessage())) {
                    responseDTO = consignmentUtilities.genResponse(responseDTO, CodeErrorEnum.getEnum(responseDTO.getCode()), SeverityEnum.ERROR);
                }
            }

            return responseDTO;
        } catch (BusinessException var17) {
            ErrorDTO errorDTO = var17.getErrorDTO();
            throw new BusinessException(var17, errorDTO);
        }
    }

    private void validateNotifyParameters(NotifyBillPaymentInDTO notifyBillPaymentInDTO) throws BusinessException {
        log.info(CLASS_NAME + " - validateNotifyParameters");
        Validation.validateRequiredFields(notifyBillPaymentInDTO, consignmentUtilities.loadPropertyBogota("requiredFieldsNotifyBankBogota").split(","), errorMessagesLoader);
        Validation.valideParamInteger(BILLTOTALAMOUNT, notifyBillPaymentInDTO.getBillTotalAmount(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(EFFECTIVEAMOUNT, notifyBillPaymentInDTO.getEffectiveAmount(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(BILLINGCOMPANYAGREEMENTONE, notifyBillPaymentInDTO.getBillingCompanyAgreementOne(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(BILLINGCOMPANYAGREEMENTTWO, notifyBillPaymentInDTO.getBillingCompanyAgreementTwo(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(BANKNITONE, notifyBillPaymentInDTO.getBankNitOne(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(BANKNITTWO, notifyBillPaymentInDTO.getBankNitTwo(), errorMessagesLoader);
        Validation.validateDateFormatRequired(TRANSACTIONDATE, notifyBillPaymentInDTO.getTransactionDate(), "yyyyMMddHHmmss", errorMessagesLoader);
        Validation.valideParamInteger(UUID, notifyBillPaymentInDTO.getUuid(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(OFFICECODE, notifyBillPaymentInDTO.getOfficeCode(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(COMPENSATIONCODEONE, notifyBillPaymentInDTO.getCompensationCodeOne(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(COMPENSATIONCODETWO, notifyBillPaymentInDTO.getCompensationCodeTwo(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(EANCODE, notifyBillPaymentInDTO.getEanCode(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(AGREEMENTCODE, notifyBillPaymentInDTO.getAgreementCode(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(BILLTOTALAMOUNT, notifyBillPaymentInDTO.getBillTotalAmount(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(EFFECTIVEAMOUNT, notifyBillPaymentInDTO.getEffectiveAmount(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(ACCOUNTNUMBER, notifyBillPaymentInDTO.getAccountNumber(), errorMessagesLoader);
        Validation.valideParamInteger(WORKINGDAY, notifyBillPaymentInDTO.getWorkingDay(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(ACCOUNTTYPE, notifyBillPaymentInDTO.getAccountType(), errorMessagesLoader);
        Validation.validateDateFormatRequired(ACCOUNTANTDATE, notifyBillPaymentInDTO.getAccountantDate(), "yyyyMMdd", errorMessagesLoader);
        Validation.valideParamInteger(BILLNUMBER, notifyBillPaymentInDTO.getBillNumber(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(REFERENCEFIELDTWO, notifyBillPaymentInDTO.getReferenceFieldTwo(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(REFERENCEFIELDTHREE, notifyBillPaymentInDTO.getReferenceFieldThree(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(REFERENCEFIELDFOUR, notifyBillPaymentInDTO.getReferenceFieldFour(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(REFERENCEFIELDFIVE, notifyBillPaymentInDTO.getReferenceFieldFive(), errorMessagesLoader);
        Validation.validateAlphabetIgnoreEmpty(TRANSACCIONCHANNEL, notifyBillPaymentInDTO.getTransactionChannel(), errorMessagesLoader);
        Validation.validateAlphabetIgnoreEmpty(CURRENCYTYPE, notifyBillPaymentInDTO.getCurrencyType(), errorMessagesLoader);
        Validation.validateMaxLength(BILLTOTALAMOUNT, notifyBillPaymentInDTO.getBillTotalAmount(), 15, errorMessagesLoader);
        Validation.validateMinLength(BILLTOTALAMOUNT, notifyBillPaymentInDTO.getBillTotalAmount(), 3, errorMessagesLoader);
        Validation.validateMaxLength(EFFECTIVEAMOUNT, notifyBillPaymentInDTO.getEffectiveAmount(), 15, errorMessagesLoader);
        Validation.validateMinLength(EFFECTIVEAMOUNT, notifyBillPaymentInDTO.getEffectiveAmount(), 3, errorMessagesLoader);
        Validation.validateMaxLength(BILLINGCOMPANYNAMEONE, notifyBillPaymentInDTO.getBillingCompanyNameOne(), 20, errorMessagesLoader);
        Validation.validateMaxLength(BILLINGCOMPANYNAMETWO, notifyBillPaymentInDTO.getBillingCompanyNameTwo(), 20, errorMessagesLoader);
        Validation.valideLength(BILLINGCOMPANYAGREEMENTONE, notifyBillPaymentInDTO.getBillingCompanyAgreementOne(), 4, errorMessagesLoader);
        Validation.valideLength(BILLINGCOMPANYAGREEMENTTWO, notifyBillPaymentInDTO.getBillingCompanyAgreementTwo(), 4, errorMessagesLoader);
        Validation.validateMaxLength(BANKNITONE, notifyBillPaymentInDTO.getBankNitOne(), 10, errorMessagesLoader);
        Validation.validateMaxLength(BANKNITTWO, notifyBillPaymentInDTO.getBankNitTwo(), 10, errorMessagesLoader);
        Validation.validateMaxLength(UUID, notifyBillPaymentInDTO.getUuid(), 20, errorMessagesLoader);
        Validation.valideLength(TRANSACCIONCHANNEL, notifyBillPaymentInDTO.getTransactionChannel(), 3, errorMessagesLoader);
        Validation.validateMaxLength(OFFICECODE, notifyBillPaymentInDTO.getOfficeCode(), 4, errorMessagesLoader);
        Validation.validateMinLength(OFFICECODE, notifyBillPaymentInDTO.getOfficeCode(), 3, errorMessagesLoader);
        Validation.validateMaxLength(COMPENSATIONCODEONE, notifyBillPaymentInDTO.getCompensationCodeOne(), 3, errorMessagesLoader);
        Validation.validateMaxLength(COMPENSATIONCODETWO, notifyBillPaymentInDTO.getCompensationCodeTwo(), 3, errorMessagesLoader);
        Validation.validateMaxLength(EANCODE, notifyBillPaymentInDTO.getEanCode(), 13, errorMessagesLoader);
        Validation.validateMaxLength(AGREEMENTCODE, notifyBillPaymentInDTO.getAgreementCode(), 4, errorMessagesLoader);
        Validation.valideLength(CURRENCYTYPE, notifyBillPaymentInDTO.getCurrencyType(), 3, errorMessagesLoader);
        Validation.validateMaxLength(ACCOUNTNUMBER, notifyBillPaymentInDTO.getAccountNumber(), 10, errorMessagesLoader);
        Validation.valideLength(ACCOUNTTYPE, notifyBillPaymentInDTO.getAccountType(), 1, errorMessagesLoader);
        Validation.valideLength(WORKINGDAY, notifyBillPaymentInDTO.getWorkingDay(), 1, errorMessagesLoader);
        Validation.validateMaxLength(BILLNUMBER, notifyBillPaymentInDTO.getBillNumber(), 24, errorMessagesLoader);
        Validation.validateMaxLength(REFERENCEFIELDTWO, notifyBillPaymentInDTO.getReferenceFieldTwo(), 24, errorMessagesLoader);
        Validation.validateMaxLength(REFERENCEFIELDTHREE, notifyBillPaymentInDTO.getReferenceFieldThree(), 20, errorMessagesLoader);
        Validation.validateMaxLength(REFERENCEFIELDFOUR, notifyBillPaymentInDTO.getReferenceFieldFour(), 20, errorMessagesLoader);
        Validation.validateMaxLength(REFERENCEFIELDFIVE, notifyBillPaymentInDTO.getReferenceFieldFive(), 20, errorMessagesLoader);
        Validation.validateRange(ACCOUNTTYPE, notifyBillPaymentInDTO.getAccountType(), 1, 2, errorMessagesLoader);
        if (!Validation.isNullOrEmpty(notifyBillPaymentInDTO.getCheckAmount())) {
            String msg = errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERRORCHECKAMOUNT.getDescription(), CHECKAMOUNT);
            throw new BusinessException(CodeErrorEnum.ERRORCHECKAMOUNT, msg, CHECKAMOUNT);
        }
    }

}

