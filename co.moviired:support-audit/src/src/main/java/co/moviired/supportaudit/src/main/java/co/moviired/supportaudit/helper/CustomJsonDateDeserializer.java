package co.moviired.supportaudit.helper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class CustomJsonDateDeserializer extends JsonDeserializer<Date> {
    private static final int NUM_10 = 10;
    private static final int NUM_16 = 16;

    @Override
    public Date deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext) throws IOException {
        try {
            String date = jsonparser.getText();
            Date fecha = null;
            if ((date != null) && (!date.equalsIgnoreCase(""))) {
                if (date.length() == CustomJsonDateDeserializer.NUM_10) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    fecha = format.parse(date);

                } else if (date.length() == CustomJsonDateDeserializer.NUM_16) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    fecha = format.parse(date);

                } else {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    fecha = format.parse(date);
                }
            }

            // Fecha de Perú
            Locale l = new Locale("es", "PE");
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"), l);
            cal.setTime(fecha);

            return cal.getTime();

        } catch (ParseException e) {
            throw new IOException(e);
        }

    }

}

