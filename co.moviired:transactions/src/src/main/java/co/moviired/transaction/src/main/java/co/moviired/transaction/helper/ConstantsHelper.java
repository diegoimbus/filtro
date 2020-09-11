package co.moviired.transaction.helper;

import java.io.Serializable;

public final class ConstantsHelper implements Serializable {

    public static final String RESPONSE = "Response: ";
    public static final String REPLACE_MSISDN = "0000000000";
    public static final String TRANSACTION_OK = "200";
    public static final String BAD_REQUEST = "400";
    public static final String LBL_INVALID_PARAMETERS = "Parametros invalidos";
    public static final String LBL_END_VALIDATION = "**********Se finaliza la  validación de los usuarios enviados**********";
    public static final String LBL_NO_EXISTEN_TRANSACCIONES = "No existen transacciones pendientes.";
    public static final String ERROR_EXCEPTION = "Ocurrio un error [Exception]: ";
    public static final String ERROR_IOEXCEPTION = "Ocurrio un error [IOException]: ";
    public static final String ERROR_SOCKETEXCEPTION = "Ocurrio un error [SocketException]: {}";
    public static final String LBLREQUEST = "Request enviado: {}";
    public static final String LBLREQUESTRECEIPT = "Request recibido: {}";
    public static final String LBLRESPONSE = "Response: {}";
    public static final String LBLTIEMPO = "Tiempo empleado en el envío/respuesta de la operacion:  ";
    public static final String LBL_EMAIL_CREATE = "Email registrado satisfactoriamente";
    public static final String LBL_EMAIL_UPDATE = "Email actualizado satisfactoriamente";
    public static final String LBL_EMAIL_QUERY = "Email consultado satisfactoriamente";
    public static final String LBL_EMAIL_SEND = "Email enviado satisfactoriamente";
    public static final String METHOD_SEND_VAUCHER = "/send-baucher";
    //Numbers
    public static final int NUMBER_4 = 4;
    public static final int NUMBER_5 = 5;
    public static final int NUMBER_6 = 6;
    public static final int NUMBER_10000 = 10000;
    private static final long serialVersionUID = 417874850713244756L;
    private ConstantsHelper() {
        super();
    }

}

