package co.moviired.support.util;

import co.moviired.support.properties.GlobalProperties;
import co.moviired.support.conf.StatusCodeConfig;
import co.moviired.support.exceptions.ServiceException;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.text.NumberFormat;
import java.util.*;

import static co.moviired.support.util.ConstantsHelper.*;

@Slf4j
public final class UtilsHelper {

    private static final Random RANDOM_GENERATOR = new Random();
    private static final int NUMBER_4 = 4;
    private static final int NUMBER_100 = 100;
    private static final int NUMBER_400 = 400;
    private static final int NUMBER_10 = 10;
    private static final int ENERO = 1;
    private static final int FEBRERO = 2;
    private static final int MARZO = 3;
    private static final int ABRIL = 4;
    private static final int MAYO = 5;
    private static final int JUNIO = 6;
    private static final int JULIO = 7;
    private static final int AGOSTO = 8;
    private static final int SEPTIEMBRE = 9;
    private static final int OCTUBRE = 10;
    private static final int NOVIEMBRE = 11;
    private static final int DICIEMBRE = 12;

    private static final Map<Integer, Map<String, String>> MONTHS_DATA;

    private static final String END_DAY = "end_day";
    private static final String NAME = "name";

    static {
        MONTHS_DATA = new HashMap<>();
        MONTHS_DATA.put(UtilsHelper.ENERO, new ImmutableMap.Builder<String, String>().put(NAME, "Enero").put(END_DAY, "31").build());
        MONTHS_DATA.put(UtilsHelper.FEBRERO, new ImmutableMap.Builder<String, String>().put(NAME, "Febrero").put(END_DAY, "28").build());
        MONTHS_DATA.put(UtilsHelper.MARZO, new ImmutableMap.Builder<String, String>().put(NAME, "Marzo").put(END_DAY, "31").build());
        MONTHS_DATA.put(UtilsHelper.ABRIL, new ImmutableMap.Builder<String, String>().put(NAME, "Abril").put(END_DAY, "30").build());
        MONTHS_DATA.put(UtilsHelper.MAYO, new ImmutableMap.Builder<String, String>().put(NAME, "Mayo").put(END_DAY, "31").build());
        MONTHS_DATA.put(UtilsHelper.JUNIO, new ImmutableMap.Builder<String, String>().put(NAME, "Junio").put(END_DAY, "30").build());
        MONTHS_DATA.put(UtilsHelper.JULIO, new ImmutableMap.Builder<String, String>().put(NAME, "Julio").put(END_DAY, "31").build());
        MONTHS_DATA.put(UtilsHelper.AGOSTO, new ImmutableMap.Builder<String, String>().put(NAME, "Agosto").put(END_DAY, "31").build());
        MONTHS_DATA.put(UtilsHelper.SEPTIEMBRE, new ImmutableMap.Builder<String, String>().put(NAME, "Septiembre").put(END_DAY, "30").build());
        MONTHS_DATA.put(UtilsHelper.OCTUBRE, new ImmutableMap.Builder<String, String>().put(NAME, "Octubre").put(END_DAY, "31").build());
        MONTHS_DATA.put(UtilsHelper.NOVIEMBRE, new ImmutableMap.Builder<String, String>().put(NAME, "Noviembre").put(END_DAY, "30").build());
        MONTHS_DATA.put(UtilsHelper.DICIEMBRE, new ImmutableMap.Builder<String, String>().put(NAME, "Diciembre").put(END_DAY, "31").build());
    }

    private UtilsHelper() {
        //Not is necessary this implementation
    }

    public static String getRandomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception ie) {
            log.error(ie.getMessage());
        }
    }

    // Verificar turno de ejecución del JOB actual
    public static boolean validateShift(int ipAddress) {
        // IP + RANDOM + TIMESTAMP
        long shift = ipAddress + RANDOM_GENERATOR.nextInt() + new Date().getTime();
        return (shift % 2 != 0);
    }


    public static String asignarCorrelativo(String correlation) {
        String cId = correlation;

        if (correlation == null || correlation.isEmpty()) {
            cId = UtilsHelper.getRandomUUID();
        }

        MDC.putCloseable("correlation-id", cId);
        MDC.putCloseable("component", "register");
        return cId;
    }

    public static String[] getAuthorizationParts(StatusCodeConfig statusCodeConfig, GlobalProperties globalProperties, String authorizationHeader) throws ServiceException {
        ServiceException exception = new ServiceException(
                AUTHORIZATION_HEADER_INVALID_CODE,
                statusCodeConfig.of(AUTHORIZATION_HEADER_INVALID_CODE).getMessage(), globalProperties.getApplicationName(), null);
        if (authorizationHeader == null) {
            throw exception;
        }
        if (!authorizationHeader.contains(TWO_DOTS)) {
            throw exception;
        }
        if (authorizationHeader.length() != AUTHORIZATION_LENGTH) {
            throw exception;
        }
        String[] authorizationParts = authorizationHeader.split(TWO_DOTS);
        if (authorizationParts.length != 2) {
            throw exception;
        }
        if (authorizationParts[0].length() != PHONE_NUMBER_LENGTH) {
            throw exception;
        }
        if (authorizationParts[1].length() != PIN_LENGTH) {
            throw exception;
        }
        if (!isLongNumber(authorizationParts[0]) || !isLongNumber(authorizationParts[1])) {
            throw exception;
        }
        log.info(LOG_NUMBER_OF_REQUEST, authorizationParts[0]);
        return authorizationParts;
    }

    public static boolean isLongNumber(String chain) {
        try {
            Long.parseLong(chain);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String getDateStringForExtract(Integer year, Integer month, boolean isStartDate) {
        Map<String, String> monthData = getMonthData(year, month);
        String day = "1";
        if (!isStartDate) {
            day = monthData.get(END_DAY);
        }

        return day + " de " + monthData.get(NAME) + " del " + year;
    }

    public static Map<String, String> getMonthData(Integer year, Integer month) {
        boolean isLeap = ((year % UtilsHelper.NUMBER_4 == 0) && ((year % UtilsHelper.NUMBER_100 != 0) || (year % UtilsHelper.NUMBER_400 == 0)));
        if (month == 2 && isLeap) {
            MONTHS_DATA.get(2).put(END_DAY, "29");
        }
        return MONTHS_DATA.get(month);
    }

    public static String getCurrencyFormat(Double amount) {
        try {
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.ENGLISH);
            return formatter.format(round(amount, 2));
        } catch (NumberFormatException e) {
            log.error("FormatToCurrency error for value {}: {}", amount, e.getMessage());
        }
        return String.valueOf(amount);
    }

    public static double round(double pvalue, int places) {
        double value = pvalue;
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        long factor = (long) Math.pow(UtilsHelper.NUMBER_10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}

