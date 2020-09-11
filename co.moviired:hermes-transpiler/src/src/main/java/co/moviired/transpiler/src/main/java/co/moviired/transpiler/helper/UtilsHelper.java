package co.moviired.transpiler.helper;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public final class UtilsHelper {

    private UtilsHelper() {
        super();
    }

    public static String parseDate(String requestDate) {
        String retorno = requestDate;
        try {
            final SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date dateValue = input.parse(requestDate);
            SimpleDateFormat output = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
            retorno = output.format(dateValue);
        } catch (java.text.ParseException e) {
            log.error("\n{}\n", e.getMessage());
        }
        return retorno;
    }

}

