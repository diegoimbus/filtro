package co.moviired.support.service;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ParsingException;
import co.moviired.connector.connector.ReactiveConnector;
import co.moviired.support.domain.client.mahindra.MahindraDTO;
import co.moviired.support.domain.dto.ExtractDTO;
import co.moviired.support.domain.dto.FactoryMahindraHelper;
import co.moviired.support.domain.dto.ResponseStatus;
import co.moviired.support.domain.dto.ServiceManagerDTO;
import co.moviired.support.domain.entity.account.Document;
import co.moviired.support.domain.entity.redshift.ExtractData;
import co.moviired.support.domain.repository.account.DocumentRepository;
import co.moviired.support.domain.repository.redshift.IExtractDataRepository;
import co.moviired.support.exceptions.ServiceException;
import co.moviired.support.helper.CertificateHelper;
import co.moviired.support.helper.HelperFactory;
import co.moviired.support.properties.PropertiesFactory;
import co.moviired.support.util.UtilsHelper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static co.moviired.support.util.ConstantsHelper.*;

@Slf4j
@Service
public final class ExtractService extends BaseService {

    private final ReactiveConnector mahindraClient;
    private final HelperFactory helperFactory;
    private final PropertiesFactory propertiesFactory;

    private final IExtractDataRepository extractDataRepository;
    private final DocumentRepository documentRepository;
    private final ReactiveConnector emailGeneratorConnector;
    private final ReactiveConnector serviceManagerConnector;

    public ExtractService(
            HelperFactory helperFactory,
            @NotNull PropertiesFactory ppropertiesFactory,
            @NotNull @Qualifier("pmahindraClient") ReactiveConnector pmahindraClient,
            IExtractDataRepository extractDataRepository,
            DocumentRepository documentRepository,
            @Qualifier(value = EMAIL_GENERATOR_API) @NotNull ReactiveConnector pemailGeneratorConnector,
            @Qualifier(value = SERVICE_MANAGER_API) @NotNull ReactiveConnector pserviceManagerConnector
    ) {
        super(ppropertiesFactory.getGlobalProperties(), ppropertiesFactory.getStatusCodeConfig());
        this.helperFactory = helperFactory;
        this.propertiesFactory = ppropertiesFactory;
        this.mahindraClient = pmahindraClient;
        this.extractDataRepository = extractDataRepository;
        this.documentRepository = documentRepository;
        this.emailGeneratorConnector = pemailGeneratorConnector;
        this.serviceManagerConnector = pserviceManagerConnector;
    }

    // GET AVAILABLE EXTRACTS ******************************************************************************************

    public Mono<ExtractDTO> getAvailableExtracts(String authorizationHeader) {
        String correlative = super.logsStart(OPERATION_GET_AVAILABLE_EXTRACTS, EMPTY_JSON);
        ExtractDTO response = new ExtractDTO();
        try {
            String[] authentication = UtilsHelper.getAuthorizationParts(this.getStatusCodeConfig(), this.getGlobalProperties(), authorizationHeader);
            // Invoke login
            return callMahindraLogin(authentication[ZERO_INT], authentication[ONE_INT], correlative)
                    .flatMap(loginResponse -> Mono.just(ExtractDTO.builder()
                            .availableExtracts(this.extractDataRepository.getAvailableExtracts(authentication[ZERO_INT]))
                            .build()))
                    .flatMap(responseMapped -> {
                        StatusCode statusCode = this.getStatusCodeConfig().of(SUCCESS_CODE);
                        responseMapped.setStatus(new ResponseStatus(statusCode.getCode(), statusCode.getMessage(), this.getGlobalProperties().getApplicationName()));
                        super.logsEnd(responseMapped);
                        return Mono.just(responseMapped);
                    })
                    // On Error
                    .onErrorResume(e -> {
                        handleThrowableError(LOG_ERROR_EXECUTING_GET_AVAILABLE_EXTRACTS_STEPS, e, response);
                        super.logsEnd(response);
                        return Mono.just(response);
                    });
        } catch (ServiceException e) {
            handleThrowableError(LOG_ERROR_GETTING_AVAILABLE_EXTRACTS, e, response);
        } catch (Exception e) {
            handleExceptionError(LOG_ERROR_GETTING_AVAILABLE_EXTRACTS, e, response);
        }
        super.logsEnd(response);
        return Mono.just(response);
    }

    // REQUEST EXTRACT *************************************************************************************************

