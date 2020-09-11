package co.moviired.transaction.helper;

import co.moviired.base.domain.exception.DataException;
import co.moviired.transaction.domain.request.RequestManager;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public final class UtilitiesHelper {

    private UtilitiesHelper() {
        super();
    }

    public static void closeConnection(Connection connection, PreparedStatement statement, ResultSet resultSet) {
        try {
            if (resultSet != null && !resultSet.isClosed()) {
                resultSet.close();
            }
        } catch (SQLException e) {
            log.debug(ConstantsHelper.ERROR_EXCEPTION + e.getMessage());
        }

        try {
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }
        } catch (SQLException e) {
            log.debug(ConstantsHelper.ERROR_EXCEPTION + e.getMessage());
        }

        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            log.debug(ConstantsHelper.ERROR_EXCEPTION + e.getMessage());
        }
    }


    public static void validateAuthorization(RequestManager requestManager, String autorization) throws DataException {

        if (!autorization.trim().matches("")) {

            String[] vautorization = autorization.split(":");

            if (!vautorization[0].trim().matches("") && !vautorization[1].trim().matches("")) {
                requestManager.setUser(vautorization[0]);
                requestManager.setMpin(vautorization[1]);
            } else {
                throw new DataException("10", "Error header autorization");
            }

        } else {
            throw new DataException("10", "Error header autorization");
        }

    }
}

