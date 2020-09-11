package co.moviired.cardManager.service;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.util.Generator;
import co.moviired.cardManager.conf.StatusCodeConfig;
import co.moviired.cardManager.domain.dto.request.RequestFormatCard;
import co.moviired.cardManager.domain.dto.response.Response;
import co.moviired.cardManager.domain.entity.ReclaimCard;
import co.moviired.cardManager.domain.repository.IReclaimCard;
import co.moviired.cardManager.domain.repository.IReclaimCardList;
import co.moviired.cardManager.properties.GlobalProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

@Slf4j
@Service

public class CardManagerService {

    private final IReclaimCardList iReclaimCardList;
    private final IReclaimCard iReclaimCard;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GlobalProperties globalProperties;
    private final StatusCodeConfig statusCodeConfig;

    //Constructor
    public CardManagerService(IReclaimCardList iReclaimCardList, IReclaimCard iReclaimCard, GlobalProperties globalProperties, StatusCodeConfig statusCodeConfig) {
        this.iReclaimCardList = iReclaimCardList;
        this.iReclaimCard = iReclaimCard;
        this.globalProperties = globalProperties;
        this.statusCodeConfig = statusCodeConfig;
    }

    //metodo para crear un registro
    public Mono<Response> registryCarRequest(Mono<RequestFormatCard> requestFormatCard) {

        return requestFormatCard.flatMap(request -> {

            StatusCode statusCode;
            Response response;

            try {

                // Generación del nuevo correlation id
                String newCorrelationId = Generator.correlationId(InetAddress.getLocalHost().getHostAddress());
                setVariablesLog(newCorrelationId);
                log.info("************ INICIANDO - PROCESO REQUEST CARD REGISTRY ************");

                ReclaimCard reclaimCard = new ReclaimCard();

                reclaimCard.setPointName(request.getPointName());
                reclaimCard.setAddress(request.getPointAddress());
                reclaimCard.setNeighborhood((request.getPointNeighborhood()));
                reclaimCard.setDetail(request.getAddresDetail());
                reclaimCard.setCity(request.getCity());
                reclaimCard.setPhoneNumber(request.getPhoneNumber());
                reclaimCard.setDocumentNumber(request.getIdNumber());
                reclaimCard.setDocumentType(request.getIdType());
                reclaimCard.setSubsidiary(request.getIsSubsidiary());
                reclaimCard.setRequestDate(new Date());
                reclaimCard.setCardDelivered(false);

                log.info("Petición recibida: " + this.objectMapper.writeValueAsString(request));

                this.iReclaimCard.save(reclaimCard);

                statusCode = statusCodeConfig.of("0");
                response = new Response(statusCode.getCode(), statusCode.getMessage(), "");

                log.info("Respuesta Enviada: " + this.objectMapper.writeValueAsString(response));
                log.info("************ FINALIZADO - PROCESO REQUEST CARD REGISTRY ************");
                return Mono.just(response);

            } catch (Exception | DataException e) {

                statusCode = statusCodeConfig.of("1");
                response = new Response(statusCode.getCode(), statusCode.getMessage(), ErrorType.PROCESSING.name());
                log.info(e.toString());
                return Mono.just(response);

            }

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

    /*public Mono<Response> queryLocation(String pointName, String city, String address, int pageNumber){
        StatusCode statusCode;
        Response response;

        try {
            // Generación del nuevo correlation id
            String newCorrelationId = Generator.correlationId(InetAddress.getLocalHost().getHostAddress());
            setVariablesLog(newCorrelationId);

            if (city.isEmpty() || city == null){



            }if (!city.isEmpty() || city != null || address.isEmpty() || address ==  null){

            }else{

            }

        }catch (DataException | UnknownHostException e) {
            statusCode = statusCodeConfig.of("1");
            response = new Response(statusCode.getCode(), statusCode.getMessage(), ErrorType.DATA.name());
            log.info(e.toString());
            return Mono.just(response);
        }
    }*/

    public Page<ReclaimCard> queryLocation(String pointName, Pageable pageable){

        Page<ReclaimCard> reclaimCard = this.iReclaimCardList.findByPointNameOrderByRequestDateDesc(pointName, pageable);



        return reclaimCard;
    }

    private void setVariablesLog(String correlationId) {
        MDC.putCloseable("component", this.globalProperties.getApplicationName());
        MDC.putCloseable("correlation-id", correlationId);
    }

}