    public Mono<ExtractDTO> requestExtract(String authorizationHeader, ExtractDTO request) {
        String correlative = super.logsStart(OPERATION_GENERATE_EXTRACT, new Gson().toJson(request));
        AtomicReference<MahindraDTO> loginResp = new AtomicReference<>();
        ExtractDTO response = new ExtractDTO();
        try {
            String[] authentication = UtilsHelper.getAuthorizationParts(this.getStatusCodeConfig(), this.getGlobalProperties(), authorizationHeader);
            validateGenerateDocumentRequest(request);

            // Invoke login and generate document
            return callMahindraLogin(authentication[ZERO_INT], authentication[ONE_INT], correlative)
                    .flatMap(loginResponse -> {
                        loginResp.set(loginResponse);
                        return startGenerateExtract(request, response, authentication[ZERO_INT]);
                    })
                    // Get email address and Send message
                    .flatMap(generateDocumentResponse -> getEmailAddressAndSendMessage(request, response, loginResp.get(),
                            authentication[ZERO_INT], authentication[ONE_INT], correlative))
                    // Handle response
                    .flatMap(responseMapped -> {
                        StatusCode statusCode = this.getStatusCodeConfig().of(SUCCESS_CODE);
                        responseMapped.setStatus(new ResponseStatus(statusCode.getCode(), statusCode.getMessage(), this.getGlobalProperties().getApplicationName()));
                        super.logsEnd(responseMapped);
                        return Mono.just(responseMapped);
                    })
                    // Handle error
                    .onErrorResume(e -> {
                        handleThrowableError(LOG_ERROR_EXECUTING_GENERATE_EXTRACT_STEPS, e, response);
                        super.logsEnd(response);
                        return Mono.just(response);
                    });
        } catch (ServiceException e) {
            handleThrowableError(LOG_ERROR_GENERATING_EXTRACT, e, response);
        } catch (Exception e) {
            handleExceptionError(LOG_ERROR_GENERATING_EXTRACT, e, response);
        }
        super.logsEnd(response);
        return Mono.just(response);
    }

    private Mono<ExtractDTO> startGenerateExtract(ExtractDTO request, ExtractDTO response, String msisdn) {
        List<ExtractData> extractDataList = this.extractDataRepository.validateAvailableExtract(msisdn,
                request.getAvailableExtract().getYear(), request.getAvailableExtract().getMonth());

        if (extractDataList.isEmpty()) {
            return Mono.error(new ServiceException(this.getStatusCodeConfig().of(NOT_FOUND_CODE).getCode(),
                    this.getStatusCodeConfig().of(NOT_FOUND_CODE).getMessage(),
                    this.getGlobalProperties().getApplicationName(), null));
        }
        try {
            String token = generateDocumentAndGetToken(request, msisdn);
            response.setDocumentUrl(this.getGlobalProperties().getBaseUrl() + this.getGlobalProperties().getPathGetPdf().replace(TOKEN_TAG, token));
        } catch (ServiceException e) {
            log.error(LOG_ERROR_GENERATING_DOCUMENT, e.getMessage());
            return Mono.error(e);
        }
        return Mono.just(response);
    }

    private String generateDocumentAndGetToken(ExtractDTO request, String msisdn) throws ServiceException {
        Date date = new Date();
        Document document = Document.builder()
                .altered(false)
                .creationDate(date)
                .month(request.getAvailableExtract().getMonth())
                .year(request.getAvailableExtract().getYear())
                .phoneNumber(msisdn)
                .token(DigestUtils.md5Hex(msisdn + date.getTime() +
                        request.getAvailableExtract().getYear() + request.getAvailableExtract().getMonth()))
                .type(CHANNEL)
                .build();
        try {
            document.setSignature(this.helperFactory.getSignatureHelper().sign(document));
        } catch (ParsingException e) {
            log.error(LOG_ERROR_GENERATING_SIGNATURE_OF_DOCUMENT, document);
            throw new ServiceException(this.getStatusCodeConfig().of(SERVER_ERROR_CODE).getCode(),
                    this.getStatusCodeConfig().of(SERVER_ERROR_CODE).getMessage(),
                    this.getGlobalProperties().getApplicationName(), e);
        }
        this.documentRepository.save(document);
        return document.getToken();
    }

    private Mono<ExtractDTO> getEmailAddressAndSendMessage(ExtractDTO request, ExtractDTO response, MahindraDTO loginResp, String msisdn, String pin, String correlative) {
        if (request.getSendEmail().booleanValue()) {
            try {
                String authorization = this.helperFactory.getCryptoHelper().encoder(msisdn) + TWO_DOTS + this.helperFactory.getCryptoHelper().encoder(pin);
                // Get email address
                return invokeServiceManager(serviceManagerConnector, this.propertiesFactory.getServiceManagerProperties(),
                        ServiceManagerDTO.builder().actionMethod(SERVICE_MANAGER_CRUD_EMAIL_ACTION_METHOD)
                                .build(), authorization, correlative)
                        // Send message
                        .flatMap(serviceManagerResponse ->
                                sendEmail(response.getDocumentUrl(), correlative, response, loginResp, serviceManagerResponse));
            } catch (ParsingException e) {
                log.error(LOG_ERROR_CREATING_AUTHORIZATION_FOR_CRUD_EMAIL, e.getMessage());
                return Mono.error(e);
            }
        } else {
            return Mono.just(response);
        }
    }

    // GET DOCUMENT ****************************************************************************************************

