package co.moviired.support.endpoint.bancobogota.factory.impl;

import co.moviired.support.endpoint.bancobogota.dto.consignment.in.ConsignmentInDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.in.GetBillAmountInDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.ConsignmentOutDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.GetBillAmountOutDTO;
import co.moviired.support.endpoint.bancobogota.interfaces.IGetBillPayment;
import co.moviired.support.endpoint.util.exceptions.BusinessException;
import co.moviired.support.endpoint.util.exceptions.CodeErrorEnum;
import co.moviired.support.endpoint.util.generics.ErrorMessagesLoader;
import co.moviired.support.endpoint.util.generics.LoadProperty;
import co.moviired.support.endpoint.util.generics.Validation;
import co.moviired.support.endpoint.util.util.ConsignmentUtilities;
import co.moviired.support.endpoint.util.util.MahindraUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class GetBillPaymentBogota implements IGetBillPayment {

    private static final String CLASS_NAME = GetBillPaymentBogota.class.getSimpleName();
    private static final String MESSAGE_RESPONSE = "Consulta referencia exitosa";
    private static final String SEVERY_RESPONSE = "I";

    private final ConsignmentUtilities consignmentUtilities;
    private final ErrorMessagesLoader errorMessagesLoader;
    private final MahindraUtils mahindraUtils;
    private final LoadProperty loadProperty;

    public GetBillPaymentBogota(@NotNull ConsignmentUtilities consignmentUtilities,
                                @NotNull ErrorMessagesLoader errorMessagesLoader,
                                @NotNull MahindraUtils mahindraUtils,
                                @NotNull LoadProperty loadProperty) {
        super();
        this.consignmentUtilities = consignmentUtilities;
        this.errorMessagesLoader = errorMessagesLoader;
        this.mahindraUtils = mahindraUtils;
        this.loadProperty = loadProperty;
    }

    public GetBillAmountOutDTO getBillAmount(GetBillAmountInDTO getBillAmountInDTO) throws BusinessException {

        log.debug(CLASS_NAME + " - getBillAmount");

        String[] channelsAvailable = new String[]{consignmentUtilities.loadProperty("CHANNEL_TAT"), consignmentUtilities.loadProperty("CHANNEL_ALIANZAS"), consignmentUtilities.loadProperty("CHANNEL_MAHINDRA")};
        Map<String, String> channelInfo = ExtractChannelCommon.extractConsignmentChannel(getBillAmountInDTO.getBillNumber());
        String channel = channelInfo.get("CONSIGNMENT_CHANNEL");
        String client = channelInfo.get("CONSIGNMENT_CLIENT");
        Validation.validateConsignmentChannel(channel, channelsAvailable, errorMessagesLoader);

        Validation.validateRequiredFields(getBillAmountInDTO, consignmentUtilities.loadProperty("REQUIRED_FIELDS_GET_BILL_AMOUNT_" + consignmentUtilities.loadProperty("BANK_BOGOTA")).split(","), errorMessagesLoader);

        ConsignmentInDTO inDTOM = new ConsignmentInDTO();
        inDTOM.setClient(client);
        String codeRes;
        String descRes;

        ConsignmentOutDTO mahindraOutDTO = mahindraUtils.mahindraQuery(inDTOM);
        int status = mahindraOutDTO.getStatus();

        if (Validation.isNullOrEmpty(status)) {
            throw new BusinessException(CodeErrorEnum.ERRORDISPERSE);
        }

        GetBillAmountOutDTO getBill = new GetBillAmountOutDTO();
        Date date = new Date();
        DateFormat hourFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        if( status == 2) {
            codeRes = CodeErrorEnum.PAYMENT_NO_FOUND.getCode();
            descRes = MESSAGE_RESPONSE;
        }else{
            codeRes = CodeErrorEnum.ERROR_RESPONSE_MAHINDRA.getCode();
            descRes = consignmentUtilities.messageProperty(CodeErrorEnum.ERROR_RESPONSE_MAHINDRA.getDescription());
        }

        getBill.setBankNitOne(getBillAmountInDTO.getBankNitOne());
        getBill.setTransactionDate(getBillAmountInDTO.getTransactionDate());
        getBill.setSystemDate(hourFormat.format(date));
        getBill.setCurrencyType(loadProperty.systemPropertyBogota("currencyType"));
        getBill.setBillAmount(loadProperty.systemPropertyBogota("billAmount"));
        getBill.setBillNumber(getBillAmountInDTO.getBillNumber());
        getBill.setReferenceFieldTwo(getBillAmountInDTO.getReferenceFieldTwo());
        getBill.setReferenceFieldThree(getBillAmountInDTO.getReferenceFieldThree());
        getBill.setReferenceFieldFour(getBillAmountInDTO.getReferenceFieldFour());
        getBill.setReferenceFieldFive(getBillAmountInDTO.getReferenceFieldFive());
        getBill.setBillingCompanyName(getBillAmountInDTO.getBillingCompanyNameOne());
        getBill.setBillingCompanyAgreement(getBillAmountInDTO.getBillingCompanyAgreementOne());
        getBill.setBankNitTwo(getBillAmountInDTO.getBankNitTwo());
        getBill.setSeverity(SEVERY_RESPONSE);
        getBill.setExpirationDate(loadProperty.systemPropertyBogota("expirationDays"));
        getBill.setCode(codeRes);
        getBill.setMessage(descRes);

        log.info(CLASS_NAME + " - Cod. Error: " + codeRes + " - Mensaje: " + descRes);

        return getBill;
    }

}

