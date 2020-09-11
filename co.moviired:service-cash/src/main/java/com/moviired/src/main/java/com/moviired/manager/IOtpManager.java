package com.moviired.manager;

import co.moviired.base.domain.exception.ServiceException;
import com.moviired.client.supportotp.Response;
import com.moviired.excepciones.ManagerException;
import com.moviired.model.dto.OtpDTO;

public interface IOtpManager {

    Response generate(OtpDTO otpDTO) throws ServiceException;

    Response isValid(String phoneNumber, String otp) throws ServiceException;

    void resend(String phoneNumber, String notifyChannel) throws ManagerException;


}

