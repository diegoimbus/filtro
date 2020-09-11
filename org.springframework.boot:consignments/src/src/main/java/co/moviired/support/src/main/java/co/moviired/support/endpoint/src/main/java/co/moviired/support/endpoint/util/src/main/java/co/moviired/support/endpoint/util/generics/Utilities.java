package co.moviired.support.endpoint.util.generics;

import co.moviired.base.util.Generator;
import co.moviired.support.conf.GlobalProperties;
import co.moviired.support.endpoint.util.exceptions.BusinessException;
import co.moviired.support.endpoint.util.exceptions.CodeErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Slf4j
@Component
public final class Utilities implements Serializable {

    private static final long serialVersionUID = -1143184049994629351L;

    private final GlobalProperties globalProperties;

    public Utilities(@NotNull GlobalProperties globalProperties) {
        super();
        this.globalProperties = globalProperties;
    }

    public static String getCurrentDate() {
        Date fechaActual = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(fechaActual);
    }

    public static String getCurrentDateFormat(String format) {
        Date fechaActual = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, new Locale("es"));
        return sdf.format(fechaActual);
    }

    public static String parseDateToStringBusiness(Date date, String format) throws BusinessException {
        if (Validation.isNull(date)) {
            return "";
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                return sdf.format(date);
            } catch (IllegalArgumentException var3) {
                throw new BusinessException(var3, CodeErrorEnum.ERRORINCORRECTDATE);
            }
        }
    }

    public String asignarCorrelativo(String correlativo) {
        if (correlativo == null || correlativo.isEmpty()) {
            correlativo = String.valueOf(Generator.correlationId());
        }

        MDC.putCloseable("correlation-id", correlativo);
        MDC.putCloseable("component", this.globalProperties.getApplicationName());
        return correlativo;
    }
}

