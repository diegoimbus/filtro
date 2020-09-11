package co.moviired.transpiler.hermes.parser;

import co.moviired.transpiler.exception.ParseException;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.hermes.IHermesResponse;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandRequest;
import co.moviired.transpiler.jpa.movii.domain.dto.mahindra.ICommandResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


public interface IMahindraParser extends Serializable {

    default ICommandRequest parseRequest(@NotNull IHermesRequest hermesRequest) throws ParseException, JsonProcessingException {
        throw new ParseException("Método no implementado");
    }

    default IHermesResponse parseResponse(@NotNull IHermesRequest hermesRequest, @NotNull ICommandResponse command) throws ParseException {
        throw new ParseException("Método no implementado");
    }

    // UTILS METHODS

    // Transformar el TransactionID de Mahindra de alfanumérico a numérico
    // Origen - CustomerTxReference: RC180326.1150.C00991
    // Destino - RequestReference: XX180326.XXXX.XXXXXX. Deben quedar 12 dígitos:
    //      4 dígitos operacion. Se cambia RC=2202, ON=2203...
    //      2 dígitos del día
    //      1 dígito para el NODO, Se cambia la letra del servidor por su ordinal correspondiente: A=1, B=2, C=3, ...)
    //      5 dígitos del consecutivo
    default String transformAuthorizationNumber(String txnId) throws ParseException {
        if ((txnId == null) || (txnId.trim().isEmpty())) {
            throw new ParseException("Número de autorización inválido");
        }

        String authorization = txnId;

        // Si el número viene de MAHINDRA, reducir a 12
        if (authorization.trim().length() == "RC180326.1150.C00991".length()) {
            txnId = changeTipoOperacion(txnId);
            txnId = replaceOrdinal(txnId);

            // QUEDAN LAS X: XXXX1803XX.1150.XXXXXX:
            // Origen: 2202180326.1150.300991 --> Resultante: 023026300991
            authorization = txnId.substring(0, 4) + txnId.substring(8, 10) + txnId.substring(16);
        }

        return authorization;
    }

    // Cambia una letra por su posición ordinal, A=1, B=2, C=3, etc...
    default String replaceOrdinal(String cadena) {
        final String origen = "ABCDEFGHIJ";
        final String replace = "1234567890";
        cadena = cadena.toUpperCase();
        for (int i = 0; i < origen.length(); i++) {
            cadena = cadena.replace(origen.charAt(i), replace.charAt(i));
        }
        return cadena;
    }

    // Cambia el tipo de operación por su representación numérica
    // Ejemplo: RC=02, ON=03, etc...
    default String changeTipoOperacion(String number) {
        final String[] codesOrigin = new String[]{"RC", "ON"};
        final String[] codesDest = new String[]{"2202", "2203"};
        for (int i = 0; i < codesOrigin.length; i++) {
            number = number.replace(codesOrigin[i], codesDest[i]);
        }

        return number;
    }

}

