package co.moviired.support.domain.dto.validator;
/*
 * Copyright @2019. Movii, SAS. Todos los derechos reservados.
 *
 * @author Bejarano, Cindy
 * @version 1, 2019
 * @since 1.0
 */

import co.moviired.base.domain.exception.DataException;
import co.moviired.support.domain.dto.Request;
import co.moviired.support.domain.dto.enums.OperationType;

public final class ValidatorHelper {

    private static final String LOG_LENGHT = "La contraseña debe tener al menos cinco digitos";
    private static final Integer LENGTH_5 = 5;

    private ValidatorHelper() {
        super();
    }

    public static void validationInput(Request request, OperationType operation) throws DataException {

        if (!OperationType.SET_PASSWORD_MAHINDRA.equals(operation) && !OperationType.INSERT.equals(operation)
                && !OperationType.UPDATE_ALL.equals(operation) && request.getMsisdn() == null) {
            throw new DataException("-2", "msisdn es un parámetro obligatorio");
        }

        ValidatorHelper.loginUpdate(request,operation);


        ValidatorHelper.generalSetPassword(request,operation);

        ValidatorHelper.updateReset(request,operation);



        ValidatorHelper.generalInsertUpdateAll(request,operation);


    }


    private static void generalInsertUpdate(Request request) throws DataException {
        if (request.getUser().getFirstName() == null) {
            throw new DataException("-2", "firsName es un parámetro obligatorio para user");
        }


        if (request.getUser().getCellphone() == null) {
            throw new DataException("-2", "cellphone es un parámetro obligatorio para user");
        }

        if (request.getUser().getUserType() == null) {
            throw new DataException("-2", "userType es un parámetro obligatorio para user");
        }

        if (request.getUser().getDob() == null) {
            throw new DataException("-2", "dob es un parámetro obligatorio para user");
        }

        if (request.getUser().getIdtype() == null) {
            throw new DataException("-2", "idtype es un parámetro obligatorio para user");
        }


        if (request.getUser().getIdno() == null) {
            throw new DataException("-2", "idno es un parámetro obligatorio para user");
        }

        if (request.getUser().getEmail() == null) {
            throw new DataException("-2", "email es un parámetro obligatorio para user");
        }

        if (request.getUser().getGender() == null) {
            throw new DataException("-2", "gender es un parámetro obligatorio para user");
        }
    }



    private static void generalInsertUpdateAll(Request request, OperationType operation) throws DataException {
        if (OperationType.INSERT.equals(operation) || OperationType.UPDATE_ALL.equals(operation)) {

            if (request.getUser() == null) {
                throw new DataException("-2", "user es un parámetro obligatorio");
            }

            if (request.getUser().getMsisdn() == null) {
                throw new DataException("-2", "msisdn es un parámetro obligatorio para user");
            }

            if (OperationType.INSERT.equals(operation)) {

                if (request.getUser().getMpin() == null) {
                    throw new DataException("-2", "mpin es un parámetro obligatorio para user");
                }

                if (request.getUser().getMpin().length() < LENGTH_5) {
                    throw new DataException("-2", LOG_LENGHT);
                }
            }

            ValidatorHelper.generalInsertUpdate(request);

        }
    }

    private static void generalSetPassword(Request request, OperationType operation) throws DataException {
        if (OperationType.SET_PASSWORD_MAHINDRA.equals(operation)) {

            if (request.getUser().getMsisdn() == null) {
                throw new DataException("-2", "msisdn es un parámetro obligatorio");
            }

            if (request.getUser().getMahindraUser() == null) {
                throw new DataException("-2", "mahindraUser es un parámetro obligatorio");
            }
            if (request.getUser().getMahindraPassword() == null) {
                throw new DataException("-2", "mahindraPassword es un parámetro obligatorio");
            }

        }
    }


    private static void loginUpdate(Request request, OperationType operation) throws DataException {
        if (OperationType.LOGIN.equals(operation) || OperationType.UPDATE_PASSWORD.equals(operation)) {

            if (request.getMpin() == null) {
                throw new DataException("-2", "mpin es un parámetro obligatorio");
            }

            if (request.getMpin().length() < LENGTH_5) {
                throw new DataException("-2", LOG_LENGHT);
            }
        }
    }

    private static void updateReset(Request request, OperationType operation) throws DataException {
        if (OperationType.UPDATE_PASSWORD.equals(operation) || OperationType.RESET_PASSWORD.equals(operation)) {

            if (request.getNewmpin() == null) {
                throw new DataException("-2", "newmpin es un parámetro obligatorio");
            }

            if (request.getNewmpin().length() < LENGTH_5) {
                throw new DataException("-2", LOG_LENGHT);
            }

        }

        if (OperationType.RESET_PASSWORD.equals(operation) && request.getOtp() == null) {
            throw new DataException("-2", "otp es un parámetro obligatorio");
        }
    }


}

