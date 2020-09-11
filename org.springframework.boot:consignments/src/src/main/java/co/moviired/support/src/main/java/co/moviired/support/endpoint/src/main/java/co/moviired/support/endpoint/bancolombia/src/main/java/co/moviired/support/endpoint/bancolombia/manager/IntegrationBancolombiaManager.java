package co.moviired.support.endpoint.bancolombia.manager;

import co.moviired.support.endpoint.bancolombia.dto.*;
import co.moviired.support.endpoint.bancolombia.factory.GetBillPaymentBancolombia;
import co.moviired.support.endpoint.bancolombia.factory.NotifyBillPaymentBancolombia;
import co.moviired.support.endpoint.bancolombia.factory.RevertBankBancolombia;
import co.moviired.support.endpoint.util.exceptions.BusinessException;
import co.moviired.support.endpoint.util.exceptions.CodeErrorEnum;
import co.moviired.support.endpoint.util.generics.Validation;
import co.moviired.support.endpoint.util.util.ConsignmentUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Slf4j
@Component
public class IntegrationBancolombiaManager {
    private static final String CLASS_NAME = IntegrationBancolombiaManager.class.getSimpleName();

    private final GetBillPaymentBancolombia getBillPaymentBancolombia;
    private final NotifyBillPaymentBancolombia notifyBillPaymentBancolombia;
    private final RevertBankBancolombia revertBankBancolombia;
    private final ConsignmentUtilities consignmentUtilities;

    public IntegrationBancolombiaManager(@NotNull GetBillPaymentBancolombia getBillPaymentBancolombia,
                                         @NotNull ConsignmentUtilities consignmentUtilities,
                                         @NotNull NotifyBillPaymentBancolombia notifyBillPaymentBancolombia,
                                         @NotNull RevertBankBancolombia revertBankBancolombia) {
        super();
        this.getBillPaymentBancolombia = getBillPaymentBancolombia;
        this.notifyBillPaymentBancolombia = notifyBillPaymentBancolombia;
        this.consignmentUtilities = consignmentUtilities;
        this.revertBankBancolombia = revertBankBancolombia;
    }

    public DataQueryResponse getBillAmount(DataQueryRequest dataQueryRequest) {
        DataQueryResponse response = new DataQueryResponse();


        try {
            log.debug(CLASS_NAME + " - getBillAmount");
            response = getBillPaymentBancolombia.getBillAmount(dataQueryRequest);
        } catch (BusinessException var4) {
            response.setResponseCode(var4.getErrorDTO().getCode());
            response.setResponseMessage(var4.getErrorDTO().getDescription());
        } catch (Exception var5) {
            response.setResponseCode(CodeErrorEnum.ERROR.getCode());
            response.setResponseMessage(consignmentUtilities.messageProperty(CodeErrorEnum.ERROR.getDescription()));
        }

        return response;
    }

    public DataConsignmentResponse notifyBillPayment(DataConsignmentRequest dataConsignmentRequest) {
        DataConsignmentResponse responseNotify = new DataConsignmentResponse();

        try {
            log.debug(CLASS_NAME, "notifyBillPayment", dataConsignmentRequest);
            responseNotify = notifyBillPaymentBancolombia.notifyBillPayment(dataConsignmentRequest);
        } catch (BusinessException var4) {
            if (Validation.isNull(var4.getErrorDTO())) {
                responseNotify.setResponseCode(CodeErrorEnum.ERROR.getCode());
                responseNotify.setResponseMessage(consignmentUtilities.messageProperty(CodeErrorEnum.ERROR.getDescription()));
            } else {
                responseNotify.setResponseCode(var4.getErrorDTO().getCode());
                responseNotify.setResponseMessage(var4.getErrorDTO().getDescription());
            }
        } catch (Exception var5) {
            responseNotify.setResponseCode(CodeErrorEnum.ERROR.getCode());
            responseNotify.setResponseMessage(consignmentUtilities.messageProperty(CodeErrorEnum.ERROR.getDescription()));
        }

        return responseNotify;
    }

    public DataRevertResponse revertBillPayment(DataRevertRequest dataRevertRequest) {
        DataRevertResponse responseRevert = new DataRevertResponse();

        try {
            log.debug(CLASS_NAME + " - revertBillPayment - " + dataRevertRequest);
            responseRevert = revertBankBancolombia.revert(dataRevertRequest);
        } catch (BusinessException var4) {
            if (Validation.isNull(var4.getErrorDTO())) {
                responseRevert.setResponseCode(CodeErrorEnum.ERROR.getCode());
                responseRevert.setResponseMessage(consignmentUtilities.messageProperty(CodeErrorEnum.ERROR.getDescription()));
            } else {
                responseRevert.setResponseCode(var4.getErrorDTO().getCode());
                responseRevert.setResponseMessage(var4.getErrorDTO().getDescription());
            }
        } catch (Exception var5) {
            responseRevert.setResponseCode(CodeErrorEnum.ERROR.getCode());
            responseRevert.setResponseMessage(consignmentUtilities.messageProperty(CodeErrorEnum.ERROR.getDescription()));
        }

        return responseRevert;
    }

}

