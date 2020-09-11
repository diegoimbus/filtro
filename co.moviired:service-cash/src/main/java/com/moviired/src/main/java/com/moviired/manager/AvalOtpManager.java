package com.moviired.manager;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.ServiceException;
import com.moviired.client.supportotp.Response;
import com.moviired.excepciones.ManagerException;
import com.moviired.model.dto.OtpDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AvalOtpManager implements IOtpManager {

    private static final String OPERATION_NOT_ALLOWED = "Operación no permitida. Interna al proveedor";

    public AvalOtpManager() {
        super();
    }

    /**
     * @param otpDTO
     */
    @Override
    public Response generate(OtpDTO otpDTO) throws ServiceException {
        throw new ServiceException(ErrorType.CONFIGURATION, "-000", OPERATION_NOT_ALLOWED);
    }

    /**
     * @param phoneNumber
     * @param otp
     */
    @Override
    public Response isValid(String phoneNumber, String otp) throws ServiceException {
        throw new ServiceException(ErrorType.CONFIGURATION, "-000", OPERATION_NOT_ALLOWED);
    }

    /**
     * @param phoneNumber
     * @param notifyChannel
     */
    @Override
    public void resend(String phoneNumber, String notifyChannel) throws ManagerException {
        throw new ManagerException(0, "-000", OPERATION_NOT_ALLOWED);
    }

}

