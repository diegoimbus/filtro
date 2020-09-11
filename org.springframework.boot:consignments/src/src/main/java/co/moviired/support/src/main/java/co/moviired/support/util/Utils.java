package co.moviired.support.util;

import co.moviired.support.conf.GlobalProperties;
import co.moviired.support.conf.StatusCodeConfig;
import co.moviired.support.exceptions.ServiceException;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import java.text.NumberFormat;
import java.util.*;

import static co.moviired.support.util.Constants.*;

@Slf4j
public class Utils {

    private static final HashMap<Integer, Map<String, String>> monthsData;

    private static final String END_DAY = "end_day";
    private static final String NAME = "name";

    static {
        monthsData = new HashMap<>();
        monthsData.put(1, new ImmutableMap.Builder<String, String>().put(NAME, "Enero").put(END_DAY, "31").build());
        monthsData.put(2, new ImmutableMap.Builder<String, String>().put(NAME, "Febrero").put(END_DAY, "28").build());
        monthsData.put(3, new ImmutableMap.Builder<String, String>().put(NAME, "Marzo").put(END_DAY, "31").build());
        monthsData.put(4, new ImmutableMap.Builder<String, String>().put(NAME, "Abril").put(END_DAY, "30").build());
        monthsData.put(5, new ImmutableMap.Builder<String, String>().put(NAME, "Mayo").put(END_DAY, "31").build());
        monthsData.put(6, new ImmutableMap.Builder<String, String>().put(NAME, "Junio").put(END_DAY, "30").build());
        monthsData.put(7, new ImmutableMap.Builder<String, String>().put(NAME, "Julio").put(END_DAY, "31").build());
        monthsData.put(8, new ImmutableMap.Builder<String, String>().put(NAME, "Agosto").put(END_DAY, "31").build());
        monthsData.put(9, new ImmutableMap.Builder<String, String>().put(NAME, "Septiembre").put(END_DAY, "30").build());
        monthsData.put(10, new ImmutableMap.Builder<String, String>().put(NAME, "Octubre").put(END_DAY, "31").build());
        monthsData.put(11, new ImmutableMap.Builder<String, String>().put(NAME, "Noviembre").put(END_DAY, "30").build());
        monthsData.put(12, new ImmutableMap.Builder<String, String>().put(NAME, "Diciembre").put(END_DAY, "31").build());
    }

    private Utils() {
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

    //TERMINOS Y CONDICIONES
    public static String asignarCorrelativo(String correlation) {
        String cId = correlation;

        if (correlation == null || correlation.isEmpty())
            cId = Utils.getRandomUUID();

        MDC.putCloseable("correlation-id", cId);
        MDC.putCloseable("component", "register");
        return cId;
    }

    public static String[] getAuthorizationParts(StatusCodeConfig statusCodeConfig, GlobalProperties globalProperties, String authorizationHeader) throws ServiceException {
        ServiceException exception = new ServiceException(
                AUTHORIZATION_HEADER_INVALID_CODE,
                statusCodeConfig.of(AUTHORIZATION_HEADER_INVALID_CODE).getMessage(), globalProperties.getApplicationName());
        if (authorizationHeader == null) throw exception;
        if (!authorizationHeader.contains(TWO_DOTS)) throw exception;
        if (authorizationHeader.length() != AUTHORIZATION_LENGTH) throw exception;
        String[] authorizationParts = authorizationHeader.split(TWO_DOTS);
        if (authorizationParts.length != 2) throw exception;
        if (authorizationParts[0].length() != PHONE_NUMBER_LENGTH) throw exception;
        if (authorizationParts[1].length() != PIN_LENGTH) throw exception;
        if (!isLongNumber(authorizationParts[0]) || !isLongNumber(authorizationParts[1])) throw exception;
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

    public static ServiceException getDefaultException(StatusCodeConfig statusCodeConfig, GlobalProperties globalProperties) {
        return new ServiceException(statusCodeConfig.of(SERVER_ERROR_CODE).getCode(),
                statusCodeConfig.of(SERVER_ERROR_CODE).getMessage(),
                globalProperties.getApplicationName());
    }

    public static String getDateStringForExtract(Integer year, Integer month, boolean isStartDate) {
        Map<String, String> monthData = getMonthData(year, month);
        String day = "1";
        if (!isStartDate) day = monthData.get(END_DAY);

        return day + " de " + monthData.get(NAME) + " del " + year;
    }

    public static Map<String, String> getMonthData(Integer year, Integer month) {
        boolean isLeap = ((year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0)));
        if (month == 2 && isLeap) {
            monthsData.get(2).put(END_DAY, "29");
        }
        return monthsData.get(month);
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

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}

