package co.moviired.digitalcontent.business.domain.dto.validator;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.base.domain.exception.DataException;
import co.moviired.digitalcontent.business.domain.dto.DigitalContentValidator;
import co.moviired.digitalcontent.business.domain.dto.request.DigitalContentRequest;
import org.springframework.stereotype.Service;

@Service
public class ValidatePinesValidator extends DigitalContentValidator {

    public final void validationInput(DigitalContentRequest requestFormat, String merchantId, String posId, String autorization, boolean validateAmount) throws DataException {
        validateParameters(requestFormat);
        if (validateAmount) {
            validateAmount(requestFormat);
        }
        validatePostMerch(requestFormat, merchantId, posId);
        validateAuthorization(requestFormat, autorization);
    }
}

