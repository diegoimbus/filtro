package co.moviired.support.endpoint.bancobogota.factory.impl;

import co.moviired.support.endpoint.util.exceptions.BusinessException;
import co.moviired.support.endpoint.util.exceptions.CodeErrorEnum;

import java.util.HashMap;
import java.util.Map;

public class ExtractChannelCommon {
    public static final String CONSIGNMENT_CHANNEL = "CONSIGNMENT_CHANNEL";
    public static final String CONSIGNMENT_CLIENT = "CONSIGNMENT_CLIENT";

    private ExtractChannelCommon() {
    }

    public static Map<String, String> extractConsignmentChannel(String reference) throws BusinessException {
        String realReference = "";
        Map<String, String> channelInfo = new HashMap<>();
        StringBuilder channel = new StringBuilder("0");
        if (reference.length() <= 12) {
            realReference = reference.substring(1);
            realReference = realReference.replaceFirst("^0*", "");
            channel.append(reference, 0, 1);
        } else {
            if (reference.length() < 16) {
                throw new BusinessException(CodeErrorEnum.INVALID_BILL_NUMBER_LENGTH);
            }

            realReference = reference.substring(reference.length() - 16);
            channel.append(realReference, 5, 6);
        }

        channelInfo.put(CONSIGNMENT_CHANNEL, channel.toString());
        channelInfo.put(CONSIGNMENT_CLIENT, realReference);
        return channelInfo;
    }
}

