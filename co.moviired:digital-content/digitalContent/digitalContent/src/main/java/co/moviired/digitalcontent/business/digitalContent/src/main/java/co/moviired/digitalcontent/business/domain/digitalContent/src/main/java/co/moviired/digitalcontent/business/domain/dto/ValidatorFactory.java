package co.moviired.digitalcontent.business.domain.dto;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Rivas, Rodolfo
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.base.domain.exception.ParsingException;
import co.moviired.digitalcontent.business.domain.dto.validator.ValidateCardValidator;
import co.moviired.digitalcontent.business.domain.dto.validator.ValidatePinesValidator;
import co.moviired.digitalcontent.business.domain.enums.OperationType;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class ValidatorFactory implements Serializable {

    private final ValidateCardValidator activateCardValidator;
    private final ValidatePinesValidator activatePinesValidator;

    public ValidatorFactory(ValidateCardValidator activateCardValidator, ValidatePinesValidator activatePinesValidator) {
        super();
        this.activateCardValidator = activateCardValidator;
        this.activatePinesValidator = activatePinesValidator;
    }

    public final DigitalContentValidator getValidator(OperationType operationType) throws ParsingException {
        DigitalContentValidator validator;

        switch (operationType) {

            case DIGITAL_CONTENT_CARD_ACTIVATE:
            case DIGITAL_CONTENT_CARD_INACTIVATE:
                validator = this.activateCardValidator;
                break;

            case DIGITAL_CONTENT_PINES_SALE:
            case DIGITAL_CONTENT_PINES_INACTIVATE:
                validator = this.activatePinesValidator;
                break;

            default:
                throw new ParsingException("99", "Operación inválida");
        }

        return validator;
    }

}

