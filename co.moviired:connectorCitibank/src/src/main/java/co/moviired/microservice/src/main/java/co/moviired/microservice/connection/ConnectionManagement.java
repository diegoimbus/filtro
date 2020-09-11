package co.moviired.microservice.connection;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.exception.CommunicationException;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.base.util.Generator;
import co.moviired.microservice.conf.BankProperties;
import co.moviired.microservice.conf.StatusCodeConfig;
import co.moviired.microservice.provider.citibank.ReverseDeposit;
import co.moviired.microservice.soap.Debtpayment;
import co.moviired.microservice.soap.Rdebtpayment;
import co.moviired.microservice.soap.RdebtpaymentResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.net.InetAddress;

@Slf4j
@Component
@AllArgsConstructor
public class ConnectionManagement {

    private final ReverseDeposit reverseDeposit;
    private final BankProperties bankProperties;
    private final StatusCodeConfig statusCodeConfig;
    private final WebServiceTemplate webServiceTemplate;

    public Object sendMessageToProvider(Object debtinquire) throws ServiceException {
        try {
            return webServiceTemplate.marshalSendAndReceive(bankProperties.getUrlConnection(), debtinquire);
        } catch (Exception e) {
            if (debtinquire instanceof Debtpayment) {
                new Thread(() ->
                        threadReversDeposit(debtinquire)
                ).start();
            }
            StatusCode statusCode = statusCodeConfig.of("C10");
            throw new CommunicationException(statusCode.getCode(), statusCode.getMessage());
        }
    }

    @SneakyThrows
    private void threadReversDeposit(Object debtinquire) {
        Thread.sleep(bankProperties.getDelayReverse());
        setIdentLog(Generator.correlationId(InetAddress.getLocalHost().getHostAddress()));

        log.info("{}", "************ INICIANDO - PROCESO DE REVERSO CONNECTOR CITIBANK ************");
        RdebtpaymentResponse rdebtpaymentResponse;
        Rdebtpayment rdebtpayment = reverseDeposit.parseRequestReverse(debtinquire);

        do {
            Thread.sleep(bankProperties.getDelayReverse());
            rdebtpaymentResponse = (RdebtpaymentResponse) sendMessageToProvider(rdebtpayment);
        } while (!rdebtpaymentResponse.getRdebtpaymentresponse().getStatus().equals("00")
                && !rdebtpaymentResponse.getRdebtpaymentresponse().getStatus().equals("02"));

        log.info("{}", "************ FINALIZADO - PROCESO DE REVERSO CONNECTOR CITIBANK ************");
    }

    private void setIdentLog(String correlationId) {
        MDC.putCloseable("correlation-id", correlationId);
    }

}

