package co.moviired.microservice.domain.provider.bbva;


import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.DataException;
import co.moviired.microservice.client.soap.operacionesclean.ReferenciaFacturaType;
import co.moviired.microservice.client.soap.operacionesclean.ValidarFactura;
import co.moviired.microservice.client.soap.operacionesclean.ValidarFacturaRequestType;
import co.moviired.microservice.client.soap.operacionesclean.ValidarFacturaResponseType;
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

@Slf4j
@Service
public class QuerySwitchParser implements IParser {

    private static final String HTTP_STATUS_OK = "00";
    private static final String REGEX = "\\|";

    private final BankProductsProperties bankProducts;
    private final IBillerRepository iBillerRepository;

    public QuerySwitchParser(BankProductsProperties bankProducts,
                             IBillerRepository iBillerRepository) {
        super();
        this.bankProducts = bankProducts;
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
        requestTicket.setCodigoOperacion(this.bankProducts.getCodOperacionConsulta());
        requestTicket.setMecAutent(this.bankProducts.getMecAutentConsulta());
        requestTicket.setUsuarioRequest(usuarioType);

        return requestTicket;
    }

    @Override
    public final ValidarFactura parseRequestQuery(@NotNull Input params, @NotNull OperationType operationType) throws DataException {

        ValidarFactura requesValidarFactura = new ValidarFactura();
        ValidarFacturaRequestType validarFacturaRequestType = new ValidarFacturaRequestType();

        String reference = operationType.equals(OperationType.AUTOMATIC_QUERY) ? this.getReferenceByEanCode(params) : params.getShortReferenceNumber().split(REGEX)[0];

        String oficinaChannel = params.getImei().split(REGEX,-1)[10].equals("") ? params.getImei().split(REGEX)[7] : params.getImei().split(REGEX)[10];

        //Obtener datos y construir request
        ReferenciaFacturaType ref = new ReferenciaFacturaType();
        ref.setCodigoReferencia(this.bankProducts.getCodigoReferencia());
        ref.setValorReferencia(reference);

        validarFacturaRequestType.setTicketID(params.getTicket());
        validarFacturaRequestType.setCodigoConvenio(Integer.parseInt(params.getShortReferenceNumber().split(REGEX)[2]));//Codigo nura
        validarFacturaRequestType.getReferenciasDeLaFactura().add(ref);
        validarFacturaRequestType.setUsuarioId(params.getImei().split(REGEX)[8]);
        validarFacturaRequestType.setOficina(params.getImei().split(REGEX)[2].equals("CHANNEL") ? oficinaChannel : this.bankProducts.getSubscriberId());
        validarFacturaRequestType.setTerminal(params.getImei().split(REGEX)[2].equals("CHANNEL") ? this.bankProducts.getTerminalChannel() : this.bankProducts.getTerminalSuscriber());
        validarFacturaRequestType.setIpAddress(params.getImei().split(REGEX)[1]);
        validarFacturaRequestType.setCanal(this.bankProducts.getChannel());
        validarFacturaRequestType.setSubCanal(this.bankProducts.getSubChannel());
        validarFacturaRequestType.setDispositivo(this.bankProducts.getDeviceType());
        validarFacturaRequestType.setVersion(this.bankProducts.getVersion());
        validarFacturaRequestType.setErrorLanguage(this.bankProducts.getErrorLanguage());

        requesValidarFactura.setValidarFacturaRequest(validarFacturaRequestType);

        return requesValidarFactura;
    }

    @Override
    public final Response parseResponse(@NotNull Input params, @NotNull Object object, @NotNull OperationType operationType) {

        Response response;
        Data data = new Data();

        ValidarFacturaResponseType validarFacturaResponse = (ValidarFacturaResponseType) object;

        if (validarFacturaResponse.getUPCStatus().equals(this.bankProducts.getFacturaValidadaStatus())) {

            if (operationType.equals(OperationType.AUTOMATIC_QUERY)) {
                data.setBillReferenceNumber(validarFacturaResponse.getFactura().getReferencias().get(0).getValorReferencia());
            } else {
                data.setShortReferenceNumber(validarFacturaResponse.getFactura().getReferencias().get(0).getValorReferencia());
            }

            data.setDate(validarFacturaResponse.getTimestamp().toString());
            data.setAuthorizationCode("");
            data.setEchoData(validarFacturaResponse.getUPCId() + "#" + params.getLastName());
            data.setValueToPay(String.valueOf(validarFacturaResponse.getFactura().getValorTotal().getValue()));
            data.setBankId(this.bankProducts.getGestorId());
            data.setAuthorizationCode(validarFacturaResponse.getUPCId());

            Outcome result = new Outcome(HttpStatus.OK, new ErrorDetail(0, HTTP_STATUS_OK, "Transaccion exitosa"));
            response = new Response(result, data);

        } else {
            Outcome result = new Outcome(HttpStatus.PROCESSING, new ErrorDetail(ErrorType.PROCESSING.ordinal(), "99", "Transaccion rechazada: " + validarFacturaResponse.getUPCStatus()));
            response = new Response(result, data);
        }

        return response;
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

    private void validateInput(Input params) throws DataException {
        if (params.getShortReferenceNumber() == null) {
            throw new DataException("-2", "El shortReferenceNumber es un parámetro obligatorio");
        }

        if (params.getImei() == null) {
            throw new DataException("-2", "El imei es un parámetro obligatorio");
        }

        if (params.getLastName() == null) {
            throw new DataException("-2", "El lastName es un parámetro obligatorio");
        }

    }
}

