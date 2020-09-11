package co.moviired.support.endpoint.bancobogota.factory.impl;

import co.moviired.support.domain.entity.mysql.ConsignmentWS;
import co.moviired.support.endpoint.bancobogota.dto.consignment.in.*;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.ConsignmentOutDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.DisperseConsignmentOutDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.RevertBillPaymentOutDTO;
import co.moviired.support.endpoint.bancobogota.interfaces.IRevertBillPayment;
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
import java.util.Map;

@Slf4j
@Component
public class RevertBankBogota implements IRevertBillPayment {

    private static final String LOG_COMPONENT = "PROCESS RevertBankBogota";
    private static final String BILLINGCOMPANYNAME1 = "billingCompanyName1";
    private static final String BILLINGCOMPANYAGREEMENT = "billingCompanyAgreement";
    private static final String BANKNIT = "bankNit";
    private static final String TRANSACTIONDATE = "transactionDate";
    private static final String TRANSACCIONCHANNEL = "transaccionChannel";
    private static final String OFFICECODE = "officeCode";
    private static final String COMPENSATIONCODE = "compensationCode";
    private static final String DOCUMENTTYPE = "documentType";
    private static final String CLIENTDOCUMENTNUMBER = "clientDocumentNumber";
    private static final String UUIDREVERT = "uuidRevert";
    private static final String PAYMENTCHANNELREVERT = "paymentChannelRevert";
    private static final String OFFICECODEREVERT = "officeCodeRevert";
    private static final String COMPENSATIONCODEREVERTONE = "compensationCodeRevertOne";
    private static final String COMPANYDOCUMENTTYPEREVERT = "companyDocumentTypeRevert";
    private static final String CLIENTDOCUMENTNUMBERREVERT = "clientDocumentNumberRevert";
    private static final String ACCOUNTANTDATEREVERT = "accountantDateRevert";
    private static final String AGREEMENTCODEREVERT = "agreementCodeRevert";
    private static final String BILLNUMBERREVERT = "billNumberRevert";
    private static final String REFERENCEFIELDTWO = "referenceFieldTwo";
    private static final String REFERENCEFIELDTHREE = "referenceFieldThree";
    private static final String REFERENCEFIELDFOUR = "referenceFieldFour";
    private static final String REFERENCEFIELDFIVE = "referenceFieldFive";
    private static final String CURRENCYTYPEREVERT = "currencyTypeRevert";
    private static final String TOTALBILLAMOUNTREVERT = "totalBillAmountRevert";
    private static final String EFFECTIVEAMOUNTREVERT = "effectiveAmountRevert";
    private static final String CHECKAMOUNTREVERT = "checkAmountRevert";
    private static final String ACCOUNTNUMBERREVERT = "accountNumberRevert";
    private static final String ACCOUNTTYPEREVERT = "accountTypeRevert";
    private static final String WORKINGDAYREVERT = "workingDayRevert";
    private static final String COMPENSATIONCODEREVERTTWO = "compensationCodeRevertTwo";
    private static final String BILLINGCOMPANYNAMEREVERT = "billingCompanyNameRevert";
    private static final String BILLINGCOMPANYAGREEMENTREVERT = "billingCompanyAgreementRevert";
    private static final String BANKNITREVERT = "bankNitRevert";

    private final ConsignmentWSService consignmentWSService;
    private final MahindraUtils mahindraUtils;
    private final ConsignmentUtilities consignmentUtilities;
    private final ErrorMessagesLoader errorMessagesLoader;

    public RevertBankBogota(@NotNull ConsignmentWSService consignmentWSService,
                            @NotNull MahindraUtils mahindraUtils,
                            @NotNull ConsignmentUtilities consignmentUtilities,
                            @NotNull ErrorMessagesLoader errorMessagesLoader) {
        super();
        this.consignmentWSService = consignmentWSService;
        this.mahindraUtils = mahindraUtils;
        this.consignmentUtilities = consignmentUtilities;
        this.errorMessagesLoader = errorMessagesLoader;
    }

