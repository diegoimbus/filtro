package co.moviired.support.endpoint.util.util;

import co.moviired.connector.connector.RestConnector;
import co.moviired.support.domain.enums.ConsignmentWSStatus;
import co.moviired.support.endpoint.bancobogota.dto.consignment.in.ConsignmentInDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.ConsignmentOutDTO;
import co.moviired.support.endpoint.bancobogota.dto.consignment.out.MahindraResponseDTO;
import co.moviired.support.endpoint.util.enums.ConsignmentStatusEnum;
import co.moviired.support.endpoint.util.exceptions.BusinessException;
import co.moviired.support.endpoint.util.exceptions.CodeErrorEnum;
import co.moviired.support.endpoint.util.generics.ErrorMessagesLoader;
import co.moviired.support.endpoint.util.generics.Validation;
import co.moviired.support.endpoint.util.util.enums.MahindraOperationEnum;
import co.moviired.support.service.ConsignmentWSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;

@Slf4j
@Service
public class MahindraUtils {

    private int consigId;

    private final ConsignmentWSService consignmentWSService;
    private final ConsignmentUtilities consignmentUtilities;
    private final ErrorMessagesLoader errorMessagesLoader;
    private final RestConnector mahindraClient;

    public MahindraUtils(@NotNull ConsignmentWSService consignmentWSService,
                         @NotNull ConsignmentUtilities consignmentUtilities,
                         @NotNull ErrorMessagesLoader errorMessagesLoader,
                         @NotNull RestConnector mahindraClient) {
        super();
        this.consignmentWSService = consignmentWSService;
        this.consignmentUtilities = consignmentUtilities;
        this.errorMessagesLoader = errorMessagesLoader;
        this.mahindraClient = mahindraClient;
    }

