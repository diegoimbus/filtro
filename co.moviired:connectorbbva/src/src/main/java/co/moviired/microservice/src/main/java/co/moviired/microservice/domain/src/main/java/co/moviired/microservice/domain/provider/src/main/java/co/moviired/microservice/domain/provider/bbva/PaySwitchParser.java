package co.moviired.microservice.domain.provider.bbva;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.microservice.client.soap.cargos.*;
import co.moviired.microservice.client.soap.seguridadbasecb.GETTICKET;
import co.moviired.microservice.client.soap.seguridadbasecb.UsuarioType;
import co.moviired.microservice.conf.BankProductsProperties;
import co.moviired.microservice.domain.enums.OperationType;
import co.moviired.microservice.domain.jpa.convenios.entity.Biller;
import co.moviired.microservice.domain.jpa.convenios.repository.IBillerRepository;
import co.moviired.microservice.domain.provider.IParser;
import co.moviired.microservice.domain.request.Input;
import co.moviired.microservice.domain.response.Data;
import co.moviired.microservice.domain.response.ErrorDetail;
import co.moviired.microservice.domain.response.Outcome;
import co.moviired.microservice.domain.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Slf4j
@Service
public class PaySwitchParser implements IParser {

    private static final String HTTP_STATUS_OK = "00";
    private static final String REGEX = "\\|";

    private final BankProductsProperties bankProducts;
    private final IBillerRepository iBillerRepository;

    public PaySwitchParser(BankProductsProperties pbankProducts,
                           IBillerRepository iBillerRepository) {
        super();
        this.bankProducts = pbankProducts;
        this.iBillerRepository = iBillerRepository;
    }

    @Override
    public final GETTICKET parseRequestTicket(Input params) throws DataException {

        //Validar datos obligatorios
        validateInput(params);

        GETTICKET requestTicket = new GETTICKET();
        UsuarioType usuarioType = new UsuarioType();

        //Obtener datos y construir request
        usuarioType.setTipoIdentificacion(this.bankProducts.getTipoIdentificacion());
        usuarioType.setNumeroIdentificacion(this.bankProducts.getNumeroIdentificacion());
        requestTicket.setCanal(this.bankProducts.getCanal());
        requestTicket.setCodigoOperacion(this.bankProducts.getCodOperacionPago());
        requestTicket.setMecAutent(this.bankProducts.getMecAutentPago());
        requestTicket.setUsuarioRequest(usuarioType);

        return requestTicket;

    }

    @Override
    public final GenerarCargo parseRequestPay(@NotNull Input params) {

        GenerarCargo generarCargoRequest = new GenerarCargo();
        PagoType pagoType = new PagoType();
        AmountType amountType = new AmountType();
        DetallePagoType detallePagoType = new DetallePagoType();
        GenerarCargoRequestType generarCargoRequestType = new GenerarCargoRequestType();

        String oficinaChannel = params.getImei().split(REGEX,-1)[10].equals("") ? params.getImei().split(REGEX)[7] : params.getImei().split(REGEX)[10];

        //Obtener datos y construir request
        amountType.setCurrencyID(CurrencyCodeType.COP);
        amountType.setValue(Double.parseDouble(params.getValueToPay()));

        detallePagoType.setDatosDelPago("");
        detallePagoType.setFormaDePago(this.bankProducts.getFormaPago());
        detallePagoType.setImporte(amountType);

        pagoType.setCodigoMonedaPago(CurrencyCodeType.COP);
        pagoType.setValorTotalAPagar(amountType);
        pagoType.setFechaPago(formatDateCalendar());
        pagoType.getDetalleDelPago().add(detallePagoType);

        generarCargoRequestType.setTicketID(params.getTicket());
        generarCargoRequestType.setUPCId(params.getEchoData().split("#")[0]);
        generarCargoRequestType.setMontoDelCargo(pagoType);
        generarCargoRequestType.setUsuarioId(params.getImei().split(REGEX)[8]);
        generarCargoRequestType.setOficina(params.getImei().split(REGEX)[2].equals("CHANNEL") ? oficinaChannel : this.bankProducts.getSubscriberId());
        generarCargoRequestType.setTerminal(params.getImei().split(REGEX)[2].equals("CHANNEL") ? this.bankProducts.getTerminalChannel() : this.bankProducts.getTerminalSuscriber());
        generarCargoRequestType.setIpAddress(params.getImei().split(REGEX)[1]);
        generarCargoRequestType.setCanal(this.bankProducts.getChannel());
        generarCargoRequestType.setSubCanal(this.bankProducts.getSubChannel());
        generarCargoRequestType.setDispositivo(this.bankProducts.getDeviceType());
        generarCargoRequestType.setVersion(this.bankProducts.getVersion());
        generarCargoRequestType.setErrorLanguage(this.bankProducts.getErrorLanguage());

        generarCargoRequest.setGenerarCargoRequest(generarCargoRequestType);

        return generarCargoRequest;
    }