    public RevertBillPaymentOutDTO revert(RevertBillPaymentInDTO revertBillPaymentInDTO) throws BusinessException {
        this.validateParameters(revertBillPaymentInDTO);
        String trnsId;
        RevertBillPaymentOutDTO revert = new RevertBillPaymentOutDTO();
        ConsignmentWS consignmentWSRevert = consignmentWSService.consignmentRevertMahindra(revertBillPaymentInDTO);
        if(consignmentWSRevert == null) {
            if(consignmentWSService.consignmentVerRevertMahindra(revertBillPaymentInDTO)) {
                revert.setCode(CodeErrorEnum.ERROR_DUPLICATE_REVERT.getCode());
                revert.setMessage(errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERROR_DUPLICATE_REVERT.getDescription()));
                revert.setBankNit(revertBillPaymentInDTO.getBankNit());
                revert.setAuthorizationNumber(revertBillPaymentInDTO.getAccountNumberRevert());
            }else {
                revert.setCode(CodeErrorEnum.PAYMENT_NO_FOUND.getCode());
                revert.setMessage(errorMessagesLoader.getErrorMensage(CodeErrorEnum.PAYMENT_NO_FOUND.getDescription()));
                revert.setBankNit(revertBillPaymentInDTO.getBankNit());
                revert.setAuthorizationNumber(revertBillPaymentInDTO.getAccountNumberRevert());
            }
        }else{
            String[] channels = new String[]{consignmentUtilities.loadPropertyBogota("channelTat"), consignmentUtilities.loadPropertyBogota("channelAlianzas"), consignmentUtilities.loadPropertyBogota("channelMahindra")};
            Map<String, String> channelInfo = ExtractChannelCommon.extractConsignmentChannel(revertBillPaymentInDTO.getBillNumberRevert());
            String channel = channelInfo.get("CONSIGNMENT_CHANNEL");
            String client = channelInfo.get("CONSIGNMENT_CLIENT");
            Validation.validateConsignmentChannel(channel, channels, errorMessagesLoader);

            revertBillPaymentInDTO.setTransaccionChannel(channel);
            new DisperseConsignmentOutDTO();

            ConsignmentInDTO inDTOM = new ConsignmentInDTO();
            inDTOM.setAmount(revertBillPaymentInDTO.getTotalBillAmountRevert());
            inDTOM.setBank(consignmentUtilities.loadPropertyMahindra("mahinbraBankIdBogota"));
            inDTOM.setRegId(consignmentWSRevert.getId());
            inDTOM.setClient(client);
            inDTOM.setDocumentDate(revertBillPaymentInDTO.getTransactionDate());
            inDTOM.setExternalReference(revertBillPaymentInDTO.getUuidRevert());
            inDTOM.setMovilRedKey(revertBillPaymentInDTO.getCompensationCode() + revertBillPaymentInDTO.getBillNumberRevert() + revertBillPaymentInDTO.getTotalBillAmountRevert() + revertBillPaymentInDTO.getTransactionDate());
            inDTOM.setCorrelationId(consignmentWSRevert.getCorrelationIdRevert());

            try {
                ConsignmentOutDTO mahindraOutDTO = mahindraUtils.mahindraCommunication(inDTOM, MahindraOperationEnum.REVERT);
                trnsId = mahindraOutDTO.getTransactionIdentificator();
            } catch (BusinessException var15) {
                var15.getErrorDTO().setDescription(errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERROR_MAHINDRA.getDescription()).concat(" ").concat(var15.getErrorDTO().getDescription()));
                throw var15;
            }