    public ConsignmentOutDTO mahindraCommunication(ConsignmentInDTO inDTO, MahindraOperationEnum mahindraOperation) throws BusinessException {
        log.info(MahindraUtils.class.getName() + " - mahindraCommunication ()");
        Validation.validateRequiredFields(inDTO, consignmentUtilities.loadPropertyMahindra("requiredFieldsMahindra").split(","), errorMessagesLoader);
        Validation.validateParam("BANKID", inDTO.getBank(), errorMessagesLoader);
        Validation.valideParamInteger("MSISDN", inDTO.getClient(), errorMessagesLoader);
        Validation.valideParamInteger("AMOUNT", inDTO.getAmount(), errorMessagesLoader);
        Validation.valideParamInteger("REFERENCEID", inDTO.getMovilRedKey(), errorMessagesLoader);
        Validation.validateMaxLength("MSISDN", inDTO.getClient(), 10, errorMessagesLoader);
        Validation.validateMaxLength("AMOUNT", inDTO.getAmount(), 10, errorMessagesLoader);
        Validation.validateMaxLength("REFERENCEID", inDTO.getMovilRedKey(), 50, errorMessagesLoader);
        String xmlRequest;
        ConsignmentOutDTO out;
        String response;
        consigId = inDTO.getRegId();
        if (mahindraOperation == MahindraOperationEnum.APPROVAL) {
            xmlRequest = generateMahindraApprovalRequest(inDTO);
        } else {
            xmlRequest = generateMahindraRevertRequest(inDTO);
        }

        if (xmlRequest != null && !xmlRequest.trim().equals("")) {
            response = callMahindraServices(xmlRequest);
            if (!response.trim().equals("")) {
                out = generateMahindraResponse(response, mahindraOperation);
                return out;
            } else {
                String msg = errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERROR_NO_RESPONSE_MAHINDRA.getDescription());
                throw new BusinessException(CodeErrorEnum.ERROR_NO_RESPONSE_MAHINDRA, msg);
            }
        } else {
            String propiedad = mahindraOperation == MahindraOperationEnum.APPROVAL ? "mahindraApprovalRequest" : "mahindraRevertRequest";
            throw new BusinessException(CodeErrorEnum.ERROR_NO_PROPERTY_CONSIGNMENT, propiedad);
        }
    }

    public ConsignmentOutDTO mahindraQuery(ConsignmentInDTO inDTO) throws BusinessException {
        log.info(MahindraUtils.class.getName() + " - mahindraQuery ()");
        String xmlRequest;
        ConsignmentOutDTO out;
        String response;

        xmlRequest = generateMahindraQueryRequest(inDTO);

        if (xmlRequest != null && !xmlRequest.trim().equals("")) {
            response = callMahindraServices(xmlRequest);
            if (!response.trim().equals("")) {
                out = generateMahindraResponseQuery(response);
                log.info(MahindraUtils.class.getName() + " - mahindraQuery Response ()");
                return out;
            } else {
                String msg = errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERROR_NO_RESPONSE_MAHINDRA.getDescription());
                throw new BusinessException(CodeErrorEnum.ERROR_NO_RESPONSE_MAHINDRA, msg);
            }
        } else {
            String propiedad = "MAHINDRA_QUERY_REQUEST";
            throw new BusinessException(CodeErrorEnum.ERROR_NO_PROPERTY_CONSIGNMENT, propiedad);
        }
    }

    private String generateMahindraApprovalRequest(ConsignmentInDTO inDTO) {
        return consignmentUtilities.loadProperty("mahindraApprovalRequest", inDTO.getClient(), inDTO.getAmount(), inDTO.getBank(), inDTO.getMovilRedKey(), inDTO.getCorrelationId());
    }

    private String generateMahindraRevertRequest(ConsignmentInDTO inDTO) {
        return consignmentUtilities.loadProperty("mahindraRevertRequest", inDTO.getClient(), inDTO.getAmount(), inDTO.getBank(), inDTO.getMovilRedKey(), inDTO.getCorrelationId());
    }

    private String generateMahindraQueryRequest(ConsignmentInDTO inDTO) {
        return consignmentUtilities.loadProperty("mahindraQueryRequest", inDTO.getClient());
    }

    private ConsignmentOutDTO generateMahindraResponse(String responseXml, MahindraOperationEnum mahindraOperation) throws BusinessException {
        ConsignmentOutDTO outDTO = new ConsignmentOutDTO();

        try {
            new MahindraResponseDTO();
            JAXBContext jaxbContext = JAXBContext.newInstance(MahindraResponseDTO.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            MahindraResponseDTO response = (MahindraResponseDTO)jaxbUnmarshaller.unmarshal(new InputSource(new StringReader(responseXml)));
            outDTO.setTransactionIdentificator(response.getTransactionIdentificator());
            outDTO.setConsignmentType("Automática");
            if (response.getTransactionStatus() != null && response.getTransactionStatus().equals("200")) {
                outDTO.setResponse("00:Efectiva");
                if (mahindraOperation == MahindraOperationEnum.APPROVAL) {
                    outDTO.setStatus(ConsignmentStatusEnum.APPROVED.getStatusId());
                    consignmentWSService.consignmentUpdate(consigId, ConsignmentWSStatus.APPROVED.getId(), response.getTransactionIdentificator());
                }else{
                    outDTO.setStatus(ConsignmentStatusEnum.REVERSED.getStatusId());
                    consignmentWSService.consignmentUpdate(consigId, ConsignmentWSStatus.REVERSED.getId(), response.getTransactionIdentificator());
                }

                return outDTO;
            } else {
                if (mahindraOperation == MahindraOperationEnum.APPROVAL) {
                    consignmentWSService.consignmentUpdate(consigId, ConsignmentWSStatus.REJECTED.getId(), null);
                }
                if (response.getTransactionStatus() != null && response.getTransactionStatus().equals("01025")) {
                    String message = consignmentUtilities.messageProperty(CodeErrorEnum.REJECTED_RESPONSE_MAHINDRA.getDescription());
                    throw new BusinessException(CodeErrorEnum.REJECTED_RESPONSE_MAHINDRA, message);
                }else if (response.getTransactionStatus() != null && response.getTransactionStatus().equals("00261")) {
                    String message = consignmentUtilities.messageProperty(CodeErrorEnum.ERROR_USER.getDescription());
                    throw new BusinessException(CodeErrorEnum.ERROR_USER, message);
                }

                String message = consignmentUtilities.messageProperty(CodeErrorEnum.ERROR_RESPONSE_MAHINDRA.getDescription());
                throw new BusinessException(CodeErrorEnum.ERROR_RESPONSE_MAHINDRA, message);
            }
        } catch (JAXBException var6) {
            throw new BusinessException(var6);
        }
    }

    private ConsignmentOutDTO generateMahindraResponseQuery(String responseXml) throws BusinessException {
        ConsignmentOutDTO outDTO = new ConsignmentOutDTO();

        try {
            new MahindraResponseDTO();
            JAXBContext jaxbContext = JAXBContext.newInstance(MahindraResponseDTO.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            MahindraResponseDTO response = (MahindraResponseDTO)jaxbUnmarshaller.unmarshal(new InputSource(new StringReader(responseXml)));
            outDTO.setTransactionIdentificator(response.getTransactionIdentificator());
            outDTO.setConsignmentType("Automática");
            if (response.getTransactionStatus() != null && response.getTransactionStatus().equals("200")) {
                outDTO.setStatus(ConsignmentStatusEnum.APPROVED.getStatusId());
                outDTO.setPhoneNumber(response.getClient());
                outDTO.setResponseMessage(response.getMessage());
                outDTO.setFirtsName(response.getFirtsName());
                outDTO.setLastName(response.getLastName());
                outDTO.setEmail(response.getEmail());
                outDTO.setDob(response.getDob());
                outDTO.setCity(response.getCity());
                outDTO.setGender(response.getGender());
                outDTO.setStatusId(response.getStatus());
            } else {
                outDTO.setStatus(ConsignmentStatusEnum.REJECTED.getStatusId());
            }
            return outDTO;

        } catch (JAXBException var6) {
            throw new BusinessException(var6);
        }
    }

    private String callMahindraServices(String urlParameters) throws BusinessException {

        try {
            log.info("Inicia comunicación con sistema Mahindra \nServicio - Parametros de Entrada: " + urlParameters);
            String response = mahindraClient.post(urlParameters, String.class, MediaType.APPLICATION_XML);
            log.info("Finaliza comunicación con sistema Mahindra \nRespuesta: " + response);
            return response;

        } catch (Exception var17) {
            log.error(String.valueOf(var17));
            String msg = errorMessagesLoader.getErrorMensage(CodeErrorEnum.ERROR_NO_COMMUNICATION_MAHINDRA.getDescription());
            throw new BusinessException(CodeErrorEnum.ERROR_NO_RESPONSE_MAHINDRA, msg);
        }
    }
}
