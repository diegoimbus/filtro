package co.moviired.digitalcontent.incomm.helper;

import co.moviired.digitalcontent.incomm.model.request.Input;
import co.moviired.digitalcontent.incomm.model.response.Data;
import co.moviired.digitalcontent.incomm.model.response.ErrorDetail;
import co.moviired.digitalcontent.incomm.model.response.Outcome;
import co.moviired.digitalcontent.incomm.model.response.Response;
import co.moviired.digitalcontent.incomm.properties.IncommProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOBasePackager;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public final class BuildTxnHelper implements Serializable {

    private static final long serialVersionUID = 2626608527218126568L;

    private static final String HOUR_FORMAT = "HHmmssZ";
    private static final String SHORT_DATE_FORMAT = "yyyyMMdd";
    private static final String LONG_DATE_FORMAT = "yyyyMMdd'T'HHmmss'Z'";
    private static final String LBL_PIN_VALUE = "Valor PIN: ";
    private static final String HEALTH_CHECK = "0800";
    private static final String ACTIVATION_CODE = "0200";
    private static final String CODE_RESULT_FIELD = "39";
    private static final String DEACTIVATION_CODE = "0400";

    private static final int PROCESING_CODE_FIELD = 3;
    private static final int EAN_CODE_FIELD = 54;
    private static final int TERMINAL_LENGTH = 8;
    private static final int USERNAME_LENGTH = 10;
    private static final int TXNID_LENGTH = 12;
    private static final int REFERENCE_NUMBER_LENGTH = 12;
    private static final int AMOUNT_LENGTH = 12;
    private static final int EAN_LENGTH = 11;
    private static final int REFERENCE_LENGTH = 19;
    private static final int STATUS_OK = 200;

    private static final int FIELD_02 = 2;
    private static final int FIELD_04 = 4;
    private static final int FIELD_07 = 7;
    private static final int FIELD_11 = 11;
    private static final int FIELD_12 = 12;
    private static final int FIELD_13 = 13;
    private static final int FIELD_22 = 22;
    private static final int FIELD_32 = 32;
    private static final int FIELD_37 = 37;
    private static final int FIELD_41 = 41;
    private static final int FIELD_42 = 42;
    private static final int FIELD_49 = 49;
    private static final int FIELD_53 = 53;
    private static final int FIELD_54 = 54;
    private static final int FIELD_70 = 70;

    private final IncommProperties incommProperties;

    // Iso8583 Packer definitions
    private transient ISOBasePackager incommPackager;

    public BuildTxnHelper(@NotNull IncommProperties pincommProperties) {
        super();
        this.incommProperties = pincommProperties;
    }

    // SERVICE METHODS

    private static String getTransactionId() {
        String idTransaccion = "" + System.currentTimeMillis();
        idTransaccion = idTransaccion.substring(idTransaccion.length() - TXNID_LENGTH);
        return idTransaccion;
    }

    public ISOMsg requestHealthCheck() throws ISOException, IOException {
        Date fechaActual = new Date();
        String currentDate = new SimpleDateFormat(LONG_DATE_FORMAT).format(fechaActual);

        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setPackager(getIncommPackager());
        isoMsg.setMTI(HEALTH_CHECK);
        isoMsg.set(FIELD_07, currentDate);
        isoMsg.set(FIELD_11, getTransactionId());
        isoMsg.set(FIELD_70, "301");

        return isoMsg;
    }

    public ISOMsg requestActivationMessage(Input request) throws ISOException, IOException {
        ISOMsg isoMsg = buildMessageRequest(request);
        isoMsg.set(PROCESING_CODE_FIELD, incommProperties.getProcessCodeActivationIncomm());
        isoMsg.set(EAN_CODE_FIELD, request.getEanCode());
        return isoMsg;
    }

    public ISOMsg requestDesactivationMessage(@NotNull Input request) throws ISOException, IOException {
        ISOMsg isoMsg = buildMessageRequest(request);
        isoMsg.set(PROCESING_CODE_FIELD, incommProperties.getProcessCodeDeactivationIncomm());

        // Limitir el EANCode a 11 dígitos
        String eanCode = request.getEanCode();
        if (eanCode != null && eanCode.length() > EAN_LENGTH) {
            eanCode = eanCode.substring(0, EAN_LENGTH);
        }
        isoMsg.set(EAN_CODE_FIELD, eanCode);

        return isoMsg;
    }

    // UTILS METHOD

    public ISOMsg requestReversionMessage(@NotNull Input request) throws ISOException, IOException {
        String idProceso = request.getProcesingCode();
        ISOMsg isoMsg = null;
        if (idProceso.equals(incommProperties.getProcessCodeActivation())) {
            isoMsg = requestActivationMessage(request);

        } else if (idProceso.equals(incommProperties.getProcessCodeDeactivation())) {
            isoMsg = requestDesactivationMessage(request);
        }

        if (isoMsg != null) {
            isoMsg.setMTI(DEACTIVATION_CODE);
        }

        return isoMsg;
    }

    private ISOBasePackager getIncommPackager() throws ISOException, IOException {
        if (incommPackager == null) {
            // Cargar el ISO packager
            incommPackager = new GenericPackager(new ClassPathResource("package/ISO87A_Incomm.xml").getInputStream());
            log.debug("INCOMM - ISO Packager [OK]");
        }

        return incommPackager;
    }

    private ISOMsg buildMessageRequest(@NotNull Input request) throws ISOException, IOException {
        Date fechaActual = new Date();
        String currentDate = new SimpleDateFormat(LONG_DATE_FORMAT).format(fechaActual);
        String currentHour = new SimpleDateFormat(HOUR_FORMAT).format(fechaActual);
        String currentMonth = new SimpleDateFormat(SHORT_DATE_FORMAT).format(fechaActual);

        String customerId = request.getCustomerId();
        customerId = customerId.length() > TERMINAL_LENGTH ? customerId.substring(0, TERMINAL_LENGTH) : customerId;

        // Username
        String username = request.getUserName();
        username = username.length() > USERNAME_LENGTH ? username.substring(0, USERNAME_LENGTH) : username;

        // Valor PIN
        String pinValue = String.valueOf(request.getAmount());
        pinValue = ISOUtil.zeropad(pinValue.concat("00"), AMOUNT_LENGTH);
        log.debug(LBL_PIN_VALUE + pinValue);

        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setPackager(getIncommPackager());
        isoMsg.setMTI(ACTIVATION_CODE);
        if (request.getShortReferenceNumber().length() > REFERENCE_LENGTH) {
            isoMsg.set(FIELD_02, request.getShortReferenceNumber().substring(request.getShortReferenceNumber().length() - REFERENCE_LENGTH).trim());
        } else {
            isoMsg.set(FIELD_02, request.getShortReferenceNumber().trim());
        }
        isoMsg.set(FIELD_04, pinValue);
        isoMsg.set(FIELD_07, currentDate);
        if (request.getControlNumber().length() < TXNID_LENGTH) {
            isoMsg.set(FIELD_11, ISOUtil.zeropad(request.getControlNumber(), TXNID_LENGTH));
        } else {
            isoMsg.set(FIELD_11, request.getControlNumber().substring(0, TXNID_LENGTH));
        }
        isoMsg.set(FIELD_12, currentHour);
        isoMsg.set(FIELD_13, currentMonth);
        isoMsg.set(FIELD_22, incommProperties.getIsoField22());
        isoMsg.set(FIELD_32, request.getIncommCode());
        isoMsg.set(FIELD_37, getField37(request.getControlNumber()));
        isoMsg.set(FIELD_41, customerId);
        isoMsg.set(FIELD_42, username);
        isoMsg.set(FIELD_49, incommProperties.getIsoField49());

        return isoMsg;
    }

    public ISOMsg requestPinSale(@NotNull Input request) throws ISOException, IOException {
        Date fechaActual = new Date();
        String currentDate = new SimpleDateFormat(LONG_DATE_FORMAT).format(fechaActual);
        String currentHour = new SimpleDateFormat(HOUR_FORMAT).format(fechaActual);
        String currentMonth = new SimpleDateFormat(SHORT_DATE_FORMAT).format(fechaActual);

        // Terminal ID
        String customerId = request.getCustomerId();
        customerId = customerId.length() > TERMINAL_LENGTH ? customerId.substring(0, TERMINAL_LENGTH) : customerId;

        // Username
        String username = request.getUserName();
        username = username.length() > USERNAME_LENGTH ? username.substring(0, USERNAME_LENGTH) : username;

        // Valor PIN
        String pinValue = String.valueOf(request.getAmount());
        pinValue = ISOUtil.zeropad(pinValue.concat("00"), AMOUNT_LENGTH);
        log.debug(LBL_PIN_VALUE + pinValue);

        // ARMAR EL ISO MESSAGE
        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setPackager(getIncommPackager());
        isoMsg.setMTI(ACTIVATION_CODE);
        isoMsg.set(PROCESING_CODE_FIELD, incommProperties.getPinIncomm());
        isoMsg.set(FIELD_04, pinValue);
        isoMsg.set(FIELD_07, currentDate);
        if (request.getControlNumber().length() < TXNID_LENGTH) {
            isoMsg.set(FIELD_11, ISOUtil.zeropad(request.getControlNumber(), TXNID_LENGTH));
        } else {
            isoMsg.set(FIELD_11, request.getControlNumber().substring(0, TXNID_LENGTH));
        }
        isoMsg.set(FIELD_12, currentHour);
        isoMsg.set(FIELD_13, currentMonth);
        isoMsg.set(FIELD_22, incommProperties.getIsoField22());
        isoMsg.set(FIELD_32, request.getIncommCode());
        isoMsg.set(FIELD_37, getField37(request.getControlNumber()));
        isoMsg.set(FIELD_41, customerId);
        isoMsg.set(FIELD_42, username);
        isoMsg.set(FIELD_49, incommProperties.getIsoField49());
        isoMsg.set(FIELD_53, incommProperties.getPinTandc());
        isoMsg.set(FIELD_54, request.getEanCode());
        return isoMsg;
    }

    public ISOMsg requestPinReverso(@NotNull Input request) throws ISOException, IOException {
        Date fechaActual = new Date();
        String currentDate = new SimpleDateFormat(LONG_DATE_FORMAT).format(fechaActual);
        String currentHour = new SimpleDateFormat(HOUR_FORMAT).format(fechaActual);
        String currentMonth = new SimpleDateFormat(SHORT_DATE_FORMAT).format(fechaActual);

        // Terminal ID
        String customerId = request.getCustomerId();
        customerId = customerId.length() > TERMINAL_LENGTH ? customerId.substring(0, TERMINAL_LENGTH) : customerId;

        // Username
        String username = request.getUserName();
        username = username.length() > USERNAME_LENGTH ? username.substring(0, USERNAME_LENGTH) : username;

        // Valor PIN
        String pinValue = String.valueOf(request.getAmount());
        pinValue = ISOUtil.zeropad(pinValue.concat("00"), AMOUNT_LENGTH);
        log.debug(LBL_PIN_VALUE + pinValue);

        // ARMAR EL ISO MESSAGE
        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setPackager(getIncommPackager());
        isoMsg.setMTI(DEACTIVATION_CODE);
        isoMsg.set(PROCESING_CODE_FIELD, incommProperties.getInactpinIncomm());
        isoMsg.set(FIELD_04, pinValue);
        isoMsg.set(FIELD_07, currentDate);
        if (request.getControlNumber().length() < TXNID_LENGTH) {
            isoMsg.set(FIELD_11, ISOUtil.zeropad(request.getControlNumber(), TXNID_LENGTH));
        } else {
            isoMsg.set(FIELD_11, request.getControlNumber().substring(0, TXNID_LENGTH));
        }
        isoMsg.set(FIELD_12, currentHour);
        isoMsg.set(FIELD_13, currentMonth);
        isoMsg.set(FIELD_22, incommProperties.getIsoField22());
        isoMsg.set(FIELD_32, request.getIncommCode());
        isoMsg.set(FIELD_37, getField37(request.getControlNumber()));
        isoMsg.set(FIELD_41, customerId);
        isoMsg.set(FIELD_42, username);
        isoMsg.set(FIELD_49, incommProperties.getIsoField49());
        isoMsg.set(FIELD_53, incommProperties.getPinTandc());
        isoMsg.set(FIELD_54, request.getEanCode());
        return isoMsg;
    }

    private String getField37(String request) throws ISOException {
        String resp;
        if (request.length() > REFERENCE_NUMBER_LENGTH) {
            resp = request.substring(request.length() - REFERENCE_NUMBER_LENGTH);
        } else {
            resp = ISOUtil.zeropad(request, REFERENCE_NUMBER_LENGTH);
        }

        return resp;
    }

    public Response parseResponse(ISOMsg incommResponse, ErrorHelper errorHelper) {
        // Estado de la transacción
        Data data;
        Outcome outcome = new Outcome();

        // Si la respuesta es errada armar el ERROR
        if ((incommResponse.getString(CODE_RESULT_FIELD).equals("00"))) {
            // Si la respuesta es OK, armar el detalle
            data = new Data();
            data.setAuthorizationCode(incommResponse.getString("38"));

            // Cifrar el PIN
            if (incommResponse.getString("2") != null) {
                data.setPin(AESCrypt.crypt(incommResponse.getString("2")));
            }

            ErrorDetail error = new ErrorDetail();
            error.setErrorType(0);
            error.setErrorCode("00");
            error.setErrorMessage("TRANSACCION EXITOSA");

            outcome.setError(error);
            outcome.setStatusCode(STATUS_OK);
            outcome.setMessage("TRANSACCION EXITOSA");

        } else {

            data = new Data();
            data.setAuthorizationCode(incommResponse.getString("38"));

            ErrorDetail error = new ErrorDetail();
            error.setErrorType(0);
            String[] arrayError = StringUtils.splitPreserveAllTokens(errorHelper.getError(incommResponse.getString(CODE_RESULT_FIELD), "Error inesperado"), '|');
            error.setErrorCode(arrayError[0]);
            error.setErrorMessage(arrayError[1]);

            outcome.setError(error);
            outcome.setStatusCode(Integer.parseInt(arrayError[0]));
            outcome.setMessage(arrayError[1]);

        }

        // Armar la respuesta completa
        Response responseDTO = new Response();
        responseDTO.setOutcome(outcome);
        responseDTO.setData(data);

        return responseDTO;
    }

}

