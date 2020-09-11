package co.moviired.support.helper;

import co.moviired.base.domain.exception.DataException;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.support.domain.enums.ParameterValidator;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
@Slf4j
public class Validator implements Serializable {

    private static final long serialVersionUID = -6521736626584871003L;

    public static void main(String[] args) {
        if ("3829999994:lAM1QkCcIowrCsBO4teNsg==".matches("^\\d{10}+[:]\\s$")) {
            log.info("valido");
        } else {
            log.info("NO");
        }
    }

    /**
     * validar campo parámetro, si es requerido obligatorio valida que no sea nulo,
     * si no es requerido pero viene el campo; valida que cumpla con su regex,
     * si no cumple con sus condiciones lanza un error de servicio
     *
     * @param parameter
     * @param required
     * @param validator
     * @param additionalMsg
     * @throws ServiceException
     */
    public void validateField(String parameter, boolean required, ParameterValidator validator, String additionalMsg)
            throws ServiceException {
        final String msg = null != additionalMsg
                ? new StringBuilder(validator.getMsgError()).append(" ").append(additionalMsg).toString()
                : validator.getMsgError();
        if (required) {
            validateRequiredField(parameter, msg);
        }
        if (null != parameter && !parameter.matches(validator.getRegex())) {
            throw new DataException("400", msg);
        }
    }

    private void validateRequiredField(String parameter, String msg) throws ServiceException {
        if (null == parameter) {
            throw new DataException("400", msg);
        }
    }
}