            revert.setCode(CodeErrorEnum.SUCCESSFUL_NOTIFICATION.getCode());
            revert.setMessage(errorMessagesLoader.getErrorMensage(CodeErrorEnum.SUCCESSFUL_REVERT.getDescription()));
            revert.setIdMahindra(trnsId);
            revert.setBankNit(revertBillPaymentInDTO.getBankNit());
            revert.setAuthorizationNumber(revertBillPaymentInDTO.getAccountNumberRevert());
            revert.setCorrelationId(inDTOM.getCorrelationId());
        }

        log.info(LOG_COMPONENT + " - code: " + revert.getCode() + " - messsage: " + revert.getMessage());
        return revert;
    }

    private void validateParameters(RevertBillPaymentInDTO revertBillPaymentInDTO) throws BusinessException {
        Validation.validateRequiredFields(revertBillPaymentInDTO, consignmentUtilities.loadPropertyBogota("requiredFieldsRevertBankBogota").split(","), errorMessagesLoader);
        Validation.validateDateFormatRequired(TRANSACTIONDATE, revertBillPaymentInDTO.getTransactionDate(), "yyyyMMddHHmmss", errorMessagesLoader);
        Validation.valideParamInteger(BILLNUMBERREVERT, revertBillPaymentInDTO.getBillNumberRevert(), errorMessagesLoader);
        Validation.valideParamInteger(TOTALBILLAMOUNTREVERT, revertBillPaymentInDTO.getTotalBillAmountRevert(), errorMessagesLoader);
        Validation.valideParamInteger(WORKINGDAYREVERT, revertBillPaymentInDTO.getWorkingDayRevert(), errorMessagesLoader);
        Validation.valideParamInteger(UUIDREVERT, revertBillPaymentInDTO.getUuidRevert(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(COMPENSATIONCODE, revertBillPaymentInDTO.getCompensationCode(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(BILLINGCOMPANYAGREEMENT, revertBillPaymentInDTO.getBillingCompanyAgreement(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(BANKNIT, revertBillPaymentInDTO.getBankNit(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(OFFICECODE, revertBillPaymentInDTO.getOfficeCode(), errorMessagesLoader);
        Validation.validateAlphabetIgnoreEmpty(TRANSACCIONCHANNEL, revertBillPaymentInDTO.getTransaccionChannel(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(CLIENTDOCUMENTNUMBER, revertBillPaymentInDTO.getClientDocumentNumber(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(OFFICECODEREVERT, revertBillPaymentInDTO.getOfficeCodeRevert(), errorMessagesLoader);
        Validation.validateAlphabetIgnoreEmpty(COMPANYDOCUMENTTYPEREVERT, revertBillPaymentInDTO.getCompanyDocumentTypeRevert(), errorMessagesLoader);
        Validation.validateAlphabetIgnoreEmpty(DOCUMENTTYPE, revertBillPaymentInDTO.getDocumentType(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(CLIENTDOCUMENTNUMBERREVERT, revertBillPaymentInDTO.getClientDocumentNumberRevert(), errorMessagesLoader);
        Validation.validateAlphabetIgnoreEmpty(PAYMENTCHANNELREVERT, revertBillPaymentInDTO.getPaymentChannelRevert(), errorMessagesLoader);
        Validation.validateDateFormat(ACCOUNTANTDATEREVERT, revertBillPaymentInDTO.getAccountantDateRevert(), "yyyyMMdd", errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(AGREEMENTCODEREVERT, revertBillPaymentInDTO.getAgreementCodeRevert(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(REFERENCEFIELDTWO, revertBillPaymentInDTO.getReferenceFieldTwo(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(REFERENCEFIELDTHREE, revertBillPaymentInDTO.getReferenceFieldThree(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(REFERENCEFIELDFOUR, revertBillPaymentInDTO.getReferenceFieldFour(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(REFERENCEFIELDFIVE, revertBillPaymentInDTO.getReferenceFieldFive(), errorMessagesLoader);
        Validation.validateAlphabetIgnoreEmpty(CURRENCYTYPEREVERT, revertBillPaymentInDTO.getCurrencyTypeRevert(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(EFFECTIVEAMOUNTREVERT, revertBillPaymentInDTO.getEffectiveAmountRevert(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(CHECKAMOUNTREVERT, revertBillPaymentInDTO.getCheckAmountRevert(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(ACCOUNTNUMBERREVERT, revertBillPaymentInDTO.getAccountNumberRevert(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(ACCOUNTTYPEREVERT, revertBillPaymentInDTO.getAccountTypeRevert(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(BILLINGCOMPANYAGREEMENTREVERT, revertBillPaymentInDTO.getBillingCompanyAgreementRevert(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(BANKNITREVERT, revertBillPaymentInDTO.getBankNitRevert(), errorMessagesLoader);
        Validation.validateIntegerIgnoreEmpty(COMPENSATIONCODEREVERTTWO, revertBillPaymentInDTO.getCompensationCodeRevertTwo(), errorMessagesLoader);
        Validation.validateMaxLength(BILLINGCOMPANYNAME1, revertBillPaymentInDTO.getBillingCompanyName1(), 20, errorMessagesLoader);
        Validation.validateLength(BILLINGCOMPANYAGREEMENT, revertBillPaymentInDTO.getBillingCompanyAgreement(), 1, 4, errorMessagesLoader);
        Validation.validateLength(BANKNIT, revertBillPaymentInDTO.getBankNit(), 1, 10, errorMessagesLoader);
        Validation.valideLength(TRANSACCIONCHANNEL, revertBillPaymentInDTO.getTransaccionChannel(), 3, errorMessagesLoader);
        Validation.validateStringValuesConfig(TRANSACCIONCHANNEL, revertBillPaymentInDTO.getTransaccionChannel(), consignmentUtilities.loadProperty("VALUES_ALLOWED_transaccionChannel"), errorMessagesLoader);
        Validation.validateLength(OFFICECODE, revertBillPaymentInDTO.getOfficeCode(), 3, 4, errorMessagesLoader);
        Validation.validateLength(COMPENSATIONCODE, revertBillPaymentInDTO.getCompensationCode(), 1, 3, errorMessagesLoader);
        Validation.valideLength(DOCUMENTTYPE, revertBillPaymentInDTO.getDocumentType(), 1, errorMessagesLoader);
        Validation.validateStringValuesConfig(DOCUMENTTYPE, revertBillPaymentInDTO.getDocumentType(), consignmentUtilities.loadProperty("VALUES_ALLOWED_documentType"), errorMessagesLoader);
        Validation.validateLength(CLIENTDOCUMENTNUMBER, revertBillPaymentInDTO.getClientDocumentNumber(), 6, 16, errorMessagesLoader);
        Validation.validateLength(UUIDREVERT, revertBillPaymentInDTO.getUuidRevert(), 1, 20, errorMessagesLoader);
        Validation.valideLength(PAYMENTCHANNELREVERT, revertBillPaymentInDTO.getPaymentChannelRevert(), 3, errorMessagesLoader);
        Validation.validateLength(OFFICECODEREVERT, revertBillPaymentInDTO.getOfficeCodeRevert(), 3, 4, errorMessagesLoader);
        Validation.validateLength(COMPENSATIONCODEREVERTONE, revertBillPaymentInDTO.getCompensationCodeRevertOne(), 1, 3, errorMessagesLoader);
        Validation.validateLength(COMPANYDOCUMENTTYPEREVERT, revertBillPaymentInDTO.getCompanyDocumentTypeRevert(), 1, 1, errorMessagesLoader);
        Validation.validateLength(CLIENTDOCUMENTNUMBERREVERT, revertBillPaymentInDTO.getClientDocumentNumberRevert(), 6, 16, errorMessagesLoader);
        Validation.validateLength(AGREEMENTCODEREVERT, revertBillPaymentInDTO.getAgreementCodeRevert(), 1, 4, errorMessagesLoader);
        Validation.validateLength(BILLNUMBERREVERT, revertBillPaymentInDTO.getBillNumberRevert(), 1, 24, errorMessagesLoader);
        Validation.validateMaxLength(REFERENCEFIELDTWO, revertBillPaymentInDTO.getReferenceFieldTwo(), 24, errorMessagesLoader);
        Validation.validateMaxLength(REFERENCEFIELDTHREE, revertBillPaymentInDTO.getReferenceFieldThree(), 20, errorMessagesLoader);
        Validation.validateMaxLength(REFERENCEFIELDFOUR, revertBillPaymentInDTO.getReferenceFieldFour(), 20, errorMessagesLoader);
        Validation.validateMaxLength(REFERENCEFIELDFIVE, revertBillPaymentInDTO.getReferenceFieldFive(), 20, errorMessagesLoader);
        Validation.valideLength(CURRENCYTYPEREVERT, revertBillPaymentInDTO.getCurrencyTypeRevert(), 3, errorMessagesLoader);
        Validation.validateLength(TOTALBILLAMOUNTREVERT, revertBillPaymentInDTO.getTotalBillAmountRevert(), 3, 15, errorMessagesLoader);
        Validation.validateLength(EFFECTIVEAMOUNTREVERT, revertBillPaymentInDTO.getEffectiveAmountRevert(), 3, 15, errorMessagesLoader);
        Validation.validateLength(CHECKAMOUNTREVERT, revertBillPaymentInDTO.getCheckAmountRevert(), 3, 15, errorMessagesLoader);
        Validation.validateLength(ACCOUNTNUMBERREVERT, revertBillPaymentInDTO.getAccountNumberRevert(), 1, 10, errorMessagesLoader);
        Validation.validateLength(ACCOUNTTYPEREVERT, revertBillPaymentInDTO.getAccountTypeRevert(), 1, 1, errorMessagesLoader);
        Validation.validateStringValuesConfig(ACCOUNTTYPEREVERT, revertBillPaymentInDTO.getAccountTypeRevert(), consignmentUtilities.loadProperty("VALUES_ALLOWED_accountTypeRevert"), errorMessagesLoader);
        Validation.validateLength(WORKINGDAYREVERT, revertBillPaymentInDTO.getWorkingDayRevert(), 1, 1, errorMessagesLoader);
        Validation.validateStringValuesConfig(WORKINGDAYREVERT, revertBillPaymentInDTO.getWorkingDayRevert(), consignmentUtilities.loadProperty("VALUES_ALLOWED_workingDayRevert"), errorMessagesLoader);
        Validation.validateLength(COMPENSATIONCODEREVERTTWO, revertBillPaymentInDTO.getCompensationCodeRevertTwo(), 1, 3, errorMessagesLoader);
        Validation.validateMaxLength(BILLINGCOMPANYNAMEREVERT, revertBillPaymentInDTO.getBillingCompanyNameRevert(), 20, errorMessagesLoader);
        Validation.validateLength(BILLINGCOMPANYAGREEMENTREVERT, revertBillPaymentInDTO.getBillingCompanyAgreementRevert(), 1, 4, errorMessagesLoader);
        Validation.validateLength(BANKNITREVERT, revertBillPaymentInDTO.getBankNitRevert(), 1, 10, errorMessagesLoader);
    }

}