    @Override
    public final Response parseResponse(@NotNull Input params, @NotNull Object object, @NotNull OperationType operationType) throws DataException {

        Response response;
        Data data = new Data();
        GenerarCargoResponseType generarCargoResponse = (GenerarCargoResponseType) object;

        if (generarCargoResponse.getUPCStatus().equals(this.bankProducts.getCargoGeneradoStatus())) {

            String reference = operationType.equals(OperationType.AUTOMATIC_QUERY) ? this.getReferenceByEanCode(params) : params.getShortReferenceNumber().split(REGEX)[0];

            data.setShortReferenceNumber(reference);
            data.setAuthorizationCode(generarCargoResponse.getCodigoAutorizacion() + "|" + params.getEchoData().split("#")[0]);
            data.setBankId(this.bankProducts.getGestorId());
            data.setValueToPay(String.valueOf(generarCargoResponse.getValorTotalPago().getValue()));
            data.setTransactionId(reference);


            Outcome result = new Outcome(HttpStatus.OK, new ErrorDetail(0, HTTP_STATUS_OK, "Transaccion exitosa"));
            response = new Response(result, data);

        } else {
            Outcome result = new Outcome(HttpStatus.PROCESSING, new ErrorDetail(ErrorType.PROCESSING.ordinal(), "99", "Transaccion rechazada:" + generarCargoResponse.getUPCStatus()));
            response = new Response(result, data);
        }

        return response;
    }

    private void validateInput(Input params) throws DataException {
        if (params.getShortReferenceNumber() == null) {
            throw new DataException("-2", "El shortReferenceNumber es un parámetro obligatorio");
        }

        if (params.getImei() == null) {
            throw new DataException("-2", "El imei es un parámetro obligatorio");
        }

        if (params.getEchoData() == null) {
            throw new DataException("-2", "El echoData es un parámetro obligatorio");
        }

        if (params.getValueToPay() == null) {
            throw new DataException("-2", "El valueToPay es un parámetro obligatorio");
        }

    }

    private XMLGregorianCalendar formatDateCalendar() {

        XMLGregorianCalendar xmlDate = null;
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date());

        try {
            xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        } catch (DatatypeConfigurationException e) {
            log.error("Error format date XMLGregorianCalendar");
        }

        return xmlDate;
    }

    private String getReferenceByEanCode(Input params) throws DataException {

        String eanCode = params.getShortReferenceNumber().split(REGEX)[0];
        Biller biller = this.iBillerRepository.getByThirdPartyCode(params.getShortReferenceNumber().split(REGEX)[2]);//Codigo nura

        try {
            return eanCode.substring(biller.getReferencePosition1()-1, biller.getReferencePosition1()-1 + biller.getReferenceLength1());
        } catch (IndexOutOfBoundsException e) {
            throw new DataException("Error al obtener numero de referencia",e);
        }

    }
}

