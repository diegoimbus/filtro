package co.moviired.digitalcontent.business.domain.repository.impl;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.digitalcontent.business.properties.SMSProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;

@Slf4j
@Service
public class SMSRepository extends DataBaseRepository {

    private static final String QUERY = "INSERT INTO SMS(srcAddress,destAddress,messageText,inserted_at,channel) VALUES ('DigitalContent',?,?,now(),1)";

    private final SMSProperties config;

    public SMSRepository(@NotNull SMSProperties pconfig) {
        super(pconfig.getDriverSmsDb(), pconfig.getPoolNameSmsDb(), pconfig.getUrlSmsDb(), pconfig.getUserSmsDb(), pconfig.getPassSmsDb());
        this.config = pconfig;
    }

    public final void sendSMS(@NotNull String phoneNumber, @NotNull String mensaje) throws ServiceException {
        try (Connection connection = this.getConnection();
             PreparedStatement cstmt = connection.prepareCall(QUERY)) {

            String suffif = (this.config.getSufijoCelular() != null) ? this.config.getSufijoCelular() : "";
            cstmt.setString(1, suffif + phoneNumber);
            cstmt.setString(2, mensaje);
            cstmt.executeUpdate();

        } catch (Exception e) {
            throw new ServiceException(ErrorType.COMMUNICATION, "-20", e.getMessage(), e);
        }
    }
}

