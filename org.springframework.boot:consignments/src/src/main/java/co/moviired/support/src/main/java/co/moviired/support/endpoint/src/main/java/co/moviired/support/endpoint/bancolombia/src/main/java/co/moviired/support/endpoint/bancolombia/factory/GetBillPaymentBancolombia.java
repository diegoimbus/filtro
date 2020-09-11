package co.moviired.support.endpoint.bancolombia.factory;

import co.moviired.support.endpoint.bancobogota.dto.consignment.in.ConsignmentInDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.ConsignmentOutDTO;
import co.moviired.support.endpoint.bancolombia.dto.DataQueryRequest;
import co.moviired.support.endpoint.bancolombia.dto.DataQueryResponse;
import co.moviired.support.endpoint.bancolombia.interfaces.IGetBillPaymentBancolombia;
import co.moviired.support.endpoint.util.exceptions.BusinessException;
import co.moviired.support.endpoint.util.exceptions.CodeErrorEnum;
import co.moviired.support.endpoint.util.generics.ErrorMessagesLoader;
import co.moviired.support.endpoint.util.generics.Validation;
import co.moviired.support.endpoint.util.util.ConsignmentUtilities;
import co.moviired.support.endpoint.util.util.MahindraUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;


@Slf4j
@Component
public class GetBillPaymentBancolombia implements IGetBillPaymentBancolombia {

    private static final String CLASS_NAME = GetBillPaymentBancolombia.class.getSimpleName();
    private static final String MESSAGE_RESPONSE = "Consulta referencia exitosa";

    private final ConsignmentUtilities consignmentUtilities;
    private final ErrorMessagesLoader errorMessagesLoader;
    private final MahindraUtils mahindraUtils;

    public GetBillPaymentBancolombia(@NotNull ConsignmentUtilities consignmentUtilities,
                                     @NotNull ErrorMessagesLoader errorMessagesLoader,
                                     @NotNull MahindraUtils mahindraUtils) {
        super();
        this.consignmentUtilities = consignmentUtilities;
        this.errorMessagesLoader = errorMessagesLoader;
        this.mahindraUtils = mahindraUtils;
    }

    public DataQueryResponse getBillAmount(DataQueryRequest dataQueryRequest) throws BusinessException {

        log.debug(CLASS_NAME + " - getBillAmount");
        Validation.validateRequiredFields(dataQueryRequest, consignmentUtilities.loadPropertyBancolombia("requiredFieldsGetBillAmountBancolombia").split(","), errorMessagesLoader);

        ConsignmentInDTO inDTOM = new ConsignmentInDTO();
        inDTOM.setClient(dataQueryRequest.getPhoneNumber());

        ConsignmentOutDTO mahindraOutDTO = mahindraUtils.mahindraQuery(inDTOM);
        int status = mahindraOutDTO.getStatus();

        if (Validation.isNullOrEmpty(status)) {
            throw new BusinessException(CodeErrorEnum.ERRORDISPERSE);
        }
        DataQueryResponse response = new DataQueryResponse();

        if( status == 2) {
            response.setResponseCode(CodeErrorEnum.PAYMENT_NO_FOUND.getCode());
            response.setResponseMessage(MESSAGE_RESPONSE);

            response.setPhoneNumber(mahindraOutDTO.getPhoneNumber());
            response.setMessage(mahindraOutDTO.getResponseMessage());
            response.setFirtsName(mahindraOutDTO.getFirtsName());
            response.setLastName(mahindraOutDTO.getLastName());
            response.setEmail(mahindraOutDTO.getEmail());
            response.setDob(mahindraOutDTO.getDob());
            response.setCity(mahindraOutDTO.getCity());
            response.setGender(mahindraOutDTO.getGender());
            response.setStatus(mahindraOutDTO.getStatusId());

        }else{
            response.setResponseCode(CodeErrorEnum.ERROR_RESPONSE_MAHINDRA.getCode());
            response.setResponseMessage(consignmentUtilities.messageProperty(CodeErrorEnum.ERROR_RESPONSE_MAHINDRA.getDescription()));
        }

        return response;
    }

}

