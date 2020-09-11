package co.moviired.microservice.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public final class UtilHelper {

    private static final Double NUM_0P75 = 0.75D;
    private static final Double NUM_365P25 = 365.25D;
    private static final Double NUM_30P6001 = 30.6001D;
    private static final Double NUM_1720994P5 = 1720994.5D;

    private UtilHelper() {
        super();
    }

    public static String getDateCompensation(String dateCompensation) throws ParseException {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        String fecha;
        try {
            Date horaCorte = timeFormat.parse(dateCompensation.trim());
            Date horaActual = timeFormat.parse(timeFormat.format(calendar.getTime()));
            if (horaActual.compareTo(horaCorte) > 0) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            fecha = new SimpleDateFormat("MMdd").format(calendar.getTime());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
        return fecha;
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

    public static String getJulianDate(Calendar calendarDate) {
        int year = calendarDate.get(Calendar.YEAR);
        int month = calendarDate.get(Calendar.MONTH) + 1;
        int day = calendarDate.get(Calendar.DAY_OF_MONTH);
        double hour = (double) calendarDate.get(Calendar.HOUR_OF_DAY);
        double minute = (double) calendarDate.get(Calendar.MINUTE);
        double second = (double) calendarDate.get(Calendar.SECOND);
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

