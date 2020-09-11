package co.moviired.moneytransfer.helper;


import co.moviired.base.util.Generator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public final class UtilHelper {

    private UtilHelper() {
    }

    public static String assignCorrelative(String correlation) {

        String correlationId;

        if (correlation == null || correlation.isEmpty()) {
            correlationId = String.valueOf(Generator.correlationId());
        }else{
            correlationId = correlation;
        }
        MDC.putCloseable("correlation-id", correlationId);
        return correlationId;
    }
}