    public byte[] getPdf(String token) throws ServiceException {
        super.logsStart(OPERATION_GENERATE_CERTIFICATE, token);
        Optional<Document> documentOptional = this.documentRepository.findByToken(token);
        if (documentOptional.isPresent()) {
            Document document = documentOptional.get();
            if (document.isAltered()) {
                log.error(LOG_DOCUMENT_ALTERED, document);
            } else {
                validateSignature(document);
            }
            try {
                List<ExtractData> data = this.extractDataRepository.getExtractData(document.getPhoneNumber(), document.getYear(), document.getMonth());

                byte[] response = CertificateHelper.generatePdf(this.propertiesFactory.getCertificatesProperties().getPathExtractTemplate(), token,
                        CertificateHelper.getParametersForExtractCertificate(data));
                log.info(LOG_DOCUMENT_GENERATED, token);
                log.info(LBL_END);
                log.info(EMPTY_STRING);
                return response;
            } catch (Exception e) {
                log.error(LOG_ERROR_GENERATING_DOCUMENT_FOR_TOKEN, token, e.getMessage());
            }
        } else {
            log.info(LOG_DOCUMENT_NOT_FOUND_FOR_TOKEN, token);
        }
        log.info(LBL_END);
        log.info(EMPTY_STRING);
        throw getDefaultException();
    }

    // VALIDATIONS *****************************************************************************************************

    private void validateGenerateDocumentRequest(ExtractDTO request) throws ServiceException {
        String message = null;
        if (request == null) {
            message = ERROR_PARAMETER_BODY;
        } else if (request.getSendEmail() == null) {
            message = ERROR_PARAMETER_SEND_EMAIL;
        } else if (request.getAvailableExtract() == null) {
            message = ERROR_PARAMETER_AVAILABLE_EXTRACT;
        } else if (request.getAvailableExtract().getYear() == null) {
            message = ERROR_PARAMETER_YEAR;
        } else if (request.getAvailableExtract().getMonth() == null) {
            message = ERROR_PARAMETER_MONTH;
        }
        generateErrorValidation(message);
    }

    // CALL MAHINDRA ***************************************************************************************************

    private Mono<MahindraDTO> callMahindraLogin(String msisdn, String pin, String correlative) {
        try {
            return invokeMahindra(mahindraClient, this.propertiesFactory.getMahindraProperties(),
                    FactoryMahindraHelper.getLoginRequest(this.propertiesFactory.getMahindraProperties(), msisdn, pin), correlative)
                    // Handle login response
                    .flatMap(loginResponse -> onMahindraResponse(loginResponse, correlative))
                    .flatMap(this::onLoginResponse);
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    // CALL EMAIL SENDER ***********************************************************************************************

    private Mono<ExtractDTO> sendEmail(String link, String correlative, ExtractDTO response, MahindraDTO loginResponse, ServiceManagerDTO serviceManagerDTO) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add(EMAIL, (serviceManagerDTO.getEmail() != null && !serviceManagerDTO.getEmail().trim().isEmpty()) ?
                    serviceManagerDTO.getEmail() : loginResponse.getEmail());
            formData.add(LINK, link);
            formData.add(MESSAGE, MESSAGE_FOR_EMAIL);
            formData.add(IMAGE, IMAGE_OPTION);
            return invokeEmailSender(emailGeneratorConnector, this.propertiesFactory.getEmailGeneratorProperties(), this.propertiesFactory.getEmailGeneratorProperties().getUrlPathSendMovements(), formData, correlative)
                    .flatMap(responseLoans -> Mono.just(response));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private void validateSignature(Document document) {
        try {
            log.info(LOG_VALIDATING_SIGNATURE_DOCUMENT, document.getToken());
            this.helperFactory.getSignatureHelper().validate(document);
        } catch (ParsingException e) {
            log.error(LOG_ERROR_VALIDATING_SIGNATURE_OF_DOCUMENT, document.getToken(), e.getMessage());
        } catch (DataException e) {
            if (document.getSignature().equalsIgnoreCase(STRING_LINE)) {
                log.error(LOG_DOCUMENT_NOT_HAS_SIGNATURE, document.getToken());
            } else {
                log.error(LOG_DOCUMENT_ALTERED, document.getToken());
                document.setAltered(true);
                this.documentRepository.save(document);
            }
        }
    }

    // UTILS ***********************************************************************************************************

    private ServiceException getDefaultException() {
        return new ServiceException(this.getStatusCodeConfig().of(SERVER_ERROR_CODE).getCode(),
                this.getStatusCodeConfig().of(SERVER_ERROR_CODE).getMessage(),
                this.getGlobalProperties().getApplicationName(), null);
    }

    private void generateErrorValidation(String messageForParameterError) throws ServiceException {
        if (messageForParameterError != null) {
            throw new ServiceException(
                    this.getStatusCodeConfig().of(BAD_REQUEST_CODE).getMessage() + TWO_DOTS + messageForParameterError,
                    BAD_REQUEST_CODE,
                    this.getGlobalProperties().getApplicationName(), null);
        }
    }
}

