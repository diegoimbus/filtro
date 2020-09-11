package co.moviired.gateway.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

/***
 * This class generates a correlationId as the standard say, rule by the format
 * : yyMMddHHSSsssRR. where
 **/

@Slf4j
@Component
public final class CorrelationIdGenerator {
    private static final int NUMBER_3 = 3;
    private static final int NUMBER_4 = 4;
    private static final int NUMBER_100 = 100;
    private final Random random = new Random();

    public String generateCorrelationId(String ip) {
        LocalDateTime localDateTime = LocalDateTime.now();
        StringBuilder correlationIdPart = new StringBuilder();
        String stringYearToValidate = Integer.toString(localDateTime.getYear());
        String stringMonthToValidate = Integer.toString(localDateTime.getMonth().getValue());
        String stringDayToValidate = Integer.toString(localDateTime.getDayOfMonth());
        String stringHourToValidate = Integer.toString(localDateTime.getHour());
        String stringMinuteToValidate = Integer.toString(localDateTime.getMinute());
        String stringSecondToValidate = Integer.toString(localDateTime.getSecond());
        String stringNanoToValidate = Integer.toString(localDateTime.getNano());

        // Esto va para desarrollo
        StringBuilder ipConditional = new StringBuilder();
        try {
            if (ip == null) {
                ipConditional.append("111");
            } else if (ip.split("\\.").length > CorrelationIdGenerator.NUMBER_3) {
                ipConditional.append(ip.split("\\.")[CorrelationIdGenerator.NUMBER_3]);
            } else {
                ipConditional.append("111");
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        correlationIdPart.append(ipConditional);
        correlationIdPart.append(stringYearToValidate, 2, CorrelationIdGenerator.NUMBER_4);
        correlationIdPart.append(StringUtils.leftPad(stringMonthToValidate, 2, "0"));
        correlationIdPart.append(StringUtils.leftPad(stringDayToValidate, 2, "0"));
        correlationIdPart.append(StringUtils.leftPad(stringHourToValidate, 2, "0"));
        correlationIdPart.append(StringUtils.leftPad(stringMinuteToValidate, 2, "0"));
        correlationIdPart.append(StringUtils.leftPad(stringSecondToValidate, 2, "0"));
        correlationIdPart.append(StringUtils.leftPad(stringNanoToValidate.substring(0, CorrelationIdGenerator.NUMBER_3), CorrelationIdGenerator.NUMBER_3, "0"));
        correlationIdPart.append((random.nextInt() * CorrelationIdGenerator.NUMBER_100) / CorrelationIdGenerator.NUMBER_100);

        return correlationIdPart.toString();
    }

}

