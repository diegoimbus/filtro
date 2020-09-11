import co.moviired.base.domain.exception.ServiceException;
import co.moviired.microservice.ConnectorBBVAApplication;
import co.moviired.microservice.client.soap.cargos.GenerarCargoRequestType;
import co.moviired.microservice.client.soap.cargos.GenerarCargoResponseType;
import co.moviired.microservice.client.soap.operacionesclean.*;
import co.moviired.microservice.client.soap.seguridadbasecb.GETTICKET;
import co.moviired.microservice.conf.BankProductsProperties;
import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.domain.request.Request;
import co.moviired.microservice.domain.servicesoap.BillRepositoryImpl;
import co.moviired.microservice.service.ConnectorBBVAService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest(classes = ConnectorBBVAApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConnectorBBVAServiceTest {

    @Autowired
    ConnectorBBVAService connectorBBVAService;

    @Autowired
    BankProductsProperties bankProducts;

    @MockBean
    BillRepositoryImpl billRepository;

    private static final String RESPONSE_STATUS_OK = "00";

    @Test
    void serviceManualQuery() throws ServiceException, DatatypeConfigurationException {

        log.info("************ TEST STARTING - PROCESS serviceManualQuery CONNECTOR BBVA ************");

        Map<String, Object> parameters = new HashMap<>();
        Request request = new Request();

        parameters.put("shortReferenceNumber","12346|MANUAL|6849|CHANNEL");
        parameters.put("imei","1231213131|192.168.0.2|CHANNEL|5454554577222996|20200114152615.153|||174484|3482222222||174484|1234|11");
        parameters.put("lastName","Tienda Miss Laura Moviired punto de venta @@@");
        request.setData(parameters);

        //Simular respuesta WS BBVA
        when(billRepository.getTicket(any(GETTICKET.class))).thenReturn("10000001");
        when(billRepository.getBillTransaction(any(ValidarFactura.class))).thenReturn(getResponseBillMockTest());

        assertEquals(RESPONSE_STATUS_OK, connectorBBVAService.service(Mono.just(request), OperationType.MANUAL_QUERY).block().getOutcome().getError().getErrorCode());

        log.info("************ TEST END - PROCESS serviceManualQuery CONNECTOR BBVA ************");
    }

    @Test
    void serviceAutomaticQuery() throws ServiceException, DatatypeConfigurationException {

        log.info("************ TEST STARTING - PROCESS serviceAutomaticQuery CONNECTOR BBVA ************");

        Map<String, Object> parameters = new HashMap<>();
        Request request = new Request();

        parameters.put("shortReferenceNumber","41577777744444448020000000963258390000000150009620200102|AUTOMATIC|26617|CHANNEL");
        parameters.put("imei","1231213131|192.168.0.2|CHANNEL|5454554577222996|20200114152615.153|||174484|3482222222||174484|1234|11");
        parameters.put("lastName","Tienda Miss Laura Moviired punto de venta @@@");
        request.setData(parameters);

        //Simular respuesta WS BBVA
        when(billRepository.getTicket(any(GETTICKET.class))).thenReturn("10000002");
        when(billRepository.getBillTransaction(any(ValidarFactura.class))).thenReturn(getResponseBillMockTest());

        assertEquals(RESPONSE_STATUS_OK, connectorBBVAService.service(Mono.just(request), OperationType.AUTOMATIC_QUERY).block().getOutcome().getError().getErrorCode());

        log.info("************ END STARTING - PROCESS serviceAutomaticQuery CONNECTOR BBVA ************");
    }

    @Test
    void serviceManualPay() throws ServiceException {

        log.info("************ TEST STARTING - PROCESS serviceManualPay CONNECTOR BBVA ************");

        Map<String, Object> parameters = new HashMap<>();
        Request request = new Request();

        parameters.put("shortReferenceNumber","12346|MANUAL|6849|CHANNEL");
        parameters.put("imei","1231213131|192.168.0.2|CHANNEL|5454554577222996|20200114152615.153|||174484|3482222222||174484|1234|11");
        parameters.put("echoData","0053984281#Miss Laura");
        parameters.put("valueToPay","15000");
        request.setData(parameters);

        //Simular respuesta WS BBVA
        when(billRepository.getTicket(any(GETTICKET.class))).thenReturn("10000003");
        when(billRepository.payBill(any(GenerarCargoRequestType.class))).thenReturn(getResponsePayMockTest());

        assertEquals(RESPONSE_STATUS_OK, connectorBBVAService.service(Mono.just(request), OperationType.PAYMENT).block().getOutcome().getError().getErrorCode());

        log.info("************ TEST STARTING - PROCESS serviceManualPay CONNECTOR BBVA ************");
    }

    @Test
    void serviceAutomaticPay() throws ServiceException {

        log.info("************ TEST STARTING - PROCESS serviceAutomaticPay CONNECTOR BBVA ************");

        Map<String, Object> parameters = new HashMap<>();
        Request request = new Request();

        parameters.put("shortReferenceNumber","41577777744444448020000000963258390000000150009620200102|AUTOMATIC|26617|CHANNEL");
        parameters.put("imei","1231213131|192.168.0.2|CHANNEL|5454554577222996|20200114152615.153|||174484|3482222222||174484|1234|11");
        parameters.put("echoData","0053984301#Miss Laura");
        parameters.put("valueToPay","15000");
        request.setData(parameters);

        //Simular respuesta WS BBVA
        when(billRepository.getTicket(any(GETTICKET.class))).thenReturn("10000004");
        when(billRepository.payBill(any(GenerarCargoRequestType.class))).thenReturn(getResponsePayMockTest());

        assertEquals("00", connectorBBVAService.service(Mono.just(request), OperationType.PAYMENT).block().getOutcome().getError().getErrorCode());

        log.info("************ END STARTING - PROCESS serviceAutomaticPay CONNECTOR BBVA ************");
    }

    private ValidarFacturaResponseType getResponseBillMockTest() throws DatatypeConfigurationException {

        ValidarFacturaResponseType response = new ValidarFacturaResponseType();
        ReferenciaFacturaType referenciaFacturaType = new ReferenciaFacturaType();
        FacturaType facturaType = new FacturaType();
        AmountType amountType = new AmountType();

        referenciaFacturaType.setValorReferencia("15000");
        facturaType.getReferencias().add(referenciaFacturaType);
        amountType.setValue(1500);
        facturaType.setValorTotal(amountType);

        response.setUPCStatus(bankProducts.getFacturaValidadaStatus());
        response.setFactura(facturaType);
        response.setTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.now().toString()));
        response.setUPCId("10001");
        return response;
    }

    private GenerarCargoResponseType getResponsePayMockTest() {

        GenerarCargoResponseType response =  new GenerarCargoResponseType();
        co.moviired.microservice.client.soap.cargos.AmountType amountType = new co.moviired.microservice.client.soap.cargos.AmountType();

        amountType.setValue(1500);
        response.setUPCStatus(bankProducts.getCargoGeneradoStatus());
        response.setCodigoAutorizacion("200002");
        response.setValorTotalPago(amountType);
        return response;
    }

}
