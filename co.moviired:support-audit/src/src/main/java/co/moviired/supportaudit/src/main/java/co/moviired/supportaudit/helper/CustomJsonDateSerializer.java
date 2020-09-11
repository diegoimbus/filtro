package co.moviired.supportaudit.helper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class CustomJsonDateSerializer extends JsonSerializer<Date> {
    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (date != null) {
            String format = "yyyy-MM-dd HH:mm:ss";

            // Fecha de Perú
            Locale l = new Locale("es", "PE");
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"), l);
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, 1);

            if ((cal.get(Calendar.HOUR) == 0) && (cal.get(Calendar.MINUTE) == 0) && (cal.get(Calendar.SECOND) == 0) && (cal.get(Calendar.MILLISECOND) == 0)) {
                format = "yyyy-MM-dd";
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            jsonGenerator.writeString(simpleDateFormat.format(date));
        }
    }
}

