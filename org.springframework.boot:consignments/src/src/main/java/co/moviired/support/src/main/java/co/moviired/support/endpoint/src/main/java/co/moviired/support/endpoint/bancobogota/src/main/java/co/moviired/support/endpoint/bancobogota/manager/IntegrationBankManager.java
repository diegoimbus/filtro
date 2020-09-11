package co.moviired.support.endpoint.bancobogota.manager;

import co.moviired.support.endpoint.bancobogota.dto.consignment.in.*;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.GetBillAmountOutDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.NotifyBillPaymentOutDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.RevertBillPaymentOutDTO;
import co.moviired.support.endpoint.bancobogota.factory.impl.GetBillPaymentBogota;
import co.moviired.support.endpoint.bancobogota.factory.impl.NotifyBillPaymentBogota;
import co.moviired.support.endpoint.bancobogota.factory.impl.RevertBankBogota;
import co.moviired.support.endpoint.util.enums.SeverityEnum;
import co.moviired.support.endpoint.util.exceptions.BusinessException;
import co.moviired.support.endpoint.util.exceptions.CodeErrorEnum;
import co.moviired.support.endpoint.util.generics.Validation;
import co.moviired.support.endpoint.util.util.ConsignmentUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Slf4j
@Component
public class IntegrationBankManager {
    private static final String CLASS_NAME = IntegrationBankManager.class.getSimpleName();

    private final GetBillPaymentBogota getBillPaymentBogota;
    private final NotifyBillPaymentBogota notifyBillPaymentBogota;
    private final RevertBankBogota revertBankBogota;
    private final ConsignmentUtilities consignmentUtilities;

    public IntegrationBankManager(@NotNull GetBillPaymentBogota pGetBillPaymentBogota,
                                  @NotNull ConsignmentUtilities consignmentUtilities,
                                  @NotNull NotifyBillPaymentBogota pnotifyBillPaymentBogota,
                                  @NotNull RevertBankBogota prevertBankBogota) {
        super();
        this.getBillPaymentBogota = pGetBillPaymentBogota;
        this.notifyBillPaymentBogota = pnotifyBillPaymentBogota;
        this.consignmentUtilities = consignmentUtilities;
        this.revertBankBogota = prevertBankBogota;
    }

    public GetBillAmountOutDTO getBillAmount(GetBillAmountInDTO getBillAmountInDTO) {
        GetBillAmountOutDTO response;

        try {
            log.debug(CLASS_NAME + " - getBillAmount");
            response = getBillPaymentBogota.getBillAmount(getBillAmountInDTO);
        } catch (BusinessException var4) {
            if (Validation.isNull(var4.getErrorDTO())) {
                response = consignmentUtilities.genResponse(consignmentUtilities.genGetBillAmountResponse(getBillAmountInDTO), CodeErrorEnum.ERROR, SeverityEnum.ERROR);
            } else {
                response = consignmentUtilities.genResponse(consignmentUtilities.genGetBillAmountResponse(getBillAmountInDTO), var4.getErrorDTO(), SeverityEnum.ERROR);
                response.setCode(var4.getErrorDTO().getCode());
            }
        } catch (Exception var5) {
            response = consignmentUtilities.genResponse(consignmentUtilities.genGetBillAmountResponse(getBillAmountInDTO), CodeErrorEnum.ERROR, SeverityEnum.ERROR);
        }

        return response;
    }

    public NotifyBillPaymentOutDTO notifyBillPayment(NotifyBillPaymentInDTO notifyBillPaymentInDTO) {
        NotifyBillPaymentOutDTO response;

        try {
            log.debug(CLASS_NAME, "notifyBillPayment", notifyBillPaymentInDTO);
            response = notifyBillPaymentBogota.notifyBillPayment(notifyBillPaymentInDTO);
        } catch (BusinessException var4) {
            if (Validation.isNull(var4.getErrorDTO())) {
                response = consignmentUtilities.genResponse(consignmentUtilities.genNotifyResponse(notifyBillPaymentInDTO), CodeErrorEnum.ERROR, SeverityEnum.ERROR);
            } else {
                response = consignmentUtilities.genResponse(consignmentUtilities.genNotifyResponse(notifyBillPaymentInDTO), var4.getErrorDTO(), SeverityEnum.ERROR);
                response.setCode(var4.getErrorDTO().getCode());
            }
        } catch (Exception var5) {
            response = consignmentUtilities.genResponse(consignmentUtilities.genNotifyResponse(notifyBillPaymentInDTO), CodeErrorEnum.ERROR, SeverityEnum.ERROR);
        }

        return response;
    }

    public RevertBillPaymentOutDTO revertBillPayment(RevertBillPaymentInDTO revertBillPaymentInDTO) {
        RevertBillPaymentOutDTO response;

        try {
            log.debug(CLASS_NAME + " - revertBillPayment - " + revertBillPaymentInDTO);
            response = revertBankBogota.revert(revertBillPaymentInDTO);
        } catch (BusinessException var4) {
            if (Validation.isNull(var4.getErrorDTO())) {
                response = consignmentUtilities.genResponse(consignmentUtilities.genRevertResponse(revertBillPaymentInDTO), CodeErrorEnum.ERROR, SeverityEnum.ERROR);
            } else {
                response = consignmentUtilities.genResponse(consignmentUtilities.genRevertResponse(revertBillPaymentInDTO), var4.getErrorDTO(), SeverityEnum.ERROR);
                response.setCode(var4.getErrorDTO().getCode());
            }
        } catch (Exception var5) {
            response = consignmentUtilities.genResponse(consignmentUtilities.genRevertResponse(revertBillPaymentInDTO), CodeErrorEnum.ERROR, SeverityEnum.ERROR);
        }

        return response;
    }

}

