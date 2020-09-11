package co.moviired.microservice.service;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.microservice.client.soap.cargos.GenerarCargo;
import co.moviired.microservice.client.soap.cargos.GenerarCargoResponseType;
import co.moviired.microservice.client.soap.operacionesclean.ValidarFactura;
import co.moviired.microservice.client.soap.operacionesclean.ValidarFacturaResponseType;
import co.moviired.microservice.client.soap.seguridadbasecb.GETTICKET;
import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.domain.provider.IParser;
import co.moviired.microservice.domain.provider.ParserFactory;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.request.Request;
import co.moviired.microservice.domain.response.ErrorDetail;
import co.moviired.microservice.domain.response.Outcome;
import co.moviired.microservice.domain.response.Response;
import co.moviired.microservice.domain.servicesoap.BillRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;


@Slf4j
@Service
public class ConnectorBBVAService {

    private final ParserFactory parserFactory;
    private final BillRepository billRepository;

    public ConnectorBBVAService(@NotNull ParserFactory parserFactory,
                                @NotNull BillRepository billRepository) {
        super();
        this.billRepository = billRepository;
        log.info("{} {} {}", "Configuración del servicio: ", ConnectorBBVAService.class.getName(), " - INICIADA");
        this.parserFactory = parserFactory;
        log.info("{} {} {}", "Configuración del servicio: ", ConnectorBBVAService.class.getName(), " - EXITOSA");
    }


    public Mono<Response> service(@NotNull Mono<Request> wsRequest, OperationType opType) {
        return wsRequest.flatMap(request -> {

            Input parameters;
            Response response = null;

            try {

                parameters = Input.parseInput(request.getData());

                this.asignarCorrelativo(parameters.getImei().split("\\|")[3]);

                log.info("{}", "************ STARTING - PROCESS " + opType.name() + " CONNECTOR BBVA ************");
                log.info("INITIAL REQUEST: " + new ObjectMapper().writeValueAsString(parameters));

                IParser parser = parserFactory.getParser(opType);

                //Generar request ticket
                GETTICKET requestTicket = parser.parseRequestTicket(parameters);

                //Invocar WS token "ticket"
                parameters.setTicket(this.billRepository.getTicket(requestTicket));

                if (opType.equals(OperationType.PAYMENT)) {

                    //Generar request PAGO
                    GenerarCargo requestPayBill = parser.parseRequestPay(parameters);

                    //Invocar WS generarCargo para PAGO
                    GenerarCargoResponseType generarCargoResponseType = this.billRepository.payBill(requestPayBill.getGenerarCargoRequest());

                    //Procesar la respuesta
                    response = parser.parseResponse(parameters, generarCargoResponseType, opType);

                } else {//Consulta automatica y manual

                    //Generar request consulta FACTURA
                    ValidarFactura requestBill = parser.parseRequestQuery(parameters, opType);

                    //Generar consulta WS consulta FACTURA
                    ValidarFacturaResponseType validarFacturaResponse = this.billRepository.getBillTransaction(requestBill);

                    //Procesar la respuesta
                    response = parser.parseResponse(parameters, validarFacturaResponse, opType);

                }

                validateResponse(response);

            } catch (DataException e) {
                log.error("RESPONSE - ERROR DATA: " + e.toString());
                response = generateErrorResponse(e, HttpStatus.NOT_ACCEPTABLE);

            } catch (ServiceException se) {
                log.error("RESPONSE - ERROR: " + se.toString());
                // Armar el objeto de respuesta con el error específico
                response = generateErrorResponse(se, HttpStatus.NOT_ACCEPTABLE);

            } catch (Exception e) {
                log.error("RESPONSE - ERROR: " + e.getMessage());
                // Armar el objeto de respuesta con el error específico
                ServiceException se = new ServiceException(ErrorType.PROCESSING, "500", e.getMessage());
                response = generateErrorResponse(se, HttpStatus.INTERNAL_SERVER_ERROR);

            } finally {
                if (response != null) {
                    try {
                        log.info("RESPONSE CONNECTOR BBVA: " + new ObjectMapper().writeValueAsString(response));
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            log.info("{}", "************ END - PROCESS " + opType.name() + " CONNECTOR BBVA  ************");
            return Mono.just(response);
        });
    }

    // Generar respuesta errada, por error del servicio
    private Response generateErrorResponse(ServiceException e, HttpStatus httpStatus) {
        ErrorDetail error = new ErrorDetail(e.getErrorType().ordinal(), e.getCode(), e.getMessage());
        Outcome outcome = new Outcome(httpStatus, error);
        Response response = new Response();
        response.setOutcome(outcome);
        return response;
    }

    private void asignarCorrelativo(@NotNull String correlationId) {
        log.debug("Se genera correlativo----> {}", correlationId);
        MDC.putCloseable("correlation-id", correlationId);
    }

    private void validateResponse(Response response) throws ServiceException {
        // Verificar si se obtuvo respuesta
        if (response == null) {
            throw new ServiceException(ErrorType.DATA, "99", "NO SE OBTUVO RESPUESTA");
        }
    }

}

