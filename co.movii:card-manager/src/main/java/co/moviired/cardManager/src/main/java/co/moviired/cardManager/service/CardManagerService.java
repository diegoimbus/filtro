package co.moviired.cardManager.service;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.CommunicationException;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ProcessingException;
import co.moviired.base.helper.CommandHelper;
import co.moviired.base.util.Generator;
import co.moviired.cardManager.conf.StatusCodeConfig;
import co.moviired.cardManager.domain.dto.request.RequestFormatCard;
import co.moviired.cardManager.domain.dto.response.Response;
import co.moviired.cardManager.domain.entity.ReclaimCard;
import co.moviired.cardManager.domain.repository.IReclaimCard;
import co.moviired.cardManager.domain.repository.IReclaimCardList;
import co.moviired.cardManager.properties.GlobalProperties;
import co.moviired.cardManager.properties.LoginServiceMahindraParser;
import co.moviired.cardManager.properties.MahindraProperties;
import co.moviired.cardManager.provider.mahindra.CommandLoginServiceRequest;
import co.moviired.cardManager.provider.mahindra.CommandLoginServiceResponse;
import co.moviired.connector.connector.ReactiveConnector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class CardManagerService {

    private final IReclaimCardList iReclaimCardList;
    private final IReclaimCard iReclaimCard;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GlobalProperties globalProperties;
    private final StatusCodeConfig statusCodeConfig;

    private final MahindraProperties mahindraProperties;
    private final LoginServiceMahindraParser loginServiceMahindraParser;
    private static final String[] PROTECT_FIELDS = {"pin", "mpin", "otp"};
    public final ReactiveConnector reactiveConnector;

    private final XmlMapper xmlMapper = new XmlMapper();

    //Constructor
    public CardManagerService(IReclaimCardList iReclaimCardList, IReclaimCard iReclaimCard, GlobalProperties globalProperties, StatusCodeConfig statusCodeConfig, MahindraProperties mahindraProperties, LoginServiceMahindraParser loginServiceMahindraParser, ReactiveConnector reactiveConnector) {
        this.iReclaimCardList = iReclaimCardList;
        this.iReclaimCard = iReclaimCard;
        this.globalProperties = globalProperties;
        this.statusCodeConfig = statusCodeConfig;
        this.mahindraProperties = mahindraProperties;
        this.loginServiceMahindraParser = loginServiceMahindraParser;
        this.reactiveConnector = reactiveConnector;
    }

    //metodo para crear un registro
    public Mono<Response> registryCarRequest(Mono<RequestFormatCard> requestFormatCard, String userpass) {

        AtomicReference<RequestFormatCard> atomicRequest = new AtomicReference<>();
        ReclaimCard reclaimCard = new ReclaimCard();

        return requestFormatCard.flatMap(request ->{

            StatusCode statusCode = null;
            Response response = null;

            try {
                // Generación del nuevo correlation id
                String newCorrelationId = Generator.correlationId(InetAddress.getLocalHost().getHostAddress());
                setVariablesLog(newCorrelationId);

                log.info("************ INICIANDO - PROCESO REQUEST CARD REGISTRY ************");
                log.info("Petición recibida: " + this.objectMapper.writeValueAsString(request));
                RequestFormatCard.validateParameterRegistry(request, statusCodeConfig);
                atomicRequest.set(request);
                return loginMahindra(userpass);
            } catch (UnknownHostException | JsonProcessingException e) {
                return Mono.error(e);
            } catch (DataException se){
                log.error(se.getMessage());
                response = new Response(se.getCode(),se.getMessage(), "");
                return Mono.just(response);
            }
        }).flatMap(request -> {

            StatusCode statusCode = null;
            Response response = null;
            try {

                atomicRequest.get();

                reclaimCard.setPointName(atomicRequest.get().getPointName());
                reclaimCard.setAddress(atomicRequest.get().getPointAddress());
                reclaimCard.setNeighborhood((atomicRequest.get().getPointNeighborhood()));
                reclaimCard.setDetail(atomicRequest.get().getAddresDetail());
                reclaimCard.setCity(atomicRequest.get().getCity());
                reclaimCard.setPhoneNumber(atomicRequest.get().getPhoneNumber());
                reclaimCard.setDocumentNumber(atomicRequest.get().getIdNumber());
                reclaimCard.setDocumentType(atomicRequest.get().getIdType());
                reclaimCard.setSubsidiary(atomicRequest.get().getIsSubsidiary());
                reclaimCard.setRequestDate(new Date());
                reclaimCard.setCardDelivered(false);

            } catch (Exception e) {
                statusCode = statusCodeConfig.of("1");
                response = new Response(statusCode.getCode(), statusCode.getMessage(), ErrorType.PROCESSING.name());
                log.info(e.toString());
                return Mono.just(response);
            }
            return Mono.just(request);

        }).flatMap(request -> {
            StatusCode statusCode = null;
            Response response;
            try{
                log.info("Se envia petición de registro a la base de datos");
                this.iReclaimCard.save(reclaimCard);

                statusCode = statusCodeConfig.of("0");
                response = new Response(statusCode.getCode(), statusCode.getMessage(), "");
                log.info("Respuesta Enviada: " + this.objectMapper.writeValueAsString(response));

            } catch (Exception e) {
                statusCode = statusCodeConfig.of("1");
                response = new Response(statusCode.getCode(), statusCode.getMessage(), ErrorType.PROCESSING.name());
                log.info(e.toString());
            }
            log.info("************ FINALIZADO - PROCESO REQUEST CARD REGISTRY ************");
            return Mono.just(response);
        });

    }

    public Mono<Response> queryCardId (String idNumber, String idType) {

        StatusCode statusCode;
        Response response;

        try {
            // Generación del nuevo correlation id
            String newCorrelationId = Generator.correlationId(InetAddress.getLocalHost().getHostAddress());
            setVariablesLog(newCorrelationId);

            log.info("************ INICIANDO - PROCESO QUERY IDTYPE/IDNUMBER ************");
            log.info("Datos Recibidos:");
            log.info("Tipo de Documento: " + idType);
            log.info("Numero de Documento: " + idNumber);

            log.info("Se envia petición de consulta a la base de datos");
            List<ReclaimCard> reclaimCard = this.iReclaimCard.findByDocumentNumberAndDocumentTypeOrderByRequestDateDesc(idNumber, idType);
            if (reclaimCard == null || reclaimCard.isEmpty()){
                statusCode = statusCodeConfig.of("1");
                response = new Response("99", "No se encontro registro", "");
            }else{
                statusCode = statusCodeConfig.of("00");
                response = new Response(statusCode.getCode(), statusCode.getMessage(), "");
                response.setCardDelivered(reclaimCard.get(0).isCardDelivered());
            }

            log.info("Respuesta Enviada: " + this.objectMapper.writeValueAsString(response));
            log.info("************ FINALIZADO - PROCESO QUERY IDTYPE/IDNUMBER ************");
            return Mono.just(response);

        }catch (DataException | JsonProcessingException | UnknownHostException e) {
            statusCode = statusCodeConfig.of("1");
            response = new Response(statusCode.getCode(), statusCode.getMessage(), ErrorType.DATA.name());
            log.info(e.toString());
            return Mono.just(response);
        }

    }

    public Mono<Response> queryLocation(Pageable pageable, Mono<RequestFormatCard> requestFormatCard){

        return requestFormatCard.flatMap(request -> {

            StatusCode statusCode;
            Response response = new Response();

            try {

                // Generación del nuevo correlation id
                String newCorrelationId = Generator.correlationId(InetAddress.getLocalHost().getHostAddress());
                setVariablesLog(newCorrelationId);

                log.info("************ INICIANDO - PROCESO QUERY LOCATION ************");
                log.info("Petición recibida: " + this.objectMapper.writeValueAsString(request));
                log.info("Se envia petición de consulta a la base de datos");
                Page<ReclaimCard> reclaimCard = null;

                if ((!request.getPointName().isEmpty() || request.getPointName() != null) && (request.getCity().isEmpty() || request.getCity() == null)){

                    reclaimCard = this.iReclaimCardList.findByPointNameOrderByRequestDateDesc(request.getPointName(), pageable);

                }else if ((!request.getCity().isEmpty() || request.getCity() != null) && (request.getPointAddress().isEmpty() || request.getPointAddress() == null)){

                    reclaimCard = this.iReclaimCardList.findByPointNameAndCityOrderByRequestDateDesc(request.getPointName(), request.getCity(), pageable);

                } else if (!request.getPointAddress().isEmpty() || request.getPointAddress() != null){

                    reclaimCard = this.iReclaimCardList.findByPointNameAndCityAndAddressOrderByRequestDateDesc
                            (request.getPointName(), request.getCity(), request.getPointAddress(), pageable);

                }

                //Variables donde se cargan el numero de paginas y registros
                response.setPages(reclaimCard.getTotalPages());
                response.setNumRegistry(reclaimCard.getTotalElements());

                log.info("Número de paginas encontradas: " + reclaimCard.getTotalPages() + " | Número de registros encontrados: " + reclaimCard.getTotalElements());
                log.info("Pagina solicitada: " + pageable.getPageNumber() + " | Registros por pagina: " + pageable.getPageSize());

                //Se llama al metodo para dejar null el id del registro
                List<ReclaimCard> reclaimCardList = reclaimCard.getContent();
                reclaimCardList.forEach(ReclaimCard::deleteVariables);

                response.setPageReclaimCard(reclaimCardList);
                log.info("************ FINALIZADO - PROCESO QUERY LOCATION ************");
                return Mono.just(response);

            }catch(DataException | UnknownHostException | JsonProcessingException e){

                statusCode = statusCodeConfig.of("1");
                response = new Response(statusCode.getCode(), statusCode.getMessage(), ErrorType.PROCESSING.name());
                log.info(e.toString());
                log.info("************ FINALIZADO - PROCESO QUERY LOCATION ************");
                return Mono.just(response);
            }

        });

    }

    public Mono<Response> queryReport() {

        StatusCode statusCode;
        Response response = new Response();

        try {
            // Generación del nuevo correlation id
            String newCorrelationId = Generator.correlationId(InetAddress.getLocalHost().getHostAddress());
            setVariablesLog(newCorrelationId);

            log.info("************ INICIANDO - PROCESO QUERY REPORT ************");
            log.info("Se envia petición de consulta a la base");
            List<ReclaimCard> listAll = this.iReclaimCard.listAll();
            listAll.forEach(ReclaimCard::deleteVariables);
            response.setReponseReport(listAll);
            log.info("Se encontraron " + listAll.size() + " registros.");
        } catch (DataException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        log.info("************ FINALIZADO - PROCESO QUERY REPORT ************");
        return Mono.just(response);
    }

    public  Mono<Response> updateDeliveredCard(Mono<RequestFormatCard> requestFormatCard, String userpass){

        AtomicReference<RequestFormatCard> atomicRequestUp = new AtomicReference<>();

        return requestFormatCard.flatMap(request -> {
            StatusCode statusCode;
            Response response;
            try {
                // Generación del nuevo correlation id
                String newCorrelationId = Generator.correlationId(InetAddress.getLocalHost().getHostAddress());
                setVariablesLog(newCorrelationId);

                log.info("************ INICIANDO - PROCESO UPDATE DELIVERED CARD ************");
                log.info("Datos Recibidos: " + this.objectMapper.writeValueAsString(request));

                RequestFormatCard.validateParameterUpdate(request, statusCodeConfig);

                atomicRequestUp.set(request);

                return loginMahindra(userpass);

            }catch (JsonProcessingException | UnknownHostException e) {
                statusCode = statusCodeConfig.of("1");
                response = new Response(statusCode.getCode(), statusCode.getMessage(), ErrorType.DATA.name());
                log.info(e.toString());
                log.info("************ FINALIZADO - PROCESO UPDATE DELIVERED CARD ************");
                return Mono.just(response);
            }catch (DataException se){
                log.error(se.getMessage());
                response = new Response(se.getCode(),se.getMessage(), "");
                log.info("************ FINALIZADO - PROCESO UPDATE DELIVERED CARD ************");
                return Mono.just(response);
            }

        }) .flatMap(requestUpdate -> {
            StatusCode statusCode;
            Response response;
            log.info("Se envia petición de consulta a la base de datos");
            List<ReclaimCard> reclaimCardResult = this.iReclaimCard.findByDocumentNumberAndDocumentTypeAndPhoneNumberOrderByRequestDateDesc(
                    atomicRequestUp.get().getIdNumber(), atomicRequestUp.get().getIdType(), atomicRequestUp.get().getPhoneNumber());

            if (reclaimCardResult == null || reclaimCardResult.isEmpty()){
                statusCode = statusCodeConfig.of("1");
                response = new Response("99", "No se encontro registro", "");
            }else{

                ReclaimCard update = reclaimCardResult.get(0);

                if (update.isCardDelivered()){
                    statusCode = statusCodeConfig.of("CD09");
                    response = new Response(statusCode.getCode(), statusCode.getMessage(), "");
                }else{

                    try {

                        update.setDeliveryDate(new Date());
                        update.setCardDelivered(true);

                        log.info("Se envia petición de actualización a la base de datos");
                        this.iReclaimCard.save(update);

                        statusCode = statusCodeConfig.of("00");
                        response = new Response(statusCode.getCode(), statusCode.getMessage(), "");
                        log.info("Respuesta Enviada: " + this.objectMapper.writeValueAsString(response));

                    } catch (JsonProcessingException e) {

                        statusCode = statusCodeConfig.of("CD10");
                        response = new Response(statusCode.getCode(), statusCode.getMessage(), "");
                        log.error(e.toString());
                        log.info("************ FINALIZADO - PROCESO UPDATE DELIVERED CARD ************");
                        return Mono.just(response);
                    }
                }
            }
            log.info("************ FINALIZADO - PROCESO UPDATE DELIVERED CARD ************");
            return Mono.just(response);
        });

    }
        // Login Mahindra
    private Mono<CommandLoginServiceResponse> loginMahindra(String userpass) throws JsonProcessingException {
        Mono<CommandLoginServiceResponse> responseCliente;
        CommandLoginServiceRequest mhRequest = loginServiceMahindraParser.parseRequest(userpass);
        log.info("PETICION DE AUTENTICACION MAHINDRA: \n" + CommandHelper.printIgnore(toXmlString(mhRequest), PROTECT_FIELDS).toUpperCase());

        log.info("Conectandose a Mahindra..." + mahindraProperties.getUrlTransactional());
        responseCliente = reactiveConnector.post(this.xmlMapper.writeValueAsString(mhRequest).toUpperCase(), String.class, MediaType.APPLICATION_XML, null)
                .flatMap(obResp -> {
                    try {
                        CommandLoginServiceResponse mhResponse;
                        // Transformar XML response a  response
                        mhResponse = this.xmlMapper.readValue((String) obResp, CommandLoginServiceResponse.class);
                        log.info("RESPUESTA DE AUTENTICACION MAHINDRA: \n" + this.xmlMapper.writeValueAsString(mhResponse).toUpperCase());

                        if (!mhResponse.getTxnstatus().equals("200")) {
                            StatusCode statusCode = statusCodeConfig.of("3");
                            throw new ProcessingException(statusCode.getCode(), statusCode.getMessage());
                        }
                        return Mono.just(mhResponse);

                    } catch (IOException | ProcessingException io) {
                        return Mono.error(io);
                    }
                })
                .onErrorResume(e -> {
                    if (e instanceof ProcessingException) {
                        return Mono.error(e);
                    }
                    CommunicationException ce = new CommunicationException("400", e.getMessage());
                    return Mono.error(ce);
                });
        return responseCliente;
    }

    private String toXmlString(Object xmlObject) throws JsonProcessingException {
        return xmlMapper.writeValueAsString(xmlObject);
    }

    private void setVariablesLog(String correlationId) {
        MDC.putCloseable("component", this.globalProperties.getApplicationName());
        MDC.putCloseable("correlation-id", correlationId);
    }

}

