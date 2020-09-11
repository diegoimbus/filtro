package co.moviired.microservice.helper;

import co.moviired.microservice.constants.ConstantNumbers;
import co.moviired.microservice.domain.iso.GenericRequest;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class UtilHelper {

    private static final Double NUM_0P75 = 0.75D;
    private static final Double NUM_365P25 = 365.25D;
    private static final Double NUM_30P6001 = 30.6001D;
    private static final Double NUM_1720994P5 = 1720994.5D;
    private static final String HOUR_ISO = "HHmmss";
    private static final String MONTH_DAY_ISO = "MMdd";
    private static final String MONTH_DATE_ISO = "MMddHHmmss";

    private UtilHelper() {
        super();
    }

    public static String getDateInformation(Date currentDate, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(currentDate);
    }

    public static String strPad(String source, Integer length, String character, Integer direction) {
        String complete;
        if (direction == 0) {
            complete = StringUtils.leftPad(source, length, character);
        } else {
            complete = StringUtils.rightPad(source, length, character);
        }
        return complete;
    }

    public static GenericRequest generateBasicInfo(String[] imei, String deviceId) {
        Date currentDate = new Date();
        GenericRequest isoRequest = new GenericRequest();
        String julianDate = getJulianDate(Calendar.getInstance());
        String prefixJulian = julianDate.substring(julianDate.length() - ConstantNumbers.LENGTH_3);

        isoRequest.setTransmisionDateTime((getDateInformation(currentDate, MONTH_DATE_ISO)));
        isoRequest.setLocalHour(getDateInformation(currentDate, HOUR_ISO));
        isoRequest.setLocalDate((getDateInformation(currentDate, MONTH_DAY_ISO)));
        isoRequest.setCaptureDate((getDateInformation(currentDate, MONTH_DAY_ISO)));
        isoRequest.setRetrievalReferenceNumber(prefixJulian + imei[ConstantNumbers.LENGTH_3].substring(imei[ConstantNumbers.LENGTH_3].length() - ConstantNumbers.LENGTH_9));
        isoRequest.setCardAcceptorIdentification(UtilHelper.strPad(deviceId, ConstantNumbers.LENGTH_16, "0", ConstantNumbers.LENGTH_0));
        return isoRequest;
    }

    public static String getJulianDate(Calendar calendarDate) {
        int year = calendarDate.get(Calendar.YEAR);
        int month = calendarDate.get(Calendar.MONTH) + 1;
        int day = calendarDate.get(Calendar.DAY_OF_MONTH);
        double hour = calendarDate.get(Calendar.HOUR_OF_DAY);
        double minute = calendarDate.get(Calendar.MINUTE);
        double second = calendarDate.get(Calendar.SECOND);
        int isGregorianCal = 1;
        double fraction = (double) day + (hour + minute / 60.0D + second / 60.0D / 60.0D) / 24.0D;
        if (month < 3) {
            --year;
            month += 12;
        }

        int a = year / 100;
        int b = (2 - a + a / 4) * isGregorianCal;
        int c;
        if (year < 0) {
            c = (int) (NUM_365P25 * (double) year - NUM_0P75);
        } else {
            c = (int) (NUM_365P25 * (double) year);
        }

        int d = (int) (NUM_30P6001 * (double) (month + 1));
        double jd = (double) (b + c + d) + NUM_1720994P5 + fraction;
        return String.valueOf((int) jd);
    }

}

