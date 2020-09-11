package co.moviired.microservice.constants;

import co.moviired.base.domain.StatusCode;
import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.microservice.conf.StatusCodeConfig;
import co.moviired.microservice.domain.response.ErrorDetail;
import co.moviired.microservice.soap.ErrorcodeT;

import java.util.List;

public final class UtilHelper {

    private UtilHelper() {
        super();
    }

    public static ErrorDetail parseError(List<ErrorcodeT> listErrors, StatusCodeConfig statusCodeConfig) {
        StringBuilder errorCodes = new StringBuilder();
        StringBuilder errorMessages = new StringBuilder();
        listErrors.forEach(error -> {
            StatusCode statusCode = statusCodeConfig.of(error.getRejectCode(), error.getRejectText());
            errorCodes.append(statusCode.getCode()).append(", ");
            errorMessages.append(statusCode.getMessage()).append(", ");
        });
        return new ErrorDetail(ErrorType.PROCESSING.ordinal(),
                errorCodes.substring(ConstantNumbers.LENGTH_0, errorCodes.length() - ConstantNumbers.LENGTH_2),
                errorMessages.substring(ConstantNumbers.LENGTH_0, errorMessages.length() - ConstantNumbers.LENGTH_2));
    }

}

