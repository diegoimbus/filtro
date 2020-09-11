package co.moviired.support.endpoint.util.util;

import co.moviired.support.endpoint.bancobogota.dto.consignment.in.GetBillAmountInDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.in.NotifyBillPaymentInDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.in.RevertBillPaymentInDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.GetBillAmountOutDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.NotifyBillPaymentOutDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.RevertBillPaymentOutDTO;
import co.moviired.support.endpoint.bancobogota.dto.generics.ConsignmentResponseDTO;
import co.moviired.support.endpoint.bancobogota.dto.generics.ErrorDTO;
import co.moviired.support.endpoint.util.enums.SeverityEnum;
import co.moviired.support.endpoint.util.exceptions.CodeErrorEnum;
import co.moviired.support.endpoint.util.generics.ErrorMessagesLoader;
import co.moviired.support.endpoint.util.generics.LoadProperty;
import co.moviired.support.endpoint.util.generics.Utilities;
import co.moviired.support.endpoint.util.generics.Validation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public final class ConsignmentUtilities{

    private static final String FORMAT_DATE = "yyyyMMddHHmmss";

    private final LoadProperty loadProperty;
    private final ErrorMessagesLoader errorMessagesLoader;

    public ConsignmentUtilities(@NotNull LoadProperty loadProperty,
                                @NotNull ErrorMessagesLoader errorMessagesLoader) {
        super();
        this.loadProperty = loadProperty;
        this.errorMessagesLoader = errorMessagesLoader;
    }

    public boolean isSuccessfull(CodeErrorEnum response) {
        String code = response.getCode();
        return "00".equals(code) || "0".equals(code);
    }

    public String loadPropertyBogota(String property) {
        return loadProperty.systemPropertyBogota(property);
    }

    public String loadPropertyBancolombia(String property) {
        return loadProperty.systemPropertyBancolombia(property);
    }

    public String loadPropertyMahindra(String property) {
        return loadProperty.systemPropertyMahindra(property);
    }

    public String messageProperty(String property) {
        return loadProperty.messageProperties(property, null);
    }

    public String loadProperty(String property, String... args) {
        String message = loadPropertyMahindra(property);
        if (Validation.isNotEmpty(message) && Validation.isNotNull(args)) {
            message = replacePlaceHolders(message, args);
            return message;
        } else {
            return Validation.isNotEmpty(message) ? message : null;
        }
    }

    private String replacePlaceHolders(String message, String... placeHoldersValues) {
        MessageFormat formater = null;
        formater = new MessageFormat(message);
        message = formater.format(placeHoldersValues);
        return message;
    }

    public List<String> getValues(String keyBase) {
        List<String> values = new ArrayList<>();

        for(int index = 0; index < 100; ++index) {
            String value = loadProperty(keyBase + index);
            if (value == null) {
                break;
            }

            values.add(value);
        }

        return values;
    }

    public NotifyBillPaymentOutDTO genNotifyResponse(NotifyBillPaymentInDTO inDTO) {
        NotifyBillPaymentOutDTO responseDto = new NotifyBillPaymentOutDTO();
        responseDto.setBankNit(inDTO.getBankNitOne());
        responseDto.setSystemDate(Utilities.getCurrentDateFormat(FORMAT_DATE));
        responseDto.setTransactionDate(inDTO.getTransactionDate());
        return responseDto;
    }

    public RevertBillPaymentOutDTO genRevertResponse(RevertBillPaymentInDTO inDTO) {
        RevertBillPaymentOutDTO responseDto = new RevertBillPaymentOutDTO();
        responseDto.setBankNit(inDTO.getBankNit());
        responseDto.setSystemDate(Utilities.getCurrentDateFormat(FORMAT_DATE));
        responseDto.setTransactionDate(inDTO.getTransactionDate());
        responseDto.setUuid(inDTO.getUuidRevert());
        return responseDto;
    }

    public GetBillAmountOutDTO genGetBillAmountResponse(GetBillAmountInDTO getBillAmountInDTO) {
        GetBillAmountOutDTO responseDTO = new GetBillAmountOutDTO();
        responseDTO.setBankNitOne(getBillAmountInDTO.getBankNitOne());
        responseDTO.setBankNitTwo(getBillAmountInDTO.getBankNitTwo());
        responseDTO.setSystemDate(Utilities.getCurrentDateFormat(FORMAT_DATE));
        responseDTO.setTransactionDate(getBillAmountInDTO.getTransactionDate());
        responseDTO.setBillNumber(getBillAmountInDTO.getBillNumber());
        responseDTO.setReferenceFieldTwo(getBillAmountInDTO.getReferenceFieldTwo());
        responseDTO.setReferenceFieldThree(getBillAmountInDTO.getReferenceFieldThree());
        responseDTO.setReferenceFieldFour(getBillAmountInDTO.getReferenceFieldFour());
        responseDTO.setReferenceFieldFive(getBillAmountInDTO.getReferenceFieldFive());
        responseDTO.setBillingCompanyName(getBillAmountInDTO.getBillingCompanyNameOne());
        responseDTO.setBillingCompanyAgreement(getBillAmountInDTO.getBillingCompanyAgreementOne());
        responseDTO.setCurrencyType("COP");
        return responseDTO;
    }

    public <T extends ConsignmentResponseDTO> T genResponse(ConsignmentResponseDTO responseDto, CodeErrorEnum msgConstant, SeverityEnum severity) {
        if (responseDto == null) {
            responseDto = new ConsignmentResponseDTO();
        }

        responseDto.setCode(msgConstant.getCode());
        responseDto.setMessage(errorMessagesLoader.getErrorMensage(msgConstant.getDescription()));
        responseDto.setSeverity(severity.getSeverity());
        return (T) responseDto;
    }

    public <T extends ConsignmentResponseDTO> T genResponse(ConsignmentResponseDTO responseDto, ErrorDTO msgConstant, SeverityEnum severity) {
        if (responseDto == null) {
            responseDto = new ConsignmentResponseDTO();
        }

        if (!msgConstant.getCode().equals(CodeErrorEnum.ERROR_TIME_LIMIT.getCode()) && !msgConstant.getCode().equals(CodeErrorEnum.PENDING_REVERT.getCode()) && !msgConstant.getCode().equals(CodeErrorEnum.PAYMENT_NO_FOUND.getCode()) && !msgConstant.getCode().equals(CodeErrorEnum.FAILED_REVERT.getCode()) && !msgConstant.getCode().equals(CodeErrorEnum.FAILED_NOTIFICATION.getCode())) {
            if (!msgConstant.getCode().equals(CodeErrorEnum.SUCCESSFUL_REVERT.getCode()) && !msgConstant.getCode().equals(CodeErrorEnum.SUCCESSFUL_NOTIFICATION.getCode())) {
                responseDto.setCode(msgConstant.getCode());
            } else {
                responseDto.setCode("00000");
            }
        } else {
            responseDto.setCode("00099");
        }

        responseDto.setMessage(msgConstant.getDescription());
        responseDto.setSeverity(severity.getSeverity());
        return (T) responseDto;
    }
}
