package co.moviired.digitalcontent.business.helper;

import co.moviired.digitalcontent.business.exception.ManagerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionIdHelper {

    // Transformar el TransactionID de Mahindra de alfanumerico a numerico
    // Origen - CustomerTxReference: RC180326.1150.C00991
    // Destino - RequestReference: XX180326.XXXX.XXXXXX. Deben quedar 12 digitos:
    //      4 Se cambia RC, ON por 02, 03
    //      2 digitos del dia
    //      1 digito para el NODO, Se cambia la letra del servidor por su ordinal correspondiente: A=1, B=2, C=3, ...)
    //      5 digitos del consecutivo
    public static String translateTransactionID(String txnId) throws ManagerException {
        if ((txnId == null) || (txnId.trim().isEmpty()))
            throw new ManagerException(0, "-88", "ID vacío");

        String authorization = txnId;

        // Si el numero viene de MAHINDRA, reducir a 12
        if (authorization.trim().length() == "RC180326.1150.C00991".length()) {
            txnId = changeTipoOperacion(txnId);
            txnId = replaceOrdinal(txnId);

            // QUEDAN LAS X: XXXX1803XX.1150.XXXXXX:
            // Origen: 2202180326.1150.300991 --> Resultante: 220226300991
            authorization = txnId.substring(0, 4) + txnId.substring(8, 10) + txnId.substring(16);
        }

        return authorization;
    }

    // Cambia una letra por su posicion ordinal, A=1, B=2, C=3, etc...
    private static String replaceOrdinal(String cadena) {
        final String origen = "ABCDEFGHIJ";
        final String replace = "1234567890";
        cadena = cadena.toUpperCase();
        for (int i = 0; i < origen.length(); i++) {
            cadena = cadena.replace(origen.charAt(i), replace.charAt(i));
        }
        return cadena;
    }

    // Cambia el tipo de operacion por su representacion numerica
    // Ejemplo: RC=02, ON=03, etc...
    private static String changeTipoOperacion(String number) {
        final String[] codesOrigin = new String[]{"RC", "ON"};
        final String[] codesDest = new String[]{"2202", "2203"};
        for (int i = 0; i < codesOrigin.length; i++) {
            number = number.replace(codesOrigin[i], codesDest[i]);
        }

        return number;
    }

    public static void main(String[] args) throws ManagerException {
        String mh = "RC180326.1150.C00991";
        log.info(TransactionIdHelper.translateTransactionID(mh));
    }
}

