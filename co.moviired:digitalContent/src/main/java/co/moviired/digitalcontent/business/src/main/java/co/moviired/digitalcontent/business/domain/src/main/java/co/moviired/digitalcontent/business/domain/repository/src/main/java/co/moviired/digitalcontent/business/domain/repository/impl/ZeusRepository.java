package co.moviired.digitalcontent.business.domain.repository.impl;

import co.moviired.base.domain.enumeration.ErrorType;
import co.moviired.base.domain.exception.ServiceException;
import co.moviired.digitalcontent.business.domain.entity.PinHistory;
import co.moviired.digitalcontent.business.helper.AESCrypt;
import co.moviired.digitalcontent.business.helper.EncriptPin;
import co.moviired.digitalcontent.business.properties.ZeusProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Service
public class ZeusRepository extends DataBaseRepository {

    private static final String QUERY_FIND_TX = "SELECT VEPIN_ID, VEPIN_VALOR, VEPIN_PINCIFRADO, VEPIN_AUTORIZACION, VEPIN_CELULARDESTINO, " +
            "VEPIN_EMAILDESTINO, TRNS_ID, VEPIN_NUMEROFACTURA, VEPIN_FECHATRANSACCION, TERC_ID, PRPICA_ID FROM ZEUS.VENTAPINCARD " +
            "WHERE (VEPIN_AUTORIZACION = ? OR TRNS_ID = ?) AND (VEPIN_CELULARDESTINO IS NOT NULL OR VEPIN_EMAILDESTINO IS NOT NULL) " +
            "AND VEPIN_PINCIFRADO IS NOT NULL";

    private static final String QUERY_FIND_PRODUCT = "SELECT PRPICA_ID, PRPICA_EANCODE FROM ZEUS.PRODUCTOPINCARD where PRPICA_ID = ? AND PRPICA_EANCODE IS NOT NULL";

    private final transient ZeusProperties config;

    public ZeusRepository(@NotNull ZeusProperties pConfig) {
        super(pConfig.getDriver(), pConfig.getPoolName(), pConfig.getUrl(), pConfig.getUser(), pConfig.getKey());
        this.config = pConfig;
    }

    public final PinHistory findPin(String authorizationCode, String transferId) throws ServiceException, SQLException {
        ResultSet rsTx = null;
        try (PreparedStatement preparedStatementTX = this.getConnection().prepareStatement(QUERY_FIND_TX)) {

            preparedStatementTX.setString(1, authorizationCode);
            preparedStatementTX.setString(2, transferId);

            rsTx = preparedStatementTX.executeQuery();

            if (rsTx.next()) {
                return getProductAndMakePinHistory(rsTx);
            }

            return null;
        } catch (Exception e) {
            throw new ServiceException(ErrorType.COMMUNICATION, "-20", e.getMessage(), e);
        } finally {
            if (rsTx != null) {
                rsTx.close();
            }
        }
    }

    private PinHistory getProductAndMakePinHistory(ResultSet rsTx) throws ServiceException, SQLException {
        ResultSet rsProduct = null;
        try (PreparedStatement preparedStatementProduct = this.getConnection().prepareStatement(QUERY_FIND_PRODUCT)) {
            log.info("Pin is found in getrax");

            preparedStatementProduct.setString(1, rsTx.getString(11));
            rsProduct = preparedStatementProduct.executeQuery();

            if (rsProduct.next()) {
                log.info("Product is found in getrax");
                return PinHistory.builder()
                        .transferId(rsTx.getString(7))
                        .authorizationCode(rsTx.getString(4))
                        .amount(rsTx.getString(2))
                        .phoneNumber(rsTx.getString(5))
                        .email(rsTx.getString(6))
                        .issueDate(rsTx.getString(9))
                        .personName("")
                        .eanCode(rsProduct.getString(2))
                        .agentCode(rsTx.getString(10))
                        .source("")
                        .sendMail(!rsTx.getString(6).isBlank())
                        .sendSms(!rsTx.getString(5).isBlank())
                        .templateMail(config.getEmailTemplate())
                        .templateSms(config.getSmsTemplate())
                        .pin(AESCrypt.crypt(EncriptPin.decrypt(rsTx.getString(3), config.getCryptoKey())))
                        .build();
            } else {
                log.info("Product not found in getrax");
            }
            return null;
        } catch (Exception e) {
            throw new ServiceException(ErrorType.COMMUNICATION, "-20", e.getMessage(), e);
        } finally {
            if (rsProduct != null) {
                rsProduct.close();
            }
        }
    }
}

